package fn10.bedrockr.addons.source;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;

import javax.annotation.Nullable;
import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.ItemFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementCreationScreen;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceItemElement implements ElementSource {
    private final String Location = "/elements/items/";
    private Class<ItemFile> serilizedClass = ItemFile.class;
    private ItemFile serilized;

    public static ElementDetails getDetails() {
        return new ElementDetails("Item ", "A basic item. Can be made as food, \nblock placer, or entity spawner.",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Item.png")));
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
        var frame = new RElementCreationScreen(Parent, "Item", parent);

        frame.addField(new RElementValue(String.class, "Name", "Name", false,
                "The name of the item. e.g. \"Diamond\", \"Coal\""));
        ;

        return frame;
    }

}
