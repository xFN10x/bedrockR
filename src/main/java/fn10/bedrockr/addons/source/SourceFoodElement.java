package fn10.bedrockr.addons.source;

import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;


import javax.swing.ImageIcon;
import fn10.bedrockr.addons.source.FieldFilters.RegularStringFilter;
import fn10.bedrockr.addons.source.elementFiles.FoodFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;
import jakarta.annotation.Nullable;

public class SourceFoodElement implements ElementSource<FoodFile> {
    private final String Location = File.separator + "elements" + File.separator;
    private Class<FoodFile> serilizedClass = FoodFile.class;
    private FoodFile serilized;

    public SourceFoodElement(FoodFile obj) {
        this.serilized = obj;
    }

    public SourceFoodElement() {
        this.serilized = null;
    }

    public SourceFoodElement(String jsonString) {
        this.serilized = getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Food",
                "<html>A food, can give custom effects<br /> and run certain commands</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Food.png")));

    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public Class<FoodFile> getSerilizedClass() {
        return FoodFile.class;
    }

    @Override
    public FoodFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    @Nullable
    public File buildJSONFile(Window doingThis, String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                Location + serilized.ElementName + ".foodref");
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
    public FoodFile getSerilized() {
        return this.serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent, String Workspace) {
        var frame = new RElementEditingScreen(Parent, "Food", this, getSerilizedClass(), parent,
                RElementEditingScreen.DEFAULT_STYLE);

        for (Field field : getSerilizedClass().getFields()) { // try to get fields
            try { // then add them
                RElementValue rev = null;
                var details = field.getAnnotation(RAnnotation.FieldDetails.class);
                if (field.getAnnotation(RAnnotation.UneditableByCreation.class) == null) {
                    if (this.serilized != null) // create field with a file already there
                        rev = new RElementValue(Parent,field.getType(),
                                details.Filter() != null ? details.Filter().getConstructor().newInstance()
                                        // if no filter, dont add one
                                        : field.getType() == String.class ? new RegularStringFilter() : null,
                                // if its a string however, add a basic filter
                                field.getName(), // target
                                details.displayName(), // display name
                                details.Optional(),
                                getSerilizedClass(),
                                this.serilized,
                                Workspace);
                    else // create file without anything there
                         // ---------------------------------------------------------------------
                        rev = new RElementValue(Parent,field.getType(),
                                details.Filter() != null ? details.Filter().getConstructor().newInstance()
                                        : field.getType() == String.class ? new RegularStringFilter() : null,
                                field.getName(), // target
                                details.displayName(), // display name
                                details.Optional(),
                                getSerilizedClass(),
                                Workspace);
                    if (field.getAnnotation(RAnnotation.SpecialField.class) != null)
                        frame.setSpecialField(rev);
                    else
                        frame.addField(rev);
                }

            } catch (Exception e) {
                e.printStackTrace();
                ErrorShower.showError(Parent, "Failed to create a field for " + field.getName(), "Field Error", e);
            }
        }

        return frame;
    }

}
