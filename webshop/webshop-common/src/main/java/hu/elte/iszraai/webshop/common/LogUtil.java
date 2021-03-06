package hu.elte.iszraai.webshop.common;

public class LogUtil {

    public static void debug(final String msg) {
        System.out.flush();
        System.err.flush();
        System.out.println("DEBUG: " + msg);
        System.out.flush();
    }

    public static void info(final String msg) {
        System.out.flush();
        System.err.flush();
        System.out.println("INFO: " + msg);
        System.out.flush();
    }

    public static void warn(final String msg) {
        System.out.flush();
        System.err.flush();
        System.err.println("WARN: " + msg);
        System.err.flush();
    }

    public static void error(final String msg) {
        System.out.flush();
        System.err.flush();
        System.err.println("ERROR: " + msg);
        System.err.flush();
    }

    public static void error(final String msg, final Throwable ex) {
        System.out.flush();
        System.err.flush();
        System.err.println("ERROR: " + msg);
        ex.printStackTrace();
        System.err.flush();
    }

    public static void error(final Throwable ex) {
        System.out.flush();
        System.err.flush();
        ex.printStackTrace();
        System.err.flush();
    }

}
