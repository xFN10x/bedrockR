package fn10.bedrockr.utils;

import java.awt.Component;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.windows.RWorkspace;
import fn10.bedrockr.windows.base.RLoadingScreen;

public class RFileOperations {

    private static final String USER_DIR = System.getProperty("user.home");
    private static final String BASE_PATH = USER_DIR + "/.bedrockR/";
    private static final File BaseDirectory = new File(BASE_PATH);
    private static final String[] ILLEGAL_CHARACTERS = {
            "<",
            ">",
            ":",
            "\"",
            "/",
            "\\",
            "|",
            "?"
    };

    public static boolean validFolderName(String proposed) {
        var bool = true;

        if (proposed.length() >= 200)
            bool = false;
        for (String string : ILLEGAL_CHARACTERS) {
            if (proposed.contains(string))
                bool = false;
        }

        return bool;
    }

    public static String[] getWorkspaces(Component doingThis) {
        var folder = new File(getBaseDirectory(doingThis).getAbsolutePath() + "/workspace/").toPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            var list = new List();
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    Launcher.LOG.info(path.getFileName().toString());
                    list.add(path.getFileName().toString());
                }
            }
            return list.getItems();
        } catch (IOException e) {
            showError(doingThis, "IO Error", BASE_PATH, e);
            return new String[] {""};
        }
    }

    public static void openWorkspace(JFrame doingThis, SourceWPFile WPF) {
        var workspaceView = new RWorkspace(WPF);
        SwingUtilities.invokeLater(() -> {
            workspaceView.setVisible(true);
            doingThis.dispose();
        });
    }

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
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, ToCreate, true);
    }

    public static File getFileFromWorkspace(Component windowDoingThis, String WorkspaceName, String ToCreate,
            Boolean strict) {
        try {
            var proposed = BaseDirectory + "/workspace/" + WorkspaceName + ToCreate;
            var proposedFile = new File(proposed);
            if (proposedFile.exists() || strict) {
                return proposedFile;
            } else
                return Files.createFile(proposedFile.toPath()).toFile();
        } catch (Exception e) {
            showError(windowDoingThis, "IO Error", "Failed to get WP File", e);
            e.printStackTrace();
            return null;
        }

    }

    public static boolean createWorkspace(RLoadingScreen loading, // String workspaceName, String minimumVersion)
            WPFile wpf, ImageIcon imgIcon)
            throws DirectoryNotEmptyException, IOException {

        String[] wsFolders = {
                "/elements/",
                "/resources/"
        };

        File base = getBaseDirectory(loading);

        File wsFolder = new File(base.getAbsolutePath() + "/workspace/" + wpf.WorkspaceName + "/");

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

                var srcWPF = new SourceWPFile(wpf);
                srcWPF.buildJSONFile(loading, wpf.WorkspaceName);

                var srcIcon = new File(wsFolder.getAbsolutePath() + "/icon." + wpf.IconExtension);
                trying = srcIcon;

                // make buffered image
                var image = imgIcon.getImage();
                BufferedImage BI = new BufferedImage(image.getWidth(null),
                        image.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                BI.getGraphics().drawImage(image, 0, 0, null);

                ImageIO.write(BI, wpf.IconExtension, srcIcon);

                return true;

            } catch (Exception e) { // handle exception
                showError(loading, "IO Error, with path " + wsFolder.getAbsolutePath(), "IO Error", e);
                throw e;
            }
        }

    }

}
