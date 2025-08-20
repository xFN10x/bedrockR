package fn10.bedrockr.addons.source;

import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;

import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.ResourceFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceResourceFile implements ElementSource {

    public ResourceFile Serilized;

    public SourceResourceFile(ResourceFile serilized) {
        this.Serilized = serilized;
    }

    public SourceResourceFile(String json) {
       this.Serilized = gson.fromJson(json, ResourceFile.class);
    }

    @Override
    public String getJSONString() {
        return gson.toJson(Serilized);
    }

    @Override
    public ElementFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, ResourceFile.class);
    }

    @Override
    public File buildJSONFile(Frame doingThis, String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                File.separator+"resources"+File.separator+RFileOperations.RESOURCE_FILE_NAME);
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
    public Class<?> getSerilizedClass() {
        return ResourceFile.class;
    }

    @Override
    public ElementFile getSerilized() {
        return this.Serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Frame Parent, ElementCreationListener parent2, String Workspace) {
        return null;
    }

}
