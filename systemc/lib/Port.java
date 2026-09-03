package systemc.lib;

public class Port extends EventSource {
    EPortType port_type;
    
    public Port(EPortType port_type, String name, EDataType data_type, ESensitiveType sensitive_type) {
        super(name, data_type, sensitive_type);
        this.port_type = port_type;
    }

    public EPortType get_port_type() {
        return port_type;
    }
}
