package systemc.lib;

public class SignalBool extends Signal {
    public SignalBool(String name, ESensitiveType sensitive_type) {
        super(name, EDataType.Bool, sensitive_type);
    }

    public boolean read() {
        boolean data = false;
        try {
            data = ((int)SystemCLib.Signal_read_bool.invoke(obj_ptr) != 0) ? true : false;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return data;
    }

    public void write(boolean data_bool) {
        try {
            int data = data_bool ? 1 : 0;
            SystemCLib.Signal_write_bool.invoke(obj_ptr, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
