package fn10.bedrockr.utils;

import java.util.logging.Level;

public class RLogUtils {
    public static void exception(String msg, Throwable threw) {
        RFileOperations.LOG.log(Level.SEVERE, msg, threw);

    }

    public static void exception(Throwable threw) {
        exception(threw.getMessage(), threw);
    }

    public static void warnException(String msg, Throwable e) {
        RFileOperations.LOG.log(Level.WARNING, msg, e);
    }

    public static void warnException(Throwable e) {
        warnException(e.getMessage(), e);
    }
}
