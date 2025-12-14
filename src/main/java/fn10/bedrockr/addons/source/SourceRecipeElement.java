package fn10.bedrockr.addons.source;

import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.naming.NameNotFoundException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.UnlockCondition;
import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.utils.exception.WrongItemValueTypeException;
import fn10.bedrockr.windows.RElementEditingScreen;

public class SourceRecipeElement implements ElementSource<RecipeFile> {

    public static enum RecipeType {
        Shaped,
        Shapeless,
    }

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
    public RecipeFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, RecipeFile.class);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Recipe",

                // ------------------------------------------------| new line there
                "<html>A Crafting table recipe that you <br>edit with a visual guide.</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Recipe.png")));
    }

    @Override
    public File buildJSONFile(String workspace) {
        String string = getJSONString();
        File file = RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".reciperef");
        try {
            Files.writeString(file.toPath(), string, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public Class<RecipeFile> getSerilizedClass() {
        return RecipeFile.class;
    }

    @Override
    public RecipeFile getSerilized() {
        return serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(ElementCreationListener parent2, String Workspace) {
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
                switch (serilized.recipeType) {
                    case RecipeType.Shaped:
                        grid.setShapedRecipe(Parent, new ShapedOutput(serilized), Workspace);
                        break;

                    default:
                        serilized.ShapelessIngredients.forEach(item -> {
                            try {
                                grid.setButtonToItem(serilized.ShapelessIngredients.indexOf(item),
                                        RItemSelector.getItemById(Parent, item.item, Workspace));
                            } catch (WrongItemValueTypeException | NameNotFoundException
                                    | IncorrectWorkspaceException e1) {
                                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                            }
                        });
                        break;
                }
                grid.setShapedRecipe(Parent, new ShapedOutput(serilized), Workspace);
            }

            RItemValue outputSlot = new RItemValue(Workspace, Type.Single, true);
            if (serilized != null) {
                outputSlot.setButtonToItem(0, RItemSelector.getItemById(Parent, serilized.Result.item, Workspace));
            }

            RItemValue unlockItems = new RItemValue(Workspace, Type.ListOfItems, true);
            if (serilized != null) {
                unlockItems.addListElements(Workspace, ReturnItemInfo
                        .fromUnlockCondition(serilized.UnlockConditions, Workspace).toArray(new ReturnItemInfo[0]));
            }

            RItemValue extraResults = new RItemValue(Workspace, Type.ListOfItems, false);
            if (serilized != null) {
                extraResults.addListElements(Workspace, ReturnItemInfo.fromRecipeItem(serilized.ExtraResults, Workspace)
                        .toArray(new ReturnItemInfo[0]));
            }

            JLabel TypeDropdownText = new JLabel("Recipe Type");
            JComboBox<RecipeType> TypeDropdown = new JComboBox<RecipeType>(RecipeType.values());
            if (serilized != null) {
                TypeDropdown.setSelectedItem(serilized.recipeType);
            }

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
            Layout.putConstraint(SpringLayout.WEST, unlockItems, 30, SpringLayout.EAST, outputSlot);
            Layout.putConstraint(SpringLayout.SOUTH, unlockItems, -5, SpringLayout.NORTH, lowerFields);
            Layout.putConstraint(SpringLayout.NORTH, unlockItems, 5, SpringLayout.NORTH, frame.InnerPane);

            Layout.putConstraint(SpringLayout.EAST, extraResults, 0, SpringLayout.EAST, outputSlot);
            Layout.putConstraint(SpringLayout.WEST, extraResults, 0, SpringLayout.WEST, arrow);
            Layout.putConstraint(SpringLayout.SOUTH, extraResults, -5, SpringLayout.NORTH, lowerFields);
            Layout.putConstraint(SpringLayout.NORTH, extraResults, 5, SpringLayout.SOUTH, outputSlot);

            Layout.putConstraint(SpringLayout.WEST, TypeDropdownText, 0, SpringLayout.WEST, grid);
            Layout.putConstraint(SpringLayout.SOUTH, TypeDropdownText, 1, SpringLayout.NORTH, TypeDropdown);

            Layout.putConstraint(SpringLayout.WEST, TypeDropdown, 0, SpringLayout.WEST, grid);
            Layout.putConstraint(SpringLayout.NORTH, TypeDropdown, 15, SpringLayout.NORTH, frame.InnerPane);
            Layout.putConstraint(SpringLayout.SOUTH, TypeDropdown, -20, SpringLayout.NORTH, grid);

            Layout.putConstraint(SpringLayout.VERTICAL_CENTER, arrow, -70, SpringLayout.VERTICAL_CENTER,
                    frame.InnerPane);
            Layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, arrow, -50, SpringLayout.HORIZONTAL_CENTER,
                    frame.InnerPane);

            Layout.putConstraint(SpringLayout.VERTICAL_CENTER, grid, 0, SpringLayout.VERTICAL_CENTER, frame.InnerPane);
            Layout.putConstraint(SpringLayout.EAST, grid, -40, SpringLayout.WEST, arrow);

            Layout.putConstraint(SpringLayout.VERTICAL_CENTER, outputSlot, 0, SpringLayout.VERTICAL_CENTER,
                    arrow);
            Layout.putConstraint(SpringLayout.WEST, outputSlot, 50, SpringLayout.EAST, arrow);

            SourceRecipeElement This = this;
            frame.setCustomCreateFunction(new CustomCreateFunction() {

                @Override
                public void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener, boolean isDraft) {
                    try {
                        ShapedOutput shaped = grid.getShapedRecipe();
                        RecipeFile building = new RecipeFile();

                        building.ElementName = ElementName.getValue().toString();
                        building.RecipeID = RecipeID.getValue().toString();
                        building.recipeType = (RecipeType) TypeDropdown.getSelectedItem();
                        switch (TypeDropdown.getSelectedItem()) {

                            case RecipeType.Shaped:
                                building.ShapedPattern = shaped.pattern;
                                building.ShapedKey = shaped.key;
                                if (!extraResults.getItems().isEmpty())
                                    building.ExtraResults = extraResults.getItems();
                                break;

                            case RecipeType.Shapeless:
                                building.ShapelessIngredients = grid.getItems();
                                break;
                            default:
                                break;
                        }

                        building.UnlockConditions = UnlockCondition.fromRecipeItem(unlockItems.getItems());
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
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    }
                }

            }).addVaildations(ElementName, RecipeID, grid, outputSlot, unlockItems, extraResults);

            frame.InnerPane.add(grid);
            frame.InnerPane.add(outputSlot);
            frame.InnerPane.add(arrow);
            frame.InnerPane.add(lowerFields);
            frame.InnerPane.add(unlockItems);
            frame.InnerPane.add(extraResults);
            frame.InnerPane.add(TypeDropdown);
            frame.InnerPane.add(TypeDropdownText);

            TypeDropdown.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {

                    if (serilized == null)
                        serilized = new RecipeFile();
                    try {
                        ShapedOutput shaped = grid.getShapedRecipe();
                        serilized.ShapedPattern = shaped.pattern;
                        serilized.ShapedKey = shaped.key;
                        if (!extraResults.getItems().isEmpty())
                            serilized.ExtraResults = extraResults.getItems();

                        serilized.ShapelessIngredients = grid.getItems();
                    } catch (WrongItemValueTypeException e1) {
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                    }

                    switch (TypeDropdown.getSelectedItem()) {
                        case RecipeType.Shaped:
                            arrow.setIcon(new ImageIcon(getClass().getResource("/ui/Arrow.png")));
                            extraResults.setVisible(true);
                            try {
                                grid.empty();
                            } catch (WrongItemValueTypeException e1) {
                                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                            }
                            if (serilized != null) {
                                try {
                                    grid.setShapedRecipe(Parent, new ShapedOutput(serilized), Workspace);
                                } catch (WrongItemValueTypeException e1) {
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                                }
                            }
                            break;

                        case RecipeType.Shapeless:
                            arrow.setIcon(new ImageIcon(getClass().getResource("/ui/ArrowShapless.png")));
                            extraResults.setVisible(false);
                            try {
                                grid.empty();
                            } catch (WrongItemValueTypeException e1) {
                                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                            }
                            if (serilized != null) {
                                serilized.ShapelessIngredients.forEach(item -> {
                                    try {
                                        grid.setButtonToItem(serilized.ShapelessIngredients.indexOf(item),
                                                RItemSelector.getItemById(Parent, item.item, Workspace));
                                    } catch (WrongItemValueTypeException | NameNotFoundException
                                            | IncorrectWorkspaceException e1) {
                                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                                    }
                                });
                            }
                            break;

                        default:
                            arrow.setIcon(new ImageIcon(getClass().getResource("/ui/ArrowShapless.png")));
                            extraResults.setVisible(false);
                            break;
                    }
                }

            });

            return frame;
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }
}
