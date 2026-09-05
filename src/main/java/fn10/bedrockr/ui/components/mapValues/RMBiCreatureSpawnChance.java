package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RMBiCreatureSpawnChance extends RMapValue<BiomeComponents.CreatureSpawnProbablity, JSpinner> {
    public RMBiCreatureSpawnChance(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull JSpinner createInput() {
        return new JSpinner(new SpinnerNumberModel(0.1f, 0f, 0.75f, 0.01f));
    }

    @Override
    public BiomeComponents.CreatureSpawnProbablity getValue() {
        BiomeComponents.CreatureSpawnProbablity creatureSpawnProbablity = new BiomeComponents.CreatureSpawnProbablity();
        creatureSpawnProbablity.probability = (float) InputField.getValue();
        return creatureSpawnProbablity;
    }

    @Override
    public void setValue(BiomeComponents.CreatureSpawnProbablity val) {
        InputField.setValue(val.probability);
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
