package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.logging.Level;

public class REStringValue extends RElementValue<String, JTextField> {

    public REStringValue(@NonNull Field TargetField, @Nullable SourcelessElementFile TargetFile, @jakarta.annotation.Nullable String WorkspaceName, @jakarta.annotation.Nullable RAnnotation.FieldDetails details) {
        super(TargetField, TargetFile, WorkspaceName);
    }

    @Override
    public JTextField createInput() {
        //if string, do this
        // if normal do this

        JTextField returning = new JTextField();
        //if (anno == null && field != null && TargetFile != null) { // normal string
            try {
                returning.setText(((String) Target.get(TargetFile))); // set text to string in
                // field,
                // if it is editing
            } catch (Exception e) {
                RFileOperations.LOG.log(Level.SEVERE, "Exception thrown",
                        e);
                ErrorShower.exception(this,
                        "Failed to get field (does the passed ElementFile match the ElementSource?)", e);
            }
//        } else if (anno != null) { // dropdown string
//            switch (anno.value()[0]) {
//                case "_VANILLABIOMES" -> Input = new JComboBox<>(SourceBiomeElement.getVanillaBiomeNames());
//                case "_PREFIXEDVANILLABIOMES" ->
//                        Input = new JComboBox<>(SourceBiomeElement.getPrefixedVanillaBiomeNames());
//                case "_THEMENAMES" -> Input = new JComboBox<>(Theme.getNames());
//                default -> Input = new JComboBox<>(anno.value());
//            }
//            try {
//                // if its strict, dont make it editable
//                ((JComboBox<String>) Input).setEditable(!anno.strict());
//                ((JComboBox<String>) Input).setSelectedIndex(0);
//
//            } catch (Exception e) {
//
//                RFileOperations.LOG.log(Level.SEVERE, "Exception thrown",
//                        e);
//                if (!FromEmpty)
//                    if (TargetFile.getDraft())
//                        return;
//                ErrorShower.showError(parentFrame,
//                        "Failed to get field (does the passed ElementFile match the ElementSource?)",
//                        DisplayName, e);
//            }
//        }
        return returning;
    }

    @Override
    public void setValueInternal(String value) {
Input.setText(value);
    }

    @Override
    public String getValue(boolean shouldLog) {
        return "";
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
