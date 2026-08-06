package fn10.bedrockr.utils;

import java.util.logging.Level;

public class RLogUtils {
    public static void exception(String msg, Throwable threw) {
        RFileOperations.LOG.log(Level.SEVERE, msg, threw);
        
    }

    public static void exception(Throwable threw) {
        exception(threw.getMessage(), threw);
    }
}
