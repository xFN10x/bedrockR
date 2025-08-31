package fn10.bedrockr.addons.source;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.source.elementFiles.ScriptFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.RElementEditingScreen.CustomCreateFunction;
import fn10.bedrockr.windows.componets.RBlockly;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;
import javafx.application.Platform;

public class SourceScriptElement implements ElementSource {

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
    public ElementFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, ScriptFile.class);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Script",
                "<html>A JavaScript Script, you can edit<br>with block coding.</html>",
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

        ElementSource This = this;

        RElementValue elementName = new RElementValue(Parent, String.class, new FieldFilters.FileNameLikeStringFilter(),
                "ElementName", "Element Name", false, getSerilizedClass(), serilized, Workspace);
        elementName.setMaximumSize(new Dimension(300, 40));
        RElementValue scriptName = new RElementValue(Parent, String.class, new FieldFilters.IDStringFilter(),
                "ScriptName", "Script Name", false, getSerilizedClass(), serilized, Workspace);
        scriptName.setMaximumSize(new Dimension(300, 40));

        JTextArea preview = new JTextArea();
        preview.setEditable(false);

        RBlockly rblockly = new RBlockly(preview);

        JLabel loading = new JLabel("Loading...");

        JPanel rightStuff = new JPanel();
        JPanel toprightStuff = new JPanel();

        toprightStuff.setLayout(new BoxLayout(toprightStuff, BoxLayout.X_AXIS));
        rightStuff.setLayout(new BoxLayout(rightStuff, BoxLayout.Y_AXIS));

        rightStuff.add(preview);
        rightStuff.add(toprightStuff);

        toprightStuff.add(elementName);
        toprightStuff.add(Box.createHorizontalStrut(5));
        toprightStuff.add(scriptName);

        RElementEditingScreen frame = new RElementEditingScreen(Parent, "Item", this, getSerilizedClass(), parent2,
                RElementEditingScreen.DEFAULT_STYLE).setCustomCreateFunction(new CustomCreateFunction() {

                    @Override
                    public void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener,
                            boolean isDraft) {
                        try {
                            if (serilized == null)
                                serilized = new ScriptFile();

                            serilized.ElementName = elementName.getValue().toString();
                            serilized.ScriptName = scriptName.getValue().toString();

                            serilized.setDraft(isDraft);

                            Listener.onElementCreate(This); // create
                            Sindow.dispose();

                            Platform.runLater(() -> {
                                System.out.println("JSON IS: " + rblockly.getJson());
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            ErrorShower.showError(Sindow, "Failed to create ElementSource",
                                    "Source Creation Error", ex);
                        }
                    }

                });
        SpringLayout lay = new SpringLayout();

        lay.putConstraint(SpringLayout.NORTH, rightStuff, 0, SpringLayout.NORTH, frame.InnerPane);
        lay.putConstraint(SpringLayout.SOUTH, rightStuff, 0, SpringLayout.SOUTH, frame.InnerPane);
        lay.putConstraint(SpringLayout.EAST, rightStuff, 0, SpringLayout.EAST, frame.InnerPane);
        lay.putConstraint(SpringLayout.WEST, rightStuff, 805, SpringLayout.WEST, frame.InnerPane);

        lay.putConstraint(SpringLayout.NORTH, rblockly, 0, SpringLayout.NORTH, frame.InnerPane);
        lay.putConstraint(SpringLayout.SOUTH, rblockly, 0, SpringLayout.SOUTH, frame.InnerPane);
        lay.putConstraint(SpringLayout.WEST, rblockly, 0, SpringLayout.WEST, frame.InnerPane);

        frame.InnerPane.setLayout(lay);

        frame.InnerPane.add(rblockly);
        frame.InnerPane.add(rightStuff);
        frame.InnerPane.add(loading);

        frame.setSize(new Dimension(1500, 800));
        frame.setLocation(ImageUtilites.getScreenCenter(frame));

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                rblockly.dispose();
            }

        });

        return frame;
    }

}
