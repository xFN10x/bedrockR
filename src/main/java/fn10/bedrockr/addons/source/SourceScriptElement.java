package fn10.bedrockr.addons.source;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import fn10.bedrockr.addons.source.elementFiles.ScriptFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.componets.RBlockly;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceScriptElement implements ElementSource {

    private final String Location = File.separator + "elements" + File.separator;
    private ScriptFile serilized;

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public ElementFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, ScriptFile.class);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Script",
                "<html>A JavaScript Script, you can edit with block coding.</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Script.png")));
    }

    @Override
    public File buildJSONFile(Frame doingThis, String workspace) {
        String string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                Location + serilized.ElementName + ".scriptref");
        try {
            Files.writeString(file.toPath(), string, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<?> getSerilizedClass() {
        return ScriptFile.class;
    }

    @Override
    public ElementFile getSerilized() {
        return serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Frame Parent, ElementCreationListener parent2, String Workspace) {
        RElementEditingScreen frame = new RElementEditingScreen(Parent, "Item", this, getSerilizedClass(), parent2,
                RElementEditingScreen.DEFAULT_STYLE);

        RElementValue elementName = new RElementValue(Parent, String.class, new FieldFilters.FileNameLikeStringFilter(),
                "ElementName", "Element Name", false, getSerilizedClass(), serilized, Workspace);
        JTextArea preview = new JTextArea();
        preview.setEditable(false);

        frame.InnerPane.setLayout(new BoxLayout(frame.InnerPane, BoxLayout.X_AXIS));

        frame.InnerPane.add(new RBlockly(preview));

        frame.InnerPane.add(Box.createHorizontalStrut(5));

        frame.InnerPane.add(preview);

        frame.setSize(new Dimension(1200, 800));
        frame.setLocation(ImageUtilites.getScreenCenter(frame));

        return frame;
    }

}
