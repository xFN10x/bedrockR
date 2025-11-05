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

public interface ElementFile { // mostly for making functions better to read

    @UneditableByCreation
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
            .registerTypeAdapter(new TypeToken<HashMap<String, Object>>() {
            }.getClass(),
                    new StrictMapSerilizer())
            .create();

    Class<? extends ElementSource> getSourceClass();

    String getElementName();

    void setDraft(Boolean draft);

    Boolean getDraft();

    void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath, GlobalBuildingVariables globalResVaribles)
            throws IOException;

}
