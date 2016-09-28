package webshop.common;

public class LogUtil {

    public static void debug(final String msg) {
        System.out.println("DEBUG: " + msg);
    }

    public static void info(final String msg) {
        System.out.println("INFO: " + msg);
    }

    public static void warn(final String msg) {
        System.err.println("WARN: " + msg);
    }

    public static void error(final String msg) {
        System.err.println("ERROR: " + msg);
    }

    public static void error(final String msg, final Throwable ex) {
        System.err.println("ERROR: " + msg);
        ex.printStackTrace();
    }

}
