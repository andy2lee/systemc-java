import static systemc.lib.SystemCLib.*;

public class App {
    public static void main(String[] args) {
        new MyModule();
        sc_start(5);
    }
}
