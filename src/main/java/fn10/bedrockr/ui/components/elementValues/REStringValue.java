package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field; 

public class REStringValue extends RElementValue<String, JTextField> {


    public REStringValue(@Nullable Field TargetField, @NonNull Class<String> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public JTextField createInput() {
        return new JTextField("");
    }

    @Override
    public void setValueInternal(String value) {
        Input.setText(value);
    }

    @Override
    public String getValueInternal(boolean shouldLog) {
        return "";
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
