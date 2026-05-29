package fn10.bedrockr.addons.source.interfaces;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceResourceElement;
import fn10.bedrockr.addons.source.elementFiles.*;
import fn10.bedrockr.addons.source.supporting.block.BlockTexture;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.typeAdapters.ImageIconSerilizer;
import fn10.bedrockr.utils.typeAdapters.PathSerializer;
import fn10.bedrockr.utils.typeAdapters.StrictMapSerilizer;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SourcelessElementFile {
    @UneditableByCreation
    public int ElementVersion = 0;
    @UneditableByCreation
    public boolean Draft = false;

    @UneditableByCreation
    public static final HashMap<Class<? extends SourcelessElementFile>, Integer> highestVersions = new HashMap<>(Map.of(
            BiomeFile.class, 0,
            BlockFile.class, 1,
            FoodFile.class, 0,
            ItemFile.class, 0,
            RecipeFile.class, 0,
            ScriptFile.class, 0
    ));

    public static SourcelessElementFile upToDate(String workspace, JsonObject fileJson, Class<? extends SourcelessElementFile> elementType) {
        JsonElement elementVerJElement = fileJson.get("ElementVersion");
        int oldver;
        if (elementVerJElement == null) {
            oldver = 0;
        } else {
            oldver = elementVerJElement.getAsInt();
        }
        return upToDate(workspace, fileJson, elementType, oldver, highestVersions.get(elementType));
    }
    public static SourcelessElementFile upToDate(String workspace, JsonObject fileJson, Class<? extends SourcelessElementFile> elementType, int oldVer, int newVer) {
        boolean upgrading = false;
        if (oldVer != newVer) {
            upgrading = true;
            Launcher.LOG.info("Upgrading: " + elementType.getSimpleName() + " from: " + oldVer + ", to: " + newVer);
        }
        if (BlockFile.class == elementType) {
            if (oldVer == 0 && newVer == 1) {
                //UUID TextureUUID > BlockTexture Textures
                SourceResourceElement res = RFileOperations.getResources(workspace);
                String textureUUID = fileJson.get("TextureUUID").getAsString();
                fileJson.add("Textures", gson.toJsonTree(new BlockTexture(UUID.fromString(textureUUID))));
                fileJson.remove("TextureUUID");
            }
        }
        SourcelessElementFile sef = gson.fromJson(fileJson, elementType);
        if (upgrading && sef instanceof ElementFile<?> ef) {
            try {
                ElementSource<? extends ElementFile<?>> src = ef.getSourceClass().getConstructor(ef.getClass()).newInstance(ef);
                src.saveJSONFile(workspace);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sef;
    }


    /**
     * Builds this ElementFile to the built BP/RP
     *
     * @param rootPath          - the path to the BP, e.g. {@code rootPath + "/items/"} would be where items go
     * @param workspaceFile     - the workspace file for which this element is being built under
     * @param rootResPackPath   - the path to the RP
     * @param globalResVaribles - basicly the resource pack
     * @throws IOException
     */
    public abstract void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
               GlobalBuildingVariables globalResVaribles)
            throws IOException;

    public final void setDraft(Boolean draft) {
        this.Draft = draft;
    }

    public final Boolean getDraft() {
        return Draft;
    }
}
