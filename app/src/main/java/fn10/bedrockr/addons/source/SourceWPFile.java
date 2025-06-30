package fn10.bedrockr.addons.source;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;

import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementCreationScreen;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceWPFile implements ElementSource {
    private final String Location = "/" + RFileOperations.WPFFILENAME;
    private Class<WPFile> serilizedClass = WPFile.class;
    private WPFile serilized;

    public SourceWPFile(WPFile obj) {
        this.serilized = obj;
    }
    public SourceWPFile() {
        this.serilized = null;
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Workspace File", "dont use this cause it will break",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Element.png")));
    }

    public SourceWPFile(String jsonString) {
        this.serilized = (WPFile) getFromJSON(jsonString);
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public Class getSerilizedClass() {
        return this.serilizedClass;
    }

    @Override
    public Object getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    @Nullable
    public File buildJSONFile(Component doingThis, String workspace) {
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
    public Object getSerilized() {
        return this.serilized;
    }

    @Override
    public RElementCreationScreen getBuilderWindow(Frame Parent, ElementCreationListener parent) {
        var frame = new RElementCreationScreen(Parent,"Workspace File", parent);


        return frame;
    }

}
