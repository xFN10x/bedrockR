package fn10.bedrockr.addons.source.jsonClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Manifest.Module;
import fn10.bedrockr.addons.addon.jsonClasses.SharedJSONClasses.VersionVector;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;

public class WPFile implements ElementFile {
    @HelpMessage(message = "You should never be able to see this")
    public String WorkspaceName;
    @HelpMessage(message = "You should never be able to see this")
    public String MinimumEngineVersion;
    @HelpMessage(message = "You should never be able to see this")
    public String Version = "1.0.0";
    @HelpMessage(message = "You should never be able to see this")
    public String Description;
    @HelpMessage(message = "You should never be able to see this")
    public String IconExtension;
    @HelpMessage(message = "You should never be able to see this")
    public String Prefix;
    @HelpMessage(message = "You should never be able to see this")
    public boolean MinecraftSync;

    public WPFile(String WPName, String MEV, String DES, String IE) {
        this.WorkspaceName = WPName;
        this.MinimumEngineVersion = MEV;
        this.Description = DES;
        this.IconExtension = IE;
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
    public void build(String rootPath, WPFile workspaceFile) throws IOException {
        var uuid1 = UUID.randomUUID().toString();
        var uuid2 = UUID.randomUUID().toString();

        var manifest = new Manifest(); // build BP manifest
        manifest.formatVersion = 2;
        // header
        var header = new Manifest.Header();
        header.name = WorkspaceName;
        header.description = Description;
        header.uuid = uuid1;
        header.min_engine_version = VersionVector.fromString(MinimumEngineVersion);
        header.version = VersionVector.fromString(Version);
        // add header
        manifest.header = header;
        // modules
        var module = new Manifest.Module();
        module.type = "data";
        module.uuid = uuid2;
        module.version = VersionVector.fromString(Version);
        // add modules
        var modules = new ArrayList<Manifest.Module>();
        modules.add(module);
        // add to manifest
        manifest.modules = modules.toArray(new Module[0]);

        // build manifest
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(manifest);

        var path = new File(rootPath + "/manifest.json").toPath();
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        // Files.writeString(path, json)

        // add image
        var img = Files.readAllBytes(
                RFileOperations.getFileFromWorkspace(null, this.WorkspaceName, "/icon." + this.IconExtension, true)
                        .toPath());
        var imgpath = new File(rootPath + "/pack_icon.png").toPath();
        Files.write(imgpath, img, StandardOpenOption.CREATE);

        // add dirs
        //.createDirectory(new File(rootPath + "/texts/").toPath()); language support later
    }
}
