package fn10.bedrockr.utils.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RLogFormatter extends Formatter {

    private final static Calendar cal =  Calendar.getInstance();

    @Override
    public String format(LogRecord record) {
        return new SimpleDateFormat("hh:mm:ss.SS").format(cal.getTime()) + " : "
                + record.getMessage() + "\n";
    }

}
