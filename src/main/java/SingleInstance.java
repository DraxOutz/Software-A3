package main.java;

import java.io.*;
import java.nio.channels.*;

public class SingleInstance {

    private static FileLock lock;
    private static FileOutputStream fos;

    public static boolean lockInstance(String lockFile) {
        try {
            File file = new File(lockFile);
            fos = new FileOutputStream(file);
            FileChannel channel = fos.getChannel();

            lock = channel.tryLock();

            if (lock == null) {
                // Não conseguiu adquirir lock = já existe instância
                fos.close();
                return false;
            }

            // Lock adquirido
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public static void releaseLock() {
        try {
            if (lock != null) {
                lock.release();
            }
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}

