package systemc.lib;

public class PortBool extends Port {
    public PortBool(EPortType port_type, String name, ESensitiveType sensitive_type) {
        super(port_type, name, EDataType.Bool, sensitive_type);
    }

    boolean input_read() {
        boolean data = false;
        try {
            data = ((int)SystemCLib.InputPort_Signal_read_bool.invoke(obj_ptr) != 0) ? true : false;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return data;
    }

    boolean output_read() {
        boolean data = false;
        try {
            data = ((int)SystemCLib.OutputPort_Signal_read_bool.invoke(obj_ptr) != 0) ? true : false;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return data;
    }

    void input_write(boolean data_bool) {
        try {
            int data = data_bool ? 1 : 0;
            SystemCLib.InputPort_Signal_write_bool.invoke(obj_ptr, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void output_write(boolean data_bool) {
        try {
            int data = data_bool ? 1 : 0;
            SystemCLib.OutputPort_Signal_write_bool.invoke(obj_ptr, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean read() {
        boolean data;
        switch (port_type) {
            case port_in:
                data = input_read();
                break;
            case port_out:
                data = output_read();
                break;
            default:
                data = output_read();
                break;
        }

        return data;
    }

    public void write(boolean data) {
        switch (port_type) {
            case port_in:
                input_write(data);
                break;
            case port_out:
                output_write(data);
            default:
                output_write(data);
                break;
        }
    }
}
