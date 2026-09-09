package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.components.elementValues.REListValue;
import org.jspecify.annotations.NonNull;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class RMBiTags extends RMapValue<BiomeComponents.Tags, REListValue<String>> {
    public RMBiTags(Window Ancestor, RMapElement RME, Consumer<RMapValue<?,?>> onRemove) {
        super(Ancestor, RME, onRemove);
    }

    @Override
    protected @NonNull REListValue<String> createInput() {
        try {
            var input = new REListValue<>(BiomeComponents.Tags.class.getField("tags"), (Class<List<String>>)(Class<?>) List.class, String.class, null, null, null);
            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
            return input;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BiomeComponents.Tags getValue() {
        return null;
    }

    @Override
    public void setValue(BiomeComponents.Tags val) {

    }

    @Override
    public boolean valid(boolean strict) {
        return false;
    }
}
