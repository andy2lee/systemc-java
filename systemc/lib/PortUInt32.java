package systemc.lib;

public class PortUInt32 extends Port {
    public PortUInt32(EPortType port_type, String name, ESensitiveType sensitive_type) {
        super(port_type, name, EDataType.UInt32, sensitive_type);
    }

    int input_read() {
        try {
            return (int)SystemCLib.InputPort_Signal_read_uint32_t.invoke(obj_ptr);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return 0;
    }

    int output_read() {
        try {
            return (int)SystemCLib.OutputPort_Signal_read_uint32_t.invoke(obj_ptr);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return 0;
    }

    void input_write(int data) {
        try {
            SystemCLib.InputPort_Signal_write_uint32_t.invoke(obj_ptr, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void output_write(int data) {
        try {
            SystemCLib.OutputPort_Signal_write_uint32_t.invoke(obj_ptr, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public int read() {
        int data;
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

    public void write(int data) {
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
