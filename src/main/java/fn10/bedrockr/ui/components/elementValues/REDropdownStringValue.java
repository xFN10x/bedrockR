package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Objects;

public class REDropdownStringValue extends RElementValue<String, JComboBox<String>> {

    public REDropdownStringValue(@Nullable Field TargetField, @NonNull Class<String> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public @NonNull JComboBox<String> createInput() {
        RAnnotation.StringDropdownField dropdownAnno = getAnno(RAnnotation.StringDropdownField.class);
        assert dropdownAnno != null;
        JComboBox<String> input = new JComboBox<>(substituteArray(dropdownAnno.value()));
        
        input.setEditable(!dropdownAnno.strict());
        input.setSelectedIndex(0);

        return input;
    }

    @Override
    public void setValueInternal(String value) {
        Input.setSelectedItem(value);
    }

    @Override
    protected String getValueInternal(boolean shouldLog) {
        return Objects.requireNonNullElse(Input.getSelectedItem(), "null").toString();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        Problem = "This should always be valid.";
        return true;
    }
}
