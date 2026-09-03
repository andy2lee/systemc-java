package systemc.lib;

public class SignalUInt32 extends Signal {
    public SignalUInt32(String name, ESensitiveType sensitive_type) {
        super(name, EDataType.UInt32, sensitive_type);
    }

    public int read() {
        int data = 0;
        try {
            data = (int)SystemCLib.Signal_read_uint32_t.invoke(obj_ptr);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return data;
    }

    public void write(int data) {
        try {
            SystemCLib.Signal_write_uint32_t.invoke(obj_ptr, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
