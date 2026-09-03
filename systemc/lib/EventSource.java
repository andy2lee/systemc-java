package systemc.lib;

import java.lang.foreign.*;

public class EventSource {
    String name;
    EDataType data_type;
    ESensitiveType sensitive_type;
    MemorySegment obj_ptr;
    ClassCleaner class_cleaner;

    public EventSource(String name, EDataType data_type, ESensitiveType sensitive_type) {
        this.name = name;
        this.data_type = data_type;
        this.sensitive_type = sensitive_type;
        sensitive_valid_check();
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_name() {
        return name;
    }

    public void set_data_type(EDataType data_type) {
        this.data_type = data_type;
    }

    public EDataType get_data_type() {
        return data_type;
    }

    public int get_native_data_type() {
        switch (data_type) {
            case EDataType.Bool:
                return 0;
            case EDataType.Int:
                return 1;
            case EDataType.UInt32:
                return 2;
            default:
                return 0;
        }
    }

    public void set_sensitive_type(ESensitiveType sensitive_type) {
        this.sensitive_type = sensitive_type;
    }

    public ESensitiveType get_sensitive_type() {
        return sensitive_type;
    }

    public void set_obj_ptr(MemorySegment obj_ptr) {
        this.obj_ptr = obj_ptr;
    }

    public MemorySegment get_obj_ptr() {
        return obj_ptr;
    }

    void sensitive_valid_check() {
        if ((sensitive_type != ESensitiveType.Val)
            && (data_type != EDataType.Bool)) {
            throw new IllegalArgumentException("Posedge or negedge trigger should be set bool data type.");        
        }
    }
}
