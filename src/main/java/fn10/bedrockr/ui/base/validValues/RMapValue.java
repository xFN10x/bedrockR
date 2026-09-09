package fn10.bedrockr.ui.base.validValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.RStringDropdownMapElement;
import fn10.bedrockr.addons.element.ValidatableValue;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.addons.element.supporting.BlockComponents;
import fn10.bedrockr.addons.element.supporting.ItemComponents;
import fn10.bedrockr.ui.components.RHelpButton;
import fn10.bedrockr.ui.components.RItemValue;
import fn10.bedrockr.ui.components.mapValues.*;
import fn10.bedrockr.ui.util.RFonts;
import jakarta.annotation.Nonnull;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

@SuppressWarnings({"FieldCanBeLocal"})
/**
 * Repersants a value used in an {@link fn10.bedrockr.ui.components.elementValues.REMapValue}, with the key always being String because json.
 */
public abstract class RMapValue<V, I extends JComponent> extends JPanel implements ValidatableValue {

    protected Dimension Size = new Dimension(240, 80);

    protected final RHelpButton HelpButton = new RHelpButton();
    protected final JButton removeButton = new JButton("-");

    protected final JLabel DisplayNameLabel = new JLabel();
    protected final JLabel IDNameLabel = new JLabel();
    protected I InputField;
//    protected Map<String, Component> MultipleInputs = new HashMap<String, Component>();

    public final RMapElement rMapElement;
    protected final Window Ancestor;

    protected final SpringLayout Lay = new SpringLayout();

    protected static boolean are(RMapElement cls0, Class<?> cls1) {
        return cls1.isAssignableFrom(cls0.Type);
    }

    public static RMapValue<?, ?> ofElement(Window Ancestor, RMapElement rMapElement, Consumer<RMapValue<?,?>> onRemove) {
        if (rMapElement instanceof RStringDropdownMapElement rsd) {
            return new RMStringDropdownValue(Ancestor, rsd, onRemove);
        } else if (are(rMapElement, BlockComponents.BlockCanBeMined.class)) {
            return new RMBDestructibleByMining(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.Climate.class)) {
            return new RMBiClimate(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.CreatureSpawnProbablity.class)) {
            return new RMBiCreatureSpawnChance(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.Humidity.class)) {
            return new RMBiHumidity(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.MapTints.class)) {
            return new RMBiMapTints(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.ReplaceBiomes.class)) {
            return new RMBiReplaceBiomes(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.SurfaceBuilder.class)) {
            return new RMBiSurfaceBuilder(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, BiomeComponents.Tags.class)) {
            return new RMBiTags(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, ItemComponents.ItemBlockPlacer.class)) {
            return new RMIBlockPlacer(Ancestor, rMapElement, onRemove);
        } else if (are(rMapElement, ItemComponents.ToolDamage.class)) {
            return new RMIDamageValue(Ancestor, rMapElement, onRemove);
        }
        return new RMAutoValue<>(Ancestor, rMapElement, onRemove);
        
    }

    protected boolean hasFilter(RMapElement.MapValueFilter filter) {
        return rMapElement.Filters.contains(filter);
    }

    public RMapValue(Window Ancestor, RMapElement RME, Consumer<RMapValue<?,?>> onRemove) {

        this.rMapElement = RME;
        this.Ancestor = Ancestor;
        this.InputField = createInput();

        DisplayNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 16 - ((float) RME.DisplayName.length() / 10)));
        DisplayNameLabel.setText(RME.DisplayName);

        IDNameLabel.setText(RME.ID);
        IDNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 12 - ((float) RME.DisplayName.length() / 10)));
        
        if (!(InputField instanceof RItemValue)) {
            InputField.setMinimumSize(new Dimension(0, 70));
            Lay.putConstraint(SpringLayout.WEST, InputField, 5, SpringLayout.WEST, this);
            Lay.putConstraint(SpringLayout.EAST, InputField, -5, SpringLayout.EAST, this);
            Lay.putConstraint(SpringLayout.SOUTH, InputField, -5, SpringLayout.SOUTH, this);
            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        }

        HelpButton.setMessage(RME.HelpDescription);
        HelpButton.setTitle("Help for: " + RME.DisplayName);

        Lay.putConstraint(SpringLayout.NORTH, DisplayNameLabel, 5, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, DisplayNameLabel, 5, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.EAST, DisplayNameLabel, 0, SpringLayout.WEST, HelpButton);

        Lay.putConstraint(SpringLayout.NORTH, IDNameLabel, 5, SpringLayout.SOUTH, DisplayNameLabel);
        Lay.putConstraint(SpringLayout.WEST, IDNameLabel, 5, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.EAST, IDNameLabel, 5, SpringLayout.WEST, HelpButton);

        Lay.putConstraint(SpringLayout.NORTH, HelpButton, 5, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.EAST, HelpButton, -5, SpringLayout.EAST, this);
        
        Lay.putConstraint(SpringLayout.EAST, removeButton, 0, SpringLayout.EAST, HelpButton);
        Lay.putConstraint(SpringLayout.SOUTH, removeButton, 0, SpringLayout.SOUTH, InputField);

        removeButton.addActionListener(_ -> onRemove.accept(this));
        
        setLayout(Lay);

        setPreferredSize(Size);
        setMaximumSize(Size);
        setSize(Size);

        setBorder(new LineBorder(Color.green));

        add(removeButton);
        add(HelpButton);

        add(DisplayNameLabel);
        if (DisplayNameLabel != IDNameLabel) // only add id one if the names arent the same
            add(IDNameLabel);
        
        add(InputField);

        validate();
    }

    @Nonnull
    protected abstract I createInput();

    public Entry<String, V> getKeyAndVal() {
        return new AbstractMap.SimpleEntry<>(getKey(), getValue());
    }

    public String getKey() {
        return rMapElement.ID;
    }

    public abstract V getValue();

    public abstract void setValue(V val);

    @Override
    public String getProblemMessage() {
        return "";
    }

    @Override
    public String getValueName() {
        return rMapElement.DisplayName;
    }
}
