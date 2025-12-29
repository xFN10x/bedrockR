package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import fn10.bedrockr.addons.source.elementFiles.ItemFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import jakarta.annotation.Nullable;

public class SourceItemElement extends ElementSource<ItemFile> {
    private final String Location = File.separator + "elements" + File.separator;
    private Class<ItemFile> serilizedClass = ItemFile.class;
    private ItemFile serilized;

    public SourceItemElement(ItemFile obj) {
        this.serilized = obj;
    }

    public SourceItemElement() {
        this.serilized = null;
    }

    public SourceItemElement(String jsonString) {
        this.serilized = (ItemFile) getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() throws IOException {
                return new ElementDetails("Item ",
                "<html>A basic item. Can be made as a block<br>placer, and have custom visuals</html>",
                ElementSource.class.getResource("/addons/element/Item.png").openStream().readAllBytes());
    }

    

    @Override
    public Class<ItemFile> getSerilizedClass() {
        return ItemFile.class;
    }

    @Override
    public ItemFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    @Nullable
    public File buildJSONFile(String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".itemref");
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
    public ItemFile getSerilized() {
        return this.serilized;
    }
}
