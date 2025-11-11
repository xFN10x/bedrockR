package fn10.bedrockr.addons.source.elementFiles;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.addon.jsonClasses.RP.BlockJSONEntry;
import fn10.bedrockr.addons.addon.jsonClasses.RP.BlockTexture;
import fn10.bedrockr.addons.addon.jsonClasses.RP.ItemTexture;
import fn10.bedrockr.addons.addon.jsonClasses.RP.ItemTexture.TextureData;
import fn10.bedrockr.addons.source.interfaces.SourcelessElementFile;

/**
 * This is an ElementFile, meant to be passed to other Element Files, that holds
 * things like language texts, and textures. This should ALWAYS be built last.
 */

public class GlobalBuildingVariables implements SourcelessElementFile {

    public List<String> Langs = new ArrayList<String>();
    public Map<String, String> EnglishTexts = new HashMap<String, String>();
    public Map<String, BlockJSONEntry> BlockRPEntrys = new HashMap<String, BlockJSONEntry>();

    public ItemTexture ItemTexturesFile = new ItemTexture();
    public BlockTexture BlockTexturesFile = new BlockTexture();

    public List<File> ItemTextures = new ArrayList<File>();
    public List<File> BlockTextures = new ArrayList<File>();

    public ResourceFile Resource;
    public WorkspaceFile WPF;

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
     * @throws IllegalAccessError
     */
    public String addItemTexture(String textureName) throws IllegalAccessError, FileNotFoundException {
        var plannedkey = WPF.Prefix + "_" + textureName.replace(".png", "");

        if (ItemTexturesFile.texture_data.containsKey(plannedkey))
            ItemTexturesFile.texture_data.put(plannedkey, new TextureData(textureName));
        ItemTextures.add(Resource.getResourceFile(null, WPF.WorkspaceName, textureName, ResourceFile.ITEM_TEXTURE));
        return plannedkey;
    }

    

    /**
     * Adds/Gets the block texture from the resource pack
     * 
     * @param textureName the name of the texture, such as {@code bedrock_sword.png}
     * @return the string that can be passed to the {@code minecraft:icon}
     * @throws FileNotFoundException Resource not found
     * @throws IllegalAccessError
     */
    public String addBlockTexture(String textureName) throws IllegalAccessError, FileNotFoundException {
        var plannedkey = WPF.Prefix + "_" + textureName.replace(".png", "");

        if (BlockTexturesFile.texture_data.containsKey(plannedkey))
            BlockTexturesFile.texture_data.put(plannedkey, new BlockTexture.TextureData(textureName));
        BlockTextures.add(Resource.getResourceFile(null, WPF.WorkspaceName, textureName, ResourceFile.BLOCK_TEXTURE));
        return plannedkey;
    }

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        // #region Language support
        Langs.add("en_US"); // currently, only english support

        // make lang file
        var langsFile = new File(rootResPackPath + File.separator + "texts" + File.separator + "languages.json");
        FileUtils.createParentDirectories(langsFile);
        Files.write(langsFile.toPath(), gson.toJson(Langs).getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        var englishFile = new File(rootResPackPath + File.separator + "texts" + File.separator + "en_US.lang");
        // dont need to make parents, other file is already in the same dir
        var englishLangStringBuilder = new StringBuilder("## Generated with bedrockR " + Launcher.VERSION + "\n");

        for (Entry<String, String> ent : EnglishTexts.entrySet()) {
            englishLangStringBuilder.append(ent.getKey() + "=" + ent.getValue() + "\n");
        }

        Files.write(englishFile.toPath(), englishLangStringBuilder.toString().getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        // #endregion

        // #region Item Textures
        Path ItemTextureFolder = Path.of(rootResPackPath, "textures", "items");
        Files.createDirectories(ItemTextureFolder);

        ItemTexturesFile.resource_pack_name = workspaceFile.WorkspaceName;
        ItemTexturesFile.texture_name = "atlas.items";

        for (File file : ItemTextures) {
            FileUtils.copyFileToDirectory(file, ItemTextureFolder.toFile());
            // check texture size
            BufferedImage testImage = ImageIO.read(file);
            if (Math.pow(testImage.getHeight(), 2) >= 512) {
                JOptionPane.showMessageDialog(null,
                        "The texture " + file.getName()
                                + " is bigger than 512 pixels, which is really big, it is not reccomened to do this as this item may cause lag.",
                        "Image is Giant: " + testImage.getWidth() + "x" + testImage.getHeight(),
                        JOptionPane.WARNING_MESSAGE);
            }
            testImage.getGraphics().dispose();
            ItemTexturesFile.texture_data.put(WPF.Prefix + "_" + file.getName().replace(".png", ""),
                    new TextureData(file.getName()));
        }

        Path dest = Path.of(rootResPackPath, "textures", "item_texture.json");
        String json = gson.toJson(ItemTexturesFile);

        Files.write(dest, json.getBytes());

        // #endregion

        // #region Block RP

        // move textures
        Path BlockTextureFolder = Path.of(rootResPackPath, "textures", "blocks");
        Files.createDirectories(BlockTextureFolder);

        for (File file : BlockTextures) {
            FileUtils.copyFileToDirectory(file, BlockTextureFolder.toFile());
            // check texture size
            BufferedImage testImage = ImageIO.read(file);
            if (Math.pow(testImage.getHeight(), 2) >= 512) {
                JOptionPane.showMessageDialog(null,
                        "The texture " + file.getName()
                                + " is bigger than 512 pixels, which is really big, it is not reccomened to do this as this item may cause lag.",
                        "Image is Giant: " + testImage.getWidth() + "x" + testImage.getHeight(),
                        JOptionPane.WARNING_MESSAGE);
            }
            testImage.getGraphics().dispose();
            BlockTexturesFile.texture_data.put(WPF.Prefix + "_" + file.getName().replace(".png", ""),
                    new BlockTexture.TextureData(file.getName()));
        }

        // microsoft decided to make this werid
        Map<String, Object> ActualBlocksJSON = new HashMap<String, Object>();
        ActualBlocksJSON.put("format_version", "1.21.40");
        ActualBlocksJSON.putAll(BlockRPEntrys);

        Path blocksJsonPath = Path.of(rootResPackPath, "blocks.json");
        blocksJsonPath.toFile().createNewFile();
        Files.writeString(blocksJsonPath, gson.toJson(ActualBlocksJSON));

        // do the terrian_texture.json
        BlockTexturesFile.resource_pack_name = WPF.WorkspaceName;
        BlockTexturesFile.texture_name = "atlas.terrain";

        Path terrainTexturePath = Path.of(rootResPackPath, "textures", "terrain_texture.json");
        terrainTexturePath.toFile().createNewFile();
        Files.writeString(terrainTexturePath, gson.toJson(BlockTexturesFile));

        // #endregion

    }

}
