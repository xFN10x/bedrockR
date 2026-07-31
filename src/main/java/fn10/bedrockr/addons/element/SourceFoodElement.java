package fn10.bedrockr.addons.element;

import java.io.IOException;
import fn10.bedrockr.addons.element.elementFiles.FoodFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceFoodElement extends ElementSource<FoodFile> {
    public SourceFoodElement(FoodFile obj) {
        super(obj);
    }

    @Override
    public String getFileExtension() {
        return ".foodref";
    }

    public SourceFoodElement() {
        this(new FoodFile());
    }


    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Food",
                "<html>A food, can give custom effects<br /> and run certain commands</html>",
                RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Food.png").openStream()));

    }
    
    @Override
    public Class<FoodFile> getSerilizedClass() {
        return FoodFile.class;
    }
}
