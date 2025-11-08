package fn10.bedrockr.utils;

import java.awt.Component;
import java.awt.Frame;
import java.awt.List;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceResourceElement;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.SettingsFile;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RLoadingScreen;
import fn10.bedrockr.windows.RWorkspace;

/**
 * TODO: add javadoc to all of the functions
 */
public class RFileOperations {

    private static final String USER_DIR = System.getProperty("user.home");
    private static final String BASE_PATH = USER_DIR + File.separator + ".bedrockR" + File.separator;
    private static final File BaseDirectory = new File(BASE_PATH);
    @SuppressWarnings("unused")
    private static String COMMOJANG = null;
    static {
        var settings = SettingsFile.getSettings(null);
        COMMOJANG = settings.comMojangPath;
        // Launcher.LOG.info(COMMOJANG);
    }

    /**
     * taken from https://stackoverflow.com/a/31976060
     */
    private static final char[] ILLEGAL_CHARACTERS = {
            '<',
            '>',
            ':',
            '\"',
            '/',
            '\\',
            '|',
            '?',
            ';',
            '*',
            0, // NUL
            1, // other control characters
            2,
            3,
            4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
    };
    private static final Map<String, Class<? extends ElementSource>> ELEMENT_EXTENSION_CLASSES = new HashMap<>();
    static {
        ELEMENT_EXTENSION_CLASSES.put("itemref", SourceItemElement.class);
        ELEMENT_EXTENSION_CLASSES.put("blockref", SourceBlockElement.class);
        ELEMENT_EXTENSION_CLASSES.put("scriptref", SourceScriptElement.class);
        ELEMENT_EXTENSION_CLASSES.put("reciperef", SourceRecipeElement.class);
    }

    public static final String WPFFILENAME = "workspace.WPF";
    public static final String RESOURCE_FILE_NAME = "resources.json";

    /**
     * Gets the class of the ElementSource linked with the ElementFile, based on the
     * extension
     * 
     * @param doingThis     the closest (J)frame
     * @param fileExtension
     * @return
     */
    public static Class<? extends ElementSource> getElementSourceClassFromFileExtension(java.awt.Window doingThis,
            String fileExtension) {
        try {
            return ELEMENT_EXTENSION_CLASSES.get(fileExtension);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Invalid Element File", "Reloading Error", e);
            return null;
        }
    }

    /**
     * Gets the class of the ElementSource linked with the ElementFile, based on the
     * extension
     * 
     * @param doingThis     the closest (J)frame
     * @param fileExtension
     * @return
     */
    public static ElementSource getElementSourceFromFileExtension(java.awt.Window doingThis,
            String fileExtension) {
        try {
            return getElementSourceClassFromFileExtension(doingThis, fileExtension).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Invalid Element File", "Reloading Error", e);
            return null;
        }
    }

    public static String readResourceAsString(String resource) {
        try {
            return Files.readString(Path.of(RFileOperations.class.getResource(resource).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean validFolderName(String proposed) {

        if (proposed.length() >= 150)
            return false;

        for (int cha : proposed.chars().toArray()) {
            for (char c : ILLEGAL_CHARACTERS) {
                if (c == cha) {
                    Launcher.LOG.info("String: " + proposed + " had illegal folder char: " + cha);
                    return false;
                }
            }
        }
        Launcher.LOG.info("String: " + proposed + " is a legal filename.");

        return true;
    }

    public static String[] getWorkspaces(java.awt.Window doingThis) {
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

    public static SourceResourceElement getResources(Window doingThis, String workspaceName) {

        var file = getFileFromWorkspace(doingThis, workspaceName,
                File.separator + "resources" + File.separator + RESOURCE_FILE_NAME, true);
        if (file.exists())
            try {
                var source = new SourceResourceElement(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                return source;
            } catch (IOException e) {
                e.printStackTrace();
                ErrorShower.showError(doingThis, "Failed to get resource file.", e.getMessage(), e);
                return null;
            }
        else { // make a blank resource file
            var source = new SourceResourceElement("{}");
            source.buildJSONFile(doingThis, workspaceName);
            return source;
        }
    }

    public static void showMCSyncPopup(Window doingThis, SourceWorkspaceFile WPF) {
        ((WorkspaceFile) WPF.getSerilized()).MinecraftSync = true; // enable
        WPF.buildJSONFile(doingThis, // rebuild
                ((WorkspaceFile) WPF.getSerilized()).WorkspaceName);

        String[] platforms = { "Windows (pre-1.21.120)", "Windows" };
        var platformSelection = JOptionPane.showOptionDialog(
                doingThis,
                "To use MC Sync, bedrockR needs to be synced to Minecraft's files. Which platform are you on?",
                "Platform Selection",
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                platforms,
                "Windows");

        var settings = SettingsFile.getSettings(doingThis);
        switch (platformSelection) {
            case 0:

                settings.comMojangPath = System.getenv("LOCALAPPDATA")
                        + "\\Packages\\Microsoft.MinecraftUWP_8wekyb3d8bbwe\\LocalState\\games\\com.mojang";
                if (!new File(settings.comMojangPath).exists()) {
                    JOptionPane.showMessageDialog(doingThis,
                            "Couldn't find com.mojang (pre-1.21.120 / windows). Did you pick the right version? Do you have Minecraft Bedrock installed? \n Retry by selecting the option in the File menu.",
                            "oops", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                settings.buildFile(doingThis);

                JOptionPane.showMessageDialog(doingThis,
                        "Minecraft Sync is now enabled. You only need to reload your world to test now! (after you build of course!)",
                        "yippe", JOptionPane.INFORMATION_MESSAGE);
            case 1:

                settings.comMojangPath = System.getenv("APPDATA")
                        + "\\Minecraft Bedrock\\Users\\Shared\\games\\com.mojang";
                if (!new File(settings.comMojangPath).exists()) {
                    JOptionPane.showMessageDialog(doingThis,
                            "Couldn't find com.mojang (after 1.21.120 / windows). Did you pick the right version? Do you have Minecraft Bedrock installed? \\n"
                                    + //
                                    " Retry by selecting the option in the File menu.",
                            "oops", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                settings.buildFile(doingThis);

                JOptionPane.showMessageDialog(doingThis,
                        "Minecraft Sync is now enabled. You only need to reload your world to test now! (after you build of course!)",
                        "yippe", JOptionPane.INFORMATION_MESSAGE);

            default:
                break;
        }
    }

    public static void openWorkspace(Window doingThis, SourceWorkspaceFile WPF) {
        var workspaceView = new RWorkspace(WPF);
        // get items ready for use
        RItemSelector.downloadVanillaItems(((WorkspaceFile) WPF.getSerilized()));

        SwingUtilities.invokeLater(() -> {
            if (doingThis != null)
                doingThis.dispose();
            workspaceView.setVisible(true);
            if (!((WorkspaceFile) WPF.getSerilized()).MinecraftSync) { // ask to enable mc sync if not enabled
                var ask = JOptionPane.showConfirmDialog(doingThis,
                        "This project does not currently have Minecraft Sync enabled. Minecraft Sync automaticly copies your built project files into com.mojang, so you can build, and playtest without needing to restart the game. Enable MC Sync?",
                        "Enable MC Sync", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (ask == JOptionPane.YES_OPTION) {
                    showMCSyncPopup(doingThis, WPF);
                }
            } else if (!new File(SettingsFile.getSettings(doingThis).comMojangPath).exists()) {
                var ask = JOptionPane.showConfirmDialog(doingThis,
                        "com.mojang is gone now. Either you uninstalled minecraft, or this is a compatibility issue. Please report this on github.\n\n Do you want to re-enabled MC Sync?",
                        "Re-Enable MC Sync", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (ask == JOptionPane.YES_OPTION) {
                    showMCSyncPopup(doingThis, WPF);
                } else {
                    ((WorkspaceFile) WPF.getSerilized()).MinecraftSync = false;
                }
            }
        });
    }

    public static File getBaseDirectory(Window doingThis, String... Folders) {
        return getBaseDirectory(doingThis, false, Folders);
    }

    public static File getBaseDirectory(Window doingThis, Boolean strict, String... Folders) {
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

    public static File getBaseDirectory(Window doingThis) {
        try {
            // Launcher.LOG.info(BaseDirectory.toPath());
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

    public static File getFileFromWorkspace(Window windowDoingThis, String WorkspaceName, String ToCreate) {
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, ToCreate, true);
    }

    public static WorkspaceFile getWorkspaceFile(Component windowDoingThis, String WorkspaceName) {
        File file = getFileFromWorkspace(windowDoingThis, WorkspaceName, WPFFILENAME, true);
        try {
            return ((WorkspaceFile)new SourceWorkspaceFile(Files.readString(file.toPath())).getSerilized());
        } catch (IOException e) {
            return null;
        }
    }

    public static File getFileFromWorkspace(Component windowDoingThis, String WorkspaceName, String ToCreate,
            Boolean strict) {
        Launcher.LOG.warning("This file should start with the file seperator, or not at all! not '/'!");
        try {
            String proposed = BaseDirectory + File.separator + "workspace" + File.separator + WorkspaceName
                    + (ToCreate.startsWith(File.separator) ? "" : File.separator) + ToCreate;
            File proposedFile = new File(proposed);
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

    public static File getWorkspace(Component windowDoingThis, String WorkspaceName) {
        return getFileFromWorkspace(windowDoingThis, WorkspaceName, File.separator, true);
    }

    public static void mcSync(java.awt.Window doingThis) {
        SettingsFile settings = SettingsFile.getSettings(doingThis);
        try {
            String bpPath = getBaseDirectory(doingThis).getPath() + File.separator + "build" + File.separator + "BP"
                    + File.separator;
            String rpPath = getBaseDirectory(doingThis).getPath() + File.separator + "build" + File.separator + "RP"
                    + File.separator;
            if (!Path.of(settings.comMojangPath).toFile().exists()) {
                JOptionPane.showMessageDialog(doingThis, "The com.mojang folder doesnt exist. Cannot carry out MC Sync",
                        "com.mojang isnt at: " + settings.comMojangPath, JOptionPane.ERROR_MESSAGE);
                return;
            }
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
             * --------------------------------- CHECK BP
             * -----------------------------------
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
             * --------------------------------- CHECK RP
             * -----------------------------------
             */
            // check for unrececiniewicnew0inq390vj-[ ] (i cannot spell) RP
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

    public static SourceWorkspaceFile createWorkspace(RLoadingScreen loading, // String workspaceName, String
                                                                              // minimumVersion)
            WorkspaceFile wpf, File addonIcon)
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

                SourceWorkspaceFile srcWPF = new SourceWorkspaceFile(wpf);
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

    public static Path getFileFromElementFile(java.awt.Window doingThis, String workspace, ElementFile elementFile) {
        Path proposed = Path.of(RFileOperations
                .getFileFromWorkspace(doingThis, workspace,
                        File.separator + "elements" + File.separator)
                .getAbsolutePath(),
                elementFile.getElementName() + "."
                        + MapUtilities.getKeyFromValue(ELEMENT_EXTENSION_CLASSES, elementFile.getSourceClass()));
        Launcher.LOG.info("Found ElementFile on disk: " + proposed);
        return proposed;
    }

    public static ElementFile[] getElementsFromWorkspace(java.awt.Window doingThis, String workspace) {
        java.util.List<ElementFile> building = new ArrayList<>();
        for (File file : RFileOperations
                .getFileFromWorkspace(doingThis, workspace,
                        File.separator + "elements" + File.separator)
                .listFiles()) {
            try {
                ElementSource source = getElementSourceFromFileExtension(doingThis,
                        file.getName().substring(file.getName().lastIndexOf('.') + 1));

                building.add(source.getFromJSON(Files.readString(file.toPath())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return building.toArray(new ElementFile[0]);
    }

}
