package fn10.bedrockr.utils.logging;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class RLogFilter implements Filter {

  @Override
  public boolean isLoggable(LogRecord arg0) {
    if (arg0.getLevel() == Level.CONFIG)
      return false;
    return true;
  }

}
