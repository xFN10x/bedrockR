package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;

public class REDropdownStringValue extends RElementValue<String, JComboBox<String>> {

    public REDropdownStringValue(@Nullable Field TargetField, @NonNull Class<String> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public JComboBox<String> createInput() {
        return null;
    }

    @Override
    public void setValueInternal(String value) {

    }

    @Override
    protected String getValueInternal(boolean shouldLog) {
        return "";
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
