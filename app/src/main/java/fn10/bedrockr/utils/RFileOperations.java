package fn10.bedrockr.utils;

import java.awt.Frame;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.SettingsFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.windows.RWorkspace;
import fn10.bedrockr.windows.base.RLoadingScreen;

public class RFileOperations {

    private static final String USER_DIR = System.getProperty("user.home");
    private static final String BASE_PATH = USER_DIR + File.separator + ".bedrockR" + File.separator;
    private static final File BaseDirectory = new File(BASE_PATH);
    private static String COMMOJANG = null;
    static {
        var settings = SettingsFile.getSettings(null);
        COMMOJANG = settings.comMojangPath;
        System.out.println(COMMOJANG);
    }
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
    private static final Map<String, Class<? extends ElementSource>> ELEMENT_EXTENSION_CLASSES = new HashMap<>();
    static {
        ELEMENT_EXTENSION_CLASSES.put("itemref", SourceItemElement.class);
    }

    public static final String WPFFILENAME = "workspace.WPF";

    public static Class<? extends ElementSource> getElementClassFromFileExtension(Frame doingThis,
            String fileExtension) {
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
        var folder = new File(
                getBaseDirectory(doingThis).getAbsolutePath() + File.separator + "workspace" + File.separator).toPath();
        if (!folder.toFile().exists()) {
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            if (!folder.toFile().exists()) {
                Files.createDirectories(folder);
            }
            var list = new List();
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    // Launcher.LOG.info(path.getFileName().toString());
                    list.add(path.getFileName().toString());
                }
            }
            return list.getItems();
        } catch (IOException e) {
            ErrorShower.showError(doingThis, "IO Error", BASE_PATH, e);
            return null;
        }
    }

    public static void openWorkspace(Frame doingThis, SourceWPFile WPF) {
        var workspaceView = new RWorkspace(WPF);
        SwingUtilities.invokeLater(() -> {
            doingThis.dispose();
            workspaceView.setVisible(true);
            if (!((WPFile) WPF.getSerilized()).MinecraftSync) { // ask to enable mc sync if not enabled
                var ask = JOptionPane.showConfirmDialog(doingThis,
                        "This project does not currently have Minecraft Sync enabled. Minecraft Sync automaticly copies your built project files into com.mojang, so you can build, and playtest without needing to restart the game. Enable MC Sync?",
                        "Enable MC Sync", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (ask == JOptionPane.YES_OPTION) {
                    ((WPFile) WPF.getSerilized()).MinecraftSync = true; // enable
                    WPF.buildJSONFile(doingThis, // rebuild
                            ((WPFile) WPF.getSerilized()).WorkspaceName);

                    String[] platforms = { "Windows" };
                    var platformSelection = JOptionPane.showOptionDialog(
                            doingThis,
                            "To use MC Sync, bedrockR needs to be synced to Minecraft's files. Which platform are you on?",
                            "Platform Selection",
                            0,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            platforms,
                            null);

                    switch (platformSelection) {
                        case 0:

                            var settings = SettingsFile.getSettings(doingThis);

                            settings.comMojangPath = System.getenv("LOCALAPPDATA")
                                    + "\\Packages\\Microsoft.MinecraftUWP_8wekyb3d8bbwe\\LocalState\\games\\com.mojang";

                            settings.buildFile(doingThis);

                            JOptionPane.showMessageDialog(doingThis,
                                    "Minecraft Sync is now enabled. You only need to reload your world to test now!",
                                    "yippe", JOptionPane.INFORMATION_MESSAGE);

                        default:
                            break;
                    }
                }
            }
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
            System.out.println(BaseDirectory.toPath());
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

    public static File getFileFromWorkspace(Frame windowDoingThis, String WorkspaceName, String ToCreate) {
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, ToCreate, true);
    }

    public static File getFileFromWorkspace(Frame windowDoingThis, String WorkspaceName, String ToCreate,
            Boolean strict) {
        try {
            var proposed = BaseDirectory + File.separator + "workspace" + File.separator + WorkspaceName + ToCreate;
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
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, File.separator, true);
    }

    public static void mcSync(Frame doingThis) {
        SettingsFile settings = SettingsFile.getSettings(doingThis);
        String bpPath = getBaseDirectory(doingThis).getPath() + File.separator + "build" + File.separator + "BP"
                + File.separator;
        File comBpPath = new File(settings.comMojangPath + File.separator + "development_behavior_packs");

        try {
            // check to see if there are other things there
            for (File f : comBpPath.listFiles()) {
                if (f.isDirectory()) {
                    if (!settings.currentBPSynced.contains(f.getName()) && !settings.ignored.contains(f.getName())) { // if
                                                                                                                      // it
                                                                                                                      // doesnt
                                                                                                                      // recicnise
                                                                                                                      // it

                        var choice = JOptionPane.showConfirmDialog(doingThis,
                                "These is an unrecignised addon in here, \"" + f.getName()
                                        + "\"\nDo you want to remove it, (yes) or ignore it (no).",
                                "Development BP warning",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        switch (choice) {
                            case JOptionPane.YES_OPTION:
                                f.delete();
                            case JOptionPane.NO_OPTION:
                                settings.ignored.add(f.getName());
                            case JOptionPane.CANCEL_OPTION:
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            // clear currently synced
            settings.currentBPSynced.clear();
            for (File f : new File(bpPath).listFiles()) {
                if (f.isDirectory() && Arrays.asList(f.list()).contains("manifest.json")) { // if its a dir, and it has
                                                                                            // manifest
                    File bpDestPath = new File(
                            settings.comMojangPath + File.separator + "development_behavior_packs" + File.separator
                                    + f.getName());

                    if (bpDestPath.exists())
                        FileUtils.deleteDirectory(comBpPath);

                    settings.currentBPSynced.add(f.getName()); // add to currently synced
                    try {
                        FileUtils.copyDirectory(f, bpDestPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorShower.showError(doingThis, "Failed to copy addon to com.mojang.", bpDestPath.getPath(),
                                e);
                    }
                } else {
                    var choice = JOptionPane
                            .showConfirmDialog(
                                    doingThis, "There is a file/folder in the build folder that is not an addon, "
                                            + f.getName() + ". Remove this?",
                                    "Invaild Folder/File", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Failed to execute Minecraft Sync", e.getMessage(), e);
        } finally {
            settings.buildFile(doingThis); // finally
        }
    }

    public static SourceWPFile createWorkspace(RLoadingScreen loading, // String workspaceName, String minimumVersion)
            WPFile wpf, ImageIcon imgIcon)
            throws Exception {

        String[] wsFolders = {
                File.separator + "elements" + File.separator,
                File.separator + "resources" + File.separator
        };

        File base = getBaseDirectory((Frame) loading.getParent());

        File wsFolder = new File(base.getAbsolutePath() + File.separator + "workspace" + File.separator
                + wpf.WorkspaceName + File.separator);

        if (wsFolder.exists()) { // throw if folder is already here
            var e = new IOException("Folder " + wsFolder.getAbsolutePath() + " already exists.");
            ErrorShower.showError((Frame) loading.getParent(),
                    "Failed to make folder; Folder" + wsFolder.getAbsolutePath() + " already exists.",
                    "Failure!", e);
            throw e;
        } else {
            File trying;
            try { // try making dirs

                SwingUtilities.invokeLater(() -> {
                    loading.setVisible(true);
                });
                trying = wsFolder;

                loading.changeText("Making directories...");

                Files.createDirectories(wsFolder.toPath());

                for (String string : wsFolders) {
                    trying = new File(wsFolder.getAbsolutePath() + File.separator + string);
                    Files.createDirectories(trying.toPath());
                }

                loading.changeText("Creating workspace...");

                var srcWPF = new SourceWPFile(wpf);
                srcWPF.buildJSONFile((Frame) loading.getParent(), wpf.WorkspaceName);

                var srcIcon = new File(wsFolder.getAbsolutePath() + File.separator + "icon." + wpf.IconExtension);
                trying = srcIcon;

                // make buffered image
                var image = imgIcon.getImage();
                BufferedImage BI = new BufferedImage(image.getWidth(null),
                        image.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                BI.getGraphics().drawImage(image, 0, 0, null);

                ImageIO.write(BI, wpf.IconExtension, srcIcon);

                return srcWPF;

            } catch (Exception e) { // handle exception
                ErrorShower.showError((Frame) loading.getParent(), "IO Error, with path " + wsFolder.getAbsolutePath(),
                        "IO Error", e);
                throw e;
            } finally {
                loading.dispose();
            }
        }

    }

}
