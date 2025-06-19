package fn10.bedrockr.windows.utils;

import java.io.File;
import java.nio.file.Files;

public class RFileOperations {

    private static String USER_DIR = System.getProperty("user.home");
    private static String BASE_PATH = USER_DIR + "/.bedrockR/";
    private static File BaseDirectory = new File(BASE_PATH);

    public static File getBaseDirectory() {
        try {
            if (!BaseDirectory.exists()) {
                return Files.createDirectories(BaseDirectory.toPath()).toFile();
            } else
                return BaseDirectory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseDirectory;
    }
    public static File getFileFromWorkspace(String WorkspaceName,String ToCreate) {
        try {
           var proposed = BaseDirectory+"workspace/"+WorkspaceName+ToCreate;
           var proposedFile = new File(proposed);
           if (proposedFile.exists()) {
                return proposedFile;
           } else
           return Files.createFile(proposedFile.toPath()).toFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseDirectory;
    }

}
