package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.BlockTextureResource;
import fn10.bedrockr.addons.resource.ResourcePointer;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class REBlockTexturesValue extends RElementValue<BlockTexture, JScrollPane> {
    private JComboBox<String> BlockTexturesModeDropdown;
    private RElementValue<ResourcePointer<BlockTextureResource>, ?>
            BlockTexturesTop, 
            BlockTexturesBottom, 
            BlockTexturesNorth, 
            BlockTexturesSouth, 
            BlockTexturesEast, 
            BlockTexturesWest;

    public REBlockTexturesValue(@Nullable Field TargetField, @NonNull Class<BlockTexture> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public JScrollPane createInput() {
        //the input will be a scroll pane with a panel that has a dropdown for what mode, and 6 other elementvalues.
        JPanel inner = new JPanel();
        BlockTexturesModeDropdown = new JComboBox<>(new String[]{
                "One Texture",
                "Log",
                "Per-face"
        });

        Dimension dropdownsize = new Dimension(500, 30);
        BlockTexturesModeDropdown.setPreferredSize(dropdownsize);
        BlockTexturesModeDropdown.setMaximumSize(dropdownsize);

        var input = new JScrollPane(inner);
        input.getVerticalScrollBar().setUnitIncrement(12);

        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        Dimension sizes = new Dimension(500, 80);
//                    BlockTexturesTop = new RElementValue(null, UUID.class, null, "_blocktexture", "Top Texture/All Sides", false, null, WorkspaceName);
//                    BlockTexturesBottom = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "Bottom Texture", false, null, WorkspaceName);
//                    BlockTexturesNorth = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "North Texture/Side Texture", false, null, WorkspaceName);
//                    BlockTexturesSouth = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "South Texture", false, null, WorkspaceName);
//                    BlockTexturesEast = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "East Texture", false, null, WorkspaceName);
//                    BlockTexturesWest = new RElementValue(parentFrame, UUID.class, null, "_blocktexture", "West Texture", false, null, WorkspaceName);


        BlockTexturesModeDropdown.addActionListener(_ -> {
            int selected = BlockTexturesModeDropdown.getSelectedIndex();

            switch (selected) {
                // one tex
                case 0:
                    BlockTexturesTop.Input.setEnabled(true);
                    BlockTexturesBottom.Input.setEnabled(false);
                    BlockTexturesNorth.Input.setEnabled(false);
                    BlockTexturesSouth.Input.setEnabled(false);
                    BlockTexturesEast.Input.setEnabled(false);
                    BlockTexturesWest.Input.setEnabled(false);
                    break;

                // log
                case 1:
                    BlockTexturesTop.Input.setEnabled(true);
                    BlockTexturesBottom.Input.setEnabled(true);
                    BlockTexturesNorth.Input.setEnabled(true);
                    BlockTexturesSouth.Input.setEnabled(false);
                    BlockTexturesEast.Input.setEnabled(false);
                    BlockTexturesWest.Input.setEnabled(false);
                    break;

                // all
                default:
                    BlockTexturesTop.Input.setEnabled(true);
                    BlockTexturesBottom.Input.setEnabled(true);
                    BlockTexturesNorth.Input.setEnabled(true);
                    BlockTexturesSouth.Input.setEnabled(true);
                    BlockTexturesEast.Input.setEnabled(true);
                    BlockTexturesWest.Input.setEnabled(true);
                    break;
            }
        });

        BlockTexturesTop.setPreferredSize(sizes);
        BlockTexturesTop.setMaximumSize(sizes);

        BlockTexturesBottom.setPreferredSize(sizes);
        BlockTexturesBottom.setMaximumSize(sizes);

        BlockTexturesNorth.setPreferredSize(sizes);
        BlockTexturesNorth.setMaximumSize(sizes);

        BlockTexturesSouth.setPreferredSize(sizes);
        BlockTexturesSouth.setMaximumSize(sizes);

        BlockTexturesEast.setPreferredSize(sizes);
        BlockTexturesEast.setMaximumSize(sizes);

        BlockTexturesWest.setPreferredSize(sizes);
        BlockTexturesWest.setMaximumSize(sizes);

        inner.add(BlockTexturesModeDropdown);
        inner.add(Box.createVerticalStrut(5));
        inner.add(BlockTexturesTop);
        inner.add(BlockTexturesBottom);
        inner.add(BlockTexturesNorth);
        inner.add(BlockTexturesSouth);
        inner.add(BlockTexturesEast);
        inner.add(BlockTexturesWest);

        BlockTexturesModeDropdown.setSelectedIndex(0);
        return input;
    }

    @Override
    public void setValueInternal(BlockTexture value) {

    }

    @Override
    protected BlockTexture getValueInternal(boolean shouldLog) {
        return null;
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
