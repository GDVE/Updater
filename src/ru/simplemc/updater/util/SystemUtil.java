package ru.simplemc.updater.util;

import java.lang.reflect.Method;

public class SystemUtil {

    private static final byte operationSystemId = generateOperationSystemId();
    private static final byte operationSystemArch = generateOperationSystemArch();

    private static byte generateOperationSystemId() {
        String operationSystem = System.getProperty("os.name").toLowerCase();

        if (operationSystem.contains("win"))
            return 2;

        if (operationSystem.contains("mac"))
            return 3;

        if (operationSystem.contains("solaris") || operationSystem.contains("sunos"))
            return 1;

        if (operationSystem.contains("linux") || operationSystem.contains("unix"))
            return 0;

        return 4;
    }

    private static byte generateOperationSystemArch() {
        if (isWindows())
            return System.getenv("ProgramFiles(x86)") == null ? (byte) 32 : 64;
        else
            return System.getProperty("os.arch").contains("64") ? (byte) 64 : 32;
    }

    public static boolean isWindows() {
        return getOperationSystemId() == 2;
    }

    public static boolean isMac() {
        return getOperationSystemId() == 3;
    }

    public static boolean isUnix() {
        return getOperationSystemId() == 0;
    }

    public static byte getOperationSystemId() {
        return operationSystemId;
    }

    public static String getSystemIdWithArch() {

        switch (getOperationSystemId()) {
            case 0:
                return getSystemArch() == 64 ? "linux-x64" : "linux-i586";
            case 2:
                return getSystemArch() == 64 ? "windows-x64" : "windows-i586";
            case 3:
                return "macosx-x64";
        }

        return getSystemArch() == 64 ? "unknown-x64" : "unknown-i586";
    }

    public static byte getSystemArch() {
        return operationSystemArch;
    }

    public static void halt() {
        try {
            Class<?> af = Class.forName("java.lang.Shutdown");
            Method m = af.getDeclaredMethod("halt0", int.class);
            m.setAccessible(true);
            m.invoke(null, 0);
        } catch (Exception ex) {
            System.exit(0);
        }
    }

    public static void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
