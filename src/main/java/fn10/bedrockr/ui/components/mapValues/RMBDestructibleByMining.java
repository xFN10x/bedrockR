package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BlockComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RMBDestructibleByMining extends RMapValue<BlockComponents.BlockCanBeMined, JSpinner> {
    public RMBDestructibleByMining(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull JSpinner createInput() {
        return new JSpinner(new SpinnerNumberModel(0, -2147483648f, 2147483647f, 0.1));
    }

    @Override
    public BlockComponents.BlockCanBeMined getValue() {
        BlockComponents.BlockCanBeMined blockCanBeMined = new BlockComponents.BlockCanBeMined();
        blockCanBeMined.hardness = (float) InputField.getValue();
        return blockCanBeMined;
    }

    @Override
    public void setValue(BlockComponents.BlockCanBeMined val) {
        InputField.setValue(val.hardness);
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
