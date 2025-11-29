package fn10.bedrockr.windows.componets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.border.LineBorder;

import java.io.File;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.rendering.RenderHandler;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.MapUtilities;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RFonts;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.MapFieldSelectables;
import fn10.bedrockr.utils.RAnnotation.NumberRange;
import fn10.bedrockr.utils.RAnnotation.ResourcePackResourceType;
import fn10.bedrockr.utils.RAnnotation.StringDropdownField;
import fn10.bedrockr.windows.RMapValueAddingSelector;
import fn10.bedrockr.windows.RTextureAddingSelector;
import fn10.bedrockr.windows.interfaces.ValidatableValue;

/**
 * The main Components for <code>RElementCreationScreen</code>.
 * This class includes lots of useful tools for making a field for a user to
 * edit, and to save certain <code>java.reflect.Field</code>s.
 */
public class RElementValue extends JPanel implements ValidatableValue {

    private SpringLayout Lay = new SpringLayout();
    private JLabel Name = new JLabel();
    public JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));
    public Component Input = null;
    private JCheckBox EnableDis = new JCheckBox();

    private String Target = "";
    private FieldFilter Filter;
    private Class<?> InputType;
    private Class<?> SourceFileClass;
    private final String WorkspaceName;

    // componets used for uuid of block texture
    private JLabel NameBlock;
    private JLabel IDBlock;
    private JLabel TypeBlock;
    private JButton AddButtonBlock;
    private JButton PreviewButtonBlock;
    private JButton SelectButtonBlock;
    private JLabel IconBlock;
    // item texture
    private JLabel NameItem;
    private JLabel IDItem;
    private JLabel TypeItem;
    private JButton AddButtonItem;
    private JButton SelectButtonItem;
    private JLabel IconItem;

    public Window parentFrame;

    protected JPanel HashMapInnerPane = new JPanel();
    protected JButton HashMapAdd = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/New.png")));

    public boolean Required = false;
    public String Problem = "No problem here!";

    public RElementValue(Window parentFrame, Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> ElementFileClass,
            String WorkspaceName) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        this(parentFrame, InputType, Filter, TargetField, DisplayName, Optional, ElementFileClass,
                ElementFileClass != null ? ((ElementFile<?>) ElementFileClass.getConstructor().newInstance()) : null,
                true,
                WorkspaceName);
    }

    public RElementValue(Window frame, Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile<?> TargetFile,
            String WorkspaceName) {
        this(frame, InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass, TargetFile,
                TargetFile == null,
                WorkspaceName);
    }

    @SuppressWarnings({ "unchecked", "null" })
    protected RElementValue(Window parentFrame, Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile<?> TargetFile,
            boolean FromEmpty,
            String WorkspaceName) {
        super();

        this.parentFrame = parentFrame;
        this.Target = TargetField;
        this.Required = !Optional;
        this.Filter = Filter;
        this.SourceFileClass = SourceFileClass;
        this.InputType = InputType;
        this.WorkspaceName = WorkspaceName;

        final Dimension Size;
        if (Map.class.isAssignableFrom(InputType) || List.class.isAssignableFrom(InputType))
            Size = new Dimension(350, 150);
        else
            Size = new Dimension(350, 40);

        setMaximumSize(Size);
        setPreferredSize(Size);
        setBorder(new LineBorder(getBackground()));
        setLayout(Lay);

        Help.putClientProperty("JButton.buttonType", "help");

        Field field;
        try { // try to get field
            if (SourceFileClass != null) {
                field = SourceFileClass.getField(TargetField);
            } else {
                field = null;
            }
        } catch (Exception e) {
            if (!FromEmpty)
                if (TargetFile.getDraft())
                    return;

            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(parentFrame,
                    "Failed to get field (does the passed ElementFile match the ElementSource?)",
                    DisplayName, e);
            return;
        }

        // dont do this if its set manually
        if (Input == null)
            // do corrisponding actions depending on the type
            try {
                if (List.class.isAssignableFrom(InputType)) {
                    /*
                     * im just stealing most of the hash map stuff, since it is basicly already a
                     * list view.
                     */
                    Input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    ((JScrollPane) Input).getVerticalScrollBar().setUnitIncrement(18);

                    /*
                     * HashMapInnerScroll is the pane that is inside input, IT IS A JPANEL, NOT A
                     * JSCROLLPANE!!!!
                     */

                    // do things to the panels
                    HashMapInnerPane.setLayout(new BoxLayout(HashMapInnerPane, BoxLayout.Y_AXIS));
                    ((JScrollPane) Input).setBorder(new LineBorder(Color.DARK_GRAY));
                    Input.setBackground(getBackground().brighter());
                    // get the RMapProvider

                    final Class<?> genericType;
                    if (!InputType.isArray()) {
                        if (field == null) {
                            return;
                        }
                        if (field.getGenericType() instanceof java.lang.reflect.ParameterizedType) {
                            java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) field
                                    .getGenericType();
                            genericType = (Class<?>) pt.getActualTypeArguments()[0];
                        } else
                            genericType = null;
                        if (genericType == null) {
                            throw new NullPointerException("This list doesnt have a type.");
                        }
                    } else {
                        genericType = null;
                    }

                    if (!FromEmpty && field != null) {
                        try {
                            if (InputType.isArray()) {
                                for (Object entry : (Object[]) field.get(TargetFile)) {
                                    RElementValue toAdd = new RElementValue(parentFrame, InputType.getComponentType(),
                                            Filter,
                                            null, "",
                                            false, null, WorkspaceName);
                                    toAdd.setValue(entry);

                                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                                    HashMapInnerPane.add(toAdd);
                                }
                            } else if (genericType != null) {
                                for (Object entry : (List<?>) field.get(TargetFile)) {
                                    RElementValue toAdd = new RElementValue(parentFrame, genericType, Filter,
                                            null, "",
                                            false, null, WorkspaceName);
                                    toAdd.setValue(entry);

                                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                                    HashMapInnerPane.add(toAdd);
                                }
                            }
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                            ErrorShower.showError(parentFrame, e.getMessage(), WorkspaceName, e);
                        }
                    }

                    final StringDropdownField anno = field.getAnnotation(RAnnotation.StringDropdownField.class);
                    // add the button
                    HashMapAdd.addActionListener((e) -> {
                        try {
                            RElementValue toAdd;
                            if (InputType.isArray()) {
                                // Launcher.LOG.info("make an array value element with class: "
                                // + InputType.getComponentType().getCanonicalName());
                                toAdd = new RElementValue(parentFrame, InputType.getComponentType(), Filter, null, "",
                                        false,
                                        null, WorkspaceName);
                            } else if (genericType != null) {

                                // Launcher.LOG.info("make an list value element with class: "
                                // + genericType.getCanonicalName());
                                toAdd = new RElementValue(parentFrame, genericType, Filter, null,
                                        "",
                                        false,
                                        null, WorkspaceName);
                            } else {
                                return;
                            }

                            if (anno != null) {
                                toAdd.remove(toAdd.Input);
                                JComboBox<String> newInput;
                                if (anno.value()[0].equals("_VANILLABIOMES"))
                                    newInput = new JComboBox<String>(SourceBiomeElement.getVanillaBiomeNames());
                                else
                                    newInput = new JComboBox<String>(anno.value());

                                toAdd.Lay.putConstraint(SpringLayout.WEST, newInput, 3, SpringLayout.EAST, toAdd.Name);
                                toAdd.Lay.putConstraint(SpringLayout.NORTH, newInput, 3, SpringLayout.NORTH, toAdd);
                                toAdd.Lay.putConstraint(SpringLayout.SOUTH, newInput, -3, SpringLayout.SOUTH, toAdd);
                                toAdd.Lay.putConstraint(SpringLayout.EAST, newInput, -3, SpringLayout.WEST, toAdd.Help);
                                toAdd.add(newInput);
                                toAdd.Input = newInput;

                                if (anno.strict()) {
                                    ((JComboBox<String>) newInput).setEditable(false);
                                    ((JComboBox<String>) newInput).setSelectedIndex(0);
                                }
                            }

                            toAdd.setAlignmentX(0.5f);

                            HashMapInnerPane.add(Box.createVerticalStrut(10));
                            HashMapInnerPane.add(toAdd);

                            HashMapInnerPane.revalidate();
                            HashMapInnerPane.repaint();

                        } catch (Exception e1) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                            ErrorShower.showError(parentFrame, "Failed to add a map element.", e1.getMessage(), e1);
                        }
                    });
                    add(HashMapAdd);

                    Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, Input);
                    Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
                } else if (Boolean.class.isAssignableFrom(InputType) || boolean.class.isAssignableFrom(InputType)) { // if
                                                                                                                     // bool,
                                                                                                                     // its
                                                                                                                     // dropdown
                    String[] vals = { "true", "false" };
                    Input = new JComboBox<String>(vals);
                    try {
                        if (!FromEmpty)
                            ((JComboBox<String>) Input).setSelectedIndex((boolean) field.get(TargetFile)
                                    ? 0 // convert bool to index
                                    : 1);
                        else
                            ((JComboBox<String>) Input).setSelectedItem("false");
                    } catch (Exception e) {

                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                        if (!FromEmpty)
                            if (TargetFile.getDraft())
                                return;
                        ErrorShower.showError(parentFrame,
                                "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                DisplayName, e);
                    }
                } else if (String.class.isAssignableFrom(InputType)) { // if string, do this
                    // if normal do this

                    final StringDropdownField anno;
                    if (field != null) {
                        anno = field.getAnnotation(RAnnotation.StringDropdownField.class);
                    } else {
                        anno = null;
                    }
                    Input = new JTextField();
                    if (anno == null && field != null) { // normal string
                        Input = new JTextField();
                        try {
                            if (!FromEmpty)
                                ((JTextField) Input).setText(((String) field.get(TargetFile))); // set text to string in
                                                                                                // field,
                                                                                                // if it is editing
                        } catch (Exception e) {
                            if (!FromEmpty)
                                if (TargetFile.getDraft())
                                    return;
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                            ErrorShower.showError(parentFrame,
                                    "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                    DisplayName, e);
                        }
                    } else if (anno != null && field != null) { // dropdown string
                        if (anno.value()[0].equals("_VANILLABIOMES"))
                            Input = new JComboBox<String>(SourceBiomeElement.getVanillaBiomeNames());
                        else
                            Input = new JComboBox<String>(anno.value());
                        try {
                            // if its strict, dont make it editable
                            ((JComboBox<String>) Input).setEditable(!anno.strict());
                            if (!FromEmpty) {
                                ((JComboBox<String>) Input).setSelectedItem(field.get(TargetFile));
                            } else {
                                if (anno.value()[0].equals("_VANILLABIOMES"))
                                    ((JComboBox<String>) Input)
                                            .setSelectedItem(SourceBiomeElement.getVanillaBiomeNames()[0]);
                                else
                                    ((JComboBox<String>) Input).setSelectedItem(anno.value()[0]);
                            }
                        } catch (Exception e) {

                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                            if (!FromEmpty)
                                if (TargetFile.getDraft())
                                    return;
                            ErrorShower.showError(parentFrame,
                                    "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                    DisplayName, e);
                        }
                    }
                } else if (Map.class.isAssignableFrom(InputType)) {
                    Input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    ((JScrollPane) Input).getVerticalScrollBar().setUnitIncrement(18);
                    /*
                     * HashMapInnerScroll is the pane that is inside input, IT IS A JPANEL, NOT A
                     * JSCROLLPANE!!!!
                     */

                    // do things to the panels
                    HashMapInnerPane.setLayout(new BoxLayout(HashMapInnerPane, BoxLayout.Y_AXIS));
                    ((JScrollPane) Input).setBorder(new LineBorder(Color.DARK_GRAY));
                    Input.setBackground(getBackground().brighter());
                    // get the RMapProvider

                    // finally, get the annotation after getting the field
                    final MapFieldSelectables anno;
                    if (field != null) {
                        anno = field.getAnnotation(RAnnotation.MapFieldSelectables.class);
                    } else {
                        anno = null;
                    }
                    List<RMapElement> picked = new ArrayList<RMapElement>();
                    if (!FromEmpty && field != null) {
                        try {
                            for (Map.Entry<String, Object> entry : ((Map<String, Object>) field.get(TargetFile))
                                    .entrySet()) {
                                RElementMapValue ToAdd = new RElementMapValue(parentFrame,
                                        RMapElement.LookupMap.get(entry.getKey()));
                                ToAdd.setVal(entry.getValue());

                                picked.add(RMapElement.LookupMap.get(entry.getKey()));

                                HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                                HashMapInnerPane.add(ToAdd);
                            }
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                            ErrorShower.showError(parentFrame, e.getMessage(), WorkspaceName, e);
                        }
                    }

                    // add the button
                    HashMapAdd.addActionListener((e) -> {
                        if (field != null && anno != null)
                            try {
                                RMapElement select = RMapValueAddingSelector.openSelector(parentFrame,
                                        ((RMapElement[]) anno.value().getMethod("getPickable")
                                                .invoke(anno.value().getConstructor().newInstance())),
                                        picked);
                                if (select == null)
                                    return;
                                var toAdd = new RElementMapValue(parentFrame, select);
                                toAdd.setSize(HashMapInnerPane.getWidth() - 5,
                                        Double.valueOf(toAdd.getSize().getHeight()).intValue());
                                toAdd.setAlignmentX(0.5f);

                                picked.add(select);

                                HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                                HashMapInnerPane.add(toAdd);

                                // finish, and why this wasnt working before
                                HashMapInnerPane.revalidate();
                                HashMapInnerPane.repaint();

                            } catch (Exception e1) {
                                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                                ErrorShower.showError(parentFrame, "Failed to add a map element.", e1.getMessage(), e1);
                            }
                    });
                    add(HashMapAdd);

                    Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, Input);
                    Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
                } else if (Integer.class.isAssignableFrom(InputType) || int.class.isAssignableFrom(InputType)) { // int
                    final NumberRange anno;
                    if (field != null) {
                        anno = field.getAnnotation(RAnnotation.NumberRange.class);
                    } else {
                        anno = null;
                    }
                    Input = new JSpinner(new SpinnerNumberModel(0, anno != null ? (int) anno.min() : Integer.MIN_VALUE,
                            anno != null ? (int) anno.max() : Integer.MAX_VALUE, 1));

                    if (!FromEmpty)
                        ((JSpinner) Input).setValue(((Integer) field.get(TargetFile)));

                } else if (Float.class.isAssignableFrom(InputType) || float.class.isAssignableFrom(InputType)) { // int
                    final NumberRange anno;
                    if (field != null) {
                        anno = field.getAnnotation(RAnnotation.NumberRange.class);
                    } else {
                        anno = null;
                    }

                    Input = new JSpinner(new SpinnerNumberModel(0f, anno != null ? anno.min() : -Float.MAX_VALUE,
                            anno != null ? anno.max() : Float.MAX_VALUE, 0.01f));
                    if (!FromEmpty)
                        ((JSpinner) Input).setValue(((Float) field.get(TargetFile)));
                } else if (UUID.class.isAssignableFrom(InputType)) { // resource

                    final ResourcePackResourceType anno;
                    if (field != null) {
                        anno = field.getAnnotation(RAnnotation.ResourcePackResourceType.class);
                    } else {
                        anno = null;
                    }
                    if (anno == null)
                        return;
                    switch (anno.value()) {
                        case ResourceFile.ITEM_TEXTURE: // if its an item texture
                            SpringLayout layout = new SpringLayout();
                            Input = new JPanel();
                            Input.setName("null");
                            ((JPanel) Input).setBorder(getBorder());
                            ((JPanel) Input).setLayout(layout);

                            NameItem = new JLabel("(Select a texture.)");
                            NameItem.setFont(RFonts.RegMinecraftFont.deriveFont(12f));
                            IDItem = new JLabel();
                            IDItem.setFont(RFonts.RegMinecraftFont.deriveFont(6f));
                            TypeItem = new JLabel("Item Texture");
                            TypeItem.setFont(RFonts.RegMinecraftFont.deriveFont(8f));
                            TypeItem.setForeground(getForeground().darker().darker());
                            AddButtonItem = new JButton("+");
                            SelectButtonItem = new JButton("Select");
                            IconItem = new JLabel(ImageUtilites.ResizeIcon(
                                    new ImageIcon(getClass().getResource("/addons/DefaultItemTexture.png")), 64, 64));

                            AddButtonItem.addActionListener(ac -> {
                                RFileOperations.getResources(parentFrame, WorkspaceName).Serilized
                                        .importTexture(parentFrame, ResourceFile.ITEM_TEXTURE,
                                                WorkspaceName);
                            });
                            SelectButtonItem.addActionListener(ac -> {
                                try {
                                    var Selected = RTextureAddingSelector.openSelector(parentFrame,
                                            ResourceFile.ITEM_TEXTURE,
                                            WorkspaceName);
                                    if (Selected == null)
                                        return;
                                    var filename = MapUtilities.getKeyFromValue(
                                            RFileOperations.getResources(parentFrame,
                                                    WorkspaceName).Serilized.ResourceIDs,
                                            Selected.getKey());
                                    NameItem.setText(
                                            filename);
                                    // Launcher.LOG.info(Selected.getKey());
                                    IDItem.setText(Selected.getKey());
                                    IconItem.setIcon(Selected.getValue());
                                    Input.setName(Selected.getKey());

                                } catch (InterruptedException e1) {
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown",
                                            e1);
                                }
                            });

                            IconItem.setMaximumSize(new Dimension(64, 64));
                            IconItem.setPreferredSize(new Dimension(64, 64));
                            IconItem.setBorder(((JPanel) Input).getBorder());

                            layout.putConstraint(SpringLayout.WEST, IconItem, 5, SpringLayout.WEST, Input);
                            layout.putConstraint(SpringLayout.VERTICAL_CENTER, IconItem, 0,
                                    SpringLayout.VERTICAL_CENTER,
                                    Input);

                            layout.putConstraint(SpringLayout.WEST, NameItem, 5, SpringLayout.EAST, IconItem);
                            layout.putConstraint(SpringLayout.NORTH, NameItem, 0, SpringLayout.NORTH, IconItem);

                            layout.putConstraint(SpringLayout.WEST, IDItem, 5, SpringLayout.EAST, IconItem);
                            layout.putConstraint(SpringLayout.NORTH, IDItem, 0, SpringLayout.SOUTH, NameItem);

                            layout.putConstraint(SpringLayout.WEST, TypeItem, 5, SpringLayout.EAST, IconItem);
                            layout.putConstraint(SpringLayout.SOUTH, TypeItem, 0, SpringLayout.SOUTH, IconItem);

                            layout.putConstraint(SpringLayout.SOUTH, SelectButtonItem, -5, SpringLayout.SOUTH, Input);
                            layout.putConstraint(SpringLayout.EAST, SelectButtonItem, -5, SpringLayout.EAST, Input);

                            Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, AddButtonItem, 0,
                                    SpringLayout.HORIZONTAL_CENTER,
                                    this.Name);
                            Lay.putConstraint(SpringLayout.SOUTH, AddButtonItem, 0, SpringLayout.SOUTH, Input);

                            ((JPanel) Input).add(IconItem);
                            ((JPanel) Input).add(NameItem);
                            ((JPanel) Input).add(IDItem);
                            ((JPanel) Input).add(TypeItem);
                            ((JPanel) Input).add(SelectButtonItem);
                            add(AddButtonItem);

                            setMaximumSize(new Dimension(350, 80));
                            setPreferredSize(new Dimension(350, 80));

                            if (!FromEmpty && field != null) {
                                UUID Id;
                                try {
                                    Id = (UUID) field.get(TargetFile);
                                } catch (IllegalArgumentException | IllegalAccessException e) {
                                    if (TargetFile.getDraft())
                                        return;
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                    return;
                                }
                                if (Id == null)
                                    break;

                                String id = Id.toString();

                                var res = RFileOperations.getResources(parentFrame,
                                        WorkspaceName);

                                var filename = MapUtilities.getKeyFromValue(
                                        res.Serilized.ResourceIDs,
                                        id);

                                NameItem.setText(
                                        filename);
                                IDItem.setText(id);
                                try {
                                    IconItem.setIcon(ImageUtilites.ResizeIcon(
                                            new ImageIcon(Files.readAllBytes(res.Serilized
                                                    .getFileOfResource(parentFrame,
                                                            WorkspaceName, NameItem.getText(),
                                                            ResourceFile.ITEM_TEXTURE)
                                                    .toPath())),
                                            64, 64));
                                } catch (Exception e) {
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                }
                                Input.setName(id);
                            }
                            break;

                        case ResourceFile.BLOCK_TEXTURE: // if its an item texture
                            SpringLayout layoutBlock = new SpringLayout();
                            Input = new JPanel();
                            Input.setName("null");
                            ((JPanel) Input).setBorder(new LineBorder(Color.darkGray));
                            ((JPanel) Input).setLayout(layoutBlock);

                            NameBlock = new JLabel("(Select a texture.)");
                            NameBlock.setFont(RFonts.RegMinecraftFont.deriveFont(12f));
                            IDBlock = new JLabel();
                            IDBlock.setFont(RFonts.RegMinecraftFont.deriveFont(6f));
                            TypeBlock = new JLabel("Block Texture");
                            TypeBlock.setFont(RFonts.RegMinecraftFont.deriveFont(8f));
                            TypeBlock.setForeground(getForeground().darker().darker());
                            AddButtonBlock = new JButton("+");
                            PreviewButtonBlock = new JButton("Preview");
                            SelectButtonBlock = new JButton("Select");
                            IconBlock = new JLabel(ImageUtilites.ResizeIcon(
                                    new ImageIcon(getClass().getResource("/addons/DefaultItemTexture.png")), 64, 64));
                            PreviewButtonBlock.addActionListener(ac -> {
                                Image icon = ((ImageIcon) IconBlock.getIcon()).getImage();
                                BufferedImage bi = new BufferedImage(icon.getWidth(parentFrame), icon.getHeight(null),
                                        BufferedImage.TYPE_INT_RGB);
                                bi.getGraphics().drawImage(icon, 0, 0, parentFrame);
                                RenderHandler.CurrentHandler.showPreviewWindow(parentFrame,
                                        RenderHandler.make6Sided(bi));
                            });
                            AddButtonBlock.addActionListener(ac -> {
                                RFileOperations.getResources(parentFrame, WorkspaceName).Serilized
                                        .importTexture(parentFrame, ResourceFile.BLOCK_TEXTURE,
                                                WorkspaceName);
                            });
                            SelectButtonBlock.addActionListener(ac -> {
                                try {
                                    var Selected = RTextureAddingSelector.openSelector(parentFrame,
                                            ResourceFile.BLOCK_TEXTURE,
                                            WorkspaceName);
                                    if (Selected == null)
                                        return;
                                    var filename = MapUtilities.getKeyFromValue(
                                            RFileOperations.getResources(parentFrame,
                                                    WorkspaceName).Serilized.ResourceIDs,
                                            Selected.getKey());
                                    NameBlock.setText(
                                            filename);
                                    // Launcher.LOG.info(Selected.getKey());
                                    IDBlock.setText(Selected.getKey());
                                    IconBlock.setIcon(Selected.getValue());
                                    Input.setName(Selected.getKey());

                                } catch (InterruptedException e1) {
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown",
                                            e1);
                                }
                            });

                            IconBlock.setMaximumSize(new Dimension(64, 64));
                            IconBlock.setPreferredSize(new Dimension(64, 64));
                            IconBlock.setBorder(((JPanel) Input).getBorder());

                            layoutBlock.putConstraint(SpringLayout.WEST, IconBlock, 5, SpringLayout.WEST, Input);
                            layoutBlock.putConstraint(SpringLayout.VERTICAL_CENTER, IconBlock, 0,
                                    SpringLayout.VERTICAL_CENTER,
                                    Input);

                            layoutBlock.putConstraint(SpringLayout.WEST, NameBlock, 5, SpringLayout.EAST, IconBlock);
                            layoutBlock.putConstraint(SpringLayout.NORTH, NameBlock, 0, SpringLayout.NORTH, IconBlock);

                            layoutBlock.putConstraint(SpringLayout.WEST, IDBlock, 5, SpringLayout.EAST, IconBlock);
                            layoutBlock.putConstraint(SpringLayout.NORTH, IDBlock, 0, SpringLayout.SOUTH, NameBlock);

                            layoutBlock.putConstraint(SpringLayout.WEST, TypeBlock, 5, SpringLayout.EAST, IconBlock);
                            layoutBlock.putConstraint(SpringLayout.SOUTH, TypeBlock, 0, SpringLayout.SOUTH, IconBlock);

                            layoutBlock.putConstraint(SpringLayout.SOUTH, SelectButtonBlock, -5, SpringLayout.SOUTH,
                                    Input);
                            layoutBlock.putConstraint(SpringLayout.EAST, SelectButtonBlock, -5, SpringLayout.EAST,
                                    Input);

                            Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, AddButtonBlock, 0,
                                    SpringLayout.HORIZONTAL_CENTER,
                                    this.Name);
                            Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, PreviewButtonBlock, 0,
                                    SpringLayout.HORIZONTAL_CENTER,
                                    this.Name);
                            Lay.putConstraint(SpringLayout.SOUTH, AddButtonBlock, 0, SpringLayout.SOUTH, Input);
                            Lay.putConstraint(SpringLayout.NORTH, PreviewButtonBlock, 0, SpringLayout.NORTH, Input);

                            ((JPanel) Input).add(IconBlock);
                            ((JPanel) Input).add(NameBlock);
                            ((JPanel) Input).add(IDBlock);
                            ((JPanel) Input).add(TypeBlock);
                            ((JPanel) Input).add(SelectButtonBlock);
                            add(AddButtonBlock);
                            add(PreviewButtonBlock);

                            setMaximumSize(new Dimension(350, 80));
                            setPreferredSize(new Dimension(350, 80));

                            if (!FromEmpty && field != null) {
                                UUID Id;
                                try {
                                    Id = (UUID) field.get(TargetFile);
                                } catch (IllegalArgumentException | IllegalAccessException e) {
                                    if (TargetFile.getDraft())
                                        return;
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                    return;
                                }
                                if (Id == null)
                                    break;

                                String id = Id.toString();

                                var res = RFileOperations.getResources(parentFrame,
                                        WorkspaceName);

                                var filename = MapUtilities.getKeyFromValue(
                                        res.Serilized.ResourceIDs,
                                        id);

                                NameBlock.setText(
                                        filename);
                                IDBlock.setText(id);
                                try {
                                    IconBlock.setIcon(ImageUtilites.ResizeIcon(
                                            new ImageIcon(Files.readAllBytes(res.Serilized
                                                    .getFileOfResource(parentFrame,
                                                            WorkspaceName, NameBlock.getText(),
                                                            ResourceFile.BLOCK_TEXTURE)
                                                    .toPath())),
                                            64, 64));
                                } catch (Exception e) {
                                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                }
                                Input.setName(id);
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    Input = new JLabel("Not supported.");
                }
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }

        if (Optional) // stop the enable check affecting non-optional things
            EnableDis.addItemListener(new ItemListener() {
                {
                    Input.setEnabled(EnableDis.isSelected());
                }

                @Override
                public void itemStateChanged(ItemEvent e) {
                    Input.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                }

            });
        if (SourceFileClass != null)
            Help.addActionListener(e -> {
                try {
                    JOptionPane.showMessageDialog(parentFrame,
                            SourceFileClass.getDeclaredField(Target).getAnnotation(HelpMessage.class).value(),
                            "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Failed to get help message! Tell the dev! Field: " + Target + " Class: "
                                    + SourceFileClass.getName(),
                            "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
                }
            });

        Name.setText(DisplayName);

        try {
            if (!FromEmpty && SourceFileClass != null) {
                if (field.get(TargetFile) != null) {
                    EnableDis.setSelected(true);
                }
            }
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }

        // put in center is not hashmap
        if (Map.class.isAssignableFrom(InputType)) {
            Lay.putConstraint(SpringLayout.NORTH, Name, 10, SpringLayout.NORTH, this);
            Lay.putConstraint(SpringLayout.NORTH, Help, 10, SpringLayout.NORTH, this);
        } else {
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Help, 0, SpringLayout.VERTICAL_CENTER, this);
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Name, 0, SpringLayout.VERTICAL_CENTER, this);
        }

        Lay.putConstraint(SpringLayout.WEST, Name, 0, SpringLayout.WEST, this);

        Lay.putConstraint(SpringLayout.EAST, Help, 0, SpringLayout.EAST, this);

        Lay.putConstraint(SpringLayout.WEST, Input, 3, SpringLayout.EAST, Name);
        Lay.putConstraint(SpringLayout.NORTH, Input, 3, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.SOUTH, Input, -3, SpringLayout.SOUTH, this);
        if (Optional)
            Lay.putConstraint(SpringLayout.EAST, Input, -3, SpringLayout.WEST, EnableDis);
        else
            Lay.putConstraint(SpringLayout.EAST, Input, -3, SpringLayout.WEST, Help);

        Lay.putConstraint(SpringLayout.EAST, EnableDis, -3, SpringLayout.WEST, Help);
        // put in center is not hashmap
        if (Map.class.isAssignableFrom(InputType))
            Lay.putConstraint(SpringLayout.NORTH, EnableDis, 10, SpringLayout.NORTH, this);
        else
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, EnableDis, 0, SpringLayout.VERTICAL_CENTER, this);

        if (!Optional)
            EnableDis.setEnabled(false);
        try {
            if (SourceFileClass != null) {
                var tg = SourceFileClass.getField(TargetField);
                if (tg.getAnnotation(RAnnotation.CantEditAfter.class) != null) {
                    if (tg.get(TargetFile) != null) {
                        Input.setEnabled(false);
                    }
                }
            }
        } catch (Exception e1) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
        }

        add(Name);
        add(Input);
        if (Optional)
            add(EnableDis);

        final HelpMessage anno;
        if (field != null) {
            anno = field.getAnnotation(RAnnotation.HelpMessage.class);
        } else {
            anno = null;
        }
        if (SourceFileClass != null && anno != null) {
            add(Help);
        }

    }

    /**
     * 
     * @return A bool, indicating if this field should be read.
     */
    public Boolean getOptionallyEnabled() {
        if (Required)
            return true;
        return EnableDis.isSelected();
    }

    public String getTarget() {
        return Target;
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object value) throws ClassNotFoundException {
        if (value == null)
            return;
        else if (!InputType.isAssignableFrom(value.getClass())) {
            throw new ClassNotFoundException("This ElementValue isnt the class of the object. ("
                    + InputType.getCanonicalName() + " != " + value.getClass().getCanonicalName() + ")");
        } else if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
            var casted = ((JComboBox<String>) Input);
            casted.setSelectedItem(value);
        } else if (List.class.isAssignableFrom(InputType)) {
            try {
                Field field;
                try { // try to get field
                    if (SourceFileClass != null) {
                        field = SourceFileClass.getField(Target);
                    } else {
                        field = null;
                    }
                } catch (Exception e) {
                    return;
                }
                final Class<?> genericType;
                if (!InputType.isArray()) {
                    if (field == null) {
                        return;
                    }
                    if (field.getGenericType() instanceof java.lang.reflect.ParameterizedType) {
                        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) field
                                .getGenericType();
                        genericType = (Class<?>) pt.getActualTypeArguments()[0];
                    } else
                        genericType = null;
                    if (genericType == null) {
                        throw new NullPointerException("This list doesnt have a type.");
                    }
                } else {
                    genericType = null;
                }
                final StringDropdownField anno;
                if (field != null) {
                    anno = field.getAnnotation(RAnnotation.StringDropdownField.class);
                } else {
                    anno = null;
                }
                for (Object entry : ((List<Object>) value)) {
                    RElementValue toAdd = new RElementValue(parentFrame, genericType,
                            Filter,
                            null, "",
                            false, null, WorkspaceName);
                    toAdd.setValue(entry);

                    if (anno != null) {
                        toAdd.remove(toAdd.Input);
                        JComboBox<String> newInput;
                        if (anno.value()[0].equals("_VANILLABIOMES")) {
                            newInput = new JComboBox<String>(SourceBiomeElement.getVanillaBiomeNames());
                        } else {
                            newInput = new JComboBox<String>(anno.value());
                        }

                        toAdd.Lay.putConstraint(SpringLayout.WEST, newInput, 3, SpringLayout.EAST, toAdd.Name);
                        toAdd.Lay.putConstraint(SpringLayout.NORTH, newInput, 3, SpringLayout.NORTH, toAdd);
                        toAdd.Lay.putConstraint(SpringLayout.SOUTH, newInput, -3, SpringLayout.SOUTH, toAdd);
                        toAdd.Lay.putConstraint(SpringLayout.EAST, newInput, -3, SpringLayout.WEST, toAdd.Help);
                        toAdd.add(newInput);
                        toAdd.Input = newInput;

                        if (anno.strict()) {
                            ((JComboBox<String>) newInput).setEditable(false);
                        }
                        ((JComboBox<String>) newInput).setSelectedItem(entry);
                    }

                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                    HashMapInnerPane.add(toAdd);
                }
            } catch (Exception e) {
                ErrorShower.exception(parentFrame, e);
            }
        } else if (Map.class.isAssignableFrom(InputType)) {
            try {
                for (Map.Entry<Object, Object> entry : ((HashMap<Object, Object>) value).entrySet()) {
                    var ToAdd = new RElementMapValue(parentFrame,
                            RMapElement.LookupMap.get(entry.getKey().toString()));
                    ToAdd.setVal(entry.getValue());

                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                    HashMapInnerPane.add(ToAdd);
                }
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(parentFrame, e.getMessage(), e);
            }
        } else if (InputType.equals(UUID.class)) {
            UUID Id = null;
            try {
                if (value instanceof String) {
                    Id = UUID.fromString(((String) value));
                } else if (value instanceof UUID) {
                    Id = ((UUID) value);
                }
            } catch (IllegalArgumentException e) {

                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)", e);
                return;
            }
            if (Id == null)
                return;

            String id = Id.toString();

            var res = RFileOperations.getResources(parentFrame,
                    WorkspaceName);

            var filename = MapUtilities.getKeyFromValue(
                    res.Serilized.ResourceIDs,
                    id);

            NameBlock.setText(
                    filename);
            IDBlock.setText(id);
            try {
                IconBlock.setIcon(ImageUtilites.ResizeIcon(
                        new ImageIcon(Files.readAllBytes(res.Serilized.getFileOfResource(parentFrame,
                                WorkspaceName, NameBlock.getText(), ResourceFile.BLOCK_TEXTURE).toPath())),
                        64, 64));
            } catch (Exception e) {
                ErrorShower.exception(parentFrame, e);
            }
            NameItem.setText(
                    filename);
            IDItem.setText(id);
            try {
                IconItem.setIcon(ImageUtilites.ResizeIcon(
                        new ImageIcon(Files.readAllBytes(res.Serilized.getFileOfResource(parentFrame,
                                WorkspaceName, NameItem.getText(), ResourceFile.BLOCK_TEXTURE).toPath())),
                        64, 64));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)", e);
            }
            Input.setName(id);
        } else if (InputType.equals(Integer.class) || InputType.equals(int.class) || InputType == float.class
                || InputType == Float.class) { // int, float
            // Launcher.LOG.info("this is an int, or float, and its getting set to a " +
            // value.getClass().getSimpleName());
            ((JSpinner) Input).setValue(value);
        } else {
            try {
                if (Input instanceof JComboBox jcb) // if its a drop down
                {
                    jcb.setSelectedItem(value);
                    return;
                } else if (Input instanceof JTextField)
                    ((JTextField) Input).setText(String.valueOf(value));
                // else is called when the input is a JLabel, its only that when not supported
                else {
                    // just ignore unsupported fields
                    return;
                }
            } catch (Exception ex) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                ErrorShower.showError(parentFrame, "There was a problem setting a field.", "Error", ex);
                return;
            }

        }
    }

    @SuppressWarnings("unchecked")
    public Object getValue() {
        // Launcher.LOG.info(InputType.getName());
        if (valid(true)) {
            try {
                if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
                    var casted = ((JComboBox<String>) Input);
                    return (casted.getSelectedIndex() == 0);
                } else if (Map.class.isAssignableFrom(InputType)) {
                    var mapToBuild = new HashMap<RMapElement, Object>();
                    for (Component comp : HashMapInnerPane.getComponents()) {
                        if (comp instanceof RElementMapValue remv) {
                            RElementMapValue mapElement = remv;
                            mapToBuild.put(mapElement.rMapElement, mapElement.getKeyAndVal().getValue());
                        }
                    }
                    return mapToBuild;
                } else

                if (List.class.isAssignableFrom(InputType)) {
                    List<Object> listToBuild = new ArrayList<Object>();
                    for (Component comp : HashMapInnerPane.getComponents()) {
                        if (comp instanceof RElementValue rev) {
                            listToBuild.add(rev.getValue());
                        }
                    }
                    if (InputType.isArray())
                        return listToBuild.toArray();
                    else
                        return listToBuild;
                } else if (InputType.equals(UUID.class)) {
                    return UUID.fromString(Input.getName());
                } else if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int
                    if (((JSpinner) Input).getValue() instanceof Double doubleVal)
                        return doubleVal.intValue();
                    else
                        return ((JSpinner) Input).getValue();
                } else if (InputType == float.class
                        || InputType == Float.class) {
                    if (((JSpinner) Input).getValue() instanceof Double doubleVal)
                        return doubleVal.floatValue();
                    else
                        return ((JSpinner) Input).getValue();
                } else {
                    try {
                        if (Input instanceof JComboBox jcb) // if its a drop down
                        {
                            // JOptionPane.showMessageDialog(jcb, jcb.getSelectedItem());
                            return jcb.getSelectedItem();
                        }

                        if (Input instanceof JTextField) {
                            String text = ((JTextField) Input).getText();
                            if (InputType.equals(Float.class) || InputType.equals(float.class)) { // float
                                return Float.parseFloat(text);
                            } else if (InputType.equals(String.class)) { // string
                                if (Filter != null && !Filter.getValid(text))
                                    Problem = "String is not valid.";
                                return text;
                            } else {
                                return null;
                            }
                        } else {
                            // just ignore unsupported fields
                            return null;
                        }

                    } catch (Exception ex) {
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                        ErrorShower.showError(parentFrame, "There was a problem getting a field.", "Error", ex);
                        return null;
                    }
                }
            } catch (IllegalArgumentException
                    | SecurityException e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
        } else {
            Launcher.LOG.info("Not valid, not getting");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean valid(boolean strict) {
        var log = Launcher.LOG;
        log.info("================== Checking field " + this.Target + "... ==================");

        if (!strict) {
            Field field;
            try {
                field = SourceFileClass.getField(Target);
            } catch (Exception e1) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                log.info(Target + ": failed to get field; so it fails");

                return false;
            }
            if (field.getAnnotation(RAnnotation.VeryImportant.class) == null) {
                log.info(Target + ": its not important, and drafting; so it passes");

                return true; // if its not strict (drafing) and not important (not like ElementName)
            }
        }

        if (!getOptionallyEnabled()) {
            log.info(Target + ": Not Enabled, so it passes");
            return true;// if its disabled, true, because it wont get written anyways
        }

        try {
            if (List.class.isAssignableFrom(InputType)) {
                log.info(Target + ": Arrays cannot be wrong, so it passes");

                return true;
            } else if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
                log.info(Target + ": Bool cannot be wrong, so it passes");

                return true;
            } else if (InputType.equals(Integer.class) || InputType.equals(int.class) || InputType.equals(Float.class)
                    || InputType.equals(float.class)) { // numbers
                log.info(Target + ": All numbers cannot be wrong, so it passes");

                return true;
            } else if (InputType.equals(UUID.class)) {

                if (Input.getName().equals("null")) {
                    Problem = "No Texture Selected";
                    log.info(Target + ": No UUID can be read, so it fails");

                    return false;
                } else {
                    log.info(Target + ": Texture is selected, and cannot be wrong. it passes");
                    return true;
                }
            } else if (File.class.isAssignableFrom(InputType)) {
                log.info(Target + ": File cannot be wrong. it passes");

                return true;
            } else if (Map.class.isAssignableFrom(InputType)) {
                log.info(Target + ": Map cannot be wrong. it passes");

                return true;
            } else {
                try {
                    log.info(Target + ": Scary!");

                    if (Input instanceof JComboBox jcb) { // for a string drop down
                        Problem = "String is not valid.";
                        if (Filter != null)
                            if (!Filter.getValid(((String) jcb.getSelectedItem()))) {
                                Problem = "String is not valid.";
                                log.info(Target + ": Drop down didnt pass filter, " + Filter.getClass().getName()
                                        + "it doesnt pass");

                                return false;
                            }
                        return !(((JComboBox<String>) Input).getSelectedItem() == "(Select a value)");
                    }
                    if (Input instanceof JTextField) {
                        String text = ((JTextField) Input).getText(); // get the text if its not specilized
                        if (InputType.equals(Float.class) || InputType.equals(float.class)) { // float
                            Problem = "Failed to turn into Float";
                            log.info(Target + ": Parseing to float (Scary!)");

                            Float.parseFloat(text);
                        } else if (InputType.equals(String.class)) { // string

                            Problem = "String is not valid.";
                            log.info(Target + ": String is checking if vaild");
                            // no filter, it doesnt matter
                            // this might be unsafe however
                            if (Filter == null)
                                return true;
                            return Filter.getValid(text);
                        }
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean valid() {
        return valid(true);
    }

    @Override
    public String getProblemMessage() {
        return Problem;
    }

    @Override
    public String getName() {
        return getTarget();
    }
}
