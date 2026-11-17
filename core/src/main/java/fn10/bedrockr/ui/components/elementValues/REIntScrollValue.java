package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;

public class REIntScrollValue extends RENumberScrollValue<Integer> {
    public REIntScrollValue(@Nullable Field TargetField, @NonNull Class<Integer> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details, Integer min, Integer max, Integer step) {
        super(TargetField, type, TargetFile, WorkspaceName, details, min, max, step);
    }

    @Override
    public @NonNull JSpinner createInput() {
        return new JSpinner(new SpinnerNumberModel(Integer.valueOf(0), getMin(), getMax(), step));
    }
}
