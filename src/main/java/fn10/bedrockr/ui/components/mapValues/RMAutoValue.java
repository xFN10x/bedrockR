package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.components.elementValues.RELimitableValue;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class RMAutoValue<V> extends RMapValue<V, RElementValue<V, ?>> {
    private final Class<V> valClass;
    public RMAutoValue(Window Ancestor, RMapElement RME) {
        valClass = (Class<V>) RME.Type;
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull RElementValue<V, ?> createInput() {
        RElementValue<V, ?> elementVal = RElementValue.ofClass(valClass);
        if (hasFilter(RMapElement.MapValueFilter.NotNegative) && elementVal instanceof RELimitableValue) {
            ((RELimitableValue) elementVal).setMin(0);
        }
        return elementVal;
    }

    @Override
    public V getValue() {
        return InputField.getValue();
    }

    @Override
    public void setValue(V val) {
        InputField.setValue(val);
    }

    @Override
    public boolean valid(boolean strict) {
        return InputField.valid(strict);
    }
}
