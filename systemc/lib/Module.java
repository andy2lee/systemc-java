package systemc.lib;

import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;

public class Module {
    String module_name;
    int port_in_nums, port_out_nums, port_nums;
    MemorySegment port_in_arr, port_in_types_arr, port_in_obj_arr;
    MemorySegment port_out_arr, port_out_types_arr, port_out_obj_arr;
    MemorySegment module_ptr;
    Port[] ports_in, ports_out;
    ClassCleaner class_cleaner;

    public Module(String module_name, Port[] ports) {
        this.module_name = module_name;
        port_allocate(ports);
        port_add_all(ports);
        create_module();
    }

    void port_in_add(int port_in_push_cnt, Port port_in) {
        port_in_arr.setAtIndex(ADDRESS, port_in_push_cnt, SystemCLib.arena.allocateFrom(port_in.get_name()));
        port_in_types_arr.setAtIndex(JAVA_INT, port_in_push_cnt, port_in.get_native_data_type());
        ports_in[port_in_push_cnt] = port_in;
    }

    void port_out_add(int port_out_push_cnt, Port port_out) {
        port_out_arr.setAtIndex(ADDRESS, port_out_push_cnt, SystemCLib.arena.allocateFrom(port_out.get_name()));
        port_out_types_arr.setAtIndex(JAVA_INT, port_out_push_cnt, port_out.get_native_data_type());
        ports_out[port_out_push_cnt] = port_out;
    }

    void port_allocate(Port[] ports) {
        for (Port port : ports) {
            switch (port.get_port_type()) {
                case port_in:
                    port_in_nums++;
                    break;
                case port_out:
                    port_out_nums++;
                    break;
                default:
                    port_out_nums++;
                    break;
            }
        }
        port_nums = ports.length;

        port_in_arr = SystemCLib.arena.allocate(ADDRESS, port_in_nums);
        port_in_types_arr = SystemCLib.arena.allocate(JAVA_INT, port_in_nums);
        port_in_obj_arr = SystemCLib.arena.allocate(ADDRESS, port_in_nums);

        port_out_arr = SystemCLib.arena.allocate(ADDRESS, port_out_nums);
        port_out_types_arr = SystemCLib.arena.allocate(JAVA_INT, port_out_nums);
        port_out_obj_arr = SystemCLib.arena.allocate(ADDRESS, port_out_nums);

        ports_in = new Port[port_in_nums];
        ports_out = new Port[port_out_nums];
    }

    void port_add_all(Port[] ports) {
        int port_in_push_cnt = 0, port_out_push_cnt = 0;

        for (int i = 0; i < port_nums; i++) {
            switch (ports[i].get_port_type()) {
                case port_in:
                    port_in_add(port_in_push_cnt, ports[i]);
                    port_in_push_cnt++;
                    break;
                case port_out:
                    port_out_add(port_out_push_cnt, ports[i]);
                    port_out_push_cnt++;
                    break;
                default:
                    port_out_add(port_out_push_cnt, ports[i]);
                    port_out_push_cnt++;
                    break;
            }
        }
    }

    void create_module() {
        try {
            // init sc_module
            module_ptr = (MemorySegment)SystemCLib.sc_module.invoke(
                SystemCLib.arena.allocateFrom(module_name),
                port_in_arr, port_in_types_arr, port_in_nums,
                port_out_arr, port_out_types_arr, port_out_nums,
                port_in_obj_arr, port_out_obj_arr
            );
            class_cleaner = new ClassCleaner(SystemCLib.sc_del_module, module_ptr);

            // port obj ptr dispatch to each Ports when module create done.
            for (int i = 0; i < port_in_nums; i++)
                ports_in[i].set_obj_ptr(port_in_obj_arr.getAtIndex(ADDRESS, i));
            
            for (int i = 0; i < port_out_nums; i++)
                ports_out[i].set_obj_ptr(port_out_obj_arr.getAtIndex(ADDRESS, i));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public MemorySegment get_module_ptr() {
        return module_ptr;
    }
}
