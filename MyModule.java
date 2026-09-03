import systemc.lib.Module;
import systemc.lib.Port;
import systemc.lib.PortUInt32;
import systemc.lib.Process;
import systemc.lib.SignalUInt32;

import static systemc.lib.SystemCLib.*;
import static systemc.lib.EPortType.*;
import static systemc.lib.EProcessType.*;
import static systemc.lib.ESensitiveType.*;

public class MyModule {
    PortUInt32 port_a;
    PortUInt32 port_b;
    PortUInt32 port_c;
    SignalUInt32 sig_d;
    int a_count, b_count;
    
    public MyModule() {
        port_a = new PortUInt32(port_in, "a", Val);
        port_b = new PortUInt32(port_in, "b", Val);
        port_c = new PortUInt32(port_out, "c", Val);
        sig_d = new SignalUInt32("d", Val);
        var mod = new Module("MyModule", new Port[] {port_a, port_b, port_c});

        var proc = new Process(mod, SC_METHOD);
        proc.sensitive_add(port_a);
        proc.sensitive_add(port_b);
        proc.sensitive_add(port_c);
        proc.sensitive_add(sig_d);
        proc.create_process(this, "always_block");
    }

    public void always_block() {
        System.out.println("always_block");
        int a_data = port_a.read();
        int b_data = port_b.read();
        int y_data = a_data + b_data;

        sig_d.write(y_data);
        System.out.println(y_data);
        int d_data = sig_d.read();
        System.out.println(d_data);

        port_c.write(d_data);

        System.out.println("update tb");
        a_count++;
        b_count++;

        port_a.write(a_count);
        port_b.write(b_count);

        next_trigger(1.0);
    }
}
