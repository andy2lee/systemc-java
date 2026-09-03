#include "systemc_wrapper.h"


class EventSourceBase {
public:
    virtual ~EventSourceBase() = default;
    virtual const sc_core::sc_event& value_changed_event() = 0;
    virtual const sc_core::sc_event& posedge_event() = 0;
    virtual const sc_core::sc_event& negedge_event() = 0;
};

class SignalBase : public EventSourceBase {
public:
    virtual ~SignalBase() = default;
};

template<typename T>
class SignalImpl : public SignalBase {
public:
    sc_core::sc_signal<T> sig;

    SignalImpl(const char* name) : sig(name) {}
    
    const sc_core::sc_event& value_changed_event() override {
        return sig.value_changed_event();
    }

    const sc_core::sc_event& posedge_event() override {
        throw std::runtime_error("posedge only support for bool signal");
    }

    const sc_core::sc_event& negedge_event() override {
        throw std::runtime_error("negedge only support for bool signal");
    }
};

template<>
class SignalImpl<bool> : public SignalBase {
public:
    sc_core::sc_signal<bool> sig;

    SignalImpl(const char* name) : sig(name) {}

    const sc_core::sc_event& value_changed_event() override {
        return sig.value_changed_event();
    }

    const sc_core::sc_event& posedge_event() override {
        return sig.posedge_event();
    }

    const sc_core::sc_event& negedge_event() override {
        return sig.negedge_event();
    }
};

class PortBase : public EventSourceBase {
public:
    virtual ~PortBase() = default;
};

template<typename T>
class InputPortImpl : public PortBase {
public:
    sc_core::sc_in<T> port;
    sc_core::sc_signal<T>* sig;

    InputPortImpl(const char* name) : port(name) {
        sig = new sc_core::sc_signal<T>((std::string(name) + "_sig").c_str());
        port.bind(*sig);
    }

    ~InputPortImpl() {
        delete sig;
    }

    const sc_core::sc_event& value_changed_event() override {
        return port.value_changed_event();
    }

    const sc_core::sc_event& posedge_event() override {
        throw std::runtime_error("Not support.");
    }

    const sc_core::sc_event& negedge_event() override {
        throw std::runtime_error("Not support.");
    }
};

template<typename T>
class OutputPortImpl : public PortBase {
public:
    sc_core::sc_out<T> port;
    sc_core::sc_signal<T>* sig;

    OutputPortImpl(const char* name) : port(name) {
        sig = new sc_core::sc_signal<T>((std::string(name) + "_sig").c_str());
        port.bind(*sig);
    }

    ~OutputPortImpl() {
        delete sig;
    }

    const sc_core::sc_event& value_changed_event() override {
        return port.value_changed_event();
    }

    const sc_core::sc_event& posedge_event() override {
        throw std::runtime_error("Not support.");
    }

    const sc_core::sc_event& negedge_event() override {
        throw std::runtime_error("Not support.");
    }
};

class Module : public sc_core::sc_module {
public:
    std::vector<PortBase*> input_ports;
    std::vector<PortBase*> output_ports;

    Module(sc_core::sc_module_name name,
        char** sc_in_names, uint32_t* sc_in_types, uint32_t sc_in_n,
        char** sc_out_names, uint32_t* sc_out_types, uint32_t sc_out_n,
        void** sc_in_obj, void** sc_out_obj
    ) : sc_module(name) {
        build_ports(sc_in_names, sc_in_types, sc_in_n, 
                    sc_out_names, sc_out_types, sc_out_n, 
                    sc_in_obj, sc_out_obj);
    }

    ~Module() {
        for (auto& vec : input_ports) {
            delete vec;
        }
        for (auto& vec : output_ports) {
            delete vec;
        }
    }

    void build_ports(
        char** sc_in_names, uint32_t* sc_in_types, uint32_t sc_in_n,
        char** sc_out_names, uint32_t* sc_out_types, uint32_t sc_out_n,
        void** sc_in_obj, void** sc_out_obj
    ) {
        for (uint32_t i = 0; i < sc_in_n; i++) {
            auto* new_input_port = new_InputPort(sc_in_types[i], sc_in_names[i]);
            sc_in_obj[i] = new_input_port;
            input_ports.push_back(new_input_port);
        }
        for (uint32_t i = 0; i < sc_out_n; i++) {
            auto* new_output_port = new_OutputPort(sc_out_types[i], sc_out_names[i]);
            sc_out_obj[i] = new_output_port;
            output_ports.push_back(new_output_port);
        }
    }

    void create_process(
        EventSourceBase** event_source_bases, uint32_t sigbases_nums, uint32_t* sigbases_sensitive_type,
        callback_t cb, const char* cb_name,
        uint32_t method_type
    ) {
        sc_core::sc_spawn_options opt;
        if (method_type == 1) {
            opt.spawn_method();
        }

        for (uint32_t i = 0; i < sigbases_nums; i++) {
            switch (sigbases_sensitive_type[i]) {
                case 0:
                    opt.set_sensitivity(&event_source_bases[i]->value_changed_event());
                    break;
                case 1:
                    opt.set_sensitivity(&event_source_bases[i]->posedge_event());
                    break;
                case 2:
                    opt.set_sensitivity(&event_source_bases[i]->negedge_event());
                    break;
                default:
                    opt.set_sensitivity(&event_source_bases[i]->value_changed_event());
                    break;
            }
        }

        sc_core::sc_spawn(cb, cb_name, &opt);
    }

    PortBase* new_InputPort(uint32_t type, char* sig_name) {
        switch (type) {
            case 0:
                return new InputPortImpl<bool>(sig_name);
            case 1:
                return new InputPortImpl<int>(sig_name);
            case 2:
                return new InputPortImpl<uint32_t>(sig_name);
            default:
                return new InputPortImpl<bool>(sig_name);
        }

        return nullptr;
    }

    PortBase* new_OutputPort(uint32_t type, char* sig_name) {
        switch (type) {
            case 0:
                return new OutputPortImpl<bool>(sig_name);
            case 1:
                return new OutputPortImpl<int>(sig_name);
            case 2:
                return new OutputPortImpl<uint32_t>(sig_name);
            default:
                return new OutputPortImpl<bool>(sig_name);
        }

        return nullptr;
    }
};

void sc_start() {
    sc_core::sc_start();
}

void sc_start_args1(double sec) {
    sc_core::sc_start(sec, sc_core::SC_SEC);
}


void Module_create_process(
    void* module,
    void** event_source_bases, uint32_t sigbases_nums, uint32_t* sigbases_sensitive_type,
    callback_t cb, char* cb_name,
    uint32_t method_type
) {
    static_cast<Module*>(module)->create_process(
        reinterpret_cast<EventSourceBase**>(event_source_bases), sigbases_nums, sigbases_sensitive_type,
        cb, cb_name,
        method_type);
}

void* Module_new_InputPort(void* module, int type, char* sig_name) {
    return static_cast<Module*>(module)->new_InputPort(type, sig_name);
}

void* Module_new_OutputPort(void* module, int type, char* sig_name) {
    return static_cast<Module*>(module)->new_OutputPort(type, sig_name);
}

const char* Module_name(void* module) {
    return (static_cast<Module*>(module)->name());
}

void* new_Module(char* name,
    char** sc_in_nums, uint32_t* sc_in_types, uint32_t sc_in_n,
    char** sc_out_nums, uint32_t* sc_out_types, uint32_t sc_out_n,
    void** sc_in_obj, void** sc_out_obj
) {
    return new Module(name,
        sc_in_nums, sc_in_types, sc_in_n,
        sc_out_nums, sc_out_types, sc_out_n,
        sc_in_obj, sc_out_obj
    );
}

void del_Module(void* module) {
    delete static_cast<Module*>(module);
}

void next_trigger(double sec) {
    sc_core::next_trigger(sc_core::sc_time(sec, sc_core::SC_SEC));
}

void* new_InputPort(int type, char* sig_name) {
    switch (type) {
        case 0:
            return new InputPortImpl<bool>(sig_name);
        case 1:
            return new InputPortImpl<int>(sig_name);
        case 2:
            return new InputPortImpl<uint32_t>(sig_name);
        default:
            return new InputPortImpl<bool>(sig_name);
    }
}

int InputPort_read_uint32_t(void* signal) {
    auto* sig = static_cast<InputPortImpl<uint32_t>*>(static_cast<PortBase*>(signal));
    return sig->port.read();
}

int InputPort_Signal_read_uint32_t(void* signal) {
    auto* sig = static_cast<InputPortImpl<uint32_t>*>(static_cast<PortBase*>(signal));
    return sig->sig->read();
}

void InputPort_Signal_write_uint32_t(void* signal, uint32_t data) {
    auto* sig = static_cast<InputPortImpl<uint32_t>*>(static_cast<PortBase*>(signal));
    return sig->sig->write(data);
}

int InputPort_Signal_read_bool(void* signal) {
    auto* sig = static_cast<InputPortImpl<bool>*>(static_cast<PortBase*>(signal));
    return static_cast<int>(sig->sig->read());
}

void InputPort_Signal_write_bool(void* signal, uint32_t data) {
    auto* sig = static_cast<InputPortImpl<bool>*>(static_cast<PortBase*>(signal));
    sig->sig->write(static_cast<bool>(data));
}

void* new_OutputPort(int type, char* sig_name) {
    switch (type) {
        case 0:
            return new OutputPortImpl<bool>(sig_name);
        case 1:
            return new OutputPortImpl<int>(sig_name);
        case 2:
            return new OutputPortImpl<uint32_t>(sig_name);
        default:
            return new OutputPortImpl<bool>(sig_name);
    }
}

void OutputPort_write_uint32_t(void* signal, uint32_t data) {
    auto* sig = static_cast<OutputPortImpl<uint32_t>*>(static_cast<PortBase*>(signal));
    sig->port.write(data);
}

int OutputPort_Signal_read_uint32_t(void* signal) {
    auto* sig = static_cast<OutputPortImpl<uint32_t>*>(static_cast<PortBase*>(signal));
    return sig->sig->read();
}

void OutputPort_Signal_write_uint32_t(void* signal, uint32_t data) {
    auto* sig = static_cast<OutputPortImpl<uint32_t>*>(static_cast<PortBase*>(signal));
    sig->sig->write(data);
}

int OutputPort_Signal_read_bool(void* signal) {
    auto* sig = static_cast<OutputPortImpl<bool>*>(static_cast<PortBase*>(signal));
    return static_cast<int>(sig->sig->read());
}

void OutputPort_Signal_write_bool(void* signal, uint32_t data) {
    auto* sig = static_cast<OutputPortImpl<bool>*>(static_cast<PortBase*>(signal));
    sig->sig->write(static_cast<bool>(data));
}

void* new_Signal(int type, char* sig_name) {
    switch (type) {
        case 0:
            return new SignalImpl<bool>(sig_name);
        case 1:
            return new SignalImpl<int>(sig_name);
        case 2:
            return new SignalImpl<uint32_t>(sig_name);
        default:
            return new SignalImpl<bool>(sig_name);
    }

    return nullptr;
}

void del_Signal(int type, void* signal) {
    switch (type) {
        case 0:
            delete static_cast<SignalImpl<bool>*>(signal);
            break;
        case 1:
            delete static_cast<SignalImpl<int>*>(signal);
            break;
        case 2:
            delete static_cast<SignalImpl<uint32_t>*>(signal);
            break;
        default:
            delete static_cast<SignalImpl<bool>*>(signal);
            break;
    }
}

void Signal_write_uint32_t(void* signal, uint32_t data) {
    auto* sig = static_cast<SignalImpl<uint32_t>*>(static_cast<SignalBase*>(signal));
    sig->sig.write(data);
}

int Signal_read_uint32_t(void* signal) {
    auto* sig = static_cast<SignalImpl<uint32_t>*>(static_cast<SignalBase*>(signal));
    return sig->sig.read();
}

void Signal_write_bool(void* signal, uint32_t data) {
    auto* sig = static_cast<SignalImpl<bool>*>(static_cast<SignalBase*>(signal));
    sig->sig.write(static_cast<bool>(data));
}

int Signal_read_bool(void* signal) {
    auto* sig = static_cast<SignalImpl<bool>*>(static_cast<SignalBase*>(signal));
    return static_cast<int>(sig->sig.read());
}

double sc_time_stamp() {
    return sc_core::sc_time_stamp().value();
}
