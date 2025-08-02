package fn10.bedrockr.windows.componets;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.LineBorder;

import java.awt.event.MouseEvent;
import java.io.File;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.jsonClasses.ResourceFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
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
    private JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));
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

    public RElementValue(Frame parentFrame, @Nonnull Class<?> InputType, FieldFilter Filter, String TargetField,
            String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile TargetFile,
            String WorkspaceName) {
        this(parentFrame, InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass, TargetFile, false,
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
        setBorder(new LineBorder(Color.DARK_GRAY));
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
            /*--------*/} else if (InputType.equals(HashMap.class)) { // hashmap stuf
                                                                      // ---------------------------------------------------
            Input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

            if (!FromEmpty) {
                try {
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) field.get(TargetFile)).entrySet()) {
                        var ToAdd = new RElementMapValue(parentFrame,
                                new RMapElement(((String) entry.getKey()), entry.getValue().getClass()));
                        ToAdd.setVal(entry.getValue());
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
                    var select = RMapValueAddingSelector.openSelector(parentFrame,
                            ((RMapElement[]) anno.value().getMethod("getPickable").invoke(null)));
                    if (select == null)
                        return;
                    var toAdd = new RElementMapValue(parentFrame, select);
                    toAdd.setAlignmentX(0.5f);
                    toAdd.setName("E");

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
        // END OF HASH MAP STUFF
        // ------------------------------------------------------------------------------------------

        else if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int
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
            // see if this is a resource
            var anno = field.getAnnotation(RAnnotation.ResourcePackResourceType.class);

            if (anno != null) { // if a resource
                switch (anno.value()) {
                    case ResourceFile.ITEM_TEXTURE:
                        Input = new JPanel();
                        ((JPanel) Input).setBorder(getBorder());
                        Input.addMouseListener(new MouseAdapter() {

                            public void mouseClicked(MouseEvent e) {
                                try {
                                    RTextureAddingSelector.openSelector(parentFrame, ResourceFile.ITEM_TEXTURE,
                                            WorkspaceName);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });

                    default:
                        break;
                }
            } else { // its a regular int TODO: add handling for int values

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
        Help.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(parentFrame,
                            SourceFileClass.getDeclaredField(Target).getAnnotation(HelpMessage.class).message(),
                            "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame,
                            "Failed to get help message! Tell the dev! Field: " + Target + " Class: "
                                    + SourceFileClass.getName(),
                            "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });

        Name.setText(DisplayName);

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
                var mapToBuild = new HashMap<String, Object>();
                for (Component comp : HashMapInnerPane.getComponents()) {
                    if (comp.getName() == null)
                        continue;
                    if (comp.getName().equals("E")) {
                        var mapElement = ((RElementMapValue) comp).getKeyAndVal();
                        mapToBuild.put(mapElement.getKey(), mapElement.getValue());
                    }
                }
                return mapToBuild;
            } else {
                try {
                    if (Input.getName() != null)
                        if (Input.getName().equals("dd")) // if its a drop down
                            return ((JComboBox<String>) Input).getSelectedItem();
                    String text = ((JTextField) Input).getText();
                    if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int
                        return Integer.parseInt(text);
                    } else if (InputType.equals(Float.class) || InputType.equals(float.class)) { // float
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
        if (!getOptionallyEnabled())
            return true; // if its disabled, true, because it wont get written anyways
        if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
            return true;
        } else if (InputType.equals(File.class)) {
            return true;
        } else if (InputType.equals(HashMap.class)) {
            return true;
        } else {
            var process = Problem;
            try {
                if (Input.getName() == "dd") { // for a string drop down
                    Problem = "String is not valid.";
                    if (!Filter.getValid(((String) ((JComboBox<String>) Input).getSelectedItem()))) {
                        Problem = "String is not valid.";
                        return false;
                    }
                    return !(((JComboBox<String>) Input).getSelectedItem() == "(Select a value)");
                }
                String text = ((JTextField) Input).getText(); // get the text if its not specilized
                if (InputType.equals(Integer.class) || InputType.equals(int.class)) { // int
                    process = "Failed to turn into Integer";
                    Integer.parseInt(text);
                } else if (InputType.equals(Float.class) || InputType.equals(float.class)) { // float
                    process = "Failed to turn into Integer";
                    Float.parseFloat(text);
                } else if (InputType.equals(String.class)) { // string
                    if (!Filter.getValid(text))
                        Problem = "String is not valid.";
                    return Filter.getValid(text);
                }
            } catch (Exception e) {
                Problem = process;
                if (strict)
                    return false;
                else {
                    Field field;
                    try {
                        field = SourceFileClass.getField(Target);
                    } catch (Exception e1) {
                        return false;
                    }
                    if (field.getAnnotation(RAnnotation.VeryImportant.class) != null)
                        return false;
                }
            }
        }
        return false;
    }
}
