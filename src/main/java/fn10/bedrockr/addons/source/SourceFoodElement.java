package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import fn10.bedrockr.addons.source.elementFiles.FoodFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
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

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Food",
                "<html>A food, can give custom effects<br /> and run certain commands</html>",
                ElementSource.class.getResource("/addons/element/Food.png").openStream().readAllBytes());

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
    public File buildJSONFile(String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".foodref");
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
    public FoodFile getSerilized() {
        return this.serilized;
    }
}
