package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.ItemComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RMDamageValue extends RMapValue<ItemComponents.ToolDamage, JSpinner> {
    public RMDamageValue(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull JSpinner createInput() {
        return new JSpinner();
    }

    @Override
    public ItemComponents.ToolDamage getValue() {
        return new ItemComponents.ToolDamage((Integer) InputField.getValue());
    }

    @Override
    public void setValue(ItemComponents.ToolDamage val) {
        InputField.setValue(val.damage);
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
