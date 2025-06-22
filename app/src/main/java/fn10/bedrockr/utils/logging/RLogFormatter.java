package fn10.bedrockr.utils.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return new Date(record.getMillis()) + ": "
                + record.getMessage() + "\n";
    }

}
