package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import fn10.bedrockr.addons.source.elementFiles.ItemFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class SourceItemElement extends ElementSource<ItemFile> {
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

    public static ElementDetails getDetails() throws IOException {
                return new ElementDetails("Item ",
                "<html>A basic item. Can be made as a block<br>placer, and have custom visuals</html>",
                RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Item.png").openStream()));
    }

    @Override
    public Class<ItemFile> getSerilizedClass() {
        return ItemFile.class;
    }
}
