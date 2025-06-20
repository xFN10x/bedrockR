package fn10.bedrockr.windows.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import fn10.bedrockr.windows.base.RLoadingScreen;

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

    public static File getFileFromWorkspace(String WorkspaceName, String ToCreate) {
        try {
            var proposed = BaseDirectory + "workspace/" + WorkspaceName + ToCreate;
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

    public static boolean createWorkspace(RLoadingScreen loading, String workspaceName)
            throws DirectoryNotEmptyException, IOException {

        File base = getBaseDirectory();

        loading.setModal(true);

        File wsFolder = new File(base.getAbsolutePath() + "/" + workspaceName + "/");



        if (wsFolder.exists()) { // throw if folder is already here
            var e = new IOException("Folder " + wsFolder.getAbsolutePath() + " already exists.");
            JOptionPane.showMessageDialog(loading,
                    "Folder " + wsFolder.getAbsolutePath() + " already exists. \n" + e.getStackTrace().toString(),
                    "Failed to make workspace.", JOptionPane.ERROR_MESSAGE);
            throw e;
        } else {
            try { //try

                Files.createDirectories(wsFolder.toPath());

                return true;

            } catch (Exception e) { //handle exception
                JOptionPane.showMessageDialog(loading,
                        "Failed to make Directory " + wsFolder.getAbsolutePath() + ". \n"
                                + e.getStackTrace().toString(),
                        "Failed to make workspace.", JOptionPane.ERROR_MESSAGE);
                throw e;
            }
        }

    }

}
