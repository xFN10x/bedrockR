package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RMBiHumidity extends RMapValue<BiomeComponents.Humidity, JComboBox<Boolean>> {
    public RMBiHumidity(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull JComboBox<Boolean> createInput() {
        return new JComboBox<>(new Boolean[]{true, false});
    }

    @Override
    public BiomeComponents.Humidity getValue() {
        BiomeComponents.Humidity humidity = new BiomeComponents.Humidity();
        humidity.is_humid = (Boolean) InputField.getSelectedItem();
        return humidity;
    }

    @Override
    public void setValue(BiomeComponents.Humidity val) {
        InputField.setSelectedItem(val.is_humid);
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
