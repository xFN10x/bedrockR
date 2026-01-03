package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.source.elementFiles.BlockFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceBlockElement extends ElementSource<BlockFile> {
    private final String Location = File.separator + "elements" + File.separator;
    private Class<BlockFile> serilizedClass = BlockFile.class;
    private BlockFile serilized;

    public SourceBlockElement(BlockFile obj) {
        this.serilized = obj;
    }

    public SourceBlockElement() {
        this.serilized = null;
    }

    public SourceBlockElement(String jsonString) {
        this.serilized = (BlockFile) getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Block",
                "<html>A block. Can have a custom texture<br>and custom sounds.</html>",
                RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Element.png").openStream()));
    }

    

    @Override
    public Class<BlockFile> getSerilizedClass() {
        return this.serilizedClass;
    }

    @Override
    public BlockFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    public BlockFile getSerilized() {
        return this.serilized;
    }

    @Override
    public File getLocation(String workspace) {
        return RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".blockref");
    }
}
