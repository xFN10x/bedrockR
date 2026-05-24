package fn10.bedrockr.addons.source.interfaces;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.ToNumberPolicy;
import fn10.bedrockr.addons.source.SourceResourceElement;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.*;
import fn10.bedrockr.addons.source.supporting.block.BlockTexture;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.typeAdapters.PathSerializer;
import fn10.bedrockr.utils.typeAdapters.StrictMapSerilizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SourcelessElementFile {
    public static @UneditableByCreation
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
            .registerTypeAdapter(new TypeToken<HashMap<String, Object>>() {
                    }.getClass(),
                    new StrictMapSerilizer())
            .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
            .create();

    @UneditableByCreation
    public int ElementVersion = 0;
    @UneditableByCreation
    public boolean Draft = false;

    public static final HashMap<Class<? extends SourcelessElementFile>, Integer> highestVersions = new HashMap<>(Map.of(
            BiomeFile.class, 0,
            BlockFile.class, 1,
            FoodFile.class, 0,
            ItemFile.class, 0,
            RecipeFile.class, 0,
            ScriptFile.class, 0
    ));

    public static SourcelessElementFile convertElementToNewerVersion(SourceWorkspaceFile wpf, JsonObject fileJson, Class<? extends SourcelessElementFile> elementType, int oldVer, int newVer) {
        if (BlockFile.class == elementType) {
            if (oldVer == 0 && newVer == 1) {
                //UUID TextureUUID > BlockTexture Textures
                SourceResourceElement res = RFileOperations.getResources(wpf.workspaceName());
                String textureUUID = fileJson.get("TextureUUID").getAsString();
                fileJson.add("Textures", gson.toJsonTree(new BlockTexture(UUID.fromString(textureUUID))));
                fileJson.remove("TextureUUID");
            }
        }
        return gson.fromJson(fileJson, elementType);
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
