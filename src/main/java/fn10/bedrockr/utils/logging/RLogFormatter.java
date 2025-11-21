package fn10.bedrockr.utils.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RLogFormatter extends Formatter {

    private final static Calendar cal = Calendar.getInstance();
    // taken from
    // https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Override
    public String format(LogRecord record) {
        return ANSI_GREEN + "(" + record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1)
                + " @ "
                + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS").format(cal.getTime())) + ") : " + ANSI_WHITE
                + record.getMessage() + "\n";
    }

}
