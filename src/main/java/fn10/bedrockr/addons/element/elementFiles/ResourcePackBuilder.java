package fn10.bedrockr.addons.element.elementFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.mcjson.resource.BlockJSONEntry;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.utils.RFileOperations;

import static fn10.bedrockr.utils.RFileOperations.gson;

/**
 * This is an ElementFile, meant to be passed to other Element Files, that holds
 * things like language texts, and textures. This should ALWAYS be built last, and it should not persist through builds.
 */
public class ResourcePackBuilder extends SourcelessElementFile {

    public final Map<String, String> EnglishTexts = new HashMap<>();
    private final Map<String, BlockJSONEntry> BlockRPEntries = new HashMap<>();

    private final WorkspaceFile WPF;

    public ResourcePackBuilder(WorkspaceFile WPF) {
        this.WPF = WPF;
    }

    public void addBlockResources(String id, String sounds, BlockTexture texture) {
        try {
            BlockJSONEntry entry = new BlockJSONEntry(sounds, texture.convertToBlockJsonTextures(), null, null);
            BlockRPEntries.put(WPF.Prefix + ":" + id, entry);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
                      ResourcePackBuilder globalResVaribles, WorkspaceResources res) throws IOException {
        //#region Language support
        List<String> Langs = new ArrayList<>();
        Langs.add("en_US"); // currently, only english support

        // make lang file

        Path langsFile = Path.of(rootResPackPath, "texts", "languages.json");
        FileUtils.createParentDirectories(langsFile.toFile());
        Files.write(langsFile, gson.toJson(Langs).getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        Path englishFile = Path.of(rootResPackPath, "texts", "en_US.lang");

        StringBuilder englishLangStringBuilder = new StringBuilder("## Generated with bedrockR " + RFileOperations.VERSION + "\n");

        for (Entry<String, String> ent : EnglishTexts.entrySet()) {
            englishLangStringBuilder.append(ent.getKey() + "=" + ent.getValue() + "\n");
        }

        Files.write(englishFile, englishLangStringBuilder.toString().getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        //#endregion

        //#region Block RP

        // microsoft decided to make this werid
        Map<String, Object> BlocksJson = new HashMap<>();
        BlocksJson.put("format_version", "1.21.40");
        BlocksJson.putAll(BlockRPEntries);

        Path blocksJsonPath = Path.of(rootResPackPath, "blocks.json");
        Files.write(blocksJsonPath, gson.toJson(BlocksJson).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        //#endregion
        
        if (res != null)
            res.build(rootResPackPath);
    }
}
