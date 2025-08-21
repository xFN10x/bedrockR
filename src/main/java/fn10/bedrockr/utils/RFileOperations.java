package fn10.bedrockr.utils;

import java.awt.Frame;
import java.awt.Image;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Generated;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import com.google.errorprone.annotations.DoNotCall;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceResourceFile;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.elementFiles.SettingsFile;
import fn10.bedrockr.addons.source.elementFiles.WPFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.windows.RLoadingScreen;
import fn10.bedrockr.windows.RWorkspace;

public class RFileOperations {

    private static final String USER_DIR = System.getProperty("user.home");
    private static final String BASE_PATH = USER_DIR + File.separator + ".bedrockR" + File.separator;
    private static final File BaseDirectory = new File(BASE_PATH);
    private static String COMMOJANG = null;
    static {
        var settings = SettingsFile.getSettings(null);
        COMMOJANG = settings.comMojangPath;
        // System.out.println(COMMOJANG);
    }

    @Generated("jsonschema2pojo")
    public static class DateResponce {
        public class InnerRFileOperations {
            @SerializedName("sunrise")
            @Expose
            public String sunrise;
            @SerializedName("sunset")
            @Expose
            public String sunset;
            @SerializedName("solar_noon")
            @Expose
            public String solarNoon;
            @SerializedName("day_length")
            @Expose
            public Integer dayLength;
            @SerializedName("civil_twilight_begin")
            @Expose
            public String civilTwilightBegin;
            @SerializedName("civil_twilight_end")
            @Expose
            public String civilTwilightEnd;
            @SerializedName("nautical_twilight_begin")
            @Expose
            public String nauticalTwilightBegin;
            @SerializedName("nautical_twilight_end")
            @Expose
            public String nauticalTwilightEnd;
            @SerializedName("astronomical_twilight_begin")
            @Expose
            public String astronomicalTwilightBegin;
            @SerializedName("astronomical_twilight_end")
            @Expose
            public String astronomicalTwilightEnd;
        }

        @SerializedName("results")
        @Expose
        public InnerRFileOperations results;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("tzid")
        @Expose
        public String tzid;
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
        ELEMENT_EXTENSION_CLASSES.put("blockref", SourceBlockElement.class);
    }

    public static final String WPFFILENAME = "workspace.WPF";
    public static final String RESOURCE_FILE_NAME = "resources.json";

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

    public static SourceResourceFile getResources(Frame doingThis, String workspaceName) {

        var file = getFileFromWorkspace(doingThis, workspaceName,
                File.separator + "resources" + File.separator + RESOURCE_FILE_NAME, true);
        if (file.exists())
            try {
                var source = new SourceResourceFile(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                return source;
            } catch (IOException e) {
                e.printStackTrace();
                ErrorShower.showError(doingThis, "Failed to get resource file.", e.getMessage(), e);
                return null;
            }
        else { // make a blank resource file
            var source = new SourceResourceFile("{}");
            source.buildJSONFile(doingThis, workspaceName);
            return source;
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

    public static File getBaseDirectory(Frame doingThis, String... Folders) {
        return getBaseDirectory(doingThis, false, Folders);
    }

    public static File getBaseDirectory(Frame doingThis, Boolean strict, String... Folders) {
        File file = Path.of(BASE_PATH, Folders).toFile();
        try {
            if (!file.exists() && !strict) {
                return Files.createDirectories(file.toPath()).toFile();
            } else
                return file;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Failed to get base dir. (how the hell?)", "IO Error", e);
        }
        return BaseDirectory;
    }

    public static File getBaseDirectory(Frame doingThis) {
        try {
            // System.out.println(BaseDirectory.toPath());
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
        try {
            String bpPath = getBaseDirectory(doingThis).getPath() + File.separator + "build" + File.separator + "BP"
                    + File.separator;
            String rpPath = getBaseDirectory(doingThis).getPath() + File.separator + "build" + File.separator + "RP"
                    + File.separator;
            File comBpPath = new File(settings.comMojangPath + File.separator + "development_behavior_packs");
            File comRpPath = new File(settings.comMojangPath + File.separator + "development_resource_packs");
            if (!comBpPath.exists()) {
                Launcher.LOG.info("Making dev BP folder...");
                Files.createDirectories(comBpPath.toPath());
            }
            if (!comRpPath.exists()) {
                Launcher.LOG.info("Making dev RP folder...");
                Files.createDirectories(comRpPath.toPath());
            }
            File[] comBpFiles = comBpPath.listFiles();
            File[] comRpFiles = comRpPath.listFiles();
            /*
             * --------------------------------- SYNC BP -----------------------------------
             */
            // check for unrecinized BP
            if (comBpFiles != null)
                for (File f : comBpFiles) {
                    if (f.isDirectory()) {
                        if (!settings.currentBPSynced.contains(f.getName())
                                && !settings.ignored.contains(f.getName())) { // if
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
            /*
             * --------------------------------- SYNC RP -----------------------------------
             */
            // check for unrecinized RP
            if (comRpFiles != null)
                for (File f : comRpFiles) {
                    if (f.isDirectory()) {
                        if (!settings.currentRPSynced.contains(f.getName())
                                && !settings.ignored.contains(f.getName())) { // if
                                                                              // it
                                                                              // doesnt
                                                                              // recicnise
                                                                              // it

                            var choice = JOptionPane.showConfirmDialog(doingThis,
                                    "These is an unrecignised resource pack in here, \"" + f.getName()
                                            + "\"\nDo you want to remove it, (yes) or ignore it (no).",
                                    "Development RP warning",
                                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                            switch (choice) {
                                case JOptionPane.YES_OPTION:
                                    f.delete();
                                case JOptionPane.NO_OPTION:
                                    settings.ignored.add(f.getName());
                                default:
                                    break;
                            }
                        }
                    }
                }
            /*
             * --------------------------------- SYNC BP -----------------------------------
             */
            // clear currently synced
            settings.currentBPSynced.clear();
            for (File f : new File(bpPath).listFiles()) { //
                if (f.isDirectory() && Arrays.asList(f.list()).contains("manifest.json")) { // if its a dir, and it has
                                                                                            // manifest
                    File bpDestPath = new File(
                            settings.comMojangPath + File.separator + "development_behavior_packs" + File.separator
                                    + f.getName());

                    if (bpDestPath.exists())
                        FileUtils.deleteDirectory(bpDestPath);

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
            /*
             * --------------------------------- SYNC RP -----------------------------------
             */
            // clear currently synced
            settings.currentRPSynced.clear();
            for (File f : new File(rpPath).listFiles()) { //
                if (f.isDirectory() && Arrays.asList(f.list()).contains("manifest.json")) { // if its a dir, and it has
                                                                                            // manifest
                    File rpDestPath = new File(
                            settings.comMojangPath + File.separator + "development_resource_packs" + File.separator
                                    + f.getName());

                    if (rpDestPath.exists())
                        FileUtils.deleteDirectory(rpDestPath);

                    settings.currentRPSynced.add(f.getName()); // add to currently synced
                    try {
                        FileUtils.copyDirectory(f, rpDestPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        ErrorShower.showError(doingThis, "Failed to copy addon to com.mojang.", rpDestPath.getPath(),
                                e);
                    }
                } else {
                    var choice = JOptionPane
                            .showConfirmDialog(
                                    doingThis,
                                    "There is a file/folder in the build folder that is not an resource pack, "
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

    @Deprecated
    @DoNotCall("This doesnt work")
    public static SourceWPFile createWorkspace(RLoadingScreen loading, WPFile wpf, Image addonIcon) throws Exception {
        BufferedImage bufferedImage = new BufferedImage(addonIcon.getHeight(null), addonIcon.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(addonIcon, 0, 0, null);
        File file = new File(File.createTempFile("bed", "rockr").getAbsolutePath());
        ImageIO.write(bufferedImage, BASE_PATH, file);
        file.delete();
        bufferedImage.getGraphics().dispose();
        return createWorkspace(loading, wpf, file);
    }

    public static SourceWPFile createWorkspace(RLoadingScreen loading, // String workspaceName, String minimumVersion)
            WPFile wpf, File addonIcon)
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

                SourceWPFile srcWPF = new SourceWPFile(wpf);
                srcWPF.buildJSONFile((Frame) loading.getParent(), wpf.WorkspaceName);

                File srcIcon = new File(wsFolder.getAbsolutePath() + File.separator + "icon." + wpf.IconExtension);
                trying = srcIcon;

                FileUtils.copyFile(addonIcon, srcIcon);

                return srcWPF;

            } catch (Exception e) { // handle exception
                e.printStackTrace();
                ErrorShower.showError((Frame) loading.getParent(),
                        "Exepection, with path " + wsFolder.getAbsolutePath(),
                        "IO Error", e);
                throw e;
            } finally {
                loading.dispose();
            }
        }

    }

}
