package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.FileWriter;

import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceResourceElement implements ElementSource<ResourceFile> {

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
    public String getJSONString() {
        return gson.toJson(Serilized);
    }

    @Override
    public ResourceFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, ResourceFile.class);
    }

    @Override
    public File buildJSONFile(String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(workspace,
                File.separator + "resources" + File.separator + RFileOperations.RESOURCE_FILE_NAME);
        file.setWritable(true);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.close();
            return file;
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public Class<ResourceFile> getSerilizedClass() {
        return ResourceFile.class;
    }

    @Override
    public ResourceFile getSerilized() {
        return this.Serilized;
    }
}
