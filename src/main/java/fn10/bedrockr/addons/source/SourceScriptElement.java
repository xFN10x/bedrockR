package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import fn10.bedrockr.addons.source.elementFiles.ScriptFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceScriptElement implements ElementSource<ScriptFile> {

    private final String Location = File.separator + "elements" + File.separator;
    private ScriptFile serilized;

    public SourceScriptElement(ScriptFile obj) {
        this.serilized = obj;
    }

    public SourceScriptElement() {
        this.serilized = null;
    }

    public SourceScriptElement(String jsonString) {
        this.serilized = (ScriptFile) getFromJSON(jsonString);
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public ScriptFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, ScriptFile.class);
    }

    public static ElementDetails getDetails() {
        try {
            return new ElementDetails("Script",
                    "<html>A JavaScript Script, you can edit<br>with block coding.</html>",
                    ElementSource.class.getResource("/addons/element/Script.png").openStream().readAllBytes());
        } catch (IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public File buildJSONFile(String workspace) {
        String string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".scriptref");
        try {
            Files.writeString(file.toPath(), string, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public Class<ScriptFile> getSerilizedClass() {
        return ScriptFile.class;
    }

    @Override
    public ScriptFile getSerilized() {
        return serilized;
    }
}
