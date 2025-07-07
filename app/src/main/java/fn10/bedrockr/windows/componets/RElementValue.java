package fn10.bedrockr.windows.componets;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.border.LineBorder;
import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;

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

    public boolean Required = false;
    public String Problem = "No problem here!";

    public RElementValue(@Nonnull Class<?> InputType, FieldFilter Filter, String TargetField, String DisplayName,
            Boolean Optional,
            //@Nullable String HelpString,
            Class<?> SourceFileClass) {
        super();

        this.Target = TargetField;
        this.Required = !Optional;
        this.Filter = Filter;
        this.InputType = InputType;

        setMaximumSize(Size);
        setPreferredSize(Size);
        setBorder(new LineBorder(Color.DARK_GRAY));
        setLayout(Lay);

        Help.putClientProperty("JButton.buttonType", "help");

        Launcher.LOG.info(InputType.getTypeName());
        if (InputType.equals(Boolean.class)) {
            String[] vals = { "true", "false" };
            Input = new JComboBox<String>(vals);
        } else {
            Input = new JTextField();
        }

        EnableDis.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                EnableDis.setEnabled(e.getStateChange() == ItemEvent.SELECTED ? true : false);
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
                            "Failed to get help message! Tell the dev! Field: " + Target + " Class: "+ SourceFileClass.getName(),
                            "Help for: " + DisplayName, JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });

        Name.setText(DisplayName);

        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Name, 0, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.WEST, Name, 0, SpringLayout.WEST, this);

        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Help, 0, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.EAST, Help, 0, SpringLayout.EAST, this);

        Lay.putConstraint(SpringLayout.WEST, Input, 3, SpringLayout.EAST, Name);
        Lay.putConstraint(SpringLayout.NORTH, Input, 3, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.SOUTH, Input, -3, SpringLayout.SOUTH, this);
        Lay.putConstraint(SpringLayout.EAST, Input, -3, SpringLayout.WEST, EnableDis);

        Lay.putConstraint(SpringLayout.EAST, EnableDis, -3, SpringLayout.WEST, Help);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, EnableDis, 0, SpringLayout.VERTICAL_CENTER, this);

        if (!Optional)
            EnableDis.setEnabled(false);


        add(Name);
        add(Input);
        add(EnableDis);
        add(Help);

    }

    public String getTarget() {
        return Target;
    }

    public Object getValue() {
        if (valid()) {
            if (InputType.equals(Boolean.class)) {
                @SuppressWarnings("unchecked")
                var casted = ((JComboBox<String>) Input);
                return (casted.getSelectedIndex() == 0);
            } else {
                try {
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
                    ErrorShower.showError((Frame)getParent(), "There was a problem getting a field.", "Error", ex);
                    return null;
                }
            }
        } else
            return null;
    }

    public boolean valid() {
        Launcher.LOG.info(InputType.getName());
        if (InputType.equals(Boolean.class)) {
            return true;
        } else {
            var process = Problem;
            try {
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
                return false;
            }
        }
    }
}
