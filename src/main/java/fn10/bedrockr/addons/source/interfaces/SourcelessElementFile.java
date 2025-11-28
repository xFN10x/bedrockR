package fn10.bedrockr.addons.source.interfaces;

import java.io.IOException;
import java.util.HashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import fn10.bedrockr.addons.source.elementFiles.GlobalBuildingVariables;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.typeAdapters.StrictMapSerilizer;

public interface SourcelessElementFile {
    @UneditableByCreation
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
            .registerTypeAdapter(new TypeToken<HashMap<String, Object>>() {
            }.getClass(),
                    new StrictMapSerilizer())
            .create();

    /**
     * Builds this ElementFile to the built BP/RP
     * @param rootPath - the path to the BP, e.g. {@code rootPath + "/items/"} would be where items go
     * @param workspaceFile - the workspace file for which this element is being built under
     * @param rootResPackPath - the path to the RP
     * @param globalResVaribles - basicly the resource pack
     * @throws IOException
     */
    void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles)
            throws IOException;
}
