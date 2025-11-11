package fn10.bedrockr.addons.source;

import java.awt.Window;
import java.io.File;
import java.io.FileWriter;

import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

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
    public File buildJSONFile(Window doingThis, String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                File.separator + "resources" + File.separator + RFileOperations.RESOURCE_FILE_NAME);
        file.setWritable(true);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent2, String Workspace) {
        return null;
    }

}
