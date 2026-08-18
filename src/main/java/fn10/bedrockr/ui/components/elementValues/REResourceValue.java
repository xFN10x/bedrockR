package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.addons.resource.interfaces.ResourcePointer;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;

public class REResourceValue<R extends Resource> extends RElementValue<ResourcePointer<R>, JPanel> {
    
    private final Class<R> resClass;

    public REResourceValue(Class<R> resClass, @Nullable Field TargetField, @NonNull Class<ResourcePointer<R>> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
        this.resClass = resClass;
    }

    @Override
    public @NonNull JPanel createInput() {
        return new JPanel();
    }

    @Override
    public void setValueInternal(ResourcePointer<R> value) {

    }

    @Override
    protected ResourcePointer<R> getValueInternal(boolean shouldLog) {
        return null;
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
