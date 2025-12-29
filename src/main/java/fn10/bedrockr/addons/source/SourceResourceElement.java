package fn10.bedrockr.addons.source;

import java.io.File;
import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceResourceElement extends ElementSource<ResourceFile> {

    public ResourceFile Serilized;

    public SourceResourceElement(ResourceFile serilized) {
        this.Serilized = serilized;
    }

    public SourceResourceElement() {
        this.Serilized = null;
    }

    public SourceResourceElement(String json) {
        this.Serilized = gson.fromJson(json, ResourceFile.class);
    }


    @Override
    public ResourceFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, ResourceFile.class);
    }
    
    @Override
    public Class<ResourceFile> getSerilizedClass() {
        return ResourceFile.class;
    }

    @Override
    public ResourceFile getSerilized() {
        return this.Serilized;
    }

    @Override
    public File getLocation(String workspace) {
        return RFileOperations.getFileFromWorkspace(workspace,
                File.separator + "resources" + File.separator + RFileOperations.RESOURCE_FILE_NAME);
    }
}
