package fn10.bedrockr.addons.source.elementFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest.Dependence;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest.Header;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest.Module;
import fn10.bedrockr.addons.addon.jsonClasses.SharedJSONClasses.VersionVector;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.RFileOperations;

/**********************
 * The WPFile, is an ElementFile that handles workspace varibles, and building
 * manifests, and other base stuff for both packs.
 */
public class WorkspaceFile implements ElementFile<SourceWorkspaceFile> {

    public int Format;
    public String WorkspaceName;
    public String MinimumEngineVersion;
    public String BPVersion = "1.0.0";
    public String RPVersion = "1.0.0";
    public String Description;
    public String IconExtension;
    public String Prefix;
    public boolean MinecraftSync;
    public String uuid1;
    public String uuid2;
    public String uuid3;
    public String uuid4;

    public Map<UUID, String> Scripts = new HashMap<UUID, String>();

    public WorkspaceFile(String WPName, String MEV, String DES, String IE, String PREFIX) {
        this.Format = 1;

        this.WorkspaceName = WPName;
        this.MinimumEngineVersion = MEV;
        this.Description = DES;
        this.IconExtension = IE;
        this.Prefix = PREFIX;

        this.uuid1 = UUID.randomUUID().toString();
        this.uuid2 = UUID.randomUUID().toString();
        this.uuid3 = UUID.randomUUID().toString();
        this.uuid4 = UUID.randomUUID().toString();
    }

    @Override
    public Class<SourceWorkspaceFile> getSourceClass() {
        return SourceWorkspaceFile.class;
    }

    @Override
    public String getElementName() {
        return WorkspaceName;
    }

    @Override
    public void setDraft(Boolean draft) {
        return;
    }

    @Override
    public Boolean getDraft() {
        return false;
    }

    /**
     * This should be called everytime you rebuild your addon. Since some stuff
     * doesnt reset automaticly.
     */
    public void reset(String rootPath) {
        if (Scripts == null) {
            Scripts = new HashMap<UUID, String>();
        } else {
            for (Entry<UUID,String> set : Scripts.entrySet()) {
                try {
                    Launcher.LOG.info("Deleting script that already exists: " + set.getValue());
                    Files.deleteIfExists(Path.of(rootPath, set.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Scripts.clear();
    }

    /**
     * Adds a javascript script to the addon
     * 
     * @param rootPath - the root of the building BP
     * 
     * @param name     - the name of the script. {@code name = "script.js"} would be
     *                 located in {@code scripts/script.js}, and
     *                 {@code name = "folder/script.js"} would be located in
     *                 {@code scripts/folder/script.js}
     * @return the path the file can be written to.
     */
    public Path addScript(String rootPath, String name) {
        if (Scripts == null) {
            Scripts = new HashMap<UUID, String>();
        }
        if (Scripts.containsValue(name)) // return if its already here
            return Path.of(rootPath, "scripts", name);

        Scripts.put(UUID.randomUUID(), "scripts/" + name);
        return Path.of(rootPath, "scripts", name);
    }

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {

        // build BP manifest
        // --------------------------------------------------------------------
        Manifest manifest = new Manifest();
        manifest.formatVersion = 2;
        // header
        Header header = new Manifest.Header();
        header.name = WorkspaceName;
        header.description = Description;
        header.uuid = this.uuid1;
        header.min_engine_version = VersionVector.fromString(MinimumEngineVersion);
        header.version = VersionVector.fromString(BPVersion);
        // add header
        manifest.header = header;
        // modules
        List<Module> mods = new ArrayList<>();
        Module dataModule = new Manifest.Module();
        dataModule.type = "data";
        dataModule.uuid = this.uuid2;
        dataModule.version = VersionVector.fromString(BPVersion);
        mods.add(dataModule);
        if (Scripts != null) {
            if (!Scripts.isEmpty()) {
                for (Entry<UUID, String> entry : Scripts.entrySet()) {
                    Module mod = new Module();
                    mod.uuid = entry.getKey().toString();
                    mod.version = new Vector<Integer>(List.of(new Integer[] { 1, 0, 0 }));
                    mod.type = "script";
                    mod.language = "javascript";
                    mod.entry = entry.getValue();
                    mods.add(mod);
                }
            }
        }
        // dependencies
        List<Dependence> depndences = new ArrayList<>();

        if (Scripts != null)
            if (!Scripts.isEmpty()) {
                Dependence mcserver = new Manifest.Dependence();
                mcserver.module_name = "@minecraft/server";
                mcserver.version = "2.1.0";
                depndences.add(mcserver);
            }
        // add to manifest
        manifest.modules = mods.toArray(new Module[0]);
        manifest.dependencies = depndences.toArray(new Dependence[0]);

        // build manifest
        // var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(manifest);

        var path = new File(rootPath + File.separator + "manifest.json").toPath();
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // add image
        var img = Files.readAllBytes(
                RFileOperations
                        .getFileFromWorkspace(null, this.WorkspaceName, File.separator + "icon." + this.IconExtension,
                                true)
                        .toPath());
        var imgpath = new File(rootPath + File.separator + "pack_icon.png").toPath();
        Files.write(imgpath, img, StandardOpenOption.CREATE);

        // build RP manifest
        // ---------------------------------------------------------------
        var RPmanifest = new Manifest();
        RPmanifest.formatVersion = 2;
        // header
        var RPheader = new Manifest.Header();
        RPheader.name = WorkspaceName;
        RPheader.description = Description;
        RPheader.uuid = this.uuid3;
        RPheader.min_engine_version = VersionVector.fromString(MinimumEngineVersion);
        RPheader.version = VersionVector.fromString(RPVersion);
        // add header
        RPmanifest.header = RPheader;
        // modules
        var RPmodule = new Manifest.Module();
        RPmodule.type = "resources";
        RPmodule.uuid = this.uuid4;
        RPmodule.version = VersionVector.fromString(RPVersion);
        // add modules
        var RPmodules = new ArrayList<Manifest.Module>();
        RPmodules.add(RPmodule);
        // add to manifest
        RPmanifest.modules = RPmodules.toArray(new Module[0]);

        // build manifest
        var RPjson = gson.toJson(RPmanifest);

        var RPpath = new File(rootResPackPath + File.separator + "manifest.json").toPath();
        FileUtils.createParentDirectories(RPpath.toFile());
        Files.write(RPpath, RPjson.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // add image
        var img2 = Files.readAllBytes(
                RFileOperations
                        .getFileFromWorkspace(null, this.WorkspaceName, File.separator + "icon." + this.IconExtension,
                                true)
                        .toPath());
        var imgpath2 = new File(rootResPackPath + File.separator + "pack_icon.png").toPath();
        Files.write(imgpath2, img2, StandardOpenOption.CREATE);
    }
}
