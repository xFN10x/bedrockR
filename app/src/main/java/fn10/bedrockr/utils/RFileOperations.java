package fn10.bedrockr.utils;

import java.awt.Frame;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
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
    private static final Map<String,Class<? extends ElementSource>> ELEMENT_EXTENSION_CLASSES = new HashMap<>();
    static {
        ELEMENT_EXTENSION_CLASSES.put("itemref", SourceItemElement.class);
    }

    public static final String WPFFILENAME = "workspace.WPF";

    public static Class<? extends ElementSource> getElementClassFromFileExtension(Frame doingThis,String fileExtension) {
        try {
            return ELEMENT_EXTENSION_CLASSES.get(fileExtension);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Invalid Element File", "Reloading Error", e);
            return null;
        }
    }

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

    public static String[] getWorkspaces(Frame doingThis) {
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
            ErrorShower.showError(doingThis, "IO Error", BASE_PATH, e);
            return new String[] { "" };
        }
    }

    public static void openWorkspace(Frame doingThis, SourceWPFile WPF) {
        var workspaceView = new RWorkspace(WPF);
        SwingUtilities.invokeLater(() -> {
            workspaceView.setVisible(true);
            doingThis.dispose();
        });
    }

    public static File getBaseDirectory(Frame doingThis, String Folder) {
        var file = new File(BASE_PATH + Folder);
        try {
            if (!file.exists()) {
                return Files.createDirectories(file.toPath()).toFile();
            } else
                return file;
        } catch (Exception e) {
            ErrorShower.showError(doingThis, "Failed to get base dir. (how the hell?)", "IO Error", e);
            e.printStackTrace();
        }
        return BaseDirectory;
    }

    public static File getBaseDirectory(Frame doingThis) {
        try {
            if (!BaseDirectory.exists()) {
                return Files.createDirectories(BaseDirectory.toPath()).toFile();
            } else
                return BaseDirectory;
        } catch (Exception e) {
            ErrorShower.showError(doingThis, "Failed to get base dir. (how the hell?)", "IO Error", e);
            e.printStackTrace();
        }
        return BaseDirectory;
    }

    // @Deprecated
    // private static void showError(Component parent, String msg, String title,
    // Exception ex) {
    // StringWriter sw = new StringWriter();
    // ex.printStackTrace(new PrintWriter(sw));
    // JOptionPane.showMessageDialog(parent,
    // msg + "\n" + sw.toString(),
    // title, JOptionPane.ERROR_MESSAGE);
    // }

    public static File getFileFromWorkspace(Frame windowDoingThis, String WorkspaceName, String ToCreate) {
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, ToCreate, true);
    }

    public static File getFileFromWorkspace(Frame windowDoingThis, String WorkspaceName, String ToCreate,
            Boolean strict) {
        try {
            var proposed = BaseDirectory + "/workspace/" + WorkspaceName + ToCreate;
            var proposedFile = new File(proposed);
            if (proposedFile.exists() || strict) {
                return proposedFile;
            } else
                return Files.createFile(proposedFile.toPath()).toFile();
        } catch (Exception e) {
            ErrorShower.showError(windowDoingThis, "IO Error", "Failed to get WP File", e);
            e.printStackTrace();
            return null;
        }

    }

    public static File getWorkspace(Frame windowDoingThis, String WorkspaceName) {
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, "/", true);
    }

    public static boolean createWorkspace(RLoadingScreen loading, // String workspaceName, String minimumVersion)
            WPFile wpf, ImageIcon imgIcon)
            throws DirectoryNotEmptyException, IOException {

        String[] wsFolders = {
                "/elements/",
                "/resources/"
        };

        File base = getBaseDirectory((Frame) loading.getParent());

        File wsFolder = new File(base.getAbsolutePath() + "/workspace/" + wpf.WorkspaceName + "/");

        if (wsFolder.exists()) { // throw if folder is already here
            var e = new IOException("Folder " + wsFolder.getAbsolutePath() + " already exists.");
            ErrorShower.showError((Frame) loading.getParent(),
                    "Failed to make folder; Folder" + wsFolder.getAbsolutePath() + " already exists.",
                    "Failure!", e);
            throw e;
        } else {
            File trying;
            try { // try making dirs

                trying = wsFolder;

                loading.changeText("Making directories...");

                Files.createDirectories(wsFolder.toPath());

                for (String string : wsFolders) {
                    trying = new File(wsFolder.getAbsolutePath() + "/" + string);
                    Files.createDirectories(trying.toPath());
                }

                loading.changeText("Creating workspace...");

                var srcWPF = new SourceWPFile(wpf);
                srcWPF.buildJSONFile((Frame) loading.getParent(), wpf.WorkspaceName);

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

            } catch (Exception e) { // handle exception (idk why i put there here, i think its cause this code used
                                    // to be longer?)
                ErrorShower.showError((Frame) loading.getParent(), "IO Error, with path " + wsFolder.getAbsolutePath(),
                        "IO Error", e);
                throw e;
            }
        }

    }

}
