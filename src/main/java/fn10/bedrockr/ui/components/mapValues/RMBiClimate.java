package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.addons.element.supporting.ItemComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.components.RItemValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

//TODO: remove this, and code it straight into the creation screen
public class RMBiClimate extends RMapValue<BiomeComponents.Climate, JPanel> {
    private final JSpinner downfallVal = new JSpinner(new SpinnerNumberModel(0.25f, 0f, 1f, 0.01f));
    private final JPanel downfallPanel = new JPanel();
    private final JPanel snowfallPanel = new JPanel();
    private final JSpinner snowfallMax = new JSpinner(new SpinnerNumberModel(2, 0, 8, 1));
    private final JSpinner snowfallMin = new JSpinner(new SpinnerNumberModel(0, 0, 8, 1));
    private final JPanel tempPanel = new JPanel();
    private final JSpinner tempVal = new JSpinner(new SpinnerNumberModel(0.8f, 0f, 2f, 0.1f));
    private final static float snowFactor = 1f / 8f;

    public RMBiClimate(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
        Size.setSize(400, 150);
    }

    @Override
    protected @NonNull JPanel createInput() {
            var input = new JPanel();
            input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));

            downfallPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            downfallPanel.add(new JLabel("Downfall"));
            downfallPanel.add(downfallVal);
            input.add(downfallPanel);

            snowfallPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            snowfallPanel.add(new JLabel("Snowfall Accumulation"));
            snowfallPanel.add(new JLabel("<html><i>Max</i></html>"));
            snowfallPanel.add(snowfallMax);
            snowfallPanel.add(new JLabel("<html><i>Min</i></html>"));
            snowfallPanel.add(snowfallMin);
            input.add(snowfallPanel);

            snowfallMax.getModel().addChangeListener(_ -> {
                ((SpinnerNumberModel) snowfallMin.getModel())
                        .setMaximum((((Number) snowfallMax.getValue()).intValue()));
            });
            snowfallMin.getModel().addChangeListener(_ -> {
                ((SpinnerNumberModel) snowfallMax.getModel())
                        .setMinimum((((Number) snowfallMin.getValue()).intValue()));
            });

            tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            tempPanel.add(new JLabel("Temperature"));
            tempPanel.add(tempVal);
            input.add(tempPanel);

            Lay.putConstraint(SpringLayout.NORTH, input, 5, SpringLayout.SOUTH, IDNameLabel);
            return input;
    }

    @Override
    public BiomeComponents.Climate getValue() {
        BiomeComponents.Climate climate = new BiomeComponents.Climate();
        climate.downfall = (float)downfallVal.getValue();
        climate.snow_accumulation = new float[] {(float) snowfallMin.getValue() * snowFactor, (float) snowfallMax.getValue() * snowFactor};
        climate.temperature = (float) tempVal.getValue();
        return climate;
    }

    @Override
    public void setValue(BiomeComponents.Climate val) {
        downfallVal.setValue(val.downfall);
        tempVal.setValue(val.temperature);
        float[] snowAccumulation = val.snow_accumulation;
        snowfallMin.setValue(snowAccumulation[0] / snowFactor);
        snowfallMax.setValue(snowAccumulation[1] / snowFactor);
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
