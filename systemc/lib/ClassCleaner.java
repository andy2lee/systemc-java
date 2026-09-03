package systemc.lib;

// C:/Program Files/SystemC

import java.lang.ref.Cleaner;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class ClassCleaner {
    static final Cleaner cleaner = Cleaner.create();
    final Cleaner.Cleanable cleanable;
    MemorySegment ptr_addr;

    private static class clean_state implements Runnable {
        private MemorySegment ptr;
        private MethodHandle clean_hlr;
        
        public clean_state(MethodHandle clean_hlr, MemorySegment ptr) {
            this.clean_hlr = clean_hlr;
            this.ptr = ptr;
        }

        @Override
        public void run() {
            if (clean_hlr == null) {
                return;
            }
            try {
                clean_hlr.invoke(ptr);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    
    public ClassCleaner(MethodHandle clean_hlr, MemorySegment ptr) {
        ptr_addr = ptr;
        if (clean_hlr != null) {
            this.cleanable = cleaner.register(this, new clean_state(clean_hlr, ptr));
        } else {
            this.cleanable = null;
        }
    }

    void close() {
        cleanable.clean();
    }

    MemorySegment na_ptr() {
        return ptr_addr;
    }
}
