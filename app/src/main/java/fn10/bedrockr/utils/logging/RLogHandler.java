package fn10.bedrockr.utils.logging;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class RLogHandler extends StreamHandler {

    public RLogHandler() {
        super(System.out,new RLogFormatter());
        setFilter(new RLogFilter());
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public void flush() {
        super.flush();
    }

    @Override
    public void close() throws SecurityException {
        super.close();
    }
}
