package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;

public class REBooleanValue extends RElementValue<Boolean, JComboBox<Boolean>> {
    public REBooleanValue(@Nullable Field TargetField, @NonNull Class<Boolean> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public @NonNull JComboBox<Boolean> createInput() {
        return new JComboBox<>(new Boolean[]{true, false});
    }

    @Override
    public void setValueInternal(Boolean value) {
        Input.setSelectedItem(value);
    }

    @Override
    protected Boolean getValueInternal(boolean shouldLog) {
        return ((Boolean) Input.getSelectedItem());
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return true;
    }
}
