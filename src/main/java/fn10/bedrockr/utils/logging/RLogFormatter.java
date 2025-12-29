package fn10.bedrockr.utils.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class RLogFormatter extends Formatter {

    private final static Calendar cal = Calendar.getInstance();
    // taken from
    // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_ORANGE = "\u001B[38;5;214m";

    @Override
    public String format(LogRecord record) {
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            if (record.getLevel() == Level.WARNING)
                return ANSI_ORANGE + "("
                        + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1)
                        + " @ "
                        + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS").format(cal.getTime())) + ") : "
                        + record.getMessage() + "\n" + sw.toString() + ANSI_RESET;
            else {
                return ANSI_RED + "("
                        + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1)
                        + " @ "
                        + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS").format(cal.getTime())) + ") : "
                        + sw.toString() + ANSI_RESET;
            }
        } else if (record.getLevel() == Level.WARNING) {
            return ANSI_ORANGE + "("
                    + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1)
                    + " @ "
                    + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS").format(cal.getTime())) + ") : "
                    + record.getMessage() + "\n" + ANSI_RESET;
        } else if (record.getLevel() == Level.SEVERE) {
            return ANSI_RED + "("
                    + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1)
                    + " @ "
                    + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS").format(cal.getTime())) + ") : "
                    + record.getMessage() + "\n" + ANSI_RESET;
        } else {
            return ANSI_GREEN + "("
                    + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1)
                    + " @ "
                    + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS").format(cal.getTime())) + ") : " + ANSI_RESET
                    + record.getMessage() + "\n" + ANSI_RESET;
        }
    }
}
