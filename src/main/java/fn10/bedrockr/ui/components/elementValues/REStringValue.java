package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RLogUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field; 

public class REStringValue extends RElementValue<String, JTextField> {

    private final RAnnotation.FieldDetails details;
    private FieldFilters.FieldFilter filter = new FieldFilters.RegularStringFilter();

    public REStringValue(@Nullable Field TargetField, @NonNull Class<String> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
        this.details = details;
        if (details != null)
            try {
                filter = details.Filter().getConstructor().newInstance();
            } catch (Exception e) {
                RLogUtils.warnException(e);
            }
    }

    @Override
    public @NonNull JTextField createInput() {
        return new JTextField("");
    }

    @Override
    public void setValueInternal(String value) {
        Input.setText(value);
    }

    @Override
    public String getValueInternal(boolean shouldLog) {
        return Input.getText();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return problem(filter.getValid(getValueInternal(log0)), "String not valid.");
    }
}
