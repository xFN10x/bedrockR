package fn10.bedrockr.addons.source;

import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import javax.annotation.Nullable;
import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.FieldFilters.RegularStringFilter;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.ItemFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementCreationScreen;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceItemElement implements ElementSource {
    private final String Location = "/elements/";
    private Class<ItemFile> serilizedClass = ItemFile.class;
    private ItemFile serilized;

    public SourceItemElement(ItemFile obj) {
        this.serilized = obj;
    }

    public SourceItemElement() {
        this.serilized = null;
    }

    public SourceItemElement(String jsonString) {
        this.serilized = (ItemFile) getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Item ",
                "<html>A basic item. Can be made as food, <br>block placer, or entity spawner.</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Item.png")));
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
    public Object getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    @Nullable
    public File buildJSONFile(Frame doingThis, String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                Location + serilized.ElementName + ".itemref");
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

    @SuppressWarnings("null")
    @Override
    public RElementCreationScreen getBuilderWindow(Frame Parent, ElementCreationListener parent) {
        var frame = new RElementCreationScreen(Parent, "Item", this, getSerilizedClass(), parent);

        for (Field field : getSerilizedClass().getFields()) { // try to get fields
            try { // then add them
                var details = field.getAnnotation(RAnnotation.FieldDetails.class);
                if (field.getAnnotation(RAnnotation.UneditableByCreation.class) == null) {
                    frame.addField(new RElementValue(field.getType(),
                            details.Filter() != null ? details.Filter().getConstructor().newInstance()// if no filter,
                                                                                                      // dont add one
                                    : field.getType() == String.class ? new RegularStringFilter() : null, // if its a
                                                                                                          // string
                                                                                                          // however,
                                                                                                          // add a basic
                                                                                                          // filter

                            // thanks vs
                            // code
                            // formatting,
                            // very cool
                            field.getName(), // target
                            details.displayName(), // display name
                            details.Optional(),
                            getSerilizedClass(),
                            this.serilized == null ? new ItemFile(): this.serilized)); //if this is blank, make new file
                }
            } catch (Exception e) {
                e.printStackTrace();
                ErrorShower.showError(Parent, "Failed to create a field for " + field.getName(), "Field Error", e);
            }
        }

        return frame;
    }

}
