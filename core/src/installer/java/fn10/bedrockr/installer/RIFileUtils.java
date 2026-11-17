package fn10.bedrockr.installer;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

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
    
    public static void extractZippedResourceTo(String resName, Path dir) throws IOException {
        byte[] zipBytes = readRes(resName);
        File tempZip = dir.resolve(resName + "-temp").toFile();
        FileUtils.writeByteArrayToFile(tempZip, zipBytes);
        ZipUnArchiver unArchiver = new ZipUnArchiver(tempZip);
        unArchiver.extract();
    }
}
