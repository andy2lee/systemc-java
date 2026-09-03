package systemc.lib;

import java.lang.foreign.*;
import java.lang.invoke.*;
import static java.lang.foreign.ValueLayout.*;

public final class SystemCLib {
    static final Arena arena = Arena.ofConfined();
    static final Linker linker = Linker.nativeLinker();

    static MethodHandle sc_start, sc_start_args1;
    static MethodHandle sc_module, sc_del_module;
    static MethodHandle Module_add_callback;
    static MethodHandle sc_module_name;
    static MethodHandle sc_module_create_process;
    static MethodHandle sc_module_new_InputPort, sc_module_new_OutputPort;

    static MethodHandle next_trigger;
    static MethodHandle sc_time_stamp;

    static MethodHandle sc_in, sc_in_read_uint32_t;
    static MethodHandle sc_out, sc_out_write_uint32_t;

    static MethodHandle sc_signal, sc_del_signal;
    static MethodHandle Signal_write_bool, Signal_read_bool;
    static MethodHandle Signal_write_uint32_t, Signal_read_uint32_t;
    
    static MethodHandle InputPort_Signal_read_uint32_t, InputPort_Signal_write_uint32_t;
    static MethodHandle InputPort_Signal_read_bool, InputPort_Signal_write_bool;

    static MethodHandle OutputPort_Signal_read_uint32_t, OutputPort_Signal_write_uint32_t;
    static MethodHandle OutputPort_Signal_read_bool, OutputPort_Signal_write_bool;

    static MethodHandle downcall(SymbolLookup lib, String name, FunctionDescriptor fd) {
        MemorySegment addr = lib.find(name).orElseThrow(()->new RuntimeException("symbol not found: " + name));
        return linker.downcallHandle(addr, fd);
    }

    static {
        SymbolLookup lib = SymbolLookup.libraryLookup("systemc_wrapper.dll", arena);
        sc_start = downcall(lib, "sc_start", FunctionDescriptor.ofVoid());
        sc_start_args1 = downcall(lib, "sc_start_args1", FunctionDescriptor.ofVoid(JAVA_DOUBLE));
        sc_module = downcall(lib, "new_Module", 
            FunctionDescriptor.of(ADDRESS, ADDRESS,
                ADDRESS, ADDRESS, JAVA_INT,
                ADDRESS, ADDRESS, JAVA_INT,
                ADDRESS, ADDRESS
            ));
        sc_del_module = downcall(lib, "del_Module", FunctionDescriptor.ofVoid(ADDRESS));
        next_trigger = downcall(lib, "next_trigger", FunctionDescriptor.ofVoid(JAVA_DOUBLE));
        sc_module_name = downcall(lib, "Module_name", FunctionDescriptor.of(ADDRESS, ADDRESS));
        sc_module_create_process = downcall(lib, "Module_create_process", 
            FunctionDescriptor.ofVoid(
                ADDRESS, ADDRESS, JAVA_INT, 
                ADDRESS, ADDRESS, ADDRESS, JAVA_INT
            ));
        sc_module_new_InputPort = downcall(lib, "Module_new_InputPort", FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_INT, ADDRESS));
        sc_module_new_OutputPort = downcall(lib, "Module_new_OutputPort", FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_INT, ADDRESS));
        sc_time_stamp = downcall(lib, "sc_time_stamp", FunctionDescriptor.of(JAVA_DOUBLE));
        sc_in = downcall(lib, "new_InputPort", FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS));
        sc_in_read_uint32_t = downcall(lib, "InputPort_read_uint32_t", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        sc_out = downcall(lib, "new_OutputPort", FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS));
        sc_out_write_uint32_t = downcall(lib, "OutputPort_write_uint32_t", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
        
        sc_signal = downcall(lib, "new_Signal", FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS));
        sc_del_signal = downcall(lib, "del_Signal", FunctionDescriptor.ofVoid(JAVA_INT, ADDRESS));
        Signal_write_uint32_t = downcall(lib, "Signal_write_uint32_t", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
        Signal_read_uint32_t = downcall(lib, "Signal_read_uint32_t", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        Signal_write_bool = downcall(lib, "Signal_write_bool", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
        Signal_read_bool = downcall(lib, "Signal_read_bool", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        InputPort_Signal_read_uint32_t = downcall(lib, "InputPort_Signal_read_uint32_t", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        InputPort_Signal_write_uint32_t = downcall(lib, "InputPort_Signal_write_uint32_t", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
        InputPort_Signal_read_bool = downcall(lib, "InputPort_Signal_read_bool", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        InputPort_Signal_write_bool = downcall(lib, "InputPort_Signal_write_bool", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
        OutputPort_Signal_read_uint32_t = downcall(lib, "OutputPort_Signal_read_uint32_t", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        OutputPort_Signal_write_uint32_t = downcall(lib, "OutputPort_Signal_write_uint32_t", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
        OutputPort_Signal_read_bool = downcall(lib, "OutputPort_Signal_read_bool", FunctionDescriptor.of(JAVA_INT, ADDRESS));
        OutputPort_Signal_write_bool = downcall(lib, "OutputPort_Signal_write_bool", FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT));
    }

    public static void sc_start() {
        try {
            sc_start.invoke();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void sc_start(double sec) {
        try {
            sc_start_args1.invoke(sec);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void next_trigger(double sec) {
        try {
            next_trigger.invoke(sec);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
