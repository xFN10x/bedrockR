package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;

public abstract class RENumberScrollValue<N extends Number> extends RElementValue<N, JSpinner> implements RELimitableValue<N> {

    protected @Nonnull N min;
    protected @Nonnull N max;
    protected final @Nonnull N step;
    //private final boolean isInt;

    public RENumberScrollValue(@Nullable Field TargetField, @NonNull Class<N> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details, N min, N max, N step) {
        this.min = min;
        this.max = max;
        this.step = step;
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public void setValueInternal(N value) {
        Input.setValue(value);
    }


    @Override
    protected N getValueInternal(boolean shouldLog) {
        return (N) Input.getValue();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        N v = getValueInternal(false);
        return problem(v.floatValue() <= max.floatValue() && v.floatValue() >= min.floatValue(), "Number is out of range: " + min + " to " + max);
    }

    @Override
    public @NonNull N getMax() {
        return max;
    }

    @Override
    public @NonNull N getMin() {
        return min;
    }

    @Override
    public void setMax(@NonNull N val) {
        max = val;
    }

    @Override
    public void setMin(@NonNull N val) {
        min = val;
    }
}
