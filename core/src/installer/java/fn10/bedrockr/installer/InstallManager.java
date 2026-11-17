package fn10.bedrockr.installer;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InstallManager {

    private static final Logger log = LoggerFactory.getLogger(InstallManager.class);

    public static void install(InstallInfo info, Path installPath) throws IOException {
        log.info("Starting install....");
        log.info("bedrockR version {} ({}), with JDK {}", info.ver(), info.semVer(), info.jdkLevel());
        Files.createDirectories(installPath);
        
        log.debug("Moving JDK...");
        RIFileUtils.extractZippedResourceTo("jdk.zip", installPath);
    }
}
