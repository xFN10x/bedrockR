package fn10.bedrockr.addons.source.jsonClasses;

import java.awt.Image;
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
import fn10.bedrockr.addons.addon.jsonClasses.BP.ItemTexture;
import fn10.bedrockr.addons.addon.jsonClasses.BP.ItemTexture.TextureData;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;

/**
 * This is an ElementFile, meant to be passed to other Element Files, that holds
 * things like language texts, and textures. This should ALWAYS be built last.
 */

public class GlobalBuildingVariables implements ElementFile {

    public List<String> Langs = new ArrayList<String>();
    public Map<String, String> EnglishTexts = new HashMap<String, String>();
    public ItemTexture ItemTexturesFile = new ItemTexture();
    public List<File> ItemTextures = new ArrayList<File>();
    public ResourceFile Resource;
    public WPFile WPF;

    public GlobalBuildingVariables(WPFile WPF, ResourceFile Resources) {
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
        var plannedkey = textureName.replace(".png", "");

        if (ItemTexturesFile.texture_data.containsKey(plannedkey))
            ItemTexturesFile.texture_data.put(plannedkey, new TextureData(textureName));
        ItemTextures.add(Resource.getResourceFile(null, WPF.WorkspaceName, textureName, ResourceFile.ITEM_TEXTURE));
        return plannedkey;
    }

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public void setDraft(Boolean draft) {
        return;
    }

    @Override
    public Boolean getDraft() {
        return null;
    }

    @Override
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
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
                        "Image is MASSIVE: " + testImage.getWidth() + "x" + testImage.getHeight(),
                        JOptionPane.WARNING_MESSAGE);
            }
            ItemTexturesFile.texture_data.put(file.getName().replace(".png", ""), new TextureData(file.getName()));
        }

        Path dest = Path.of(rootResPackPath, "textures", "item_texture.json");
        String json = gson.toJson(ItemTexturesFile);

        Files.write(dest, json.getBytes());

        // #endregion
    }

}
