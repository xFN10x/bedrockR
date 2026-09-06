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

    public static RMapValue<?, ?> ofElement(Window Ancestor, RMapElement rMapElement) {
        if (rMapElement instanceof RStringDropdownMapElement rsd) {
            return new RMStringDropdownValue(Ancestor, rsd);
        } else if (are(rMapElement, BlockComponents.BlockCanBeMined.class)) {
            return new RMBDestructibleByMining(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.Climate.class)) {
            return new RMBiClimate(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.CreatureSpawnProbablity.class)) {
            return new RMBiCreatureSpawnChance(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.Humidity.class)) {
            return new RMBiHumidity(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.MapTints.class)) {
            return new RMBiMapTints(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.ReplaceBiomes.class)) {
            return new RMBiReplaceBiomes(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.SurfaceBuilder.class)) {
            return new RMBiSurfaceBuilder(Ancestor, rMapElement);
        } else if (are(rMapElement, BiomeComponents.Tags.class)) {
            return new RMBiTags(Ancestor, rMapElement);
        } else if (are(rMapElement, ItemComponents.ItemBlockPlacer.class)) {
            return new RMIBlockPlacer(Ancestor, rMapElement);
        } else if (are(rMapElement, ItemComponents.ToolDamage.class)) {
            return new RMIDamageValue(Ancestor, rMapElement);
        }
        return new RMAutoValue<>(Ancestor, rMapElement);
        
    }

    protected boolean hasFilter(RMapElement.MapValueFilter filter) {
        return rMapElement.Filters.contains(filter);
    }

    public RMapValue(Window Ancestor, RMapElement RME) {

        this.rMapElement = RME;
        this.Ancestor = Ancestor;
        this.InputField = createInput();

        DisplayNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 16 - ((float) RME.DisplayName.length() / 10)));
        DisplayNameLabel.setText(RME.DisplayName);

        IDNameLabel.setText(RME.ID);
        IDNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 12 - ((float) RME.DisplayName.length() / 10)));

        // check for custom ones first


//        } else if (RME.Type == Tags.class) {
//            Size.setSize(600, 500);
//            try {

//            } catch (NoSuchFieldException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        // set input field to whatever is nessesary
//        else if (RME.Type == String.class) { // string
//            InputField = new JTextField();
//        } else if (RME.Type == Integer.class || RME.Type == int.class || RME.Type == double.class
//                || RME.Type == Double.class) { // int, DOUBLES WILL BE TREATED AS INTS
//            InputField = new JSpinner();
//
//            if (RME.Filters.contains(MapValueFilter.NotNegative))
//                ((JSpinner) InputField).addChangeListener(c -> {
//                    if (((Integer) ((JSpinner) InputField).getValue()) < 0) {
//                        ((JSpinner) InputField).setValue(0);
//                    }
//                });
//        } else if (RME.Type == Float.class || RME.Type == float.class) { // float
//            if (RME.Filters.contains(MapValueFilter.Between0And1))
//                InputField = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.1));
//            else
//                InputField = new JSpinner(new SpinnerNumberModel(0, -2147483648f, 2147483647f, 0.1));
//        } else if (RME.Type.isArray()) { // array
//            InputField = new JLabel("Array input not implemented.");
//        } else if (RME.Type == Boolean.class || RME.Type == boolean.class) { // bool
//            String[] ars = {"true", "false"};
//            InputField = new JComboBox<String>(ars);
//        } else { // else
//            InputField = new JLabel("Unknown input type: " + RME.Type.getName());
//            ((JLabel) InputField).setToolTipText("Unknown input type: " + RME.Type.getName());
//        }
        if (!(InputField instanceof RItemValue)) {
            InputField.setMinimumSize(new Dimension(0, 70));
            Lay.putConstraint(SpringLayout.WEST, InputField, 5, SpringLayout.WEST, this);
            Lay.putConstraint(SpringLayout.EAST, InputField, -5, SpringLayout.EAST, this);
            Lay.putConstraint(SpringLayout.SOUTH, InputField, -5, SpringLayout.SOUTH, this);
            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        }
//
//        for (Entry<String, Component> entry : MultipleInputs.entrySet()) {
//            if (entry.getValue() instanceof RItemValue riv) {
//                try {
//                    riv.setItem(ReturnItemInfo.getBlockById("minecraft:air",
//                            RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//                } catch (NameNotFoundException | IncorrectWorkspaceException | IOException e) {
//                    ErrorShower.exception(Ancestor, e);
//                }
//            }
//        }
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

        setLayout(Lay);

        setPreferredSize(Size);
        setMaximumSize(Size);
        setSize(Size);

        setBorder(new LineBorder(Color.green));

        add(HelpButton);
        add(InputField);

        add(DisplayNameLabel);
        if (DisplayNameLabel != IDNameLabel) // only add id one if the names arent the same
            add(IDNameLabel);

        validate();
    }

    @Nonnull
    protected abstract I createInput();

//    @SuppressWarnings("unchecked")
//    public void setVal(Object val) {
//        try {
//            if (val instanceof LinkedTreeMap lrm) {
//                val = gson.fromJson(gson.toJsonTree(lrm), rMapElement.Type);
//            }
//            RFileOperations.LOG.info("Setting Value to class: " + val.getClass().getName());
//            if (rMapElement.Type == Climate.class && val instanceof Climate climate) {
//
//                ((JSpinner) MultipleInputs.get("downfallVal")).setValue(climate.downfall);
//
//                ((JSpinner) MultipleInputs.get("snowfallMax")).setValue((int) (climate.snow_accumulation[1] / 0.125f));
//
//                ((JSpinner) MultipleInputs.get("snowfallMin")).setValue((int) (climate.snow_accumulation[0] / 0.125f));
//
//                ((JSpinner) MultipleInputs.get("tempVal")).setValue(climate.temperature);
//
//            } else if (rMapElement.Type == CreatureSpawnProbablity.class
//                    && val instanceof CreatureSpawnProbablity creatureSpawnProbablity) {
//
//                ((JSpinner) InputField).setValue(creatureSpawnProbablity.probability);
//
//            } else if (rMapElement.Type == Humidity.class && val instanceof Humidity humidity) {
//
//                ((JComboBox<String>) InputField).setSelectedIndex(humidity.is_humid ? 0 : 1);
//
//            } else if (rMapElement.Type == MapTints.class && val instanceof MapTints mapTints) {
//
//                MultipleInputs.get("foliageColour").setForeground(Color.decode(mapTints.foliage));
//                MultipleInputs.get("grassColour").setForeground(Color.decode(mapTints.grass.tint));
//
//            } else if (rMapElement.Type == ReplaceBiomes.class && val instanceof ReplaceBiomes replaceBiomes) {
//
//                
//
//            } else if (rMapElement.Type == SurfaceBuilder.class && val instanceof SurfaceBuilder surfaceBuilder) {
//                ((JSpinner) MultipleInputs.get("seaDepthVal")).setValue(surfaceBuilder.builder.sea_floor_depth);
//
//                ((RItemValue) MultipleInputs.get("midMaterialVal")).setItem(ReturnItemInfo.getItemById(
//                        surfaceBuilder.builder.mid_material, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//
//                ((RItemValue) MultipleInputs.get("foundationMaterialVal")).setItem(ReturnItemInfo.getItemById(
//                        surfaceBuilder.builder.foundation_material,
//                        RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//
//                ((RItemValue) MultipleInputs.get("seaMaterialVal")).setItem(ReturnItemInfo.getItemById(
//                        surfaceBuilder.builder.sea_material, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//
//                ((RItemValue) MultipleInputs.get("seaFloorMaterialVal")).setItem(ReturnItemInfo.getItemById(
//                        surfaceBuilder.builder.sea_floor_material,
//                        RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//
//                ((RItemValue) MultipleInputs.get("surfaceMaterialVal")).setItem(ReturnItemInfo.getItemById(
//                        surfaceBuilder.builder.top_material, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//
//            } else if (rMapElement.Type == Tags.class && val instanceof Tags tags) {
//
//                ((RElementValue) InputField).setValue(tags.tags);
//
//            } else if (rMapElement instanceof RStringDropdownMapElement) {
//                ((JComboBox<String>) InputField).setSelectedItem(val);
//            } else if (rMapElement.Type == minecraftDamage.class) { // minecraft:damage
//                ((JSpinner) InputField).setValue(val);
//            } else if (rMapElement.Type == minecraftDestructibleByMining.class) { // minecraft:destructible_by_mining
//                if (val instanceof LinkedTreeMap) {
//                    val = gson.fromJson(gson.toJsonTree(val), minecraftBlockPlacer.class);
//                }
//                ((JSpinner) InputField)
//                        .setValue(((minecraftDestructibleByMining) val).seconds_to_destroy);
//            } else if (rMapElement.Type == minecraftBlockPlacer.class) {// minecraft:block_placer
//                if (val instanceof LinkedTreeMap) {
//                    val = gson.fromJson(gson.toJsonTree(val), minecraftBlockPlacer.class);
//                }
//                ((RItemValue) InputField).setButtonToItem(0, ReturnItemInfo.getBlockById(
//                        ((minecraftBlockPlacer) val).block, RFileOperations.getCurrentWorkspace().WorkspaceName, ImageUtilities.ImgHandler));
//            } else if (rMapElement.Type == String.class) { // string
//                ((JTextField) InputField).setText(((String) val));
//            } else if (rMapElement.Type == Integer.class || rMapElement.Type == int.class
//                    || rMapElement.Type == double.class || rMapElement.Type == Double.class) { // int
//
//                if (val instanceof LazilyParsedNumber)
//                    ((JSpinner) InputField).setValue(((LazilyParsedNumber) val).intValue());
//                else
//                    ((JSpinner) InputField).setValue(val);
//            } else if (rMapElement.Type == Float.class || rMapElement.Type == float.class) { // float
//                ((JSpinner) InputField).setValue(val);
//
//            } else if (rMapElement.Type == Boolean.class || rMapElement.Type == boolean.class) { // bool
//                ((JComboBox<String>) InputField).setSelectedIndex(((Boolean) val) ? 0 : 1);
//            } else { // else
//                throw new IllegalArgumentException(
//                        "Class " + val.getClass().getSimpleName() + ", cannot be used on this RElementMapValue of type "
//                                + rMapElement.Type.getSimpleName());
//            }
//        } catch (Exception e) {
//            RLogUtils.exception("Exception thrown", e);
//            ErrorShower.showError(this, "Failed to set value of map value. Type: ${}", e.getMessage(), e);
//        }
//    }

    public Entry<String, V> getKeyAndVal() {
//        Object val = null;
//        if (!(InputField instanceof JLabel))
//            try {
//
//                //RFileOperations.LOG.info(rMapElement.Type.getName());
//                if (rMapElement instanceof RStringDropdownMapElement) {
//                    val = ((JComboBox<String>) InputField).getSelectedItem();
//                }
//
//                if (rMapElement.Type != null) {
//                    if (rMapElement.Type == Climate.class) {
//                        val = new Climate();
//
//                        ((Climate) val).downfall = ((Number) ((JSpinner) MultipleInputs.get("downfallVal")).getValue())
//                                .floatValue();
//                        ((Climate) val).snow_accumulation = new float[]{
//                                (1f / 8f) * ((Number) ((JSpinner) MultipleInputs.get("snowfallMin")).getValue())
//                                        .floatValue(),
//                                (1f / 8f) * ((Number) ((JSpinner) MultipleInputs.get("snowfallMax")).getValue())
//                                        .floatValue()};
//
//                        ((Climate) val).temperature =
//
//                                ((Number) ((JSpinner) MultipleInputs.get("tempVal")).getValue()).floatValue();
//
//                    } else if (rMapElement.Type == CreatureSpawnProbablity.class) {
//                        val = new CreatureSpawnProbablity();
//                        ((CreatureSpawnProbablity) val).probability = ((Number) ((JSpinner) InputField).getValue())
//                                .floatValue();
//
//                    } else if (rMapElement.Type == Humidity.class) {
//
//                        val = new Humidity();
//                        ((Humidity) val).is_humid = ((JComboBox<String>) InputField).getSelectedIndex() == 0;
//
//                    } else if (rMapElement.Type == MapTints.class) {
//                        val = new MapTints();
//                        GrassTint grassTint = new GrassTint();
//
//                        Color foliageColor = MultipleInputs.get("foliageColour").getForeground();
//                        ((MapTints) val).foliage = String.format("#%02x%02x%02x", foliageColor.getRed(),
//                                foliageColor.getGreen(),
//                                foliageColor.getBlue());
//
//                        Color grassColor = MultipleInputs.get("grassColour").getForeground();
//                        grassTint.tint = String.format("#%02x%02x%02x", grassColor.getRed(),
//                                grassColor.getGreen(),
//                                grassColor.getBlue());
//
//                        grassTint.type = "tint";
//
//                        ((MapTints) val).grass = grassTint;
//
//                    } else if (rMapElement.Type == ReplaceBiomes.class) {
//                        val = new ReplaceBiomes();
//                        Replacement replacement = new Replacement();
//
//                        replacement.amount = ((Number) ((JSpinner) MultipleInputs.get("replacementVal")).getValue())
//                                .floatValue();
//                        replacement.noise_frequency_scale = ((Number) ((JSpinner) MultipleInputs.get("noiseVal"))
//                                .getValue()).floatValue();
//                        replacement.targets = (List<String>) ((RElementValue<?, ?>) MultipleInputs.get("targetsVal")).getValue();
//
//                        ((ReplaceBiomes) val).replacements = new Replacement[]{replacement};
//                    } else if (rMapElement.Type == SurfaceBuilder.class) {
//                        val = new SurfaceBuilder();
//                        OverworldBuilder builder = new OverworldBuilder();
//
//                        builder.sea_floor_depth = ((Number) ((JSpinner) MultipleInputs.get("seaDepthVal")).getValue())
//                                .intValue();
//                        builder.mid_material = ((RItemValue) MultipleInputs.get("midMaterialVal")).getItems().get(0).item;
//
//                        builder.foundation_material = ((RItemValue) MultipleInputs.get("foundationMaterialVal"))
//                                .getItems().get(0).item;
//
//                        builder.sea_material = ((RItemValue) MultipleInputs.get("seaMaterialVal")).getItems().get(0).item;
//
//                        builder.sea_floor_material = ((RItemValue) MultipleInputs.get("seaFloorMaterialVal")).getItems()
//                                .getFirst().item;
//
//                        builder.top_material = ((RItemValue) MultipleInputs.get("surfaceMaterialVal")).getItems()
//                                .getFirst().item;
//
//                        ((SurfaceBuilder) val).builder = builder;
//
//                    } else if (rMapElement.Type == Tags.class) {
//
//                        val = new Tags();
//                        ((Tags) val).tags = ((REListValue<String>) InputField).getValue();
//
//                    } else if (rMapElement.Type == minecraftDamage.class) { // minecraft:damage
//                        val = new minecraftDamage();
//                        ((minecraftDamage) val).damage = (int) ((JSpinner) InputField).getValue();
//                    } else if (rMapElement.Type == minecraftDestructibleByMining.class) { // minecraft:destructible_by_mining
//                        val = new minecraftDestructibleByMining();
//                        ((minecraftDestructibleByMining) val).seconds_to_destroy = ((Double) ((JSpinner) InputField)
//                                .getValue()).floatValue();
//                    } else if (rMapElement.Type == minecraftBlockPlacer.class) {
//                        val = new minecraftBlockPlacer();
//                        if (((RItemValue) InputField).getItems().isEmpty())
//                            ((minecraftBlockPlacer) val).block = "minecraft:air";
//                        else
//                            ((minecraftBlockPlacer) val).block = ((RItemValue) InputField).getItems().get(0).item;
//                    } else if (rMapElement.Type == String.class) { // string
//                        val = ((JTextField) InputField).getText();
//                    } else if (rMapElement.Type == Integer.class || rMapElement.Type == int.class
//                            || rMapElement.Type == double.class || rMapElement.Type == Double.class) { // int
//                        val = ((JSpinner) InputField).getValue();
//                    } else if (rMapElement.Type == Float.class || rMapElement.Type == float.class) { // float
//                        val = ((JSpinner) InputField).getValue();
//                    } else if (rMapElement.Type.isArray()) { // array
//
//                    } else if (rMapElement.Type == Boolean.class || rMapElement.Type == boolean.class) { // bool
//                        val = (((JComboBox<String>) InputField).getSelectedIndex() == 0);
//                    } else { // else
//                        InputField = new JLabel("Unknown input type:\n" + rMapElement.Type.getName());
//                    }
//                }
//            } catch (Exception e) {
//                RLogUtils.exception("Exception thrown", e);
//                ErrorShower.showError(this, "Failed to get value of Map Entry.", e.getMessage(), e);
//                return null;
//            }
//        else {
//            RFileOperations.LOG.warning("Map value input is a label: " + ((JLabel) InputField).getText());
//        }
//        return new AbstractMap.SimpleEntry<>(rMapElement.ID, val);
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
