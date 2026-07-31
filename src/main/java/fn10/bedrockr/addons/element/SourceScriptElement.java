package fn10.bedrockr.addons.element;

import java.io.File;
import java.io.IOException;
import fn10.bedrockr.addons.element.elementFiles.ScriptFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceScriptElement extends ElementSource<ScriptFile> {

    public SourceScriptElement(ScriptFile obj) {
        super(obj);
    }

    @Override
    public String getFileExtension() {
        return ".scriptref";
    }

    public SourceScriptElement() {
        this(new ScriptFile());
    }
    
    public static ElementDetails getDetails() {
        try {
            return new ElementDetails("Script",
                    "<html>A JavaScript Script, you can edit<br>with block coding.</html>",
                    RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Script.png").openStream()));
        } catch (IOException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public File saveJSONFile(String workspace) {
        /*
         * String string = getJSONString();
         * var file = RFileOperations.getFileFromWorkspace(workspace,
         * Location + serilized.ElementName + ".scriptref");
         * try {
         * Files.write(file.toPath(), string, StandardOpenOption.CREATE,
         * StandardOpenOption.TRUNCATE_EXISTING);
         * return file;
         * } catch (Exception e) {
         * fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE,
         * "Exception thrown", e);
         * return null;
         * }
         */
        throw new UnsupportedOperationException("Scripts are not avalible since a2.0");
    }

    @Override
    public Class<ScriptFile> getSerilizedClass() {
        return ScriptFile.class;
    }
}
