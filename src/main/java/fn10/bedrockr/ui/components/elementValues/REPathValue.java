package fn10.bedrockr.ui.components.elementValues;

import com.formdev.flatlaf.util.SystemFileChooser;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

public class REPathValue extends RElementValue<Path, JButton> {
    private final static String No_Path_Chosen_Text = "(Click to set path.)";

    public REPathValue(@Nullable Field TargetField, @NonNull Class<Path> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public JButton createInput() {
        final JButton button = new JButton(No_Path_Chosen_Text);
        RAnnotation.PathType type;
        if (Target != null)
            type = Target.getAnnotation(RAnnotation.PathType.class);
        else {
            type = null;
        }
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(_ -> {
            // new SystemFileChooser(RFileOperations.getFileChooserDefaultPath())
            // new SystemFileChooser(RFileOperations.getFileChooserDefaultPath())
            final SystemFileChooser chooser = new SystemFileChooser(RFileOperations.getFileChooserDefaultPath());
            chooser.setFileSelectionMode(type == null ? SystemFileChooser.FILES_ONLY : type.value());
            chooser.showOpenDialog(this);
            final File sel = chooser.getSelectedFile();
            if (sel != null) {
                button.setText(sel.getPath());
            }
        });
        return button;
    }

    @Override
    public void setValueInternal(Path value) {
        Input.setText(value.toString());
    }

    @Override
    public Path getValueInternal(boolean shouldLog) {
        return Path.of(Input.getText());
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        File prop = new File(Input.getText());
        return problem(FileUtils.isDirectory(prop), "Directory isn't valid.");
    }
}
