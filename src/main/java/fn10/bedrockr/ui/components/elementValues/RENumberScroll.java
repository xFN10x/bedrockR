package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;

public class RENumberScroll extends RElementValue<Float, JSpinner> {

    private final Float min;
    private final Float max;
    private final Float step;
    private final boolean isInt;
    
     public RENumberScroll(@Nullable Field TargetField, @NonNull Class<Float> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details, float min, float max, float step, boolean isInt) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
         this.min = min;
         this.max = max;
         this.step = step;
         this.isInt = isInt;
     }

    @Override
    public JSpinner createInput() {
        if (isInt) {
            return new JSpinner(new SpinnerNumberModel(0, min.intValue(), max.intValue(), step.intValue()));
        } else {
            return new JSpinner(new SpinnerNumberModel(Float.valueOf(0f), min, max, step));
        }
    }

    @Override
    public void setValueInternal(Float value) {
Input.setValue(value);
    }

    @Override
    protected Float getValueInternal(boolean shouldLog) {
        return (Float) Input.getValue();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        Float v = getValueInternal(false);
        return problem(v <= max && v >= min, "Number is out of range: " + min + " to " + max);
    }
}
