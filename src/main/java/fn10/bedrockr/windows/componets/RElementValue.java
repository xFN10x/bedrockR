package fn10.bedrockr.windows.componets;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.MapUtilities;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RFonts;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.ResourcePackResourceType;
import fn10.bedrockr.windows.RMapValueAddingSelector;
import fn10.bedrockr.windows.RTextureAddingSelector;

/**
 * The main Components for <code>RElementCreationScreen</code>.
 * This class includes lots of useful tools for making a field for a user to
 * edit, and to save certain <code>java.reflect.Field</code>s.
 */
public class RElementValue extends JPanel {

    private SpringLayout Lay = new SpringLayout();
    private JLabel Name = new JLabel();
    public JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));
    private Component Input;
    private JCheckBox EnableDis = new JCheckBox();

    private final static Dimension Size = new Dimension(350, 40);
    private String Target = "";
    private FieldFilter Filter;
    private Class<?> InputType;
    private Class<?> SourceFileClass;

    public Frame parentFrame;

    protected JPanel HashMapInnerPane = new JPanel();
    protected JButton HashMapAdd = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/New.png")));

    public boolean Required = false;
    public String Problem = "No problem here!";

    public RElementValue(Frame parentFrame, @Nonnull Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            String WorkspaceName) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        this(parentFrame, InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass,
                ((ElementFile) SourceFileClass.getConstructor().newInstance()), true, WorkspaceName);
    }

    public RElementValue(Frame frame, @Nonnull Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile TargetFile,
            String WorkspaceName) {
        this(frame, InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass, TargetFile, TargetFile == null,
                WorkspaceName);
    }

    @SuppressWarnings("unchecked")
    protected RElementValue(Frame parentFrame, @Nonnull Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile TargetFile,
            boolean FromEmpty,
            String WorkspaceName) {
        super();

        this.parentFrame = parentFrame;
        this.Target = TargetField;
        this.Required = !Optional;
        this.Filter = Filter;
        this.SourceFileClass = SourceFileClass;
        this.InputType = InputType;

        setMaximumSize(Size);
        setPreferredSize(Size);
        setBorder(new LineBorder(getBackground()));
        setLayout(Lay);

        Help.putClientProperty("JButton.buttonType", "help");

        // do corrisponding actions depending on the type
        if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) { // if bool, its dropdown
            String[] vals = { "true", "false" };
            Input = new JComboBox<String>(vals);
            try {
                var field = SourceFileClass.getField(TargetField);
                if (!FromEmpty)
                    ((JComboBox<String>) Input).setSelectedIndex((boolean) field.get(TargetFile)
                            ? 0 // convert bool to index
                            : 1);
            } catch (Exception e) {

                e.printStackTrace();
                if (!FromEmpty)
                    if (TargetFile.getDraft())
                        return;
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
            }
        } else if (InputType.equals(String.class)) { // if string, do this
            // if normal do this
            Field field;
            try { // try to get field
                field = SourceFileClass.getField(TargetField);
            } catch (Exception e) {
                if (!FromEmpty)
                    if (TargetFile.getDraft())
                        return;
                e.printStackTrace();
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
                return;
            }
            var anno = field.getAnnotation(RAnnotation.StringDropdownField.class);
            Input = new JTextField();
            if (anno == null) { // normal string
                Input = new JTextField();
                try {
                    if (!FromEmpty)
                        ((JTextField) Input).setText(((String) field.get(TargetFile))); // set text to string in field,
                                                                                        // if it is editing
                } catch (Exception e) {
                    if (!FromEmpty)
                        if (TargetFile.getDraft())
                            return;
                    e.printStackTrace();
                    ErrorShower.showError(parentFrame,
                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                            DisplayName, e);
                }
            } else { // dropdown string, an editable combobox
                Input = new JComboBox<String>(anno.value());
                try {
                    Input.setName("dd");
                    ((JComboBox<String>) Input).setEditable(true);
                    if (!FromEmpty)
                        ((JComboBox<String>) Input).setSelectedItem(field.get(TargetFile));
                    else {
                        ((JComboBox<String>) Input).setSelectedItem("(Select a value)");
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    if (!FromEmpty)
                        if (TargetFile.getDraft())
                            return;
                    ErrorShower.showError(parentFrame,
                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                            DisplayName, e);
                }
            }
        } else if (InputType.equals(HashMap.class)) {
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
            Field field;
            try { // try to get field
                field = SourceFileClass.getField(TargetField);
            } catch (Exception e) {
                if (!FromEmpty)
                    if (TargetFile.getDraft())
                        return;
                e.printStackTrace();
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
                return;
            }
            // finally, get the annotation after getting the field
            var anno = field.getAnnotation(RAnnotation.MapFieldSelectables.class);

            List<RMapElement> picked = new ArrayList<RMapElement>();
            if (!FromEmpty) {
                try {
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) field.get(TargetFile)).entrySet()) {
                        var ToAdd = new RElementMapValue(parentFrame,
                                RMapElement.LookupMap.get(entry.getKey()));
                        ToAdd.setVal(entry.getValue());
                        ToAdd.setName("E");

                        picked.add(RMapElement.LookupMap.get(entry.getKey()));

                        HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                        HashMapInnerPane.add(ToAdd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorShower.showError(parentFrame, e.getMessage(), WorkspaceName, e);
                }
            }

            // add the button
            HashMapAdd.addActionListener((e) -> {
                try {
                    RMapElement select = RMapValueAddingSelector.openSelector(parentFrame,
                            ((RMapElement[]) anno.value().getMethod("getPickable")
                                    .invoke(anno.value().getConstructor().newInstance())),
                            picked);
                    if (select == null)
                        return;
                    var toAdd = new RElementMapValue(parentFrame, select);
                    toAdd.setAlignmentX(0.5f);
                    toAdd.setName("E");
                    picked.add(select);

                    HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
                    HashMapInnerPane.add(toAdd);

                    // finish, and why this wasnt working before
                    HashMapInnerPane.revalidate();
                    HashMapInnerPane.repaint();

                } catch (Exception e1) {
                    e1.printStackTrace();
                    ErrorShower.showError(parentFrame, "Failed to add a map element.", e1.getMessage(), e1);
                }
            });
            add(HashMapAdd);

            Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, Input);
            Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
        }
        else if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int

            Input = new JSpinner();

        } else if (InputType.equals(UUID.class)) { // resource
            Field field;
            try { // try to get field
                field = SourceFileClass.getField(TargetField);
            } catch (Exception e) {
                if (!FromEmpty)
                    if (TargetFile.getDraft())
                        return;
                e.printStackTrace();
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
                return;
            }
            ResourcePackResourceType anno = field.getAnnotation(RAnnotation.ResourcePackResourceType.class);

            switch (anno.value()) {
                case ResourceFile.ITEM_TEXTURE: // if its an item texture
                    SpringLayout layout = new SpringLayout();
                    Input = new JPanel();
                    Input.setName("null");
                    ((JPanel) Input).setBorder(getBorder());
                    ((JPanel) Input).setLayout(layout);

                    JLabel Name = new JLabel("(Select a texture.)");
                    Name.setFont(RFonts.RegMinecraftFont.deriveFont(12f));
                    JLabel ID = new JLabel();
                    ID.setFont(RFonts.RegMinecraftFont.deriveFont(6f));
                    JLabel Type = new JLabel("Item Texture");
                    Type.setFont(RFonts.RegMinecraftFont.deriveFont(8f));
                    Type.setForeground(getForeground().darker().darker());
                    JButton AddButton = new JButton("+");
                    JButton SelectButton = new JButton("Select");
                    JLabel Icon = new JLabel(ImageUtilites.ResizeIcon(
                            new ImageIcon(getClass().getResource("/addons/DefaultItemTexture.png")), 64, 64));

                    AddButton.addActionListener(ac -> {
                        RFileOperations.getResources(parentFrame, WorkspaceName).Serilized
                                .importTexture(parentFrame, ResourceFile.ITEM_TEXTURE,
                                        WorkspaceName);
                    });
                    SelectButton.addActionListener(ac -> {
                        try {
                            var Selected = RTextureAddingSelector.openSelector(parentFrame,
                                    ResourceFile.ITEM_TEXTURE,
                                    WorkspaceName);
                            System.out.println(Selected);
                            if (Selected == null)
                                return;
                            var filename = MapUtilities.getKeyFromValue(
                                    RFileOperations.getResources(parentFrame,
                                            WorkspaceName).Serilized.ResourceIDs,
                                    Selected.getKey());
                            Name.setText(
                                    filename);
                            // System.out.println(Selected.getKey());
                            ID.setText(Selected.getKey());
                            Icon.setIcon(Selected.getValue());
                            Input.setName(Selected.getKey());

                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    });

                    Icon.setMaximumSize(new Dimension(64, 64));
                    Icon.setPreferredSize(new Dimension(64, 64));
                    Icon.setBorder(((JPanel) Input).getBorder());

                    layout.putConstraint(SpringLayout.WEST, Icon, 5, SpringLayout.WEST, Input);
                    layout.putConstraint(SpringLayout.VERTICAL_CENTER, Icon, 0, SpringLayout.VERTICAL_CENTER,
                            Input);

                    layout.putConstraint(SpringLayout.WEST, Name, 5, SpringLayout.EAST, Icon);
                    layout.putConstraint(SpringLayout.NORTH, Name, 0, SpringLayout.NORTH, Icon);

                    layout.putConstraint(SpringLayout.WEST, ID, 5, SpringLayout.EAST, Icon);
                    layout.putConstraint(SpringLayout.NORTH, ID, 0, SpringLayout.SOUTH, Name);

                    layout.putConstraint(SpringLayout.WEST, Type, 5, SpringLayout.EAST, Icon);
                    layout.putConstraint(SpringLayout.SOUTH, Type, 0, SpringLayout.SOUTH, Icon);

                    layout.putConstraint(SpringLayout.SOUTH, SelectButton, -5, SpringLayout.SOUTH, Input);
                    layout.putConstraint(SpringLayout.EAST, SelectButton, -5, SpringLayout.EAST, Input);

                    Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, AddButton, 0, SpringLayout.HORIZONTAL_CENTER,
                            this.Name);
                    Lay.putConstraint(SpringLayout.SOUTH, AddButton, 0, SpringLayout.SOUTH, Input);

                    ((JPanel) Input).add(Icon);
                    ((JPanel) Input).add(Name);
                    ((JPanel) Input).add(ID);
                    ((JPanel) Input).add(Type);
                    ((JPanel) Input).add(SelectButton);
                    add(AddButton);

                    setMaximumSize(new Dimension(350, 80));
                    setPreferredSize(new Dimension(350, 80));

                    if (!FromEmpty) {
                        UUID Id;
                        try {
                            Id = (UUID) field.get(TargetFile);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            if (TargetFile.getDraft())
                                return;
                            e.printStackTrace();
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

                        Name.setText(
                                filename);
                        ID.setText(id);
                        try {
                            Icon.setIcon(ImageUtilites.ResizeIcon(
                                    new ImageIcon(Files.readAllBytes(res.Serilized.getResourceFile(parentFrame,
                                            WorkspaceName, Name.getText(), ResourceFile.ITEM_TEXTURE).toPath())),
                                    64, 64));
                        } catch (Exception e) {
                            e.printStackTrace();
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

                    JLabel NameBlock = new JLabel("(Select a texture.)");
                    NameBlock.setFont(RFonts.RegMinecraftFont.deriveFont(12f));
                    JLabel IDBlock = new JLabel();
                    IDBlock.setFont(RFonts.RegMinecraftFont.deriveFont(6f));
                    JLabel TypeBlock = new JLabel("Block Texture");
                    TypeBlock.setFont(RFonts.RegMinecraftFont.deriveFont(8f));
                    TypeBlock.setForeground(getForeground().darker().darker());
                    JButton AddButtonBlock = new JButton("+");
                    JButton SelectButtonBlock = new JButton("Select");
                    JLabel IconBlock = new JLabel(ImageUtilites.ResizeIcon(
                            new ImageIcon(getClass().getResource("/addons/DefaultItemTexture.png")), 64, 64));

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
                            System.out.println(Selected);
                            if (Selected == null)
                                return;
                            var filename = MapUtilities.getKeyFromValue(
                                    RFileOperations.getResources(parentFrame,
                                            WorkspaceName).Serilized.ResourceIDs,
                                    Selected.getKey());
                            NameBlock.setText(
                                    filename);
                            // System.out.println(Selected.getKey());
                            IDBlock.setText(Selected.getKey());
                            IconBlock.setIcon(Selected.getValue());
                            Input.setName(Selected.getKey());

                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    });

                    IconBlock.setMaximumSize(new Dimension(64, 64));
                    IconBlock.setPreferredSize(new Dimension(64, 64));
                    IconBlock.setBorder(((JPanel) Input).getBorder());

                    layoutBlock.putConstraint(SpringLayout.WEST, IconBlock, 5, SpringLayout.WEST, Input);
                    layoutBlock.putConstraint(SpringLayout.VERTICAL_CENTER, IconBlock, 0, SpringLayout.VERTICAL_CENTER,
                            Input);

                    layoutBlock.putConstraint(SpringLayout.WEST, NameBlock, 5, SpringLayout.EAST, IconBlock);
                    layoutBlock.putConstraint(SpringLayout.NORTH, NameBlock, 0, SpringLayout.NORTH, IconBlock);

                    layoutBlock.putConstraint(SpringLayout.WEST, IDBlock, 5, SpringLayout.EAST, IconBlock);
                    layoutBlock.putConstraint(SpringLayout.NORTH, IDBlock, 0, SpringLayout.SOUTH, NameBlock);

                    layoutBlock.putConstraint(SpringLayout.WEST, TypeBlock, 5, SpringLayout.EAST, IconBlock);
                    layoutBlock.putConstraint(SpringLayout.SOUTH, TypeBlock, 0, SpringLayout.SOUTH, IconBlock);

                    layoutBlock.putConstraint(SpringLayout.SOUTH, SelectButtonBlock, -5, SpringLayout.SOUTH, Input);
                    layoutBlock.putConstraint(SpringLayout.EAST, SelectButtonBlock, -5, SpringLayout.EAST, Input);

                    Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, AddButtonBlock, 0, SpringLayout.HORIZONTAL_CENTER,
                            this.Name);
                    Lay.putConstraint(SpringLayout.SOUTH, AddButtonBlock, 0, SpringLayout.SOUTH, Input);

                    ((JPanel) Input).add(IconBlock);
                    ((JPanel) Input).add(NameBlock);
                    ((JPanel) Input).add(IDBlock);
                    ((JPanel) Input).add(TypeBlock);
                    ((JPanel) Input).add(SelectButtonBlock);
                    add(AddButtonBlock);

                    setMaximumSize(new Dimension(350, 80));
                    setPreferredSize(new Dimension(350, 80));

                    if (!FromEmpty) {
                        UUID Id;
                        try {
                            Id = (UUID) field.get(TargetFile);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            if (TargetFile.getDraft())
                                return;
                            e.printStackTrace();
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
                                    new ImageIcon(Files.readAllBytes(res.Serilized.getResourceFile(parentFrame,
                                            WorkspaceName, NameBlock.getText(), ResourceFile.BLOCK_TEXTURE).toPath())),
                                    64, 64));
                        } catch (Exception e) {
                            e.printStackTrace();
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
        } else { // really unsafe, if its a type is doesnt know, do this
            Input = new JTextField();
            try {
                var field = SourceFileClass.getField(TargetField);
                if (!FromEmpty)
                    ((JTextField) Input).setText(field.get(TargetFile).toString()); // set text to string if not editng
            } catch (Exception e) {
                if (!FromEmpty)
                    if (TargetFile.getDraft())
                        return;
                e.printStackTrace();
                ErrorShower.showError(parentFrame,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
            }
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
        Help.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(parentFrame,
                        SourceFileClass.getDeclaredField(Target).getAnnotation(HelpMessage.class).value(),
                        "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame,
                        "Failed to get help message! Tell the dev! Field: " + Target + " Class: "
                                + SourceFileClass.getName(),
                        "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Name.setText(DisplayName);

        try {
            if (!FromEmpty) {
                var field = SourceFileClass.getField(TargetField);
                if (field.get(TargetFile) != null) {
                    EnableDis.setSelected(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // put in center is not hashmap
        if (InputType.equals(HashMap.class))
            Lay.putConstraint(SpringLayout.NORTH, Name, 10, SpringLayout.NORTH, this);
        else
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Name, 0, SpringLayout.VERTICAL_CENTER, this);

        Lay.putConstraint(SpringLayout.WEST, Name, 0, SpringLayout.WEST, this);

        // put in center is not hashmap
        if (InputType.equals(HashMap.class))
            Lay.putConstraint(SpringLayout.NORTH, Help, 10, SpringLayout.NORTH, this);
        else
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Help, 0, SpringLayout.VERTICAL_CENTER, this);
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
        if (InputType.equals(HashMap.class))
            Lay.putConstraint(SpringLayout.NORTH, EnableDis, 10, SpringLayout.NORTH, this);
        else
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, EnableDis, 0, SpringLayout.VERTICAL_CENTER, this);

        if (!Optional)
            EnableDis.setEnabled(false);
        try {
            var tg = SourceFileClass.getField(TargetField);
            if (tg.getAnnotation(RAnnotation.CantEditAfter.class) != null) {
                if (tg.get(TargetFile) != null) {
                    Input.setEnabled(false);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        add(Name);
        add(Input);
        if (Optional)
            add(EnableDis);
        add(Help);

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
    public Object getValue() {
        // System.out.println(InputType.getName());
        if (valid(true)) {
            if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
                var casted = ((JComboBox<String>) Input);
                return (casted.getSelectedIndex() == 0);
            } else if (InputType.equals(HashMap.class)) {
                var mapToBuild = new HashMap<RMapElement, Object>();
                for (Component comp : HashMapInnerPane.getComponents()) {
                    if (comp.getName() == null)
                        continue;
                    if (comp.getName().equals("E")) {
                        var mapElement = ((RElementMapValue) comp);
                        mapToBuild.put(mapElement.rMapElement, mapElement.getKeyAndVal().getValue());
                    }
                }
                return mapToBuild;
            } else if (InputType.equals(UUID.class)) {
                return UUID.fromString(Input.getName());
            } else if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int
                return ((JSpinner) Input).getValue();
            } else {
                try {
                    if (Input.getName() != null)
                        if (Input.getName().equals("dd")) // if its a drop down
                            return ((JComboBox<String>) Input).getSelectedItem();
                    String text = ((JTextField) Input).getText();
                    if (InputType.equals(Float.class) || InputType.equals(float.class)) { // float
                        return Float.parseFloat(text);
                    } else if (InputType.equals(String.class)) { // string
                        if (!Filter.getValid(text))
                            Problem = "String is not valid.";
                        return text;
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ErrorShower.showError(parentFrame, "There was a problem getting a field.", "Error", ex);
                    return null;
                }
            }
        } else
            return null;
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
                e1.printStackTrace();
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
        if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
            log.info(Target + ": Bool cannot be wrong, so it passes");

            return true;
        } else if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int
            log.info(Target + ": Int cannot be wrong, so it passes");

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
        } else if (InputType.equals(File.class)) {
            log.info(Target + ": File cannot be wrong. it passes");

            return true;
        } else if (InputType.equals(HashMap.class)) {
            log.info(Target + ": (Hash)Map cannot be wrong. it passes");

            return true;
        } else {
            try {
                log.info(Target + ": Scary!");

                if (Input.getName() == "dd") { // for a string drop down
                    Problem = "String is not valid.";
                    if (!Filter.getValid(((String) ((JComboBox<String>) Input).getSelectedItem()))) {
                        Problem = "String is not valid.";
                        log.info(Target + ": Drop down didnt pass filter, " + Filter.getClass().getName()
                                + "it doesnt pass");

                        return false;
                    }
                    return !(((JComboBox<String>) Input).getSelectedItem() == "(Select a value)");
                }
                String text = ((JTextField) Input).getText(); // get the text if its not specilized
                if (InputType.equals(Float.class) || InputType.equals(float.class)) { // float
                    Problem = "Failed to turn into Float";
                    log.info(Target + ": Parseing to float (Scary!)");

                    Float.parseFloat(text);
                } else if (InputType.equals(String.class)) { // string

                    Problem = "String is not valid.";
                    log.info(Target + ": String is checking if vaild");

                    return Filter.getValid(text);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return false;
    }
}
