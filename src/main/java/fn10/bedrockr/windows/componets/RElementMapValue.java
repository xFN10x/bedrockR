package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.AbstractMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import com.google.gson.internal.LazilyParsedNumber;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.RMapElement.MapValueFilter;
import fn10.bedrockr.addons.addon.jsonClasses.SharedJSONClasses;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RFonts;

public class RElementMapValue extends JPanel {

    private final Dimension Size = new Dimension(240, 80);

    protected final JButton HelpButton = new JButton();
    {
        HelpButton.putClientProperty("JButton.buttonType", "help");
    }
    protected final JLabel DisplayNameLabel = new JLabel();
    protected final JLabel IDNameLabel = new JLabel();
    protected Component InputField = null;

    public final RMapElement rMapElement;
    protected final Frame Ancestor;

    protected final SpringLayout Lay = new SpringLayout();

    public RElementMapValue(Frame Ancestor, RMapElement RME) {

        this.rMapElement = RME;
        this.Ancestor = Ancestor;

        DisplayNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 16 - (RME.DisplayName.length() / 10)));
        DisplayNameLabel.setText(RME.DisplayName);

        IDNameLabel.setText(RME.ID);
        IDNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 12 - (RME.DisplayName.length() / 10)));

        if (RME.Type == SharedJSONClasses.minecraftDamage.class) { // minecraft:damage
            InputField = new JSpinner();
        }
        // set input field to whatever is nessesary
        else if (RME.Type == String.class) { // string
            InputField = new JTextField();
        } else if (RME.Type == Integer.class || RME.Type == int.class || RME.Type == double.class
                || RME.Type == Double.class) { // int, DOUBLES WILL BE TREATED AS INTS
            InputField = new JSpinner();

            if (RME.Filters.contains(MapValueFilter.NotNegitive))
                ((JSpinner) InputField).addChangeListener(c -> {
                    if (((Integer) ((JSpinner) InputField).getValue()) < 0) {
                        ((JSpinner) InputField).setValue(0);
                    }
                });
        } else if (RME.Type == Float.class || RME.Type == float.class) { // float
            if (RME.Filters.contains(MapValueFilter.Between0And1))
                InputField = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.1));
            else
                InputField = new JSpinner(new SpinnerNumberModel(0, Float.MIN_VALUE, Float.MAX_VALUE, 0.1));
        } else if (RME.Type.isArray()) { // array
            InputField = new JLabel("Array input not implemented.");
        } else if (RME.Type == Boolean.class || RME.Type == boolean.class) { // bool
            String[] ars = { "true", "false" };
            InputField = new JComboBox<String>(ars);
        } else { // else
            if (RME.Type == null) {
                InputField = new JLabel("Input type is null.");
            } else {
                InputField = new JLabel("Unknown input type: " + RME.Type.getName());
                ((JLabel) InputField).setToolTipText("Unknown input type: " + RME.Type.getName());
            }
        }

        InputField.setMinimumSize(new Dimension(0, 70));

        HelpButton.addActionListener(
                (e) -> JOptionPane.showMessageDialog(Ancestor, RME.HelpDescription, "Help for: " + RME.DisplayName,
                        JOptionPane.INFORMATION_MESSAGE));

        Lay.putConstraint(SpringLayout.WEST, InputField, 5, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.EAST, InputField, -5, SpringLayout.EAST, this);
        Lay.putConstraint(SpringLayout.SOUTH, InputField, -5, SpringLayout.SOUTH, this);

        Lay.putConstraint(SpringLayout.NORTH, DisplayNameLabel, 5, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, DisplayNameLabel, 5, SpringLayout.WEST, this);

        Lay.putConstraint(SpringLayout.NORTH, IDNameLabel, 5, SpringLayout.SOUTH, DisplayNameLabel);
        Lay.putConstraint(SpringLayout.WEST, IDNameLabel, 5, SpringLayout.WEST, this);

        Lay.putConstraint(SpringLayout.NORTH, HelpButton, 5, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.EAST, HelpButton, -5, SpringLayout.EAST, this);

        setLayout(Lay);

        setPreferredSize(Size);
        setMaximumSize(Size);
        setSize(Size);

        setBorder(new LineBorder(Color.green));

        add(HelpButton);
        add(InputField);

        add(DisplayNameLabel);
        if (DisplayNameLabel != IDNameLabel) // only add id one if the names arent the same
            add(IDNameLabel);

        validate();
    }

    @SuppressWarnings("unchecked")
    public void setVal(Object val) {
        try {
            if (rMapElement.Type == SharedJSONClasses.minecraftDamage.class) { // minecraft:damage
                ((JSpinner) InputField).setValue(val);
            } else if (rMapElement.Type == String.class) { // string
                ((JTextField) InputField).setText(((String) val));
            } else if (rMapElement.Type == Integer.class || rMapElement.Type == int.class
                    || rMapElement.Type == double.class || rMapElement.Type == Double.class) { // int

                if (val instanceof LazilyParsedNumber)
                    ((JSpinner) InputField).setValue(((LazilyParsedNumber) val).intValue());
                else
                    ((JSpinner) InputField).setValue(val);
            } else if (rMapElement.Type == Float.class || rMapElement.Type == float.class) { // float
                ((JSpinner) InputField).setValue(val);;
            } else if (rMapElement.Type == Boolean.class || rMapElement.Type == boolean.class) { // bool
                ((JComboBox<String>) InputField).setSelectedIndex(((Boolean) val) == true ? 0 : 1);
            } else { // else
                throw new IllegalArgumentException(
                        InputField.getClass().getName() + " does not suppot type " + rMapElement.Type.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(Ancestor, "Failed to set value of map value. Type: ${}", e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map.Entry<String, Object> getKeyAndVal() {
        Object val = null;
        try {
            if (rMapElement.Type == SharedJSONClasses.minecraftDamage.class) { // minecraft:damage
                val = new SharedJSONClasses.minecraftDamage();
                ((SharedJSONClasses.minecraftDamage) val).damage = (int) ((JSpinner) InputField).getValue();
            } else if (rMapElement.Type == String.class) { // string
                val = ((JTextField) InputField).getText();
            } else if (rMapElement.Type == Integer.class || rMapElement.Type == int.class
                    || rMapElement.Type == double.class || rMapElement.Type == Double.class) { // int
                val = ((JSpinner) InputField).getValue();
            } else if (rMapElement.Type == Float.class || rMapElement.Type == float.class) { // float
                val = ((JSpinner) InputField).getValue();
            } else if (rMapElement.Type.isArray()) { // array

            } else if (rMapElement.Type == Boolean.class || rMapElement.Type == boolean.class) { // bool
                val = (((JComboBox<String>) InputField).getSelectedIndex() == 0);
            } else { // else
                if (rMapElement.Type == null) {
                    InputField = new JLabel("Input type is null.");
                } else {
                    InputField = new JLabel("Unknown input type:\n" + rMapElement.Type.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(Ancestor, "Failed to get value of Map Entry.", e.getMessage(), e);
            return null;
        }
        return new AbstractMap.SimpleEntry<>(rMapElement.ID, val);

    }

}
