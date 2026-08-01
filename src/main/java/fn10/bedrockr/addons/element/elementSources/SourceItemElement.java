package fn10.bedrockr.addons.element.elementSources;

import java.io.IOException;
import fn10.bedrockr.addons.element.elementFiles.ItemFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceItemElement extends ElementSource<ItemFile> {
    @Override
    public ElementDetails getDetails() {
        return new ElementDetails("Item", "A simple item.");
    }

    public SourceItemElement(ItemFile obj) {
        super(obj);
    }

    @Override
    public String getFileExtension() {
        return ".itemref";
    }

    public SourceItemElement() {
        this(new ItemFile());
    }

    @Override
    public Class<ItemFile> getSerilizedClass() {
        return ItemFile.class;
    }
}
