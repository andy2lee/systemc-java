// g++ -shared -std=c++17 -I "D:\SOFTWARE\SystemC\include" systemc_wrapper.cc -L "D:\SOFTWARE\SystemC\lib" -lsystemc -o systemc_wrapper.dll

#include <systemc>

using callback_t = void(*)();

#if defined(_WIN32) || defined(_WIN64)
    #define DART_DLL_EXPORT __declspec(dllexport)
#else
    #define DART_DLL_EXPORT
#endif

#ifdef __cplusplus
extern "C" {
#endif
    void DART_DLL_EXPORT sc_start(void);
    void DART_DLL_EXPORT sc_start_args1(double);
    double DART_DLL_EXPORT sc_time_stamp(void);
    void DART_DLL_EXPORT next_trigger(double);
    void* DART_DLL_EXPORT new_Module(char*, char**, uint32_t*, uint32_t, char**, uint32_t*, uint32_t, void**, void**);
    void DART_DLL_EXPORT del_Module(void*);
    const char* DART_DLL_EXPORT Module_name(void*);

    void DART_DLL_EXPORT Module_create_process(void*, void**, uint32_t, uint32_t*, callback_t, char*, uint32_t);
    void* DART_DLL_EXPORT Module_new_InputPort(void*, int, char*);
    void* DART_DLL_EXPORT Module_new_OutputPort(void*, int, char*);

    void* DART_DLL_EXPORT new_InputPort(int, char*);
    int DART_DLL_EXPORT InputPort_read_uint32_t(void*);
    int DART_DLL_EXPORT InputPort_Signal_read_uint32_t(void*);
    void DART_DLL_EXPORT InputPort_Signal_write_uint32_t(void*, uint32_t);
    int DART_DLL_EXPORT InputPort_Signal_read_bool(void*);
    void DART_DLL_EXPORT InputPort_Signal_write_bool(void*, uint32_t);

    void* DART_DLL_EXPORT new_OutputPort(int, char*);
    void DART_DLL_EXPORT OutputPort_write_uint32_t(void*, uint32_t);
    int DART_DLL_EXPORT OutputPort_Signal_read_uint32_t(void*);
    void DART_DLL_EXPORT OutputPort_Signal_write_uint32_t(void*, uint32_t);
    int DART_DLL_EXPORT OutputPort_Signal_read_bool(void*);
    void DART_DLL_EXPORT OutputPort_Signal_write_bool(void*, uint32_t);

    void* DART_DLL_EXPORT new_Signal(int, char*);
    void DART_DLL_EXPORT del_Signal(int, void*);
    void DART_DLL_EXPORT Signal_write_bool(void*, uint32_t);
    int DART_DLL_EXPORT Signal_read_bool(void*);
    void DART_DLL_EXPORT Signal_write_uint32_t(void*, uint32_t);
    int DART_DLL_EXPORT Signal_read_uint32_t(void*);
#ifdef __cplusplus
}
#endif
