package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NameNotFoundException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.UnlockCondition;
import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.FieldFilters.RegularStringFilter;
import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.SourceFoodElement;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceRecipeElement.RecipeType;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.elementFiles.FoodFile;
import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.addons.source.interfaces.CreationScreenSeperator;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.interfaces.ValidatableValue;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.utils.exception.WrongItemValueTypeException;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.componets.RItemValue;
import fn10.bedrockr.windows.componets.RItemValue.ShapedOutput;
import fn10.bedrockr.windows.util.ErrorShower;
import fn10.bedrockr.windows.util.WrapLayout;

/**
 * An RDialog that provides the basic parts to make a source element builder
 * window. Fields, and field generation is up to the source element.
 */
public class RElementEditingScreen extends RDialog implements ActionListener {

    private ElementCreationListener Listener;
    public JPanel InnerPane = new JPanel();
    private JScrollPane Pane = new JScrollPane(InnerPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JPanel SpecialPane = null;
    private SpringLayout SpecialPaneLay = new SpringLayout();
    public BoxLayout PaneLay = new BoxLayout(InnerPane, BoxLayout.PAGE_AXIS);

    public Class<?> SourceClass;
    public Class<? extends ElementSource<?>> SourceElementClass;
    public ElementSource<?> SourceElement;

    public List<ValidatableValue> Fields = new ArrayList<ValidatableValue>();
    public List<ValidatableValue> RequiredFields = new ArrayList<ValidatableValue>();
    public List<ValidatableValue> IncorrectFields = new ArrayList<ValidatableValue>();

    public static final Integer DEFAULT_STYLE = 0;
    public static final Integer SPECIAL_AREA_STYLE = 1;

    private CustomCreateFunction createFunction = null;

    public final JButton CreateButton = new JButton("Create!");
    public final JButton DraftButton = new JButton("Save as draft");
    public final JButton CancelButton = new JButton("Cancel");

    public static interface CustomCreateFunction {
        void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener, boolean isDraft);

    }

    public RElementEditingScreen addVaildations(ValidatableValue... values) {
        Fields.addAll(List.of(values));
        return this;
    }

    /**
     * Replaces the Fields array, with a custom one.
     * 
     * @param func - the {@code CustomCreateFunction} to use for creation.
     * @return the {@code RElementEditingScreen}
     */
    public RElementEditingScreen setCustomCreateFunction(CustomCreateFunction func) {
        this.createFunction = func;
        return this;
    }

    public RElementEditingScreen(Window Parent, String elementName, ElementSource<?> sourceElementClass,
            Class<?> sourceClass,
            ElementCreationListener listenier) {
        this(Parent, elementName, sourceElementClass, sourceClass, listenier, DEFAULT_STYLE);
    }

    @SuppressWarnings("unchecked")
    public RElementEditingScreen(Window Parent, String elementName, ElementSource<?> sourceElementClass,
            Class<?> sourceClass,
            ElementCreationListener listenier, Integer layout) {
        super(
                Parent,
                DISPOSE_ON_CLOSE,
                "Editing " + elementName,
                new Dimension(800, 450));

        this.Listener = listenier;
        this.SourceClass = sourceClass;
        this.SourceElementClass = (Class<? extends ElementSource<?>>) sourceElementClass.getClass();

        CreateButton.setActionCommand("create");
        CreateButton.addActionListener(this);

        DraftButton.setActionCommand("draft");
        DraftButton.addActionListener(this);

        CancelButton.setActionCommand("cancel");
        CancelButton.addActionListener(this);

        JSeparator Sep = new JSeparator(JSeparator.HORIZONTAL);

        Lay.putConstraint(SpringLayout.SOUTH, CreateButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, CreateButton, -10, SpringLayout.EAST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, DraftButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, DraftButton, -10, SpringLayout.WEST, CreateButton);

        Lay.putConstraint(SpringLayout.SOUTH, CancelButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, CancelButton, 10, SpringLayout.WEST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, Sep, -10, SpringLayout.NORTH, CreateButton);
        Lay.putConstraint(SpringLayout.WEST, Sep, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, Sep, -5, SpringLayout.EAST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, Pane, -5, SpringLayout.NORTH, Sep);
        Lay.putConstraint(SpringLayout.NORTH, Pane, 5, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, Pane, 5, SpringLayout.WEST, getContentPane());
        if (layout == DEFAULT_STYLE) {
            InnerPane.setLayout(new WrapLayout(FlowLayout.CENTER, 6, 6));

            Lay.putConstraint(SpringLayout.EAST, Pane, -5, SpringLayout.EAST, getContentPane());
        } else if (layout == SPECIAL_AREA_STYLE) {
            InnerPane.setLayout(new BoxLayout(InnerPane, BoxLayout.Y_AXIS));
            InnerPane.add(Box.createRigidArea(new Dimension(0, 4)));

            Lay.putConstraint(SpringLayout.EAST, Pane, -5, SpringLayout.HORIZONTAL_CENTER, getContentPane());
            SpecialPane = new JPanel();
            SpecialPane.setLayout(SpecialPaneLay);

            Lay.putConstraint(SpringLayout.SOUTH, SpecialPane, 0, SpringLayout.NORTH, Sep);
            Lay.putConstraint(SpringLayout.NORTH, SpecialPane, 0, SpringLayout.NORTH, getContentPane());
            Lay.putConstraint(SpringLayout.WEST, SpecialPane, 0, SpringLayout.EAST, Pane); // only one not copied
            Lay.putConstraint(SpringLayout.EAST, SpecialPane, 0, SpringLayout.EAST, getContentPane());

            add(SpecialPane);
        }

        Pane.getVerticalScrollBar().setUnitIncrement(16);

        add(CreateButton);
        add(DraftButton);
        add(CancelButton);
        add(Sep);
        add(Pane);
        setModal(true);
    }

    public static RElementEditingScreen getElementsCreationScreen(ElementSource<?> src, Window Parent,
            ElementCreationListener parent2, String Workspace)
            throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (src.getClass().equals(SourceBiomeElement.class)) {
            RElementEditingScreen screen = new RElementEditingScreen(Parent, SourceBiomeElement.getDetails().Name, src,
                    src.getSerilizedClass(),
                    parent2);
            SpringLayout lay = new SpringLayout();
            screen.InnerPane.setLayout(lay);

            RElementValue elementnameVal = new RElementValue(screen, String.class,
                    new FieldFilters.FileNameLikeStringFilter(),
                    "ElementName", "Element Name", false, src.getSerilizedClass(), src.getSerilized(), Workspace);
            RElementValue idVal = new RElementValue(screen, String.class, new FieldFilters.IDStringFilter(),
                    "BiomeID", "Biome ID", false, src.getSerilizedClass(), src.getSerilized(), Workspace);
            RElementValue compsVal = new RElementValue(screen, HashMap.class, new FieldFilters.IDStringFilter(),
                    "Comps", "Biome Components", false, src.getSerilizedClass(), src.getSerilized(), Workspace);

            screen.addField(elementnameVal);
            screen.addField(idVal);
            screen.addField(compsVal);

            lay.putConstraint(SpringLayout.WEST, elementnameVal, 5, SpringLayout.WEST, screen.InnerPane);
            lay.putConstraint(SpringLayout.NORTH, elementnameVal, 5, SpringLayout.NORTH, screen.InnerPane);

            lay.putConstraint(SpringLayout.NORTH, idVal, 0, SpringLayout.NORTH, elementnameVal);
            lay.putConstraint(SpringLayout.EAST, idVal, -5, SpringLayout.EAST, screen.InnerPane);

            lay.putConstraint(SpringLayout.EAST, compsVal, -5, SpringLayout.EAST, screen.InnerPane);
            lay.putConstraint(SpringLayout.WEST, compsVal, 5, SpringLayout.WEST, screen.InnerPane);
            lay.putConstraint(SpringLayout.SOUTH, compsVal, -5, SpringLayout.SOUTH, screen.InnerPane);
            lay.putConstraint(SpringLayout.NORTH, compsVal, 5, SpringLayout.SOUTH, elementnameVal);

            return screen;
        } else if (src.getClass().equals(SourceScriptElement.class)) {
            JOptionPane.showMessageDialog(Parent, "Sadly, scripts are unavailable since a2.0.");
            return null;
            /*
             * ScriptFile serilized = (ScriptFile) src.getSerilized(); // put this here so
             * copying is easier
             * 
             * RElementValue elementName = new RElementValue(Parent, String.class,
             * new FieldFilters.FileNameLikeStringFilter(),
             * "ElementName", "Element Name", false, src.getSerilizedClass(), serilized,
             * Workspace);
             * elementName.setMaximumSize(new Dimension(300, 40));
             * RElementValue scriptName = new RElementValue(Parent, String.class, new
             * FieldFilters.IDStringFilter(),
             * "ScriptName", "Script Name", false, src.getSerilizedClass(), serilized,
             * Workspace);
             * scriptName.setMaximumSize(new Dimension(300, 40));
             * 
             * JTextArea preview = new JTextArea();
             * preview.setEditable(false);
             * 
             * JLabel loading = new JLabel("Loading...");
             * 
             * JPanel rightStuff = new JPanel();
             * JPanel toprightStuff = new JPanel();
             * 
             * toprightStuff.setLayout(new BoxLayout(toprightStuff, BoxLayout.X_AXIS));
             * rightStuff.setLayout(new BoxLayout(rightStuff, BoxLayout.Y_AXIS));
             * 
             * rightStuff.add(preview);
             * rightStuff.add(toprightStuff);
             * 
             * toprightStuff.add(elementName);
             * toprightStuff.add(Box.createHorizontalStrut(5));
             * toprightStuff.add(scriptName);
             * 
             * RElementEditingScreen frame = new RElementEditingScreen(Parent, "Item", src,
             * src.getSerilizedClass(),
             * parent2,
             * RElementEditingScreen.DEFAULT_STYLE);
             * 
             * frame.CreateButton.setEnabled(false);
             * JButton DebugButton = (JButton) frame.add(new JButton("Debug"));
             * frame.Lay.putConstraint(SpringLayout.WEST, DebugButton, 10,
             * SpringLayout.EAST, frame.CancelButton);
             * frame.Lay.putConstraint(SpringLayout.SOUTH, DebugButton, 0,
             * SpringLayout.SOUTH, frame.CancelButton);
             * frame.DraftButton.setEnabled(false);
             * 
             * RBlockly rblockly = new RBlockly(preview, serilized != null ?
             * serilized.Content : null, () -> {
             * frame.CreateButton.setEnabled(true);
             * frame.DraftButton.setEnabled(true);
             * });
             * 
             * DebugButton.addActionListener(ac -> {
             * rblockly.execute("fixRendering(150)");
             * });
             * 
             * frame.setCustomCreateFunction(new CustomCreateFunction() {
             * 
             * @Override
             * public void onCreate(RElementEditingScreen Sindow, ElementCreationListener
             * Listener,
             * boolean isDraft) {
             * try {
             * Platform.runLater(() -> {
             * ScriptFile serilized = (ScriptFile) src.getSerilized(); // put this here so
             * copying is
             * // easier
             * if (serilized == null)
             * serilized = new ScriptFile();
             * 
             * serilized.ElementName = elementName.getValue().toString();
             * serilized.ScriptName = scriptName.getValue().toString();
             * serilized.Content = rblockly.getJson();
             * serilized.setDraft(isDraft);
             * 
             * Listener.onElementCreate(src); // create
             * Sindow.dispose();
             * });
             * } catch (Exception ex) {
             * java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
             * "Exception thrown",
             * ex);
             * ErrorShower.showError(Sindow, "Failed to create ElementSource",
             * "Source Creation Error", ex);
             * }
             * }
             * 
             * }).addVaildations(elementName, scriptName);
             * SpringLayout lay = new SpringLayout();
             * 
             * lay.putConstraint(SpringLayout.NORTH, rightStuff, 0, SpringLayout.NORTH,
             * frame.InnerPane);
             * lay.putConstraint(SpringLayout.SOUTH, rightStuff, 0, SpringLayout.SOUTH,
             * frame.InnerPane);
             * lay.putConstraint(SpringLayout.EAST, rightStuff, 0, SpringLayout.EAST,
             * frame.InnerPane);
             * lay.putConstraint(SpringLayout.WEST, rightStuff, 855, SpringLayout.WEST,
             * frame.InnerPane);
             * 
             * lay.putConstraint(SpringLayout.NORTH, rblockly, 0, SpringLayout.NORTH,
             * frame.InnerPane);
             * lay.putConstraint(SpringLayout.SOUTH, rblockly, 0, SpringLayout.SOUTH,
             * frame.InnerPane);
             * lay.putConstraint(SpringLayout.WEST, rblockly, 0, SpringLayout.WEST,
             * frame.InnerPane);
             * 
             * frame.InnerPane.setLayout(lay);
             * 
             * frame.InnerPane.add(rblockly);
             * frame.InnerPane.add(rightStuff);
             * frame.InnerPane.add(loading);
             * 
             * frame.setSize(new Dimension(1500, 800));
             * frame.setLocation(ImageUtilites.getScreenCenter(frame));
             * 
             * frame.addWindowListener(new WindowAdapter() {
             * 
             * @SuppressWarnings("unused")
             * public void windowClosed(WindowEvent e) {
             * rblockly.dispose();
             * }
             * });
             * 
             * return frame;
             */
        } else if (src.getClass().equals(SourceRecipeElement.class)) {
            try {
                RecipeFile serilized = (RecipeFile) ((SourceRecipeElement) src).getSerilized();
                RElementEditingScreen frame = new RElementEditingScreen(Parent, "Item", src, src.getSerilizedClass(),
                        parent2,
                        RElementEditingScreen.DEFAULT_STYLE);

                SpringLayout Layout = new SpringLayout();

                RElementValue ElementName = new RElementValue(Parent, String.class,
                        new FieldFilters.FileNameLikeStringFilter(),
                        "ElementName", "Element Name", false, src.getSerilizedClass(), serilized, Workspace);

                RElementValue RecipeID = new RElementValue(Parent, String.class,
                        new FieldFilters.IDStringFilter(),
                        "RecipeID", "Recipe ID", false, src.getSerilizedClass(), serilized, Workspace);

                frame.InnerPane.setLayout(Layout);
                RItemValue grid = new RItemValue(Workspace, RItemValue.Type.CraftingTable, true);
                if (serilized != null) {
                    switch (serilized.recipeType) {
                        case RecipeType.Shaped:
                            grid.setShapedRecipe(Parent, new ShapedOutput(serilized), Workspace);
                            break;

                        default:
                            serilized.ShapelessIngredients.forEach(item -> {
                                try {
                                    grid.setButtonToItem(serilized.ShapelessIngredients.indexOf(item),
                                            ReturnItemInfo.getItemById(item.item, Workspace));
                                } catch (WrongItemValueTypeException | NameNotFoundException
                                        | IncorrectWorkspaceException e1) {
                                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
                                            "Exception thrown", e1);
                                }
                            });
                            break;
                    }
                    grid.setShapedRecipe(Parent, new ShapedOutput(serilized), Workspace);
                }

                RItemValue outputSlot = new RItemValue(Workspace, RItemValue.Type.Single, true);
                if (serilized != null) {
                    outputSlot.setButtonToItem(0, ReturnItemInfo.getItemById(serilized.Result.item, Workspace));
                }

                RItemValue unlockItems = new RItemValue(Workspace, RItemValue.Type.ListOfItems, true);
                if (serilized != null) {
                    unlockItems.addListElements(Workspace, ReturnItemInfo
                            .fromUnlockCondition(serilized.UnlockConditions, Workspace).toArray(new ReturnItemInfo[0]));
                }

                RItemValue extraResults = new RItemValue(Workspace, RItemValue.Type.ListOfItems, false);
                if (serilized != null) {
                    extraResults.addListElements(Workspace,
                            ReturnItemInfo.fromRecipeItem(serilized.ExtraResults, Workspace)
                                    .toArray(new ReturnItemInfo[0]));
                }

                JLabel TypeDropdownText = new JLabel("Recipe Type");
                JComboBox<RecipeType> TypeDropdown = new JComboBox<RecipeType>(RecipeType.values());
                if (serilized != null) {
                    TypeDropdown.setSelectedItem(serilized.recipeType);
                }

                JLabel arrow = new JLabel(new ImageIcon(src.getClass().getResource("/ui/Arrow.png")));

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

                Layout.putConstraint(SpringLayout.VERTICAL_CENTER, grid, 0, SpringLayout.VERTICAL_CENTER,
                        frame.InnerPane);
                Layout.putConstraint(SpringLayout.EAST, grid, -40, SpringLayout.WEST, arrow);

                Layout.putConstraint(SpringLayout.VERTICAL_CENTER, outputSlot, 0, SpringLayout.VERTICAL_CENTER,
                        arrow);
                Layout.putConstraint(SpringLayout.WEST, outputSlot, 50, SpringLayout.EAST, arrow);

                frame.setCustomCreateFunction(new CustomCreateFunction() {

                    @Override
                    public void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener,
                            boolean isDraft) {
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

                            if (isDraft) {
                                Sindow.setVisible(false);
                                Listener.onElementDraft(new SourceRecipeElement(building));
                            } else {
                                Sindow.setVisible(false);
                                Listener.onElementCreate(new SourceRecipeElement(building));
                            }
                        } catch (Exception e) {
                            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown",
                                    e);
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
                        RecipeFile Serilized = (RecipeFile) ((SourceRecipeElement) src).getSerilized();

                        try {
                            ShapedOutput shaped = grid.getShapedRecipe();
                            Serilized.ShapedPattern = shaped.pattern;
                            Serilized.ShapedKey = shaped.key;
                            if (!extraResults.getItems().isEmpty())
                                Serilized.ExtraResults = extraResults.getItems();

                            Serilized.ShapelessIngredients = grid.getItems();
                        } catch (WrongItemValueTypeException e1) {
                            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown",
                                    e1);
                        }

                        switch (TypeDropdown.getSelectedItem()) {
                            case RecipeType.Shaped:
                                arrow.setIcon(new ImageIcon(getClass().getResource("/ui/Arrow.png")));
                                extraResults.setVisible(true);
                                try {
                                    grid.empty();
                                } catch (WrongItemValueTypeException e1) {
                                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
                                            "Exception thrown", e1);
                                }
                                if (Serilized != null) {
                                    try {
                                        grid.setShapedRecipe(Parent, new ShapedOutput(Serilized), Workspace);
                                    } catch (WrongItemValueTypeException e1) {
                                        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
                                                "Exception thrown", e1);
                                    }
                                }
                                break;

                            case RecipeType.Shapeless:
                                arrow.setIcon(new ImageIcon(getClass().getResource("/ui/ArrowShapless.png")));
                                extraResults.setVisible(false);
                                try {
                                    grid.empty();
                                } catch (WrongItemValueTypeException e1) {
                                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
                                            "Exception thrown", e1);
                                }
                                if (Serilized != null) {
                                    Serilized.ShapelessIngredients.forEach(item -> {
                                        try {
                                            grid.setButtonToItem(Serilized.ShapelessIngredients.indexOf(item),
                                                    ReturnItemInfo.getItemById(item.item, Workspace));
                                        } catch (WrongItemValueTypeException | NameNotFoundException
                                                | IncorrectWorkspaceException e1) {
                                            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
                                                    "Exception thrown", e1);
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
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
        } else if (src.getClass().equals(SourceFoodElement.class)) {
            FoodFile serilized = (FoodFile) ((SourceFoodElement) src).getSerilized();
            RElementEditingScreen frame = new RElementEditingScreen(Parent, "Food", src, src.getSerilizedClass(),
                    parent2,
                    RElementEditingScreen.DEFAULT_STYLE);

            for (Field field : src.getSerilizedClass().getFields()) { // try to get fields
                if (field.getType().equals(CreationScreenSeperator.class)) {
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
                                src.getSerilizedClass(),
                                serilized,
                                Workspace);
                        frame.addField(rev);
                    }

                } catch (Exception e) {
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    ErrorShower.showError(frame, "Failed to create a field for " + field.getName(), "Field Error", e);
                }
            }

            RItemValue turnsInto = new RItemValue(Workspace, RItemValue.Type.Single, false);
            if (serilized != null && serilized.EatingTurnsInto != null) {
                try {
                    turnsInto.setButtonToItem(0, ReturnItemInfo.getItemById(serilized.EatingTurnsInto, Workspace));
                } catch (NameNotFoundException | WrongItemValueTypeException | IncorrectWorkspaceException e) {
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
            frame.setCustomCreateFunction(new CustomCreateFunction() {

                /**
                 * i copied the default one, now ill add that one item value
                 */
                @Override
                public void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener, boolean isDraft) {

                    try {
                        FoodFile creating = serilized == null ? new FoodFile() : serilized;

                        for (ValidatableValue validatable : frame.Fields) { // add the fields
                            if (validatable instanceof RElementValue elementValue) {
                                if (!elementValue.getOptionallyEnabled()) { // if its not enabled, continue
                                    continue;
                                } else
                                    try {
                                        src.getSerilizedClass().getField(elementValue.getTarget()).set(creating,
                                                elementValue.getValue());
                                        // try to set field ^
                                    } catch (Exception e) {
                                        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE,
                                                "Exception thrown", e);
                                        ErrorShower.showError(null, "Failed to change a field; continuing",
                                                e.getMessage(),
                                                e);
                                        continue;
                                    }
                            }
                        }
                        if (!turnsInto.getItems().isEmpty())
                            creating.EatingTurnsInto = turnsInto.getItems().get(0).item;

                        creating.setDraft(isDraft);

                        Listener.onElementCreate(new SourceFoodElement(serilized)); // create
                        Sindow.dispose();
                    } catch (Exception ex) {
                        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown",
                                ex);
                        ErrorShower.showError(Parent, "Failed to create ElementSource",
                                "Source Creation Error", ex);
                    }
                }

            });

            return frame;
        } else {
            // do the automatic creation
            var frame = new RElementEditingScreen(Parent,
                    ((ElementDetails) src.getClass().getMethod("getDetails").invoke(null)).Name, src,
                    src.getSerilizedClass(), parent2,
                    RElementEditingScreen.SPECIAL_AREA_STYLE);
            List<Field> fields = new ArrayList<Field>(List.of(src.getSerilizedClass().getFields()));
            fields.sort((f1, f2) -> {
                int o1 = f1.isAnnotationPresent(RAnnotation.Order.class)
                        ? f1.getAnnotation(RAnnotation.Order.class).value()
                        : -1;
                int o2 = f2.isAnnotationPresent(RAnnotation.Order.class)
                        ? f2.getAnnotation(RAnnotation.Order.class).value()
                        : -1;
                return Integer.compare(o1, o2);
            });
            for (Field field : fields) { // try to get fields
                try { // then add them
                    RElementValue rev = null;
                    var details = field.getAnnotation(RAnnotation.FieldDetails.class);
                    if (field.getAnnotation(RAnnotation.UneditableByCreation.class) == null) {
                        if (src.getSerilized() != null) // create field with a file already there
                            rev = new RElementValue(Parent, field.getType(),
                                    details.Filter() != null ? details.Filter().getConstructor().newInstance()
                                            // if no filter, dont add one
                                            : field.getType() == String.class ? new RegularStringFilter() : null,
                                    // if its a string however, add a basic filter
                                    field.getName(), // target
                                    details.displayName(), // display name
                                    details.Optional(),
                                    src.getSerilizedClass(),
                                    src.getSerilized(),
                                    Workspace);
                        else // create file without anything there
                             // ---------------------------------------------------------------------
                            rev = new RElementValue(Parent, field.getType(),
                                    details.Filter() != null ? details.Filter().getConstructor().newInstance()
                                            : field.getType() == String.class ? new RegularStringFilter() : null,
                                    field.getName(), // target
                                    details.displayName(), // display name
                                    details.Optional(),
                                    src.getSerilizedClass(),
                                    Workspace);
                        if (field.getAnnotation(RAnnotation.SpecialField.class) != null)
                            frame.setSpecialField(rev);
                        else
                            frame.addField(rev);
                    }

                } catch (Exception e) {
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    ErrorShower.showError(Parent, "Failed to create a field for " + field.getName(), "Field Error", e);
                }
            }

            return frame;
        }
    }

    public void addField(RElementValue Field) {
        InnerPane.add(Field);
        InnerPane.add(Box.createRigidArea(new Dimension(0, 4)));
        Fields.add(Field);
        if (Field.Required)
            RequiredFields.add(Field);
    }

    // make sure to meet at mercedes
    public void setSpecialField(RElementValue Field) throws IllegalAccessError {
        if (SpecialPane == null)
            throw new IllegalAccessError("This Element Creation Screen was not set to be the special layout.");
        SpecialPane.add(Field);
        // put field all over
        SpecialPaneLay.putConstraint(SpringLayout.SOUTH, Field, 0, SpringLayout.SOUTH, SpecialPane);
        SpecialPaneLay.putConstraint(SpringLayout.NORTH, Field, 0, SpringLayout.NORTH, SpecialPane);
        SpecialPaneLay.putConstraint(SpringLayout.WEST, Field, 0, SpringLayout.WEST, SpecialPane);
        SpecialPaneLay.putConstraint(SpringLayout.EAST, Field, 0, SpringLayout.EAST, SpecialPane);

        Fields.add(Field);
        if (Field.Required)
            RequiredFields.add(Field);
    }

    public List<ValidatableValue> checkForErrors(boolean strict) {
        List<ValidatableValue> IncorrectFields = new ArrayList<ValidatableValue>();
        Logger.getGlobal().info("--------------------- CHECKING FOR ERRORS-----------------------");
        for (ValidatableValue validatable : Fields) {
            if (!validatable.valid(strict))
                IncorrectFields.add(validatable);
        }
        if (IncorrectFields.size() != 0) {
            this.IncorrectFields = IncorrectFields;
            return IncorrectFields;
        } else {
            return null;
        }
    }

    private void create(boolean isDraft) {
        try { // handle if there is no constructor
            var workingClass = ((ElementFile<?>) SourceClass.getConstructor().newInstance()); // make new elementfile
            for (ValidatableValue validatable : Fields) { // add the fields
                if (validatable instanceof RElementValue) {
                    if (!((RElementValue) validatable).getOptionallyEnabled()) // if its not enabled, continue
                    {
                        continue;
                    } else
                        try {
                            SourceClass.getField(((RElementValue) validatable).getTarget()).set(workingClass,
                                    ((RElementValue) validatable).getValue());
                            // try to set field ^
                        } catch (Exception e) {
                            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown",
                                    e);
                            ErrorShower.showError(null, "Failed to change a field; continuing", e.getMessage(), e);
                            continue;
                        }
                }

            }

            workingClass.setDraft(isDraft);

            Listener.onElementCreate(SourceElementClass.getConstructor(SourceClass).newInstance(workingClass)); // create
            this.dispose();
        } catch (Exception ex) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
            ErrorShower.showError((Frame) getParent(), "Failed to create ElementSource",
                    "Source Creation Error", ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var action = e.getActionCommand();
        if (action.equals("create")) { // create, check for errors, get user to solve them, ready for build
            if (checkForErrors(true) == null) {
                if (createFunction == null)
                    create(false);
                else
                    createFunction.onCreate(this, Listener, false);
            } else { // show errored things
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (ValidatableValue EV : IncorrectFields) {
                    builder.append("<li>" + EV.getName() + ": " + EV.getProblemMessage() + "</li>");
                }

                JOptionPane.showMessageDialog(this, builder.toString(), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (action.equals("draft")) { // drafting, check if nessesary fields are entered
            if (checkForErrors(false) == null) {
                if (createFunction == null)
                    create(true);
                else
                    createFunction.onCreate(this, Listener, true);
            } else { // show errored things
                // pov: you thought something was going to complicated, but you didnt need to
                // search anything up VVVVVVV
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (ValidatableValue EV : IncorrectFields) {
                    builder.append("<li>" + EV.getName() + ": " + EV.getProblemMessage() + "</li>");
                }

                JOptionPane.showMessageDialog(this, builder.toString(), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (action.equals("cancel")) {
            Listener.onElementCancel();
            this.dispose();
        } else {
            var ex = new Exception("That button dont exist! man i forgot how good dark tranquility is");
            ErrorShower.showError((Frame) getParent(),
                    "woah mate, button dont fit, dont fit, button, it dont fit, wont fit", "I did an oppsie", ex);

            throw new IllegalAccessError();
        }
    }
}
