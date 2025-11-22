package fn10.bedrockr.addons.source;

import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import javax.naming.NameNotFoundException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import javax.swing.JPopupMenu.Separator;

import fn10.bedrockr.addons.source.FieldFilters.RegularStringFilter;
import fn10.bedrockr.addons.source.elementFiles.FoodFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.utils.exception.WrongItemValueTypeException;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RElementEditingScreen.CustomCreateFunction;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.componets.RItemValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;
import fn10.bedrockr.windows.interfaces.ValidatableValue;
import jakarta.annotation.Nullable;

public class SourceFoodElement implements ElementSource<FoodFile> {
    private final String Location = File.separator + "elements" + File.separator;
    private Class<FoodFile> serilizedClass = FoodFile.class;
    private FoodFile serilized;

    public SourceFoodElement(FoodFile obj) {
        this.serilized = obj;
    }

    public SourceFoodElement() {
        this.serilized = null;
    }

    public SourceFoodElement(String jsonString) {
        this.serilized = getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Food",
                "<html>A food, can give custom effects<br /> and run certain commands</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Food.png")));

    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public Class<FoodFile> getSerilizedClass() {
        return FoodFile.class;
    }

    @Override
    public FoodFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, serilizedClass);
    }

    @Override
    @Nullable
    public File buildJSONFile(Window doingThis, String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                Location + serilized.ElementName + ".foodref");
        file.setWritable(true);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.close();
            return file;
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public FoodFile getSerilized() {
        return this.serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent, String Workspace) {
        RElementEditingScreen frame = new RElementEditingScreen(Parent, "Food", this, getSerilizedClass(), parent,
                RElementEditingScreen.DEFAULT_STYLE);
        if (serilized == null)
            serilized = new FoodFile();
        for (Field field : getSerilizedClass().getFields()) { // try to get fields
            if (field.getType().equals(Separator.class)) {
                JSeparator sep = new JSeparator();
                sep.setPreferredSize(new Dimension(700, 10));
                frame.InnerPane.add(Box.createHorizontalStrut(1000));
                frame.InnerPane.add(sep);
                frame.InnerPane.add(Box.createHorizontalStrut(1000));
                continue;
            }
            try { // then add them
                RElementValue rev = null;
                var details = field.getAnnotation(RAnnotation.FieldDetails.class);
                if (field.getAnnotation(RAnnotation.UneditableByCreation.class) == null) {
                    rev = new RElementValue(frame, field.getType(),
                            details.Filter() != null ? details.Filter().getConstructor().newInstance()
                                    // if no filter, dont add one
                                    : field.getType() == String.class ? new RegularStringFilter() : null,
                            // if its a string however, add a basic filter
                            field.getName(), // target
                            details.displayName(), // display name
                            details.Optional(),
                            getSerilizedClass(),
                            this.serilized,
                            Workspace);
                    frame.addField(rev);
                }

            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(frame, "Failed to create a field for " + field.getName(), "Field Error", e);
            }
        }

        RItemValue turnsInto = new RItemValue(Workspace, RItemValue.Type.Single, false);
        if (serilized.EatingTurnsInto != null) {
            try {
                turnsInto.setButtonToItem(0, RItemSelector.getItemById(frame, serilized.EatingTurnsInto, Workspace));
            } catch (NameNotFoundException | WrongItemValueTypeException | IncorrectWorkspaceException e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(frame, "Failed to set item value.", e);
            }
        }
        frame.InnerPane.add(new JLabel("Eating Turns Into"));
        frame.InnerPane.add(turnsInto);
        JButton help = new JButton("?");
        help.putClientProperty("JButton.buttonType", "help");
        help.addActionListener(ac -> {
            JOptionPane.showMessageDialog(frame,
                    "Specifies what this item turns into after eating. For example, a soup turns into a bowl. Leave blank to turn into nothing");
        });
        frame.InnerPane.add(help);

        frame.addVaildations(turnsInto);
        SourceFoodElement This = this;
        frame.setCustomCreateFunction(new CustomCreateFunction() {

            /**
             * i copied the default one, now ill add that one item value
             */
            @Override
            public void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener, boolean isDraft) {

                try { // handle if there is no constructor
                    for (ValidatableValue validatable : frame.Fields) { // add the fields
                        if (validatable instanceof RElementValue elementValue) {
                            if (!elementValue.getOptionallyEnabled()) { // if its not enabled, continue
                                continue;
                            } else
                                try {
                                    serilizedClass.getField(elementValue.getTarget()).set(serilized,
                                            elementValue.getValue());
                                    // try to set field ^
                                } catch (Exception e) {
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                                    ErrorShower.showError(null, "Failed to change a field; continuing", e.getMessage(),
                                            e);
                                    continue;
                                }
                        }
                    }
                    if (!turnsInto.getItems().isEmpty())
                        serilized.EatingTurnsInto = turnsInto.getItems().get(0).item;

                    serilized.setDraft(isDraft);

                    Listener.onElementCreate(This); // create
                    Sindow.dispose();
                } catch (Exception ex) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                    ErrorShower.showError(Parent, "Failed to create ElementSource",
                            "Source Creation Error", ex);
                }
            }

        });

        return frame;
    }

}
