package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.RStringDropdownMapElement;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RMStringDropdownValue extends RMapValue<String, JComboBox<String>> {
    public RMStringDropdownValue(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull JComboBox<String> createInput() {
        if (rMapElement instanceof RStringDropdownMapElement rsdme) {
            String[] ars = rsdme.getChoices();
            return new JComboBox<>(ars);
        } return new JComboBox<>();
    }

    @Override
    public String getValue() {
        return String.valueOf(InputField.getSelectedItem());
    }

    @Override
    public void setValue(String val) {
InputField.setSelectedItem(val);
    }

    @Override
    public boolean valid(boolean strict) {
        return getValue().isBlank();
    }
}
