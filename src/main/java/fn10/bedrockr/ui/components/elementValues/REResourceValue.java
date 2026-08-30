package fn10.bedrockr.ui.components.elementValues;

import com.formdev.flatlaf.ui.FlatLineBorder;
import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.addons.resource.interfaces.ResourcePointer;
import fn10.bedrockr.ui.RResourceSelector;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.IOException;
import java.lang.reflect.Field;

public class REResourceValue<R extends Resource> extends RElementValue<ResourcePointer<R>, JPanel> {
    
    private final Class<R> resClass;
    private ResourcePointer<R> val = null;
    private final JLabel icon;
    private final JLabel resName;

    public REResourceValue(Class<R> resClass, @Nullable Field TargetField, @NonNull Class<ResourcePointer<R>> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        this.resClass = resClass;
        this.resName = new JLabel("(No Resource Selected)") {
            @Override
            public void setText(String text) {
                super.setText(text);
                Font defFont = getFont();
                if (defFont == null) return;
                int width = getFontMetrics(defFont).stringWidth(getText());
                Font resNameFont = defFont.deriveFont(12f - width/100f);
                setFont(resNameFont);
            }
        };
        this.icon = new JLabel();
        
        super(TargetField, type, TargetFile, WorkspaceName, details);
        Dimension sz = new Dimension(350,80);
        setMaximumSize(sz);
        setPreferredSize(sz);
    }

    @Override
    public @NonNull JPanel createInput() {
        String typeName = Resource.SelectionTypes.get(resClass);
        SpringLayout lay = new SpringLayout();
        JPanel building = new JPanel(lay);
        building.setBorder(new FlatLineBorder(new Insets(3,3,3,3), Color.LIGHT_GRAY));

        icon.setIcon(ImageUtilities.toScaled("/addons/DefaultItemTexture.png",64,64));
        icon.setBorder(new FlatLineBorder(new Insets(0,0,0,0), Color.LIGHT_GRAY.darker()));
        icon.setPreferredSize(new Dimension(64,64));
        
        JLabel resTypeName = new JLabel(typeName);

        lay.putConstraint(SpringLayout.WEST, icon, 0, SpringLayout.WEST, building);
        lay.putConstraint(SpringLayout.VERTICAL_CENTER, icon, 0, SpringLayout.VERTICAL_CENTER, building);

        JButton selectButton = new JButton("Select " + typeName);
        selectButton.addActionListener(_ -> {
            try {
                ResourcePointer<R> selected = RResourceSelector.openSelector(null, resClass, WorkspaceResources.load(WorkspaceName));
                if (selected == null) return;
                setValueInternal(selected);
            } catch (WorkspaceResources.WorkspaceUnsupportedException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        lay.putConstraint(SpringLayout.WEST, selectButton, 3, SpringLayout.EAST, icon);
        lay.putConstraint(SpringLayout.EAST, selectButton, -3, SpringLayout.EAST, building);
        lay.putConstraint(SpringLayout.SOUTH, selectButton , -3, SpringLayout.SOUTH, building);

        lay.putConstraint(SpringLayout.WEST, resName, 3, SpringLayout.EAST, icon);
        lay.putConstraint(SpringLayout.EAST, resName, -3, SpringLayout.EAST, building);
        lay.putConstraint(SpringLayout.NORTH, resName , 3, SpringLayout.NORTH, building);

        lay.putConstraint(SpringLayout.WEST, resTypeName, 3, SpringLayout.EAST, icon);
        lay.putConstraint(SpringLayout.NORTH, resTypeName , 4, SpringLayout.SOUTH, resName);

        building.add(icon);
        building.add(selectButton);
        building.add(resName);
        building.add(resTypeName);

        return building;
    }

    @Override
    public void setValueInternal(ResourcePointer<R> value) {
        try {
            val = value;
            WorkspaceResources wres = WorkspaceResources.load(WorkspaceName);
            R res = value.get(wres);
            icon.setIcon(ImageUtilities.toScaled(res.getResourceIcon(), 64));
            resName.setText(res.Name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected ResourcePointer<R> getValueInternal(boolean shouldLog) {
        return val;
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return val != null;
    }
}
