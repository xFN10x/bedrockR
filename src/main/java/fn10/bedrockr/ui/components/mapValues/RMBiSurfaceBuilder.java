package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.components.RItemValue;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class RMBiSurfaceBuilder extends RMapValue<BiomeComponents.SurfaceBuilder, JPanel> {
    private JPanel surfaceMaterialPanel;
    private RItemValue surfaceMaterialVal;
    private JPanel seaFloorDepthPanel;
    private JSpinner seaDepthVal;
    private JPanel seaMaterialPanel;
    private RItemValue seaMaterialVal;
    private JPanel seaFloorMaterialPanel;
    private RItemValue seaFloorMaterialVal;
    private JPanel foundationMaterialPanel;
    private RItemValue foundationMaterialVal;
    private JPanel midMaterialPanel;
    private RItemValue midMaterialVal;

    public RMBiSurfaceBuilder(Window Ancestor, RMapElement RME, Consumer<RMapValue<?,?>> onRemove) {
        super(Ancestor, RME, onRemove);
        Size.setSize(400, 500);
    }

    @Override
    protected @NonNull JPanel createInput() {
        surfaceMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        surfaceMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                RItemValue.TYPE_SINGLE_BLOCK);
        seaFloorDepthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        seaDepthVal = new JSpinner(new SpinnerNumberModel(1, 0, 127, 1));
        seaMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        seaMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                RItemValue.TYPE_SINGLE);
        seaFloorMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        seaFloorMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                RItemValue.TYPE_SINGLE);
        foundationMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        foundationMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                RItemValue.TYPE_SINGLE_BLOCK);
        midMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        midMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                RItemValue.TYPE_SINGLE_BLOCK);

        var input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));

        seaFloorDepthPanel.add(new JLabel("Sea Floor Depth"));
        seaFloorDepthPanel.add(seaDepthVal);
        input.add(seaFloorDepthPanel);

        seaMaterialPanel.add(new JLabel("Sea Block"));
        seaMaterialPanel.add(seaMaterialVal);
        input.add(seaMaterialPanel);

        seaFloorMaterialPanel.add(new JLabel("Sea Floor Block"));
        seaFloorMaterialPanel.add(seaFloorMaterialVal);
        input.add(seaFloorMaterialPanel);

        foundationMaterialPanel.add(new JLabel("Underground Block"));
        foundationMaterialPanel.add(foundationMaterialVal);
        input.add(foundationMaterialPanel);

        midMaterialPanel.add(new JLabel("Ground Block"));
        midMaterialPanel.add(midMaterialVal);
        input.add(midMaterialPanel);

        surfaceMaterialPanel.add(new JLabel("Surface Block"));
        surfaceMaterialPanel.add(surfaceMaterialVal);
        input.add(surfaceMaterialPanel);

        Lay.putConstraint(SpringLayout.NORTH, input, 5, SpringLayout.SOUTH, IDNameLabel);
        return input;
    }

    @Override
    public BiomeComponents.SurfaceBuilder getValue() {
        try {
            var val = new BiomeComponents.SurfaceBuilder();
            BiomeComponents.SurfaceBuilder.OverworldBuilder builder = new BiomeComponents.SurfaceBuilder.OverworldBuilder();

            builder.sea_floor_depth = ((Number) seaDepthVal.getValue())
                    .intValue();
            builder.mid_material = midMaterialVal.getItem().item;

            builder.foundation_material = foundationMaterialVal
                    .getItem().item;

            builder.sea_material = seaMaterialVal.getItem().item;

            builder.sea_floor_material = seaFloorMaterialVal.getItem().item;

            builder.top_material = surfaceMaterialVal.getItem().item;

            val.builder = builder;
            return val;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(BiomeComponents.SurfaceBuilder val) {
        try {
            seaDepthVal.setValue(val.builder.sea_floor_depth);

            midMaterialVal.setItem(ItemInfo.getItemById(
                    val.builder.mid_material, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));

            foundationMaterialVal.setItem(ItemInfo.getItemById(
                    val.builder.foundation_material,
                    RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));

            seaMaterialVal.setItem(ItemInfo.getItemById(
                    val.builder.sea_material, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));

            seaFloorMaterialVal.setItem(ItemInfo.getItemById(
                    val.builder.sea_floor_material,
                    RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));

            surfaceMaterialVal.setItem(ItemInfo.getItemById(
                    val.builder.top_material, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
        } catch (Exception e) {
            RLogUtils.exception("Failed to set value of RMBiSurfaceBuilder", e);
        }
    }

    @Override
    public boolean valid(boolean strict) {
        return surfaceMaterialVal.valid(strict) &&
                seaMaterialVal.valid(strict) &&
                seaFloorMaterialVal.valid(strict) &&
                foundationMaterialVal.valid(strict) &&
                midMaterialVal.valid(strict);
    }
}
