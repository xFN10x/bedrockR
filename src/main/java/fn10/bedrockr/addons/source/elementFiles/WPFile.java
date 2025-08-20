package fn10.bedrockr.addons.source.elementFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest.Module;
import fn10.bedrockr.addons.addon.jsonClasses.SharedJSONClasses.VersionVector;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

/**********************
 * The WPFile, is an ElementFile that handles workspace varibles, and building
 * manifests, and other base stuff for both packs.
 */
public class WPFile implements ElementFile {

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

    public WPFile(String WPName, String MEV, String DES, String IE, String PREFIX) {
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
    public Class<? extends ElementSource> getSourceClass() {
        return SourceWPFile.class;
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

    @Override
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {

        // build BP manifest
        // ----------------------------------------------------------------------
        var manifest = new Manifest();
        manifest.formatVersion = 2;
        // header
        var header = new Manifest.Header();
        header.name = WorkspaceName;
        header.description = Description;
        header.uuid = this.uuid1;
        header.min_engine_version = VersionVector.fromString(MinimumEngineVersion);
        header.version = VersionVector.fromString(BPVersion);
        // add header
        manifest.header = header;
        // modules
        var module = new Manifest.Module();
        module.type = "data";
        module.uuid = this.uuid2;
        module.version = VersionVector.fromString(BPVersion);
        // add modules
        var modules = new ArrayList<Manifest.Module>();
        modules.add(module);
        // add to manifest
        manifest.modules = modules.toArray(new Module[0]);

        // build manifest
        //var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(manifest);

        var path = new File(rootPath + File.separator + "manifest.json").toPath();
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // Files.writeString(path, json)

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

        // Files.writeString(path, json)

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
