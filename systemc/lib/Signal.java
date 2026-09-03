package systemc.lib;

import java.lang.foreign.*;

public class Signal extends EventSource {
    public Signal(String name, EDataType data_type, ESensitiveType sensitive_type) {
        super(name, data_type, sensitive_type);
        create_signal();
    }

    void create_signal() {
        try {
            obj_ptr = (MemorySegment)SystemCLib.sc_signal.invoke(get_native_data_type(), SystemCLib.arena.allocateFrom(name));
            class_cleaner = new ClassCleaner(SystemCLib.sc_del_signal, obj_ptr);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
