package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.components.elementValues.REListValue;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RMBiReplaceBiomes extends RMapValue<BiomeComponents.ReplaceBiomes, JPanel> {
    JPanel replacementPercentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JSpinner replacementVal = new JSpinner(new SpinnerNumberModel(0.25f, 0f, 1f, 0.01f));
    JPanel noisePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JSpinner noiseVal = new JSpinner(new SpinnerNumberModel(0.25f, 0f, 1f, 0.01f));
    REListValue<String> targetsVal = null;

    public RMBiReplaceBiomes(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
        Size.setSize(400, 250);
    }

    @Override
    protected @NonNull JPanel createInput() {
        var input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));

        replacementPercentPanel.add(new JLabel("Replacement %"));
        replacementPercentPanel.add(replacementVal);
        input.add(replacementPercentPanel);

        noisePanel.add(new JLabel("Noise Frequency Scale"));
        noisePanel.add(noiseVal);
        input.add(noisePanel);
        try {
            targetsVal = new REListValue<>(null, (Class<List<String>>)(Class<?>)List.class, String.class, null,null,BiomeComponents.ReplaceBiomes.Replacement.class.getField("targets").getAnnotation(RAnnotation.FieldDetails.class));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

//            new RElementValue(Ancestor, new ArrayList<String>().getClass(), null, "targets",
//                    "Replace Biomes", false,
//                    Replacement.class, null, true, null);
        input.add(targetsVal);

        Lay.putConstraint(SpringLayout.NORTH, input, 5, SpringLayout.SOUTH, IDNameLabel);
        return input;
    }

    @Override
    public BiomeComponents.ReplaceBiomes getValue() {
        var val = new BiomeComponents.ReplaceBiomes();
        BiomeComponents.ReplaceBiomes.Replacement replacement = new BiomeComponents.ReplaceBiomes.Replacement();

        replacement.amount = ((Number) replacementVal.getValue())
                .floatValue();
        replacement.noise_frequency_scale = ((Number) noiseVal
                .getValue()).floatValue();
        replacement.targets = targetsVal.getValue();

        val.replacements = new BiomeComponents.ReplaceBiomes.Replacement[]{replacement};
        return val;
    }

    @Override
    public void setValue(BiomeComponents.ReplaceBiomes val) {
        replacementVal.setValue(val.replacements[0].amount);
        noiseVal
                .setValue(val.replacements[0].noise_frequency_scale);
        targetsVal.setValue(val.replacements[0].targets);
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
