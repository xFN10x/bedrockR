package fn10.bedrockr.windows.utils;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.windows.base.RLoadingScreen;

public class RFileOperations {

    private static String USER_DIR = System.getProperty("user.home");
    private static String BASE_PATH = USER_DIR + "/.bedrockR/";
    private static File BaseDirectory = new File(BASE_PATH);

    public static File getBaseDirectory(Component doingThis) {
        try {
            if (!BaseDirectory.exists()) {
                return Files.createDirectories(BaseDirectory.toPath()).toFile();
            } else
                return BaseDirectory;
        } catch (Exception e) {
            showError(doingThis, "Failed to get base dir. (how the hell?)", "IO Error", e);
            e.printStackTrace();
        }
        return BaseDirectory;
    }

    private static void showError(Component parent, String msg, String title, Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        JOptionPane.showMessageDialog(parent,
                msg + "\n" + sw.toString(),
                title, JOptionPane.ERROR_MESSAGE);
    }

    public static File getFileFromWorkspace(Component windowDoingThis, String WorkspaceName, String ToCreate) {
        try {
            var proposed = BaseDirectory + "/workspace/" + WorkspaceName + ToCreate;
            var proposedFile = new File(proposed);
            if (proposedFile.exists()) {
                return proposedFile;
            } else
                return Files.createFile(proposedFile.toPath()).toFile();
        } catch (Exception e) {
            showError(windowDoingThis, "IO Error", "Failed to get WP File", e);
            e.printStackTrace();
            return null;
        }
        
    }

    public static boolean createWorkspace(RLoadingScreen loading, String workspaceName, String minimumVersion)
            throws DirectoryNotEmptyException, IOException {

        String[] wsFolders = {
                "/elements/",
                "/resources/"
        };

        File base = getBaseDirectory(loading);

        File wsFolder = new File(base.getAbsolutePath() + "/workspace/" + workspaceName + "/");

        if (wsFolder.exists()) { // throw if folder is already here
            var e = new IOException("Folder " + wsFolder.getAbsolutePath() + " already exists.");
            showError(loading, "Failed to make folder; Folder" + wsFolder.getAbsolutePath() + " already exists.",
                    "Failure!", e);
            throw e;
        } else {
            File trying;
            try { // try

                trying = wsFolder;

                loading.changeText("Making directories...");

                Files.createDirectories(wsFolder.toPath());

                for (String string : wsFolders) {
                    trying = new File(wsFolder.getAbsolutePath() + "/" + string);
                    Files.createDirectories(trying.toPath());
                }

                loading.changeText("Creating workspace...");

                var srcWPF = new SourceWPFile(new WPFile(workspaceName, minimumVersion));
                srcWPF.buildJSONFile(loading, workspaceName);

                return true;

            } catch (Exception e) { // handle exception
                JOptionPane.showMessageDialog(loading,
                        "Failed to make Directory " + wsFolder.getAbsolutePath() + ". \n"
                                + e.getStackTrace().toString(),
                        "Failed to make workspace.", JOptionPane.ERROR_MESSAGE);
                throw e;
            }
        }

    }

}
