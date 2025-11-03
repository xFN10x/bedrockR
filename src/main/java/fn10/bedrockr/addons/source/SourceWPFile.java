package fn10.bedrockr.addons.source;

import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;


import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.elementFiles.WPFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;
import jakarta.annotation.Nullable;

public class SourceWPFile implements ElementSource {
    private final String Location = File.separator + RFileOperations.WPFFILENAME;
    private Class<WPFile> serilizedClass = WPFile.class;
    private WPFile serilized;

    public String workspaceName() {
        return serilized.WorkspaceName;
    }

    public SourceWPFile(WPFile obj) {
        this.serilized = obj;
    }

    public SourceWPFile() {
        this.serilized = null;
    }

    public SourceWPFile(String jsonString) {
        this.serilized = (WPFile) getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Workspace File", "dont use this cause it will break",
                new ImageIcon(ElementSource.class.getResource("/addons"
                        + "/element" + "/Element.png")));
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public Class<?> getSerilizedClass() {
        return this.serilizedClass;
    }

    @Override
    public ElementFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    @Nullable
    public File buildJSONFile(Frame doingThis, String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace, Location);
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
    public ElementFile getSerilized() {
        return this.serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Frame Parent, ElementCreationListener parent, String Workspace) {
        return null;
    }

}
