package fn10.bedrockr.utils;

import com.formdev.flatlaf.util.SystemFileChooser;
import com.formdev.flatlaf.util.SystemInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.element.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.element.elementSources.*;
import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.element.supporting.item.ReturnItemInfo;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.typeAdapters.ImageIconSerializer;
import fn10.bedrockr.utils.typeAdapters.PathSerializer;
import fn10.bedrockr.utils.typeAdapters.StrictMapSerializer;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;

public class RFileOperations {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
            .registerTypeAdapter(new TypeToken<HashMap<String, Object>>() {
                    }.getClass(),
                    new StrictMapSerializer())
            .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
            .registerTypeAdapter(ImageIcon.class, new ImageIconSerializer())
            .create();
    public static final String SEM_VERSION = "0.9.0";
    public static final int NUM_VERSION = 10;
    public static final String VERSION = "a3.0";
    private static final String USER_DIR = System.getProperty("user.home");
    private static String BASE_PATH = USER_DIR + File.separator + ".bedrockR" + File.separator;
    private static File BASE_DIRECTORY = new File(BASE_PATH);
    private static WorkspaceFile CURRENT_WORKSPACE = null;
    public static final Map<String, Path> MC_SYNC_OPTIONS = Map.of(
            "Windows", Path.of(Objects.requireNonNullElse(System.getenv("APPDATA"), "null"), "Minecraft Bedrock", "Users", "Shared", "games", "com.mojang"),
            "Linux/ChromeOS (MC Bedrock Launcher)", Path.of(System.getProperty("user.home"), ".local", "share", "mcpelauncher", "games", "com.mojang")
    );
    @SuppressWarnings("unused")
    private static Path COMMOJANG = null;
    // make sure these are valid versions from here
    // https://github.com/PrismarineJS/minecraft-data/blob/master/data/dataPaths.json
    // element 0 must be latest
    public final static String[] PICKABLE_VERSIONS = {
            "1.26.30",
            "1.26.20",
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
    public static final Map<String, Class<? extends ElementSource<?>>> ELEMENT_EXTENSION_CLASSES = new HashMap<>();

    static {
        ELEMENT_EXTENSION_CLASSES.put("itemref", SourceItemElement.class);
        ELEMENT_EXTENSION_CLASSES.put("blockref", SourceBlockElement.class);
        // ELEMENT_EXTENSION_CLASSES.put("scriptref", SourceScriptElement.class);
        ELEMENT_EXTENSION_CLASSES.put("reciperef", SourceRecipeElement.class);
        ELEMENT_EXTENSION_CLASSES.put("foodref", SourceFoodElement.class);
        ELEMENT_EXTENSION_CLASSES.put("biomeref", SourceBiomeElement.class);
    }

    public static final List<Class<? extends ElementSource<?>>> ELEMENTS = List.of(SourceItemElement.class,
            SourceBlockElement.class,
            SourceScriptElement.class,
            SourceRecipeElement.class,
            SourceFoodElement.class,
            SourceBiomeElement.class);

    public static final String WPFFILENAME = "workspace.WPF";
    public static final String RESOURCE_FILE_NAME = "resources.json";

    public static void setBaseDir(File folder) {
        BASE_DIRECTORY = folder;
        BASE_PATH = folder.getAbsolutePath();
    }

    public static void setComMojangDir(File folder) {
        COMMOJANG = folder.toPath();
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
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
        } else {
            fn10.bedrockr.Launcher.LOG.warning("Element: " + fileExtension + " not supported in: " + RFileOperations.VERSION);
            return null;
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
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }
    
    public static byte[] readAllOfResource(String path) {
        try (InputStream stream = RFileOperations.class.getResourceAsStream(path)) {
            if (stream == null)
                return new byte[0];
            return stream.readAllBytes();
        } catch (IOException e ) {
            return new byte[0];
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
                    fn10.bedrockr.Launcher.LOG
                            .info("String: " + proposed + " had illegal folder char: " + cha);
                    return false;
                }
            }
        }
        fn10.bedrockr.Launcher.LOG.info("String: " + proposed + " is a legal filename.");

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
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            if (!folder.toFile().exists()) {
                Files.createDirectories(folder);
            }
            List<String> list = new ArrayList<String>();
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    // fn10.bedrockr.Launcher.LOG.info(path.getFileName().toString());
                    list.add(path.getFileName().toString());
                }
            }
            return list.toArray(new String[0]);
        } catch (IOException e) {
            return null;
        }
    }

    
    /**
     * Adds an object to a list if it isn't inside of it.
     *
     * @param <T>   The list type
     * @param list  The list the element is being added too
     * @param toAdd The object being added to the list
     * @return that object, or null if it fails
     */
    public static <T> T addIfAbsent(List<T> list, T toAdd) {
        if (list == null)
            return null;
        if (!list.contains(toAdd))
            list.add(toAdd);
        return toAdd;
    }

    /**
     * Sets the current workspace.
     *
     * <p>
     * Set the current workspace when opening it for editing. You should
     * only be able to edit one workspace at a time. When opening a workspace, make
     * sure to check the {@code WorkspaceFile.Format}, and
     * {@code WorkspaceFile.bedrockRVersion} to make sure its safe to load.
     *
     * @param WPF - the SourceWorkspaceFile of the workspace.
     */
    public static boolean loadWorkspace(SourceWorkspaceFile WPF) throws WorkspaceResources.WorkspaceUnsupportedException {
        WorkspaceFile serilized = WPF.getSerialized();
        if (serilized.Format > 2) {
            throw new WorkspaceResources.WorkspaceUnsupportedException(2, serilized.Format);
        }

        if (serilized.Format < 2) {
            Launcher.LOG.info("Can't open workspace, format is older.");
            return false;
        }
        // update version things
        serilized.LatestBedrockRVersion = SEM_VERSION;
        if (serilized.ModifiedWithBedrockRVersions == null) {
            serilized.ModifiedWithBedrockRVersions = new ArrayList<>();
        }
        addIfAbsent(serilized.ModifiedWithBedrockRVersions, VERSION);

        try {
            new SourceWorkspaceFile(serilized).saveJSONFile(WPF.workspaceName());
        } catch (IOException e) {
            Launcher.LOG.log(Level.SEVERE, "Failed to save resources", e);
        }

        try {
            WorkspaceResources.load(WPF.workspaceName());
        } catch (WorkspaceResources.WorkspaceUnsupportedException | IOException e) {
            Launcher.LOG.log(Level.SEVERE, "Can't open workspace, resources failed to load.", e);
            return false;
        }

        CURRENT_WORKSPACE = serilized;
        return true;
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
     * @param Folders - the path of folder to go to. e.g. Folders = "build, rp"
     * @return the file, being the directory that you specified,
     */
    public static File getBaseDirectory(String... Folders) {
        return getBaseDirectory(false, Folders);
    }

    /**
     * Gets a directory from {@code .bedrockr}
     *
     * @param strict  - if strict, it doesn't make the directory you specify
     * @param Folders - the path of folder to go to. e.g. Folders = "build, rp"
     * @return the file, being the directory that you specified,
     */
    public static File getBaseDirectory(Boolean strict, String... Folders) {
        File file = java.nio.file.Paths.get(BASE_PATH, Folders).toFile();
        try {
            if (!file.exists() && !strict) {
                return Files.createDirectories(file.toPath()).toFile();
            } else
                return file;
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
            // fn10.bedrockr.Launcher.LOG.info(BaseDirectory.toPath());
            if (!BASE_DIRECTORY.exists()) {
                return Files.createDirectories(BASE_DIRECTORY.toPath()).toFile();
            } else
                return BASE_DIRECTORY;
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
        return BASE_DIRECTORY;
    }

    /**
     * Get a file from a workspace
     *
     * @param WorkspaceName - the name of the target workspace
     * @param ToCreate      - the file to get, creating it if it doesnt exist.
     *                      e.g. {@code icon.jpg}
     * @return the file
     */
    public static File getFileFromWorkspace(String WorkspaceName, String... ToCreate) throws IOException {
        return getFileFromWorkspace(WorkspaceName, true, ToCreate);
    }

    public static String getWorkspacePrefix(String wpName) {
        try {
            return getWorkspaceFile(wpName).Prefix;
        } catch (IOException e) {
            return "error";
        }
    }
    
    public static WorkspaceFile getWorkspaceFile(String WorkspaceName) throws IOException {
        File file = getFileFromWorkspace(WorkspaceName,true, WPFFILENAME);
        return ElementSource.getFromJSON(new String(Files.readAllBytes(file.toPath())), WorkspaceFile.class);
    }

    public static String getFileChooserDefaultPath() {
        return SystemInfo.isWindows ? SystemFileChooser.WINDOWS_DEFAULT_FOLDER : System.getProperty("user.home");
    }

    /**
     * Get a file from a workspace
     *
     * @param WorkspaceName - the name of the target workspace
     * @param ToCreate      - the file to get, creating it if it doesnt exist.
     *                      e.g. {@code icon.jpg}
     * @param strict        - if true, it doesnt create the file, and returns null
     * @return the file
     */
    public static File getFileFromWorkspace(String WorkspaceName, Boolean strict, String... ToCreate) throws IOException {
        // fn10.bedrockr.Launcher.LOG.warning("This file should start with the
        // file seperator, or not
        // at all! not '/'!");
        File proposedFile = BASE_DIRECTORY.toPath().resolve("workspace").resolve(WorkspaceName,ToCreate).toFile();
        if (proposedFile.exists() || strict) {
            return proposedFile;
        } else
            return Files.createFile(proposedFile.toPath()).toFile();
    }

    /**
     * Get a workspace's folder
     *
     * @param WorkspaceName - the target workspace
     * @return a File, being the directory of the workspace
     */
    public static File getWorkspace(String WorkspaceName) throws IOException {
        return getFileFromWorkspace(WorkspaceName, true);
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
            if (!settings.comMojangPath.toFile().exists()) {
                return;
            }
            File comBpPath = new File(settings.comMojangPath + File.separator + "development_behavior_packs");
            File comRpPath = new File(settings.comMojangPath + File.separator + "development_resource_packs");
            if (!comBpPath.exists()) {
                fn10.bedrockr.Launcher.LOG.info("Making dev BP folder...");
                Files.createDirectories(comBpPath.toPath());
            }
            if (!comRpPath.exists()) {
                fn10.bedrockr.Launcher.LOG.info("Making dev RP folder...");
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
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    }
                }
            }
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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

                File srcIcon = java.nio.file.Paths.get(wsFolder.getAbsolutePath(), "icon." + wpf.IconExtension)
                        .toFile();
                if (!srcIcon.exists())
                    if (!srcIcon.createNewFile())
                        throw new IOException("Failed to create source addon icon file");

                trying = srcIcon;

                Files.write(srcIcon.toPath(), ArrayUtils.toPrimitive(addonIcon), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                return srcWPF;

            } catch (Exception e) { // handle exception
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                throw e;
            }
        }

    }

    /**
     * Gets the ElementFile's equivalent file on disk.
     *
     * @param workspace   - the workspace the file is in
     * @param elementFile - the ElementFile to search for on disk
     * @return The path of this element file on disk.
     */
    public static Path getFileFromElementFile(String workspace, ElementFile<?> elementFile) throws IOException {
        Path proposed = java.nio.file.Paths.get(RFileOperations
                        .getFileFromWorkspace(workspace,
                                File.separator + "elements" + File.separator)
                        .getAbsolutePath(),
                elementFile.getElementName() + "."
                        + MapUtilities.getKeyFromValue(ELEMENT_EXTENSION_CLASSES, elementFile.getSourceClass()));
        Launcher.LOG.info("Found ElementFile on disk: " + proposed);
        return proposed;
    }

    public static String[] getElementNamesFromWorkspace(String workspace) {
        ElementFile<?>[] elements = getElementsFromWorkspace(workspace);
        ArrayList<String> names = new ArrayList<>();
        for (ElementFile<?> element : elements) {
            names.add(element.getElementName());
        }
        return names.toArray(new String[0]);
    }

    /**
     * Gets all the ElementFiles on disk
     *
     * @param workspace - the workspace to get the elements from
     * @return an array of ElementFiles, populated by all the ones found on disk
     */
    public static ElementFile<?>[] getElementsFromWorkspace(String workspace) {
        List<ElementFile<?>> building = new ArrayList<>();
        try {
            for (File file : Objects.requireNonNull(RFileOperations
                    .getFileFromWorkspace(workspace, "elements")
                    .listFiles())) {
                ElementSource<?> source = getElementSourceFromFileExtension(
                        file.getName().substring(file.getName().lastIndexOf('.') + 1));
                String jsonString = new String(Files.readAllBytes(file.toPath()));
                JsonObject element = JsonParser.parseString(jsonString).getAsJsonObject();
                SourcelessElementFile sef = SourcelessElementFile.upToDate(workspace, element, source.getSerilizedClass());
                if (sef instanceof ElementFile<?> ef) {
                    building.add(ef);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return building.toArray(new ElementFile[0]);
    }

    /**
     * Wrapper around {@link Files#write(Path, byte[], OpenOption...)}
     * <p>
     *     This automatically overrides a file if it already exists there, it also makes all the dirs
     * @param to The path to write to.
     * @param data The data to write.
     */
    public static void write(Path to, byte[] data) throws IOException {
        FileUtils.createParentDirectories(to.toFile());
        Files.write(to, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * Wrapper around {@link Files#write(Path, byte[], OpenOption...)}
     * @param to The path to write to.
     * @param data The data to write.
     */
    public static void write(Path to, String data) throws IOException {
        write(to, data.getBytes());
    }

    public record ElementMade<T extends ElementFile<?>>(Date timeMade, @Nullable T elementData, int bedrockRVersion,
                                                        @Nullable String workspaceName) implements Comparable<ElementMade<T>> {

        @Override
        public int compareTo(ElementMade o) {
            return timeMade.compareTo(o.timeMade);
        }
    }

    public record WorkspaceMade(Date timeMade, @Nullable WorkspaceFile workspaceData, int bedrockRVersion,
                                @Nullable Object[] elementDatas) implements Comparable<WorkspaceMade> {

        @Override
        public int compareTo(WorkspaceMade o) {
            return timeMade.compareTo(o.timeMade);
        }
    }
}
