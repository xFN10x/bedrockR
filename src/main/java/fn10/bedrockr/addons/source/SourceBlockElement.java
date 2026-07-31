package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.source.elementFiles.BlockFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class SourceBlockElement extends ElementSource<BlockFile> {
    public SourceBlockElement(@NonNull BlockFile serialized) {
        super(serialized);
    }

    public SourceBlockElement() {
        this(new BlockFile());
    }

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Block",
                "<html>A block. Can have a custom texture<br>and custom sounds.</html>",
                RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Element.png").openStream()));
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
