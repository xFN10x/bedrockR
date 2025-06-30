package fn10.bedrockr.windows.componets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Type;
import javax.swing.border.LineBorder;
import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;

public class RElementValue extends JPanel {

    private SpringLayout Lay = new SpringLayout();
    private JLabel Name = new JLabel();
    private JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));
    private Component Input;
    private JCheckBox EnableDis = new JCheckBox();

    private final static Dimension Size = new Dimension(350, 40);
    private String Target = "";
    private FieldFilter Filter;
    private Class InputType;

    public boolean Required = false;

    public RElementValue(@Nonnull Class InputType, FieldFilter Filter, String TargetField, String DisplayName,
            Boolean Optional,
            @Nullable String HelpString) {
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
                EnableDis.setEnabled(e.getStateChange() == e.SELECTED ? true : false);
            }

        });
        Help.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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

        if (HelpString == null)
            Help.setVisible(false);


        add(Name);
        add(Input);
        add(EnableDis);
        add(Help);

    }

    public boolean valid() {
        Launcher.LOG.info(InputType.getName());
        if (InputType.equals(Boolean.class)) {
            return true;
        } else {
            try {
                String text = ((JTextField) Input).getText();
                if (InputType.equals(Integer.class)) {
                    Integer.parseInt(text);
                } else if (InputType.equals(Double.class)) {
                    Double.parseDouble(text);
                } else if (InputType.equals(Float.class)) {
                    Float.parseFloat(text);
                } else if (InputType.equals(Long.class)) {
                    Long.parseLong(text);
                } else if (InputType.equals(String.class)) {
                    return Filter.getValid(text);
                } else {
                    return true;
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
