package systemc.lib;

import java.lang.foreign.*;
import java.lang.invoke.*;
import java.util.ArrayList;
import static java.lang.foreign.ValueLayout.*;

public class Process {
    Module mod;
    EProcessType process_type;
    MemorySegment sigbases_objs_arr;
    MemorySegment sigbases_sensitive_type_arr;
    int sigbases_objs_cnt;
    int process_event_sources_cnt;
    ArrayList<EventSource> process_event_sources;

    public Process(Module mod, EProcessType process_type) {
        this.mod = mod;
        this.process_type = process_type;
        process_event_sources = new ArrayList<>();
    }

    public void sensitive_add(EventSource e_src) {
        process_event_sources.add(e_src);
    }

    public void create_process(Object obj, String cb_name) {
        sensitive_allocate_add_all();
        try {
            int comb_seq_set = 0;
            if (process_type == EProcessType.SC_METHOD) {
                comb_seq_set = 1;
            }

            MethodHandle cb_hdlr = MethodHandles.lookup().findVirtual(
                obj.getClass(),
                cb_name,
                MethodType.methodType(void.class)
            ).bindTo(obj);

            MemorySegment cb_p = SystemCLib.linker.upcallStub(cb_hdlr, 
                FunctionDescriptor.ofVoid(), SystemCLib.arena);
            
            SystemCLib.sc_module_create_process.invoke(
                mod.get_module_ptr(), sigbases_objs_arr, sigbases_objs_cnt, sigbases_sensitive_type_arr,
                cb_p, SystemCLib.arena.allocateFrom(cb_name),
                comb_seq_set
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void sensitive_allocate_add_all() {
        int process_event_sources_cnt = process_event_sources.size();

        sigbases_objs_arr = SystemCLib.arena.allocate(ADDRESS, process_event_sources_cnt);
        sigbases_sensitive_type_arr = SystemCLib.arena.allocate(JAVA_INT, process_event_sources_cnt);

        for (int i = 0; i < process_event_sources_cnt; i++) {
            EventSource cur_e_src = process_event_sources.get(i);
            sigbases_objs_arr.setAtIndex(ADDRESS, i, cur_e_src.get_obj_ptr());
            sigbases_sensitive_type_arr.setAtIndex(JAVA_INT, i, cur_e_src.get_sensitive_type().ordinal());
        }
    }
}
