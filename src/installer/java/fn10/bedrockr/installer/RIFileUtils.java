package fn10.bedrockr.installer;

import java.io.IOException;
import java.io.InputStream;

public class RIFileUtils {

    private static final Class<RIFileUtils> cls = RIFileUtils.class;

    public static String readResAsString(String name) {
        return new String(readRes(name));
    }
        public static byte[] readRes(String name) {
        try {
            InputStream resStr = cls.getResourceAsStream("/" + name);
            if (resStr == null) throw new IOException("Failed to open stream for resource: " + name);
            return resStr.readAllBytes();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
