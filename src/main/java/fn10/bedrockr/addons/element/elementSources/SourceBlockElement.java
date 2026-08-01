package fn10.bedrockr.addons.element.elementSources;

import fn10.bedrockr.addons.element.elementFiles.BlockFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import org.jspecify.annotations.NonNull;

public class SourceBlockElement extends ElementSource<BlockFile> {
    @Override
    public ElementDetails getDetails() {
        return new ElementDetails("Block", "A simple block.", "Element");
    }

    public SourceBlockElement(@NonNull BlockFile serialized) {
        super(serialized);
    }

    public SourceBlockElement() {
        this(new BlockFile());
    }

    @Override
    public String getFileExtension() {
        return ".blockref";
    }

    @Override
    public Class<BlockFile> getSerilizedClass() {
        return  BlockFile.class;
    }
}
