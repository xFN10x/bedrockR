package fn10.bedrockr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.SourceFoodElement;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceResourceElement;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo;

public class RFileOperations {

    public static final String VERSION = "a2.0";
    private static final String USER_DIR = System.getProperty("user.home");
    private static String BASE_PATH = USER_DIR + File.separator + ".bedrockR" + File.separator;
    private static File BASE_DIRECTORY = new File(BASE_PATH);
    private static WorkspaceFile CURRENT_WORKSPACE = null;
    @SuppressWarnings("unused")
    private static String COMMOJANG = null;
    // make sure these are valid versions from here
    // https://github.com/PrismarineJS/minecraft-data/blob/master/data/dataPaths.json
    public final static String[] PICKABLE_VERSIONS = {
            "1.21.130",
            "1.21.124",
    };

    public static void init() {
        SettingsFile settings = SettingsFile.load();
        COMMOJANG = settings.comMojangPath;
        ReturnItemInfo.downloadVanillaItems();
        ReturnItemInfo.downloadVanillaBlocks();
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
    private static final Map<String, Class<? extends ElementSource<?>>> ELEMENT_EXTENSION_CLASSES = new HashMap<>();
    static {
        ELEMENT_EXTENSION_CLASSES.put("itemref", SourceItemElement.class);
        ELEMENT_EXTENSION_CLASSES.put("blockref", SourceBlockElement.class);
        //ELEMENT_EXTENSION_CLASSES.put("scriptref", SourceScriptElement.class);
        ELEMENT_EXTENSION_CLASSES.put("reciperef", SourceRecipeElement.class);
        ELEMENT_EXTENSION_CLASSES.put("foodref", SourceFoodElement.class);
        ELEMENT_EXTENSION_CLASSES.put("biomeref", SourceBiomeElement.class);
    }

    public static final String WPFFILENAME = "workspace.WPF";
    public static final String RESOURCE_FILE_NAME = "resources.json";

    public static void setBaseDir(File folder) {
        BASE_DIRECTORY = folder;
        BASE_PATH = folder.getAbsolutePath();
    }

    public static void setComMojangDir(File folder) {
        COMMOJANG = folder.getAbsolutePath();
    }

    /**
     * Gets the class of the ElementSource linked with the ElementFile, based on the
     * extension
     * 
     * @param fileExtension
     * @return
     */
    public static Class<? extends ElementSource<?>> getElementSourceClassFromFileExtension(
            String fileExtension) {
        if (ELEMENT_EXTENSION_CLASSES.containsKey(fileExtension)) {
            try {
                return ELEMENT_EXTENSION_CLASSES.get(fileExtension);
            } catch (Exception e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
        } else {
            throw new UnsupportedOperationException("This Element extension isnt avaliable in this version");
        }

    }

    /**
     * Gets the class of the ElementSource linked with the ElementFile, based on the
     * extension
     * 
     * @param fileExtension the extension of the file
     * @return the ElementSource accosseated with the extension.
     */
    public static ElementSource<?> getElementSourceFromFileExtension(
            String fileExtension) {
        try {
            return getElementSourceClassFromFileExtension(fileExtension).getConstructor().newInstance();
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    /**
     * Reads the resource at the path specified
     * 
     * @param resource - the path of the resource
     * @return the string of the file read
     */
    public static String readResourceAsString(String resource) {
        try {
            return new String(RFileOperations.class.getResourceAsStream(resource).readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return "";
        }
    }

    /**
     * Checks if a string can be used in a legal file name for windows, and linux.
     * 
     * @param proposed - The string to check
     * @return a bool declaring if the string can be used
     */
    public static boolean validFolderName(String proposed) {

        if (proposed.length() >= 150)
            return false;

        for (int cha : proposed.chars().toArray()) {
            for (char c : ILLEGAL_CHARACTERS) {
                if (c == cha) {
                    java.util.logging.Logger.getGlobal().info("String: " + proposed + " had illegal folder char: " + cha);
                    return false;
                }
            }
        }
        java.util.logging.Logger.getGlobal().info("String: " + proposed + " is a legal filename.");

        return true;
    }

    /**
     * Gets the workspaces that the user currently has.
     * 
     * @return an array of strings, being the names of the workspaces
     */
    public static String[] getWorkspaces() {
        var folder = new File(
                getBaseDirectory().getAbsolutePath() + File.separator + "workspace" + File.separator).toPath();
        if (!folder.toFile().exists()) {
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            if (!folder.toFile().exists()) {
                Files.createDirectories(folder);
            }
            List<String> list = new ArrayList<String>();
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    // java.util.logging.Logger.getGlobal().info(path.getFileName().toString());
                    list.add(path.getFileName().toString());
                }
            }
            return list.toArray(new String[0]);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets source resources that exist on disk from a workspace.
     * 
     * @param workspaceName - the workspace to get the resources from
     * @return a {@code SourceResourceElement}, containing the resources
     */
    public static SourceResourceElement getResources(String workspaceName) {

        var file = getFileFromWorkspace(workspaceName,
                File.separator + "resources" + File.separator + RESOURCE_FILE_NAME, true);
        if (file.exists())
            try {
                var source = new SourceResourceElement(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                return source;
            } catch (IOException e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
        else { // make a blank resource file
            var source = new SourceResourceElement("{}");
            source.saveJSONFile(workspaceName);
            return source;
        }
    }

    /**
     * Sets the current workspace.
     * @param WPF - the SourceWorkspaceFile of the workspace.
     */
    public static void setCurrentWorkspace(SourceWorkspaceFile WPF) {
        CURRENT_WORKSPACE = WPF.getSerilized();
    }

    /**
     * Gets the last opened workspace
     * 
     * @return the workspace file.
     */
    public static WorkspaceFile getCurrentWorkspace() {
        return CURRENT_WORKSPACE;
    }

    /**
     * Gets a directory from {@code .bedrockr}
     * 
     * @param Folders   - the path of folder to go to. e.g. Folders = "build, rp"
     * @return the file, being the directory that you specified,
     */
    public static File getBaseDirectory(String... Folders) {
        return getBaseDirectory(false, Folders);
    }

    /**
     * Gets a directory from {@code .bedrockr}
     * 
     * @param strict    - if strict, it doesn't make the directory you specify
     * @param Folders   - the path of folder to go to. e.g. Folders = "build, rp"
     * @return the file, being the directory that you specified,
     */
    public static File getBaseDirectory(Boolean strict, String... Folders) {
        File file = Path.of(BASE_PATH, Folders).toFile();
        try {
            if (!file.exists() && !strict) {
                return Files.createDirectories(file.toPath()).toFile();
            } else
                return file;
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
        return BASE_DIRECTORY;
    }

    /**
     * Gets the {@code .bedrockr} directory
     * 
     * @return the file, being the directory that you specified,
     */
    public static File getBaseDirectory() {
        try {
            // java.util.logging.Logger.getGlobal().info(BaseDirectory.toPath());
            if (!BASE_DIRECTORY.exists()) {
                return Files.createDirectories(BASE_DIRECTORY.toPath()).toFile();
            } else
                return BASE_DIRECTORY;
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
        return BASE_DIRECTORY;
    }

    /**
     * Get a file from a workspace
     * 
     * @param WorkspaceName   - the name of the target workspace
     * @param ToCreate        - the file to get, creating it if it doesnt exist.
     *                        e.g. {@code icon.jpg}
     * @return the file
     */
    public static File getFileFromWorkspace(String WorkspaceName, String ToCreate) {
        return getFileFromWorkspace(WorkspaceName, ToCreate, true);
    }

    public static WorkspaceFile getWorkspaceFile(String WorkspaceName) {
        File file = getFileFromWorkspace(WorkspaceName, WPFFILENAME, true);
        try {
            return ((WorkspaceFile) new SourceWorkspaceFile(new String(Files.readAllBytes(file.toPath()))).getSerilized());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get a file from a workspace
     * 
     * @param WorkspaceName   - the name of the target workspace
     * @param ToCreate        - the file to get, creating it if it doesnt exist.
     *                        e.g. {@code icon.jpg}
     * @param strict          - if true, it doesnt create the file, and returns null
     * @return the file
     */
    public static File getFileFromWorkspace(String WorkspaceName, String ToCreate,
            Boolean strict) {
        // java.util.logging.Logger.getGlobal().warning("This file should start with the file seperator, or not
        // at all! not '/'!");
        try {
            String proposed = BASE_DIRECTORY + File.separator + "workspace" + File.separator + WorkspaceName
                    + (ToCreate.startsWith(File.separator) ? "" : File.separator) + ToCreate;
            File proposedFile = new File(proposed);
            if (proposedFile.exists() || strict) {
                return proposedFile;
            } else
                return Files.createFile(proposedFile.toPath()).toFile();
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }

    }

    /**
     * Get a workspace's folder
     * 
     * @param WorkspaceName   - the target workspace
     * @return a File, being the directory of the workspace
     */
    public static File getWorkspace(String WorkspaceName) {
        return getFileFromWorkspace(WorkspaceName, File.separator, true);
    }

    /**
     * Syncs all built RP and BP to com.mojang
     * 
     */
    public static void mcSync() {
        SettingsFile settings = SettingsFile.load();
        try {
            String bpPath = getBaseDirectory().getPath() + File.separator + "build" + File.separator + "BP"
                    + File.separator;
            String rpPath = getBaseDirectory().getPath() + File.separator + "build" + File.separator + "RP"
                    + File.separator;
            if (!Path.of(settings.comMojangPath).toFile().exists()) {
                return;
            }
            File comBpPath = new File(settings.comMojangPath + File.separator + "development_behavior_packs");
            File comRpPath = new File(settings.comMojangPath + File.separator + "development_resource_packs");
            if (!comBpPath.exists()) {
                java.util.logging.Logger.getGlobal().info("Making dev BP folder...");
                Files.createDirectories(comBpPath.toPath());
            }
            if (!comRpPath.exists()) {
                java.util.logging.Logger.getGlobal().info("Making dev RP folder...");
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
                        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
                        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    }
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        } finally {
            settings.save(); // finally
        }
    }

    /**
     * Creates a workspace's folder to disk
     * 
     * @param wpf       - the workspace file to use
     * @param addonIcon - the icon to be written to disk
     * @return the new {@code SourceWorkspaceFile}
     * @throws IOException if the folder already exists
     */
    public static SourceWorkspaceFile createWorkspace( // String workspaceName, String
                                                                              // minimumVersion)
            WorkspaceFile wpf, Byte[] addonIcon) throws IOException {

        String[] wsFolders = {
                File.separator + "elements" + File.separator,
                File.separator + "resources" + File.separator
        };

        File base = getBaseDirectory();

        File wsFolder = new File(base.getAbsolutePath() + File.separator + "workspace" + File.separator
                + wpf.WorkspaceName + File.separator);

        if (wsFolder.exists()) { // throw if folder is already here
            IOException e = new IOException("Folder " + wsFolder.getAbsolutePath() + " already exists.");
            throw e;
        } else {
            File trying;
            try { // try making dirs

                trying = wsFolder;


                Files.createDirectories(wsFolder.toPath());

                for (String string : wsFolders) {
                    trying = new File(wsFolder.getAbsolutePath() + File.separator + string);
                    Files.createDirectories(trying.toPath());
                }


                SourceWorkspaceFile srcWPF = new SourceWorkspaceFile(wpf);
                srcWPF.saveJSONFile(wpf.WorkspaceName);

                File srcIcon = Path.of(wsFolder.getAbsolutePath(), "icon." + wpf.IconExtension).toFile();
                if (!srcIcon.exists())
                    if (!srcIcon.createNewFile())
                        throw new IOException("Failed to create source addon icon file");

                trying = srcIcon;

                Files.write(srcIcon.toPath(), ArrayUtils.toPrimitive(addonIcon), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                return srcWPF;

            } catch (Exception e) { // handle exception
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                throw e;
            }
        }

    }

    /**
     * Gets the ElementFile's equivilant file on disk.
     * 
     * @param workspace   - the workspace the file is in
     * @param elementFile - the ElementFile to search for on disk
     * @return
     */
    public static Path getFileFromElementFile(String workspace, ElementFile<?> elementFile) {
        Path proposed = Path.of(RFileOperations
                .getFileFromWorkspace(workspace,
                        File.separator + "elements" + File.separator)
                .getAbsolutePath(),
                elementFile.getElementName() + "."
                        + MapUtilities.getKeyFromValue(ELEMENT_EXTENSION_CLASSES, elementFile.getSourceClass()));
        Logger.getGlobal().info("Found ElementFile on disk: " + proposed);
        return proposed;
    }

    /**
     * Gets all the ElementFiles on disk
     * 
     * @param workspace - the workspace to get the elements from
     * @return an array of ElementFiles, populated by all the ones found on disk
     */
    public static ElementFile<?>[] getElementsFromWorkspace(String workspace) {
        List<ElementFile<?>> building = new ArrayList<>();
        for (File file : RFileOperations
                .getFileFromWorkspace(workspace,
                        File.separator + "elements" + File.separator)
                .listFiles()) {
            try {
                ElementSource<?> source = getElementSourceFromFileExtension(
                        file.getName().substring(file.getName().lastIndexOf('.') + 1));

                building.add(source.getFromJSON(new String(Files.readAllBytes(file.toPath()))));
            } catch (Exception e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        }
        return building.toArray(new ElementFile[0]);
    }

}
