package fn10.bedrockr.windows.componets;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.border.LineBorder;
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;

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

    protected JButton HashMapAdd = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/New.png")));

    public boolean Required = false;
    public String Problem = "No problem here!";

    public RElementValue(@Nonnull Class<?> InputType, FieldFilter Filter, String TargetField, String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        this(InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass,
                ((ElementFile) SourceFileClass.getConstructor().newInstance()), true);
    }

    public RElementValue(@Nonnull Class<?> InputType, FieldFilter Filter, String TargetField, String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile TargetFile) {
        this(InputType, Filter, TargetField, DisplayName, Optional, SourceFileClass, TargetFile, false);
    }

    @SuppressWarnings("unchecked")
    protected RElementValue(@Nonnull Class<?> InputType, FieldFilter Filter, String TargetField, String DisplayName,
            Boolean Optional,
            Class<?> SourceFileClass,
            ElementFile TargetFile,
            boolean FromEmpty) {
        super();

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
                if (TargetFile.getDraft())
                    return;
                ErrorShower.showError(((Frame) getParent()),
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
            }
        } else if (InputType.equals(String.class)) { // if string, do this
            // if normal do this
            Field field;
            try { // try to get field
                field = SourceFileClass.getField(TargetField);
            } catch (Exception e) {
                if (TargetFile.getDraft())
                    return;
                e.printStackTrace();
                ErrorShower.showError(((Frame) getParent()),
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
                    if (TargetFile.getDraft())
                        return;
                    e.printStackTrace();
                    ErrorShower.showError(((Frame) getParent()),
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
                    if (TargetFile.getDraft())
                        return;
                    ErrorShower.showError(((Frame) getParent()),
                            "Failed to get field (does the passed ElementFile match the ElementSource?)",
                            DisplayName, e);
                }
            }
        } else if (InputType.equals(HashMap.class)) { // hashmap
            Input = new JPanel();
            ((JPanel) Input).setLayout(new BoxLayout(((Container) Input), BoxLayout.Y_AXIS));
            ((JPanel) Input).setBorder(new LineBorder(Color.DARK_GRAY));
            Input.setBackground(getBackground().brighter());

            HashMapAdd.setAlignmentX(0.5f);
            HashMapAdd.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    
                }
                
            });
            ((JPanel) Input).add(HashMapAdd);
        }

        else { // really unsafe
            Input = new JTextField();
            try {
                var field = SourceFileClass.getField(TargetField);
                if (!FromEmpty)
                    ((JTextField) Input).setText(field.get(TargetFile).toString()); // set text to string if not editng
            } catch (Exception e) {
                if (TargetFile.getDraft())
                    return;
                e.printStackTrace();
                ErrorShower.showError(((Frame) getParent()),
                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
                        DisplayName, e);
            }
        }

        if (Optional) // stop this from affecting non-optional things
            EnableDis.addItemListener(new ItemListener() {
                {
                    Input.setEnabled(EnableDis.isSelected() ? true : false);
                }

                @Override
                public void itemStateChanged(ItemEvent e) {
                    Input.setEnabled(e.getStateChange() == ItemEvent.SELECTED ? true : false);
                }

            });
        Help.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JOptionPane.showMessageDialog(getParent(),
                            SourceFileClass.getDeclaredField(Target).getAnnotation(HelpMessage.class).message(),
                            "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(getParent(),
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

    public String getTarget() {
        return Target;
    }

    @SuppressWarnings("unchecked")
    public Object getValue() {
        if (valid(true)) {
            if (InputType.equals(Boolean.class)) {
                var casted = ((JComboBox<String>) Input);
                return (casted.getSelectedIndex() == 0);
            } else {
                try {
                    if (Input.getName().equals("dd"))
                        return ((JComboBox<String>) Input).getSelectedItem();
                    String text = ((JTextField) Input).getText();
                    if (InputType.equals(Integer.class)) {
                        return Integer.parseInt(text);
                    } else if (InputType.equals(Double.class)) {
                        return Double.parseDouble(text);
                    } else if (InputType.equals(Float.class)) {
                        return Float.parseFloat(text);
                    } else if (InputType.equals(Long.class)) {
                        return Long.parseLong(text);
                    } else if (InputType.equals(String.class)) {
                        if (!Filter.getValid(text))
                            Problem = "String is not valid.";
                        return text;
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ErrorShower.showError((Frame) getParent(), "There was a problem getting a field.", "Error", ex);
                    return null;
                }
            }
        } else
            return null;
    }

    @SuppressWarnings("unchecked")
    public boolean valid(boolean strict) {
        // Launcher.LOG.info(InputType.getName());
        if (InputType.equals(Boolean.class) || InputType.equals(boolean.class)) {
            return true;
        } else {
            var process = Problem;
            try {
                if (Input.getName() == "dd") {
                    Problem = "String is not valid.";
                    if (!Filter.getValid(((String) ((JComboBox<String>) Input).getSelectedItem()))) {
                        Problem = "String is not valid.";
                        return false;
                    }
                    return !(((JComboBox<String>) Input).getSelectedItem() == "(Select a value)");
                }
                String text = ((JTextField) Input).getText();
                if (InputType.equals(Integer.class)) {
                    process = "Failed to turn into Integer";
                    Integer.parseInt(text);
                } else if (InputType.equals(Double.class)) {
                    process = "Failed to turn into Double";
                    Double.parseDouble(text);
                } else if (InputType.equals(Float.class)) {
                    process = "Failed to turn into Integer";
                    Float.parseFloat(text);
                } else if (InputType.equals(Long.class)) {
                    process = "Failed to turn into Long";
                    Long.parseLong(text);
                } else if (InputType.equals(String.class)) {

                    if (!Filter.getValid(text))
                        Problem = "String is not valid.";
                    return Filter.getValid(text);
                } else {
                    return true;
                }
                return true;
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
