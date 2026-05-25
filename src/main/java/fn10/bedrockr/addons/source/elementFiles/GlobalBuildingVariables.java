package fn10.bedrockr.addons.source.elementFiles;

import java.io.File;
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

import fn10.bedrockr.addons.source.supporting.block.BlockTexture;
import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.addon.jsonClasses.RP.BlockJSONEntry;
import fn10.bedrockr.addons.addon.jsonClasses.RP.TerrianTextures;
import fn10.bedrockr.addons.addon.jsonClasses.RP.ItemTextures;
import fn10.bedrockr.addons.addon.jsonClasses.RP.ItemTextures.TextureData;
import fn10.bedrockr.addons.source.interfaces.SourcelessElementFile;
import fn10.bedrockr.utils.RFileOperations;

/**
 * This is an ElementFile, meant to be passed to other Element Files, that holds
 * things like language texts, and textures. This should ALWAYS be built last, and it should not persist through builds.
 */

public class GlobalBuildingVariables extends SourcelessElementFile {

    public final Map<String, String> EnglishTexts = new HashMap<>();
    private final Map<String, BlockJSONEntry> BlockRPEntrys = new HashMap<>();

    private final ItemTextures ItemTexturesFile = new ItemTextures();
    private final TerrianTextures TerrainJson = new TerrianTextures();

    private final List<File> ItemTextures = new ArrayList<>();
    private final List<File> BlockTextures = new ArrayList<>();

    public final ResourceFile Resource;
    private final WorkspaceFile WPF;

    public GlobalBuildingVariables(WorkspaceFile WPF, ResourceFile Resources) {
        this.Resource = Resources;
        this.WPF = WPF;
    }

    /**
     * Adds/Gets the item texture from the resource pack
     * 
     * @param textureName the name of the texture, such as {@code bedrock_sword.png}
     * @return the string that can be passed to the {@code minecraft:icon}
     * @throws FileNotFoundException Resource not found
     */
    public String addItemTexture(String textureName) throws FileNotFoundException {
        var plannedkey = WPF.Prefix + "_" + textureName.replace(".png", "");

        if (ItemTexturesFile.texture_data.containsKey(plannedkey))
            ItemTexturesFile.texture_data.put(plannedkey, new TextureData(textureName));
        ItemTextures.add(Resource.getFileOfResource(WPF.WorkspaceName, textureName, ResourceFile.ITEM_TEXTURE));
        return plannedkey;
    }

    public void addBlockResources(String id, String sounds, BlockTexture texture) {
        try {
            BlockJSONEntry entry = new BlockJSONEntry(sounds, texture.convertBlockJsonTextures(this), null, null);
            BlockRPEntrys.put(WPF.Prefix + ":" + id, entry);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds/Gets the block texture from the resource pack
     * 
     * @param textureName the name of the texture, such as {@code bedrock_bricks.png}
     * @return the resource pack texture name
     * @throws FileNotFoundException Resource not found
     */
    public String addBlockTexture(String textureName) throws FileNotFoundException {
        var plannedkey = WPF.Prefix + "_" + textureName.replace(".png", "");

        if (TerrainJson.texture_data.containsKey(plannedkey))
            TerrainJson.texture_data.put(plannedkey, new TerrianTextures.TextureData(textureName));
        BlockTextures.add(Resource.getFileOfResource(WPF.WorkspaceName, textureName, ResourceFile.BLOCK_TEXTURE));
        return plannedkey;
    }

    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
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

        //#region Item Textures
        Path ItemTextureFolder = java.nio.file.Paths.get(rootResPackPath, "textures", "items");
        Files.createDirectories(ItemTextureFolder);

        ItemTexturesFile.resource_pack_name = workspaceFile.WorkspaceName;
        ItemTexturesFile.texture_name = "atlas.items";

        for (File file : ItemTextures) {
            FileUtils.copyFileToDirectory(file, ItemTextureFolder.toFile());
            ItemTexturesFile.texture_data.put(WPF.Prefix + "_" + file.getName().replace(".png", ""),
                    new TextureData(file.getName()));
        }

        Path dest = java.nio.file.Paths.get(rootResPackPath, "textures", "item_texture.json");
        String json = gson.toJson(ItemTexturesFile);

        Files.write(dest, json.getBytes());

        //#endregion

        //#region Block RP

        //move textures
        Path BlockTextureFolder = java.nio.file.Paths.get(rootResPackPath, "textures", "blocks");
        Files.createDirectories(BlockTextureFolder);

        for (File file : BlockTextures) {
            FileUtils.copyFileToDirectory(file, BlockTextureFolder.toFile());
            // check texture size
            /*BufferedImage testImage = ImageIO.read(file);
            if (Math.pow(testImage.getHeight(), 2) >= 512) {
                JOptionPane.showMessageDialog(null,
                        "The texture " + file.getName()
                                + " is bigger than 512 pixels, which is really big, it is not reccomened to do this as this item may cause lag.",
                        "Image is Giant: " + testImage.getWidth() + "x" + testImage.getHeight(),
                        JOptionPane.WARNING_MESSAGE);
            }
            testImage.getGraphics().dispose();*/
            TerrainJson.texture_data.put(WPF.Prefix + "_" + file.getName().replace(".png", ""),
                    new TerrianTextures.TextureData(file.getName()));
        }

        // microsoft decided to make this werid
        Map<String, Object> BlocksJson = new HashMap<>();
        BlocksJson.put("format_version", "1.21.40");
        BlocksJson.putAll(BlockRPEntrys);

        Path blocksJsonPath = Path.of(rootResPackPath, "blocks.json");
        Files.write(blocksJsonPath, gson.toJson(BlocksJson).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        // do the terrian_texture.json
        TerrainJson.resource_pack_name = WPF.WorkspaceName;
        TerrainJson.texture_name = "atlas.terrain";

        Path terrainTexturePath = Path.of(rootResPackPath, "textures", "terrain_texture.json");
        Files.write(terrainTexturePath, gson.toJson(TerrainJson).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        //#endregion

    }

}
