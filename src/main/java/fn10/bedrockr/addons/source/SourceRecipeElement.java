package fn10.bedrockr.addons.source;

import java.awt.Window;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RElementEditingScreen.CustomCreateFunction;
import fn10.bedrockr.windows.componets.RItemValue;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.componets.RItemValue.ShapedOutput;
import fn10.bedrockr.windows.componets.RItemValue.Type;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceRecipeElement implements ElementSource {

    private final String Location = File.separator + "elements" + File.separator;
    private RecipeFile serilized;

    public SourceRecipeElement(RecipeFile obj) {
        this.serilized = obj;
    }

    public SourceRecipeElement() {
        this.serilized = null;
    }

    public SourceRecipeElement(String jsonString) {
        this.serilized = (RecipeFile) getFromJSON(jsonString);
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public ElementFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, RecipeFile.class);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Recipe",

                // ------------------------------------------------| new line there
                "<html>A Crafting table recipe that you <br>edit with a visual guide.</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Recipe.png")));
    }

    @Override
    public File buildJSONFile(Window doingThis, String workspace) {
        String string = getJSONString();
        File file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                Location + serilized.ElementName + ".reciperef");
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
        return RecipeFile.class;
    }

    @Override
    public ElementFile getSerilized() {
        return serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent2, String Workspace) {
        try {
            RElementEditingScreen frame = new RElementEditingScreen(Parent, "Item", this, getSerilizedClass(), parent2,
                    RElementEditingScreen.DEFAULT_STYLE);

            SpringLayout Layout = new SpringLayout();

            RElementValue ElementName = new RElementValue(Parent, String.class,
                    new FieldFilters.FileNameLikeStringFilter(),
                    "ElementName", "Element Name", false, getSerilizedClass(), serilized, Workspace);

            RElementValue RecipeID = new RElementValue(Parent, String.class,
                    new FieldFilters.IDStringFilter(),
                    "RecipeID", "Recipe ID", false, getSerilizedClass(), serilized, Workspace);

            frame.InnerPane.setLayout(Layout);
            RItemValue grid = new RItemValue(Workspace, Type.CraftingTable, true);
            if (serilized != null) {
                grid.setShapedRecipe(Parent, new ShapedOutput(serilized), Workspace);
            }

            RItemValue outputSlot = new RItemValue(Workspace, Type.Single, true);
            if (serilized != null) {
                outputSlot.setButtonToItem(0, RItemSelector.getItemById(Parent, serilized.Result.item, Workspace));
            }

            RItemValue unlockItems = new RItemValue(Workspace, Type.ListOfItems, true);
            /*
             * if (serilized != null) {
             * outputSlot.setButtonToItem(0, RItemSelector.getItemById(Parent,
             * serilized.Result.item, Workspace));
             * }
             */

            JLabel arrow = new JLabel(new ImageIcon(getClass().getResource("/ui/Arrow.png")));

            JPanel lowerFields = new JPanel();
            BoxLayout lowerLayout = new BoxLayout(lowerFields, BoxLayout.X_AXIS);
            lowerFields.setLayout(lowerLayout);

            lowerFields.add(Box.createHorizontalGlue());
            lowerFields.add(ElementName);
            lowerFields.add(Box.createHorizontalStrut(10));
            lowerFields.add(RecipeID);
            lowerFields.add(Box.createHorizontalGlue());

            Layout.putConstraint(SpringLayout.EAST, lowerFields, 0, SpringLayout.EAST, frame.InnerPane);
            Layout.putConstraint(SpringLayout.WEST, lowerFields, 0, SpringLayout.WEST, frame.InnerPane);
            Layout.putConstraint(SpringLayout.SOUTH, lowerFields, 0, SpringLayout.SOUTH, frame.InnerPane);
            Layout.putConstraint(SpringLayout.NORTH, lowerFields, 40, SpringLayout.SOUTH, grid);

            Layout.putConstraint(SpringLayout.EAST, unlockItems, -5, SpringLayout.EAST, frame.InnerPane);
            Layout.putConstraint(SpringLayout.WEST, unlockItems, 10, SpringLayout.EAST, outputSlot);
            Layout.putConstraint(SpringLayout.SOUTH, unlockItems, -5, SpringLayout.NORTH, lowerFields);
            Layout.putConstraint(SpringLayout.NORTH, unlockItems, 5, SpringLayout.NORTH, frame.InnerPane);

            Layout.putConstraint(SpringLayout.VERTICAL_CENTER, arrow, 0, SpringLayout.VERTICAL_CENTER, frame.InnerPane);
            Layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, arrow, 0, SpringLayout.HORIZONTAL_CENTER,
                    frame.InnerPane);

            Layout.putConstraint(SpringLayout.VERTICAL_CENTER, grid, 0, SpringLayout.VERTICAL_CENTER, frame.InnerPane);
            Layout.putConstraint(SpringLayout.EAST, grid, -30, SpringLayout.WEST, arrow);

            Layout.putConstraint(SpringLayout.VERTICAL_CENTER, outputSlot, 0, SpringLayout.VERTICAL_CENTER,
                    frame.InnerPane);
            Layout.putConstraint(SpringLayout.WEST, outputSlot, 30, SpringLayout.EAST, arrow);

            SourceRecipeElement This = this;
            frame.setCustomCreateFunction(new CustomCreateFunction() {

                @Override
                public void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener, boolean isDraft) {
                    try {
                        // JOptionPane.showMessageDialog(Parent, gson.toJson(grid.getShapedRecipe()));

                        ShapedOutput shaped = grid.getShapedRecipe();
                        RecipeFile building = new RecipeFile();

                        building.ElementName = ElementName.getValue().toString();
                        building.RecipeID = RecipeID.getValue().toString();
                        building.ShapedPattern = shaped.pattern;
                        building.ShapedKey = shaped.key;
                        // TODO: building.ExtraResults
                        // TODO: building.UnlockConditions
                        building.Result = outputSlot.getItems().get(0);

                        serilized = building;

                        if (isDraft) {
                            Sindow.setVisible(false);
                            Listener.onElementDraft(This);
                        } else {
                            Sindow.setVisible(false);
                            Listener.onElementCreate(This);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).addVaildations(ElementName, RecipeID, grid, outputSlot);

            frame.InnerPane.add(grid);
            frame.InnerPane.add(outputSlot);
            frame.InnerPane.add(arrow);
            frame.InnerPane.add(lowerFields);
            frame.InnerPane.add(unlockItems);

            return frame;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
