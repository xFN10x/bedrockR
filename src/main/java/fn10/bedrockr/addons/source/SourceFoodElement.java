package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import fn10.bedrockr.addons.source.elementFiles.FoodFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceFoodElement extends ElementSource<FoodFile> {
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
    public Class<FoodFile> getSerilizedClass() {
        return FoodFile.class;
    }

    @Override
    public FoodFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    public FoodFile getSerilized() {
        return this.serilized;
    }

    @Override
    public File getLocation(String workspace) {
        return RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".foodref");
    }
}
