package fn10.bedrockr.addons.element.elementSources;

import java.io.IOException;
import fn10.bedrockr.addons.element.elementFiles.FoodFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceFoodElement extends ElementSource<FoodFile> {
    @Override
    public ElementDetails getDetails() {
        return new ElementDetails("Food", "An item you can eat.");
    }

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
    
    @Override
    public Class<FoodFile> getSerilizedClass() {
        return FoodFile.class;
    }
}
