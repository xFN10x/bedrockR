package fn10.bedrockr.windows.componets;

import com.formdev.flatlaf.util.SystemFileChooser;
import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.source.supporting.block.BlockTexture;
import fn10.bedrockr.interfaces.ValidatableValue;
import fn10.bedrockr.utils.MapUtilities;
import fn10.bedrockr.utils.RAnnotation.*;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.Theme;
import fn10.bedrockr.utils.exception.WrongResourceTypeException;
import fn10.bedrockr.windows.RMapValueAddingSelector;
import fn10.bedrockr.windows.RTextureAddingSelector;
import fn10.bedrockr.windows.util.ErrorShower;
import fn10.bedrockr.windows.util.ImageUtilites;
import fn10.bedrockr.windows.util.RFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

/**
 * The main Components for {@code RElementCreationScreen}.
 * This class includes lots of useful tools for making a field for a user to
 * edit, and to save certain {@code java.reflect.Field}s.
 */
public class RElementValue extends JPanel implements ValidatableValue {
    public interface ChangedStatusListener {
        void changed(boolean to);
    }

    private final static String No_Path_Chosen_Text = "(Click to set path.)";
    private final SpringLayout Lay = new SpringLayout();
    private final JLabel Name = new JLabel();
    public JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));
    public JComponent Input = null;
    private final JCheckBox EnableDis = new JCheckBox();

    private String Target = "";
    private final FieldFilter Filter;
    private final Class<?> InputType;
    private final Class<?> SourceFileClass;
    private final String WorkspaceName;

    // componets used for uuid of block texture resource
    private JLabel NameBlock;
    private JLabel IDBlock;
    private JLabel TypeBlock;
    private JButton AddButtonBlock;
    private JButton SelectButtonBlock;
    private JLabel IconBlock;
    // item texture resource
    private JLabel NameItem;
    private JLabel IDItem;
    private JLabel TypeItem;
    private JButton AddButtonItem;
    private JButton SelectButtonItem;
    private JLabel IconItem;

    //components for selecting block textures
    private JComboBox<String> BlockTexturesModeDropdown;
    private RElementValue BlockTexturesTop;
    private RElementValue BlockTexturesBottom;
    private RElementValue BlockTexturesNorth;
    private RElementValue BlockTexturesSouth;
    private RElementValue BlockTexturesEast;
    private RElementValue BlockTexturesWest;

    private Object initValue;

    public Window parentFrame;

    protected JPanel HashMapInnerPane = new JPanel();
    protected JButton HashMapAdd = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/New.png")));

    public boolean Required = false;
    public String Problem = "No problem here!";
    public boolean Changed = false;
    private ChangedStatusListener changedListener = _ -> {
    };

    public void setChangedStatusChangedListener(ChangedStatusListener lis) {
        this.changedListener = lis;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final String editedPrefix = "(*) ";
        if (initValue == null || Map.class.isAssignableFrom(InputType)) return;
        Object val = getValue(false);
        if (!Objects.equals(val, initValue) && !Changed) {
            if (!Name.getText().startsWith(editedPrefix)) {
                Name.setText(editedPrefix + Name.getText());
            }
            Name.setForeground(Color.red);
            Changed = true;
            changedListener.changed(true);
        } else if (Changed && Objects.equals(val, initValue)) {
            if (Name.getText().startsWith(editedPrefix)) {
                Name.setText(Name.getText().replace(editedPrefix, ""));
            }
            Name.setForeground(getForeground());
            Changed = false;
            changedListener.changed(false);
        }
    }

    public RElementValue(Window parentFrame, Class<?> InputType, FieldFilter Filter, String TargetField,
                         String DisplayName,
                         Boolean Optional,
                         Class<? extends SourcelessElementFile> ElementFileClass,
                         String WorkspaceName) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        this(parentFrame, InputType, Filter, TargetField, DisplayName, Optional, ElementFileClass,
                ElementFileClass != null ? ElementFileClass.getConstructor().newInstance() : null,
                true,
                WorkspaceName);
    }

    public RElementValue(Window frame, Class<?> InputType, FieldFilter Filter, String TargetField,
                         String DisplayName,
                         Boolean Optional,
                         Class<?> SourceFileClass,
                         SourcelessElementFile TargetFile,
                         String WorkspaceName) {
        this(frame, InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass, TargetFile,
                TargetFile == null,
                WorkspaceName);
    }

    @SuppressWarnings({"unchecked", "null"})
    protected RElementValue(Window parentFrame, Class<?> InputType, FieldFilter Filter, String TargetField,
                            String DisplayName,
                            Boolean Optional,
                            Class<?> SourceFileClass,
                            SourcelessElementFile TargetFile,
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

            Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(parentFrame,
                    "Failed to get field (does the passed ElementFile match the ElementSource?)",
                    DisplayName, e);
            return;
        }

        // don't do this if its set manually
        if (Input == null)
            // do corrisponding actions depending on the type
            try {
                if (BlockTexture.class.isAssignableFrom(InputType)) {
                    //the input will be a scroll pane with a panel that has a dropdown for what mode, and 6 other elementvalues.
                    JPanel inner = new JPanel();
                    BlockTexturesModeDropdown = new JComboBox<>(new String[]{
                            "One Texture",
                            "Log",
                            "Per-face"
                    });

                    Dimension dropdownsize = new Dimension(500, 30);
                    BlockTexturesModeDropdown.setPreferredSize(dropdownsize);
                    BlockTexturesModeDropdown.setMaximumSize(dropdownsize);

                    Input = new JScrollPane(inner);
                    ((JScrollPane) Input).getVerticalScrollBar().setUnitIncrement(12);

                    inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
                    Dimension sizes = new Dimension(500, 80);
                    BlockTexturesTop = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "Top Texture/All Sides", false, null, WorkspaceName);
                    BlockTexturesBottom = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "Bottom Texture", false, null, WorkspaceName);
                    BlockTexturesNorth = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "North Texture/Side Texture", false, null, WorkspaceName);
                    BlockTexturesSouth = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "South Texture", false, null, WorkspaceName);
                    BlockTexturesEast = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "East Texture", false, null, WorkspaceName);
                    BlockTexturesWest = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "West Texture", false, null, WorkspaceName);

                    BlockTexturesModeDropdown.addActionListener(_ -> {
                        int selected = BlockTexturesModeDropdown.getSelectedIndex();

                        switch (selected) {
                            // one tex
                            case 0:
                                BlockTexturesTop.Input.setEnabled(true);
                                BlockTexturesBottom.Input.setEnabled(false);
                                BlockTexturesNorth.Input.setEnabled(false);
                                BlockTexturesSouth.Input.setEnabled(false);
                                BlockTexturesEast.Input.setEnabled(false);
                                BlockTexturesWest.Input.setEnabled(false);
                                break;

                            // log
                            case 1:
                                BlockTexturesTop.Input.setEnabled(true);
                                BlockTexturesBottom.Input.setEnabled(true);
                                BlockTexturesNorth.Input.setEnabled(true);
                                BlockTexturesSouth.Input.setEnabled(false);
                                BlockTexturesEast.Input.setEnabled(false);
                                BlockTexturesWest.Input.setEnabled(false);
                                break;

                            // all
                            default:
                                BlockTexturesTop.Input.setEnabled(true);
                                BlockTexturesBottom.Input.setEnabled(true);
                                BlockTexturesNorth.Input.setEnabled(true);
                                BlockTexturesSouth.Input.setEnabled(true);
                                BlockTexturesEast.Input.setEnabled(true);
                                BlockTexturesWest.Input.setEnabled(true);
                                break;
                        }
                    });

                    BlockTexturesTop.setPreferredSize(sizes);
                    BlockTexturesTop.setMaximumSize(sizes);

                    BlockTexturesBottom.setPreferredSize(sizes);
                    BlockTexturesBottom.setMaximumSize(sizes);

                    BlockTexturesNorth.setPreferredSize(sizes);
                    BlockTexturesNorth.setMaximumSize(sizes);

                    BlockTexturesSouth.setPreferredSize(sizes);
                    BlockTexturesSouth.setMaximumSize(sizes);

                    BlockTexturesEast.setPreferredSize(sizes);
                    BlockTexturesEast.setMaximumSize(sizes);

                    BlockTexturesWest.setPreferredSize(sizes);
                    BlockTexturesWest.setMaximumSize(sizes);

                    inner.add(BlockTexturesModeDropdown);
                    inner.add(Box.createVerticalStrut(5));
                    inner.add(BlockTexturesTop);
                    inner.add(BlockTexturesBottom);
                    inner.add(BlockTexturesNorth);
                    inner.add(BlockTexturesSouth);
                    inner.add(BlockTexturesEast);
                    inner.add(BlockTexturesWest);

                    BlockTexturesModeDropdown.setSelectedIndex(0);
                } else if (Path.class.isAssignableFrom(InputType)) {
                    if (field == null) {
                        return;
                    }
                    final JButton button = new JButton(No_Path_Chosen_Text);
                    PathType type = field.getAnnotation(PathType.class);
                    button.setHorizontalAlignment(SwingConstants.LEFT);
                    button.addActionListener(_ -> {
                        // new SystemFileChooser(RFileOperations.getFileChooserDefaultPath())
                        // new SystemFileChooser(RFileOperations.getFileChooserDefaultPath())
                        final SystemFileChooser chooser = new SystemFileChooser(RFileOperations.getFileChooserDefaultPath());
                        chooser.setFileSelectionMode(type == null ? SystemFileChooser.FILES_ONLY : type.value());
                        chooser.showOpenDialog(this);
                        final File sel = chooser.getSelectedFile();
                        if (sel != null) {
                            button.setText(sel.getPath());
                        }
                    });
                    Input = button;
                } else if (List.class.isAssignableFrom(InputType)) {
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
                        if (field.getGenericType() instanceof ParameterizedType pt) {
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
                            Launcher.LOG.log(Level.SEVERE, "Exception thrown",
                                    e);
                            ErrorShower.showError(parentFrame, e.getMessage(), WorkspaceName, e);
                        }
                    }

                    final StringDropdownField anno = field.getAnnotation(StringDropdownField.class);
                    // add the button
                    HashMapAdd.addActionListener((e) -> {
                        try {
                            RElementValue toAdd;
                            if (InputType.isArray()) {
                                // fn10.bedrockr.Launcher.LOG.info("make an array value element with
                                // class: "
                                // + InputType.getComponentType().getCanonicalName());
                                toAdd = new RElementValue(parentFrame, InputType.getComponentType(), Filter, null, "",
                                        false,
                                        null, WorkspaceName);
                            } else if (genericType != null) {

                                // fn10.bedrockr.Launcher.LOG.info("make an list value element with
                                // class: "
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
                                    newInput = new JComboBox<>(SourceBiomeElement.getVanillaBiomeNames());
                                else if (anno.value()[0].equals("_PREFIXEDVANILLABIOMES"))
                                    newInput = new JComboBox<>(SourceBiomeElement.getPrefixedVanillaBiomeNames());
                                else if (anno.value()[0].equals("_THEMENAMES"))
                                    newInput = new JComboBox<>(Theme.getNames());
                                else
                                    newInput = new JComboBox<>(anno.value());

                                toAdd.Lay.putConstraint(SpringLayout.WEST, newInput, 3, SpringLayout.EAST, toAdd.Name);
                                toAdd.Lay.putConstraint(SpringLayout.NORTH, newInput, 3, SpringLayout.NORTH, toAdd);
                                toAdd.Lay.putConstraint(SpringLayout.SOUTH, newInput, -3, SpringLayout.SOUTH, toAdd);
                                toAdd.Lay.putConstraint(SpringLayout.EAST, newInput, -3, SpringLayout.WEST, toAdd.Help);
                                toAdd.add(newInput);
                                toAdd.Input = newInput;

                                if (anno.strict()) {
                                    newInput.setEditable(false);
                                    newInput.setSelectedIndex(0);
                                }
                            }

                            JButton removeButton = new JButton("-");

                            toAdd.Lay.putConstraint(SpringLayout.VERTICAL_CENTER, removeButton, 0,
                                    SpringLayout.VERTICAL_CENTER, toAdd);
                            toAdd.Lay.putConstraint(SpringLayout.WEST, toAdd.Input, 3, SpringLayout.EAST, removeButton);

                            toAdd.add(removeButton);
                            removeButton.addActionListener(ac -> {
                                HashMapInnerPane.remove(toAdd);
                                HashMapInnerPane.repaint();
                                HashMapInnerPane.revalidate();
                            });

                            toAdd.setAlignmentX(0.5f);

                            HashMapInnerPane.add(Box.createVerticalStrut(10));
                            HashMapInnerPane.add(toAdd);

                            HashMapInnerPane.revalidate();
                            HashMapInnerPane.repaint();

                        } catch (Exception e1) {
                            Launcher.LOG.log(Level.SEVERE, "Exception thrown",
                                    e1);
                            ErrorShower.showError(parentFrame, "Failed to add a map element.", e1.getMessage(), e1);
                        }
                    });
                    add(HashMapAdd);

                    Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, Input);
                    Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
                } else if (Boolean.class.isAssignableFrom(InputType) || boolean.class.isAssignableFrom(InputType)) {
                    // if
                    // bool,
                    // its
                    // dropdown
                    String[] vals = {"true", "false"};
                    Input = new JComboBox<>(vals);
                    try {
                        ((JComboBox<String>) Input).setSelectedItem("false");
                    } catch (Exception e) {

                        Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
                        if (!FromEmpty)
                            if (TargetFile.getDraft())
                                return;
                        ErrorShower.showError(parentFrame,
                                "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                DisplayName, e);
                    }
                } else if (String.class.isAssignableFrom(InputType)) {
                    // if string, do this
                    // if normal do this

                    final StringDropdownField anno;
                    if (field != null) {
                        anno = field.getAnnotation(StringDropdownField.class);
                    } else {
                        anno = null;
                    }
                    Input = new JTextField();
                    if (anno == null && field != null) { // normal string
                        Input = new JTextField();
                        try {
                            ((JTextField) Input).setText(((String) field.get(TargetFile))); // set text to string in
                            // field,
                            // if it is editing
                        } catch (Exception e) {
                            if (!FromEmpty)
                                if (TargetFile.getDraft())
                                    return;
                            Launcher.LOG.log(Level.SEVERE, "Exception thrown",
                                    e);
                            ErrorShower.showError(parentFrame,
                                    "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                    DisplayName, e);
                        }
                    } else if (anno != null && field != null) { // dropdown string
                        switch (anno.value()[0]) {
                            case "_VANILLABIOMES" -> Input = new JComboBox<>(SourceBiomeElement.getVanillaBiomeNames());
                            case "_PREFIXEDVANILLABIOMES" ->
                                    Input = new JComboBox<>(SourceBiomeElement.getPrefixedVanillaBiomeNames());
                            case "_THEMENAMES" -> Input = new JComboBox<>(Theme.getNames());
                            default -> Input = new JComboBox<>(anno.value());
                        }
                        try {
                            // if its strict, dont make it editable
                            ((JComboBox<String>) Input).setEditable(!anno.strict());
                            ((JComboBox<String>) Input).setSelectedIndex(0);

                        } catch (Exception e) {

                            Launcher.LOG.log(Level.SEVERE, "Exception thrown",
                                    e);
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
                        anno = field.getAnnotation(MapFieldSelectables.class);
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
                            Launcher.LOG.log(Level.SEVERE, "Exception thrown",
                                    e);
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
                                Launcher.LOG.log(Level.SEVERE,
                                        "Exception thrown", e1);
                                ErrorShower.showError(parentFrame, "Failed to add a map element.", e1.getMessage(), e1);
                            }
                    });
                    add(HashMapAdd);

                    Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, Input);
                    Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
                } else if (Integer.class.isAssignableFrom(InputType) || int.class.isAssignableFrom(InputType)) { // int
                    final NumberRange anno;
                    if (field != null) {
                        anno = field.getAnnotation(NumberRange.class);
                    } else {
                        anno = null;
                    }
                    Input = new JSpinner(new SpinnerNumberModel(0, anno != null ? (int) anno.min() : Integer.MIN_VALUE,
                            anno != null ? (int) anno.max() : Integer.MAX_VALUE, 1));

                    ((JSpinner) Input).setValue(field.get(TargetFile));

                } else if (Float.class.isAssignableFrom(InputType) || float.class.isAssignableFrom(InputType)) { // int
                    final NumberRange anno;
                    if (field != null) {
                        anno = field.getAnnotation(NumberRange.class);
                    } else {
                        anno = null;
                    }

                    Input = new JSpinner(new SpinnerNumberModel(0f, anno != null ? anno.min() : -Float.MAX_VALUE,
                            anno != null ? anno.max() : Float.MAX_VALUE, 0.01f));
                    ((JSpinner) Input).setValue(field.get(TargetFile));
                } else if (UUID.class.isAssignableFrom(InputType)) { // resource
                    final ResourcePackResourceType anno;
                    final int type;
                    if (field != null) {
                        anno = field.getAnnotation(ResourcePackResourceType.class);
                        type = anno.value();
                    } else if (TargetField.equalsIgnoreCase("_blocktexture")) {
                        type = ResourceFile.BLOCK_TEXTURE;
                    } else if (TargetField.equalsIgnoreCase("_itemtexture")) {
                        type = ResourceFile.ITEM_TEXTURE;
                    } else {
                        return;
                    }


                    Dimension size = new Dimension(350, 120);
                    switch (type) {
                        //#region item texture
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
                                SystemFileChooser file = new SystemFileChooser(RFileOperations.getFileChooserDefaultPath());
                                file.setFileSelectionMode(SystemFileChooser.FILES_ONLY);
                                file.setFileFilter(new SystemFileChooser.FileNameExtensionFilter(
                                        "PNG Image Files (*.png)", "png"));

                                if (file.showOpenDialog(this) != SystemFileChooser.APPROVE_OPTION)
                                    return;
                                Object input = JOptionPane.showInputDialog(this,
                                        "What do you want to name this texture? (" + file.getSelectedFile().getName()
                                                + ")",
                                        "Name Texture", JOptionPane.INFORMATION_MESSAGE, null, null,
                                        file.getSelectedFile().getName());

                                String finalName = (((String) input).contains(".png") ? input.toString()
                                        : input + ".png");
                                File dest = Path
                                        .of(RFileOperations.getBaseDirectory("workspace").getPath(), WorkspaceName,
                                                "resources", finalName)
                                        .toFile();
                                if (dest.exists()) {
                                    JOptionPane.showMessageDialog(this, "Resource already exist. Please rename it.",
                                            "Naming Error",
                                            JOptionPane.ERROR_MESSAGE);
                                    AddButtonItem.doClick();
                                    return;
                                }
                                try {
                                    RFileOperations.getResources(WorkspaceName).Serilized
                                            .importTexture(file.getSelectedFile(), ResourceFile.ITEM_TEXTURE,
                                                    WorkspaceName);
                                } catch (WrongResourceTypeException e) {
                                    ErrorShower.exception(this, "Failed to import texture", e);
                                }
                            });
                            SelectButtonItem.addActionListener(ac -> {
                                try {
                                    var Selected = RTextureAddingSelector.openSelector(parentFrame,
                                            ResourceFile.ITEM_TEXTURE,
                                            WorkspaceName);
                                    if (Selected == null)
                                        return;
                                    var filename = MapUtilities.getKeyFromValue(
                                            RFileOperations.getResources(WorkspaceName).Serilized.ResourceIDs,
                                            Selected.getKey());
                                    NameItem.setText(
                                            filename);
                                    // fn10.bedrockr.Launcher.LOG.info(Selected.getKey());
                                    IDItem.setText(Selected.getKey());
                                    IconItem.setIcon(Selected.getValue());
                                    Input.setName(Selected.getKey());

                                } catch (InterruptedException e1) {
                                    Launcher.LOG.log(Level.SEVERE,
                                            "Exception thrown",
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

                            setMaximumSize(size);
                            setPreferredSize(size);

                            if (!FromEmpty && field != null) {
                                UUID Id;
                                try {
                                    Id = (UUID) field.get(TargetFile);
                                } catch (IllegalArgumentException | IllegalAccessException e) {
                                    if (TargetFile.getDraft())
                                        return;
                                    Launcher.LOG.log(Level.SEVERE,
                                            "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                    return;
                                }
                                if (Id == null)
                                    break;

                                String id = Id.toString();

                                var res = RFileOperations.getResources(
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
                                                    .getFileOfResource(
                                                            WorkspaceName, NameItem.getText(),
                                                            ResourceFile.ITEM_TEXTURE)
                                                    .toPath())),
                                            64, 64));
                                } catch (Exception e) {
                                    Launcher.LOG.log(Level.SEVERE,
                                            "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                }
                                Input.setName(id);
                            }
                            break;
                        //#endregion

                        case ResourceFile.BLOCK_TEXTURE: // if it's a block texture
                            SpringLayout layoutBlock = new SpringLayout();
                            Input = new JPanel();
                            Input.addPropertyChangeListener("enabled", p -> {
                                Boolean newV = (Boolean) p.getNewValue();
                                SelectButtonBlock.setEnabled(newV);
                                NameBlock.setEnabled(newV);
                                IDBlock.setEnabled(newV);
                                TypeBlock.setEnabled(newV);
                                IconBlock.setEnabled(newV);
                            });
                            Input.setName("null");
                            ((JPanel) Input).setBorder(new LineBorder(ImageUtilites.brighter(getBackground(), 0.5f)));
                            ((JPanel) Input).setLayout(layoutBlock);

                            NameBlock = new JLabel("(Select a texture.)");
                            NameBlock.setFont(RFonts.RegMinecraftFont.deriveFont(12f));
                            IDBlock = new JLabel();
                            IDBlock.setFont(RFonts.RegMinecraftFont.deriveFont(6f));
                            TypeBlock = new JLabel("Block Texture");
                            TypeBlock.setFont(RFonts.RegMinecraftFont.deriveFont(8f));
                            TypeBlock.setForeground(getForeground().darker().darker());
                            AddButtonBlock = new JButton("+");
                            SelectButtonBlock = new JButton("Select");
                            IconBlock = new JLabel(ImageUtilites.ResizeIcon(
                                    new ImageIcon(getClass().getResource("/addons/DefaultItemTexture.png")), 64, 64));
                            AddButtonBlock.addActionListener(ac -> {
                                SystemFileChooser file = new SystemFileChooser(RFileOperations.getFileChooserDefaultPath());
                                file.setFileSelectionMode(SystemFileChooser.FILES_ONLY);
                                file.setFileFilter(new SystemFileChooser.FileNameExtensionFilter(
                                        "PNG Image Files (*.png)", "png"));

                                if (file.showOpenDialog(this) != SystemFileChooser.APPROVE_OPTION)
                                    return;
                                Object input = JOptionPane.showInputDialog(this,
                                        "What do you want to name this texture? (" + file.getSelectedFile().getName()
                                                + ")",
                                        "Name Texture", JOptionPane.INFORMATION_MESSAGE, null, null,
                                        file.getSelectedFile().getName());

                                String finalName = (((String) input).contains(".png") ? input.toString()
                                        : input + ".png");
                                File dest = Path
                                        .of(RFileOperations.getBaseDirectory("workspace").getPath(), WorkspaceName,
                                                "resources", finalName)
                                        .toFile();
                                if (dest.exists()) {
                                    JOptionPane.showMessageDialog(this, "Resource already exist. Please rename it.",
                                            "Naming Error",
                                            JOptionPane.ERROR_MESSAGE);
                                    AddButtonItem.doClick();
                                    return;
                                }
                                try {
                                    RFileOperations.getResources(WorkspaceName).Serilized
                                            .importTexture(file.getSelectedFile(), ResourceFile.BLOCK_TEXTURE,
                                                    WorkspaceName);
                                } catch (WrongResourceTypeException e) {
                                    ErrorShower.exception(this, "Failed to import texture", e);
                                }
                            });
                            SelectButtonBlock.addActionListener(ac -> {
                                try {
                                    var Selected = RTextureAddingSelector.openSelector(parentFrame,
                                            ResourceFile.BLOCK_TEXTURE,
                                            WorkspaceName);
                                    if (Selected == null)
                                        return;
                                    var filename = MapUtilities.getKeyFromValue(
                                            RFileOperations.getResources(
                                                    WorkspaceName).Serilized.ResourceIDs,
                                            Selected.getKey());
                                    NameBlock.setText(
                                            filename);
                                    // fn10.bedrockr.Launcher.LOG.info(Selected.getKey());
                                    IDBlock.setText(Selected.getKey());
                                    IconBlock.setIcon(Selected.getValue());
                                    Input.setName(Selected.getKey());

                                } catch (InterruptedException e1) {
                                    Launcher.LOG.log(Level.SEVERE,
                                            "Exception thrown",
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
                            Lay.putConstraint(SpringLayout.SOUTH, AddButtonBlock, 0, SpringLayout.SOUTH, Input);

                            ((JPanel) Input).add(IconBlock);
                            ((JPanel) Input).add(NameBlock);
                            ((JPanel) Input).add(IDBlock);
                            ((JPanel) Input).add(TypeBlock);
                            ((JPanel) Input).add(SelectButtonBlock);
                            add(AddButtonBlock);

                            setMaximumSize(size);
                            setPreferredSize(size);

                            if (!FromEmpty) {
                                UUID Id;
                                try {
                                    Id = (UUID) field.get(TargetFile);
                                } catch (IllegalArgumentException | IllegalAccessException e) {
                                    if (TargetFile.getDraft())
                                        return;
                                    Launcher.LOG.log(Level.SEVERE,
                                            "Exception thrown",
                                            e);
                                    ErrorShower.showError(parentFrame,
                                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                                            DisplayName, e);
                                    return;
                                }
                                if (Id == null)
                                    break;

                                String id = Id.toString();

                                var res = RFileOperations.getResources(
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
                                                    .getFileOfResource(
                                                            WorkspaceName, NameBlock.getText(),
                                                            ResourceFile.BLOCK_TEXTURE)
                                                    .toPath())),
                                            64, 64));
                                } catch (Exception e) {
                                    Launcher.LOG.log(Level.SEVERE,
                                            "Exception thrown",
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
                Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
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
            Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
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
                Field tg = SourceFileClass.getField(TargetField);
                if (tg.getAnnotation(CantEditAfter.class) != null) {
                    if (TargetFile != null && tg.get(TargetFile) != null) {
                        Input.setEnabled(false);
                    }
                }
            }
        } catch (Exception e1) {
            Launcher.LOG.log(Level.SEVERE, "Exception thrown", e1);
        }

        add(Name);
        add(Input);
        if (Optional)
            add(EnableDis);

        final HelpMessage anno;
        if (field != null) {
            anno = field.getAnnotation(HelpMessage.class);
        } else {
            anno = null;
        }
        if (SourceFileClass != null && anno != null) {
            add(Help);
        }
        if (field != null)
            try {
                setValue(field.get(TargetFile));
            } catch (Exception e) {
                Launcher.LOG.log(Level.SEVERE, "Exception", e);
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
        if (value == null) return;
        initValue = value;
        if (value instanceof BlockTexture bt) {
            int mode = bt.getMode();
            BlockTexturesModeDropdown.setSelectedIndex(mode);
            switch (mode) {
                case BlockTexture.ALL_FACES_MODE:
                    BlockTexturesTop.setValue(bt.upTexID);
                    break;
                case BlockTexture.PILLAR_MODE:
                    BlockTexturesTop.setValue(bt.upTexID);
                    BlockTexturesBottom.setValue(bt.downTexID);
                    BlockTexturesNorth.setValue(bt.northTexID);
                    break;
                default:
                    BlockTexturesTop.setValue(bt.upTexID);
                    BlockTexturesBottom.setValue(bt.downTexID);
                    BlockTexturesNorth.setValue(bt.northTexID);
                    BlockTexturesSouth.setValue(bt.southTexID);
                    BlockTexturesEast.setValue(bt.eastTexID);
                    BlockTexturesWest.setValue(bt.westTexID);
                    break;
            }
        }
        else if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
            var casted = ((JComboBox<String>) Input);
            casted.setSelectedItem(value.toString());
        } else if (Path.class.isAssignableFrom(InputType)) {
            ((JButton) Input).setText(((Path) value).toString());
        }
        else if (List.class.isAssignableFrom(InputType)) {
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
                    if (field.getGenericType() instanceof ParameterizedType pt) {
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
                    anno = field.getAnnotation(StringDropdownField.class);
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
                        } else if (anno.value()[0].equals("_PREFIXEDVANILLABIOMES")) {
                            newInput = new JComboBox<String>(SourceBiomeElement.getPrefixedVanillaBiomeNames());
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
                            newInput.setEditable(false);
                        }
                        newInput.setSelectedItem(entry);
                    }

                    JButton removeButton = new JButton("-");

                    toAdd.Lay.putConstraint(SpringLayout.VERTICAL_CENTER, removeButton, 0, SpringLayout.VERTICAL_CENTER,
                            toAdd);
                    toAdd.Lay.putConstraint(SpringLayout.WEST, toAdd.Input, 3, SpringLayout.EAST, removeButton);

                    toAdd.add(removeButton);
                    removeButton.addActionListener(ac -> {
                        HashMapInnerPane.remove(toAdd);
                        HashMapInnerPane.repaint();
                        HashMapInnerPane.revalidate();
                    });

                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                    HashMapInnerPane.add(toAdd);
                }
            } catch (Exception e) {
                ErrorShower.exception(parentFrame, e);
            }
        }
        else if (Map.class.isAssignableFrom(InputType)) {
            try {
                for (Map.Entry<Object, Object> entry : ((HashMap<Object, Object>) value).entrySet()) {
                    RElementMapValue ToAdd = new RElementMapValue(parentFrame,
                            RMapElement.LookupMap.get(entry.getKey().toString()));
                    ToAdd.setVal(entry.getValue());

                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                    HashMapInnerPane.add(ToAdd);
                }
            } catch (Exception e) {
                Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(parentFrame, e.getMessage(), e);
            }
        }
        else if (UUID.class.isAssignableFrom(InputType)) {
            UUID Id = null;
            try {
                if (value instanceof String) {
                    Id = UUID.fromString(((String) value));
                } else if (value instanceof UUID) {
                    Id = ((UUID) value);
                }
            } catch (IllegalArgumentException e) {

                Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)", e);
                return;
            }
            if (Id == null)
                return;

            String id = Id.toString();

            ResourceFile res = Objects.requireNonNull(RFileOperations.getResources(
                    WorkspaceName)).Serilized;

            String filename = res.getNameOfResourceFromUUID(id);

            if (res.getResourceTypeFromName(filename) == ResourceFile.BLOCK_TEXTURE) {
                NameBlock.setText(
                        filename);
                IDBlock.setText(id);
                try {
                    IconBlock.setIcon(ImageUtilites.ResizeIcon(
                            new ImageIcon(Files.readAllBytes(res.getFileOfResource(
                                    WorkspaceName, NameBlock.getText(), ResourceFile.BLOCK_TEXTURE).toPath())),
                            64, 64));
                } catch (Exception e) {
                    ErrorShower.exception(parentFrame, e);
                }
            } else if (res.getResourceTypeFromName(filename) == ResourceFile.ITEM_TEXTURE) {
                NameItem.setText(
                        filename);
                IDItem.setText(id);
                try {
                    IconItem.setIcon(ImageUtilites.ResizeIcon(
                            new ImageIcon(Files.readAllBytes(res.getFileOfResource(
                                    WorkspaceName, NameItem.getText(), ResourceFile.BLOCK_TEXTURE).toPath())),
                            64, 64));
                } catch (Exception e) {
                    Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
                    ErrorShower.showError(parentFrame,
                            "Failed to get field (does the passed ElementFile match the ElementSource?)", e);
                }
            }

            Input.setName(id);
        } else if (InputType.equals(Integer.class) || InputType.equals(int.class) || InputType == float.class
                || InputType == Float.class) { // int, float
            // fn10.bedrockr.Launcher.LOG.info("this is an int, or float, and its
            // getting set to a " +
            // value.getClass().getSimpleName());
            ((JSpinner) Input).setValue(value);
        } else if (Input instanceof JTextField) {
            ((JTextField) Input).setText(String.valueOf(value));
        } else if (Input instanceof JComboBox<?> jcb) // if it's a dropdown
        {
            jcb.setSelectedItem(value);
        } else if (!InputType.isAssignableFrom(value.getClass())) {
            throw new ClassNotFoundException("This ElementValue isn't the class of the object. ("
                    + InputType.getCanonicalName() + " != " + value.getClass().getCanonicalName() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    public Object getValue() {
        return getValue(true);
    }

    public Object getValue(boolean log) {
        // fn10.bedrockr.Launcher.LOG.info(InputType.getName());
        if (valid(true, log)) {
            try {
                if (BlockTexture.class.isAssignableFrom(InputType)) {
                    return switch (BlockTexturesModeDropdown.getSelectedIndex()) {
                        case BlockTexture.ALL_FACES_MODE -> new BlockTexture((UUID) BlockTexturesTop.getValue(log));
                        case BlockTexture.PILLAR_MODE -> new BlockTexture(
                                (UUID) BlockTexturesTop.getValue(log),
                                (UUID) BlockTexturesBottom.getValue(log),
                                (UUID) BlockTexturesNorth.getValue(log));
                        //perface
                        default -> new BlockTexture(
                                (UUID) BlockTexturesTop.getValue(log),
                                (UUID) BlockTexturesBottom.getValue(log),
                                (UUID) BlockTexturesNorth.getValue(log),
                                (UUID) BlockTexturesSouth.getValue(log),
                                (UUID) BlockTexturesEast.getValue(log),
                                (UUID) BlockTexturesWest.getValue(log));
                    };
                }
                else if (Path.class.isAssignableFrom(InputType)) {
                    return Path.of(((JButton) Input).getText());
                } else if (Boolean.class.isAssignableFrom(InputType) || InputType.equals(boolean.class)) {
                    return (((JComboBox<String>) Input).getSelectedIndex() == 0);
                } else if (Map.class.isAssignableFrom(InputType)) {
                    HashMap<RMapElement, Object> mapToBuild = new HashMap<RMapElement, Object>();
                    for (Component comp : HashMapInnerPane.getComponents()) {
                        if (comp instanceof RElementMapValue remv) {
                            RElementMapValue mapElement = remv;
                            mapToBuild.put(mapElement.rMapElement, mapElement.getKeyAndVal().getValue());
                        }
                    }
                    return mapToBuild;
                } else if (List.class.isAssignableFrom(InputType)) {
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
                } else if (UUID.class.isAssignableFrom(InputType)) {
                    return UUID.fromString(Input.getName());
                } else if (Integer.class.isAssignableFrom(InputType) || InputType.equals(int.class)) { // int
                    if (((JSpinner) Input).getValue() instanceof Double doubleVal)
                        return doubleVal.intValue();
                    else
                        return ((JSpinner) Input).getValue();
                } else if (float.class.isAssignableFrom(InputType) || Float.class.isAssignableFrom(InputType)) {
                    if (((JSpinner) Input).getValue() instanceof Double doubleVal)
                        return doubleVal.floatValue();
                    else
                        return ((JSpinner) Input).getValue();
                } else {
                    try {
                        if (Input instanceof JComboBox<?> jcb) // if its a drop down
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
                        Launcher.LOG.log(Level.SEVERE, "Exception thrown",
                                ex);
                        ErrorShower.showError(parentFrame, "There was a problem getting a field.", "Error", ex);
                        return null;
                    }
                }
            } catch (IllegalArgumentException
                     | SecurityException e) {
                Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
                return null;
            }
        } else {
            Launcher.LOG.info("Not valid, not getting");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean valid(boolean strict) {
        return valid(strict, true);
    }

    public boolean valid(boolean strict, boolean log0) {
        var log = Launcher.LOG;
        if (log0)
            log.info("================== Checking field " + this.Target + "... ==================");

        if (!strict) {
            Field field;
            try {
                field = SourceFileClass.getField(Target);
            } catch (Exception e1) {
                if (log0)
                    log.log(Level.SEVERE, "Exception thrown", e1);
                if (log0)
                    log.info(Target + ": failed to get field; so it fails");

                return false;
            }
            if (field.getAnnotation(VeryImportant.class) == null) {
                if (log0)
                    log.info(Target + ": its not important, and drafting; so it passes");

                return true; // if its not strict (drafing) and not important (not like ElementName)
            }
        }

        if (!getOptionallyEnabled()) {
            if (log0)
                log.info(Target + ": Not Enabled, so it passes");
            return true;// if its disabled, true, because it wont get written anyways
        }

        try {
            if (BlockTexture.class.isAssignableFrom(InputType)) {
                // 0 is one texture
                // 1 is log
                // 2 is perface
                switch (BlockTexturesModeDropdown.getSelectedIndex()) {
                    case 0:
                        if (log0)
                            log.info(Target + ": Block texture mode is single...");
                        if (BlockTexturesTop.valid(strict)) {
                            if (log0)
                                log.info(Target + ": Texture is valid, so this passes");
                            return true;
                        } else {
                            if (log0)
                                log.info(Target + ": Texture isn't valid, so this fails");
                            return false;
                        }
                    case 1:
                        if (log0)
                            log.info(Target + ": Block texture mode is pillar mode...");
                        if (BlockTexturesTop.valid(strict) && BlockTexturesBottom.valid(strict) && BlockTexturesNorth.valid(strict)) {
                            if (log0)
                                log.info(Target + ": Textures are valid, so this passes");
                            return true;
                        } else {
                            if (log0)
                                log.info(Target + ": Textures aren't valid, so this fails");
                            return false;
                        }
                    default:
                        if (log0)
                            log.info(Target + ": Block texture mode is pillar mode...");
                        if (BlockTexturesTop.valid(strict)
                                && BlockTexturesBottom.valid(strict)
                                && BlockTexturesNorth.valid(strict)
                                && BlockTexturesSouth.valid(strict)
                                && BlockTexturesEast.valid(strict)
                                && BlockTexturesWest.valid(strict)) {
                            if (log0)
                                log.info(Target + ": Textures are valid, so this passes");
                            return true;
                        } else {
                            if (log0)
                                log.info(Target + ": Textures aren't valid, so this fails");
                            return false;
                        }
                }
            } else if (Path.class.isAssignableFrom(InputType)) {
                if (((JButton) Input).getText().equalsIgnoreCase(No_Path_Chosen_Text)) {
                    if (log0)
                        log.info(Target + ": Path not chosen, so it doesn't pass");
                    return false;
                } else {
                    if (log0)
                        log.info(Target + ": Path chosen, so it passes");
                    return true;
                }
            } else if (List.class.isAssignableFrom(InputType)) {
                if (log0)
                    log.info(Target + ": Arrays cannot be wrong, so it passes");

                return true;
            } else if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
                if (log0)
                    log.info(Target + ": Bool cannot be wrong, so it passes");

                return true;
            } else if (InputType.equals(Integer.class) || InputType.equals(int.class) || InputType.equals(Float.class)
                    || InputType.equals(float.class)) { // numbers
                if (log0)
                    log.info(Target + ": All numbers cannot be wrong, so it passes");

                return true;
            } else if (InputType.equals(UUID.class)) {

                if (Input.getName().equals("null")) {
                    Problem = "No Texture Selected";
                    if (log0)
                        log.info(Target + ": No UUID can be read, so it fails");

                    return false;
                } else {
                    if (log0)
                        log.info(Target + ": Texture is selected, and cannot be wrong. it passes");
                    return true;
                }
            } else if (File.class.isAssignableFrom(InputType)) {
                if (log0)
                    log.info(Target + ": File cannot be wrong. it passes");

                return true;
            } else if (Map.class.isAssignableFrom(InputType)) {
                if (log0)
                    log.info(Target + ": Map cannot be wrong. it passes");

                return true;
            } else {
                try {
                    if (log0)
                        log.info(Target + ": Scary!");

                    if (Input instanceof JComboBox<?> jcb) { // for a string drop down
                        Problem = "String is not valid.";
                        if (Filter != null)
                            if (!Filter.getValid(((String) jcb.getSelectedItem()))) {
                                Problem = "String is not valid.";
                                if (log0)
                                    log.info(Target + ": Dropdown didn't pass filter, " + Filter.getClass().getName()
                                            + "it doesnt pass");

                                return false;
                            }
                        return !(Objects.equals(jcb.getSelectedItem(), "(Select a value)"));
                    }
                    if (Input instanceof JTextField) {
                        String text = ((JTextField) Input).getText(); // get the text if its not specilized
                        if (InputType.equals(String.class)) { // string

                            Problem = "String is not valid.";
                            if (log0)
                                log.info(Target + ": String is checking if vaild");
                            // no filter, it doesn't matter
                            // this might be unsafe however
                            if (Filter == null)
                                return true;
                            return Filter.getValid(text);
                        }
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    Launcher.LOG.log(Level.SEVERE, "Exception thrown", e);
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

    public String getDisplayName() {
        return Name.getText();
    }

    @Override
    public String getName() {
        return getTarget();
    }
}
