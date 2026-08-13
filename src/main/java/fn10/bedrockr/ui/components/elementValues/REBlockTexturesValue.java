package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.FieldFilters;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class REBlockTexturesValue extends RElementValue<BlockTexture, JScrollPane> {
    private JComboBox<String> BlockTexturesModeDropdown;
    private REResourceValue<BlockTextureResource>
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
    public @NonNull JScrollPane createInput() {
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

        BlockTexturesTop = makeBlockTexVal("Top / All");
        BlockTexturesBottom = makeBlockTexVal("Bottom");
        BlockTexturesNorth = makeBlockTexVal("North / Side");
        BlockTexturesSouth = makeBlockTexVal("South");
        BlockTexturesEast = makeBlockTexVal("East");
        BlockTexturesWest = makeBlockTexVal("West");


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

    private static @NonNull REResourceValue<BlockTextureResource> makeBlockTexVal(String name) {
        return new REResourceValue<>(BlockTextureResource.class,
                null,
                (Class<ResourcePointer<BlockTextureResource>>) ((Class<?>) ResourcePointer.class),
                null, null, new RAnnotation.FieldDetails() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return RAnnotation.FieldDetails.class;
            }

            @Override
            public boolean Optional() {
                return false;
            }

            @Override
            public Class<? extends FieldFilters.FieldFilter> Filter() {
                return null;
            }

            @Override
            public @Nullable String displayName() {
                return name;
            }
        });
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
