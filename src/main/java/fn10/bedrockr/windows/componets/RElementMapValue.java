package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NameNotFoundException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import com.google.gson.Gson;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.LinkedTreeMap;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.RStringDropdownMapElement;
import fn10.bedrockr.addons.RMapElement.MapValueFilter;
import fn10.bedrockr.addons.source.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.Climate;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.CreatureSpawnProbablity;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.Humidity;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.MapTints;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.ReplaceBiomes;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.ReplaceBiomes.Replacement;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.SurfaceBuilder;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.SurfaceBuilder.OverworldBuilder;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.Tags;
import fn10.bedrockr.addons.source.supporting.BiomeComponents.MapTints.GrassTint;
import fn10.bedrockr.addons.source.supporting.ItemComponents.minecraftBlockPlacer;
import fn10.bedrockr.addons.source.supporting.ItemComponents.minecraftDamage;
import fn10.bedrockr.addons.source.supporting.ItemComponents.minecraftDestructibleByMining;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RFonts;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.windows.RBlockSelector;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RItemSelector.ReturnItemInfo;
import fn10.bedrockr.windows.componets.RItemValue.Type;

public class RElementMapValue extends JPanel {

    private final static Gson gson = SourcelessElementFile.gson;

    private final Dimension Size = new Dimension(240, 80);

    protected final JButton HelpButton = new JButton();
    {
        HelpButton.putClientProperty("JButton.buttonType", "help");
    }
    protected final JLabel DisplayNameLabel = new JLabel();
    protected final JLabel IDNameLabel = new JLabel();
    protected Component InputField = null;
    protected Map<String, Component> MultipleInputs = new HashMap<String, Component>();

    public final RMapElement rMapElement;
    protected final Window Ancestor;

    protected final SpringLayout Lay = new SpringLayout();

    public RElementMapValue(Window Ancestor, RMapElement RME) {

        this.rMapElement = RME;
        this.Ancestor = Ancestor;

        DisplayNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 16 - (RME.DisplayName.length() / 10)));
        DisplayNameLabel.setText(RME.DisplayName);

        IDNameLabel.setText(RME.ID);
        IDNameLabel.setFont(RFonts.RegMinecraftFont.deriveFont(Font.ITALIC, 12 - (RME.DisplayName.length() / 10)));

        // check for custom ones first
        if (RME instanceof RStringDropdownMapElement) {
            String[] ars = ((RStringDropdownMapElement) RME).getChoices();
            InputField = new JComboBox<String>(ars);
        } else if (RME.Type == minecraftDamage.class) { // minecraft:damage
            InputField = new JSpinner();
        } else if (RME.Type == minecraftDestructibleByMining.class) { // minecraft:damage
            if (RME.Filters.contains(MapValueFilter.Between0And1))
                InputField = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.1));
            else
                InputField = new JSpinner(new SpinnerNumberModel(0, -2147483648f, 2147483647f, 0.1));
        } else if (RME.Type == minecraftBlockPlacer.class) {
            InputField = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName, Type.SingleBlock, true);
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, InputField, 0, SpringLayout.VERTICAL_CENTER, this);
            Lay.putConstraint(SpringLayout.EAST, InputField, -4, SpringLayout.EAST, this);
        } else if (RME.Type == Climate.class) {
            Size.setSize(400, 150);
            InputField = new JPanel();
            ((JPanel) InputField).setLayout(new BoxLayout((JPanel) InputField, BoxLayout.Y_AXIS));

            JPanel downfallPanel = new JPanel();
            downfallPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JSpinner downfallVal = new JSpinner(new SpinnerNumberModel(0.25f, 0f, 1f, 0.01f));
            downfallPanel.add(new JLabel("Downfall"));
            downfallPanel.add(downfallVal);
            MultipleInputs.put("downfallVal", downfallVal);
            ((JPanel) InputField).add(downfallPanel);

            JPanel snowfallPanel = new JPanel();
            snowfallPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JSpinner snowfallMax = new JSpinner(new SpinnerNumberModel(2, 0, 8, 1));
            JSpinner snowfallMin = new JSpinner(new SpinnerNumberModel(0, 0, 8, 1));
            snowfallPanel.add(new JLabel("Snowfall Accumulation"));
            snowfallPanel.add(new JLabel("<html><i>Max</i></html>"));
            snowfallPanel.add(snowfallMax);
            snowfallPanel.add(new JLabel("<html><i>Min</i></html>"));
            snowfallPanel.add(snowfallMin);
            MultipleInputs.put("snowfallMax", snowfallMax);
            MultipleInputs.put("snowfallMin", snowfallMin);
            ((JPanel) InputField).add(snowfallPanel);

            JPanel tempPanel = new JPanel();
            tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JSpinner tempVal = new JSpinner(new SpinnerNumberModel(0.8f, 0f, 2f, 0.1f));
            tempPanel.add(new JLabel("Temperature"));
            tempPanel.add(tempVal);
            MultipleInputs.put("tempVal", tempVal);
            ((JPanel) InputField).add(tempPanel);

            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        } else if (RME.Type == CreatureSpawnProbablity.class) {
            InputField = new JSpinner(new SpinnerNumberModel(0.1f, 0f, 0.75f, 0.01f));
        } else if (RME.Type == Humidity.class) {
            String[] ars = { "true", "false" };
            InputField = new JComboBox<String>(ars);
        } else if (RME.Type == MapTints.class) {
            Size.setSize(400, 150);
            InputField = new JPanel();
            ((JPanel) InputField).setLayout(new BoxLayout((JPanel) InputField, BoxLayout.Y_AXIS));

            JPanel foliagePanel = new JPanel();
            foliagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JButton foliageColour = new JButton("Select Colour");
            foliageColour.addActionListener(ac -> {
                foliageColour
                        .setForeground(JColorChooser.showDialog(foliageColour, "Select Foliage Colour", Color.green));
            });
            foliagePanel.add(new JLabel("Foliage Colour"));
            foliagePanel.add(foliageColour);
            MultipleInputs.put("foliageColour", foliageColour);
            ((JPanel) InputField).add(foliagePanel);

            JPanel grassPanel = new JPanel();
            grassPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JButton grassColour = new JButton("Select Colour");
            grassColour.addActionListener(ac -> {
                grassColour.setForeground(JColorChooser.showDialog(grassColour, "Select Grass Colour", Color.green));
            });
            grassPanel.add(new JLabel("Grass Colour"));
            grassPanel.add(grassColour);
            MultipleInputs.put("grassColour", grassColour);
            ((JPanel) InputField).add(grassPanel);

            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        } else if (RME.Type == ReplaceBiomes.class) {
            Size.setSize(400, 250);
            InputField = new JPanel();
            ((JPanel) InputField).setLayout(new BoxLayout((JPanel) InputField, BoxLayout.Y_AXIS));

            JPanel replacementPercentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JSpinner replacementVal = new JSpinner(new SpinnerNumberModel(0.25f, 0f, 1f, 0.01f));
            replacementPercentPanel.add(new JLabel("Replacement %"));
            replacementPercentPanel.add(replacementVal);
            MultipleInputs.put("replacementVal", replacementVal);
            ((JPanel) InputField).add(replacementPercentPanel);

            JPanel noisePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JSpinner noiseVal = new JSpinner(new SpinnerNumberModel(0.25f, 0f, 1f, 0.01f));
            noisePanel.add(new JLabel("Noise Frequency Scale"));
            noisePanel.add(noiseVal);
            MultipleInputs.put("noiseVal", noiseVal);
            ((JPanel) InputField).add(noisePanel);

            RElementValue targetsVal = new RElementValue(Ancestor, new ArrayList<String>().getClass(), null, "targets",
                    "Replace Biomes", false,
                    Replacement.class, null, true, null);
            MultipleInputs.put("targetsVal", targetsVal);
            ((JPanel) InputField).add(targetsVal);

            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        } else if (RME.Type == SurfaceBuilder.class) {
            Size.setSize(400, 500);
            InputField = new JPanel();
            ((JPanel) InputField).setLayout(new BoxLayout((JPanel) InputField, BoxLayout.Y_AXIS));

            JPanel seaFloorDepthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JSpinner seaDepthVal = new JSpinner(new SpinnerNumberModel(1, 0, 127, 1));
            seaFloorDepthPanel.add(new JLabel("Sea Floor Depth"));
            seaFloorDepthPanel.add(seaDepthVal);
            ((JPanel) InputField).add(seaFloorDepthPanel);

            JPanel seaMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            RItemValue seaMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                    RItemValue.Type.SingleBlock);
            seaMaterialPanel.add(new JLabel("Sea Block"));
            seaMaterialPanel.add(seaMaterialVal);
            ((JPanel) InputField).add(seaMaterialPanel);

            JPanel seaFloorMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            RItemValue seaFloorMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                    RItemValue.Type.SingleBlock);
            seaFloorMaterialPanel.add(new JLabel("Sea Floor Block"));
            seaFloorMaterialPanel.add(seaFloorMaterialVal);
            ((JPanel) InputField).add(seaFloorMaterialPanel);

            JPanel foundationMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            RItemValue foundationMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                    RItemValue.Type.SingleBlock);
            foundationMaterialPanel.add(new JLabel("Underground Block"));
            foundationMaterialPanel.add(foundationMaterialVal);
            ((JPanel) InputField).add(foundationMaterialPanel);

            JPanel midMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            RItemValue midMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                    RItemValue.Type.SingleBlock);
            midMaterialPanel.add(new JLabel("Ground Block"));
            midMaterialPanel.add(midMaterialVal);
            ((JPanel) InputField).add(midMaterialPanel);

            JPanel surfaceMaterialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            RItemValue surfaceMaterialVal = new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName,
                    RItemValue.Type.SingleBlock);
            surfaceMaterialPanel.add(new JLabel("Surface Block"));
            surfaceMaterialPanel.add(surfaceMaterialVal);
            MultipleInputs.put("midMaterialVal", midMaterialVal);
            MultipleInputs.put("foundationMaterialVal", foundationMaterialVal);
            MultipleInputs.put("seaDepthVal", seaDepthVal);
            MultipleInputs.put("seaMaterialVal", seaMaterialVal);
            MultipleInputs.put("seaFloorMaterialVal", seaFloorMaterialVal);
            MultipleInputs.put("surfaceMaterialVal", surfaceMaterialVal);
            ((JPanel) InputField).add(surfaceMaterialPanel);

            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        } else if (RME.Type == Tags.class) {
            Size.setSize(400, 500);
            InputField = new RElementValue(Ancestor, new ArrayList<String>().getClass(), null, "targets",
                    "Replace Biomes", false,
                    Tags.class, null, true, null);

            Lay.putConstraint(SpringLayout.NORTH, InputField, 5, SpringLayout.SOUTH, IDNameLabel);
        }
        // set input field to whatever is nessesary
        else if (RME.Type == String.class) { // string
            InputField = new JTextField();
        } else if (RME.Type == Integer.class || RME.Type == int.class || RME.Type == double.class
                || RME.Type == Double.class) { // int, DOUBLES WILL BE TREATED AS INTS
            InputField = new JSpinner();

            if (RME.Filters.contains(MapValueFilter.NotNegitive))
                ((JSpinner) InputField).addChangeListener(c -> {
                    if (((Integer) ((JSpinner) InputField).getValue()) < 0) {
                        ((JSpinner) InputField).setValue(0);
                    }
                });
        } else if (RME.Type == Float.class || RME.Type == float.class) { // float
            if (RME.Filters.contains(MapValueFilter.Between0And1))
                InputField = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.1));
            else
                InputField = new JSpinner(new SpinnerNumberModel(0, -2147483648f, 2147483647f, 0.1));
        } else if (RME.Type.isArray()) { // array
            InputField = new JLabel("Array input not implemented.");
        } else if (RME.Type == Boolean.class || RME.Type == boolean.class) { // bool
            String[] ars = { "true", "false" };
            InputField = new JComboBox<String>(ars);
        } else { // else
            if (RME.Type == null) {
                InputField = new JLabel("Input type is null.");
            } else {
                InputField = new JLabel("Unknown input type: " + RME.Type.getName());
                ((JLabel) InputField).setToolTipText("Unknown input type: " + RME.Type.getName());
            }

        }
        if (!(InputField instanceof RItemValue)) {
            InputField.setMinimumSize(new Dimension(0, 70));
            Lay.putConstraint(SpringLayout.WEST, InputField, 5, SpringLayout.WEST, this);
            Lay.putConstraint(SpringLayout.EAST, InputField, -5, SpringLayout.EAST, this);
            Lay.putConstraint(SpringLayout.SOUTH, InputField, -5, SpringLayout.SOUTH, this);
        }

        for (Entry<String, Component> entry : MultipleInputs.entrySet()) {
            if (entry.getValue() instanceof RItemValue riv) {
                try {
                    riv.setItem(RBlockSelector.getBlockById(Ancestor, "minecraft:air", RFileOperations.getCurrentWorkspace().WorkspaceName));
                } catch (NameNotFoundException | IncorrectWorkspaceException e) {
                    ErrorShower.exception(Ancestor, e);
                }
            }
        }

        HelpButton.addActionListener(
                (e) -> JOptionPane.showMessageDialog(this, RME.HelpDescription, "Help for: " + RME.DisplayName,
                        JOptionPane.INFORMATION_MESSAGE));

        Lay.putConstraint(SpringLayout.NORTH, DisplayNameLabel, 5, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, DisplayNameLabel, 5, SpringLayout.WEST, this);

        Lay.putConstraint(SpringLayout.NORTH, IDNameLabel, 5, SpringLayout.SOUTH, DisplayNameLabel);
        Lay.putConstraint(SpringLayout.WEST, IDNameLabel, 5, SpringLayout.WEST, this);

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

    @SuppressWarnings("unchecked")
    public void setVal(Object val) {
        try {
            Launcher.LOG.info("Setting Value to class: " + val.getClass().getName());
            if (val instanceof LinkedTreeMap lrm) {
                val = gson.fromJson(gson.toJsonTree(lrm), rMapElement.Type);
            }
            if (rMapElement.Type == Climate.class && val instanceof Climate climate) {

                ((JSpinner) MultipleInputs.get("downfallVal")).setValue(climate.downfall);

                ((JSpinner) MultipleInputs.get("snowfallMax")).setValue(climate.snow_accumulation[1]);

                ((JSpinner) MultipleInputs.get("snowfallMin")).setValue(climate.snow_accumulation[0]);

                ((JSpinner) MultipleInputs.get("tempVal")).setValue(climate.temperature);

            } else if (rMapElement.Type == CreatureSpawnProbablity.class
                    && val instanceof CreatureSpawnProbablity creatureSpawnProbablity) {

                ((JSpinner) InputField).setValue(creatureSpawnProbablity.probability);

            } else if (rMapElement.Type == Humidity.class && val instanceof Humidity humidity) {

                ((JCheckBox) InputField).setSelected(humidity.is_humid);

            } else if (rMapElement.Type == MapTints.class && val instanceof MapTints mapTints) {

                ((JButton) MultipleInputs.get("foliageColour")).setForeground(Color.decode(mapTints.foliage));
                ((JButton) MultipleInputs.get("grassColour")).setForeground(Color.decode(mapTints.grass.tint));

            } else if (rMapElement.Type == ReplaceBiomes.class && val instanceof ReplaceBiomes replaceBiomes) {

                ((JSpinner) MultipleInputs.get("replacementVal")).setValue(replaceBiomes.replacements.amount);
                ((JSpinner) MultipleInputs.get("noiseVal")).setValue(replaceBiomes.replacements.noise_frequency_scale);
                ((RElementValue) MultipleInputs.get("targetsVal")).setValue(replaceBiomes.replacements.targets);

            } else if (rMapElement.Type == SurfaceBuilder.class && val instanceof SurfaceBuilder surfaceBuilder) {
                ((JSpinner) MultipleInputs.get("seaDepthVal")).setValue(surfaceBuilder.builder.sea_floor_depth);

                ((RItemValue) MultipleInputs.get("midMaterialVal")).setItem(RItemSelector.getItemById(Ancestor,
                        surfaceBuilder.builder.mid_material, RFileOperations.getCurrentWorkspace().WorkspaceName));

                ((RItemValue) MultipleInputs.get("foundationMaterialVal")).setItem(RItemSelector.getItemById(Ancestor,
                        surfaceBuilder.builder.foundation_material,
                        RFileOperations.getCurrentWorkspace().WorkspaceName));

                ((RItemValue) MultipleInputs.get("seaMaterialVal")).setItem(RItemSelector.getItemById(Ancestor,
                        surfaceBuilder.builder.sea_material, RFileOperations.getCurrentWorkspace().WorkspaceName));

                ((RItemValue) MultipleInputs.get("seaFloorMaterialVal")).setItem(RItemSelector.getItemById(Ancestor,
                        surfaceBuilder.builder.sea_floor_material,
                        RFileOperations.getCurrentWorkspace().WorkspaceName));

                ((RItemValue) MultipleInputs.get("surfaceMaterialVal")).setItem(RItemSelector.getItemById(Ancestor,
                        surfaceBuilder.builder.sea_material, RFileOperations.getCurrentWorkspace().WorkspaceName));

            } else if (rMapElement.Type == Tags.class && val instanceof Tags tags) {

                ((RElementValue) InputField).setValue(tags.tags);

            } else if (rMapElement instanceof RStringDropdownMapElement) {
                ((JComboBox<String>) InputField).setSelectedItem(val);
                return;
            } else if (rMapElement.Type == minecraftDamage.class) { // minecraft:damage
                ((JSpinner) InputField).setValue(val);
            } else if (rMapElement.Type == minecraftDestructibleByMining.class) { // minecraft:destructible_by_mining
                if (val instanceof LinkedTreeMap) {
                    val = gson.fromJson(gson.toJsonTree(val), minecraftBlockPlacer.class);
                }
                ((JSpinner) InputField)
                        .setValue(((minecraftDestructibleByMining) val).seconds_to_destroy);
            } else if (rMapElement.Type == minecraftBlockPlacer.class) {// minecraft:block_placer
                if (val instanceof LinkedTreeMap) {
                    val = gson.fromJson(gson.toJsonTree(val), minecraftBlockPlacer.class);
                }
                ((RItemValue) InputField).setButtonToItem(0, RBlockSelector.getBlockById(Ancestor,
                        ((minecraftBlockPlacer) val).block, RFileOperations.getCurrentWorkspace().WorkspaceName));
            } else if (rMapElement.Type == String.class) { // string
                ((JTextField) InputField).setText(((String) val));
            } else if (rMapElement.Type == Integer.class || rMapElement.Type == int.class
                    || rMapElement.Type == double.class || rMapElement.Type == Double.class) { // int

                if (val instanceof LazilyParsedNumber)
                    ((JSpinner) InputField).setValue(((LazilyParsedNumber) val).intValue());
                else
                    ((JSpinner) InputField).setValue(val);
            } else if (rMapElement.Type == Float.class || rMapElement.Type == float.class) { // float
                ((JSpinner) InputField).setValue(val);

            } else if (rMapElement.Type == Boolean.class || rMapElement.Type == boolean.class) { // bool
                ((JComboBox<String>) InputField).setSelectedIndex(((Boolean) val) == true ? 0 : 1);
            } else { // else
                throw new IllegalArgumentException(
                        InputField.getClass().getName() + " does not suppot type " + rMapElement.Type.getName());
            }
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(this, "Failed to set value of map value. Type: ${}", e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map.Entry<String, Object> getKeyAndVal() {
        Object val = null;
        try {
            Launcher.LOG.info(rMapElement.Type.getName());
            if (rMapElement instanceof RStringDropdownMapElement) {
                val = ((JComboBox<String>) InputField).getSelectedItem();
            }

            if (rMapElement.Type != null) {
                if (rMapElement.Type == Climate.class) {
                    val = new Climate();

                    ((Climate) val).downfall = ((Double) ((JSpinner) MultipleInputs.get("downfallVal")).getValue())
                            .floatValue();
                    ((Climate) val).snow_accumulation = new float[] {
                            (1f / 8f) * ((Integer) ((JSpinner) MultipleInputs.get("snowfallMin")).getValue())
                                    .floatValue(),
                            (1f / 8f) * ((Integer) ((JSpinner) MultipleInputs.get("snowfallMax")).getValue())
                                    .floatValue() };

                    ((Climate) val).temperature =

                            ((Double) ((JSpinner) MultipleInputs.get("tempVal")).getValue()).floatValue();

                } else if (rMapElement.Type == CreatureSpawnProbablity.class) {
                    val = new CreatureSpawnProbablity();
                    ((CreatureSpawnProbablity) val).probability = ((Double) ((JSpinner) InputField).getValue())
                            .floatValue();

                } else if (rMapElement.Type == Humidity.class) {

                    val = new Humidity();
                    ((Humidity) val).is_humid = ((JComboBox<String>) InputField).getSelectedIndex() == 0 ? true : false;

                } else if (rMapElement.Type == MapTints.class) {
                    val = new MapTints();
                    GrassTint grassTint = new GrassTint();

                    Color foliageColor = ((JButton) MultipleInputs.get("foliageColour")).getForeground();
                    ((MapTints) val).foliage = String.format("#%02x%02x%02x", foliageColor.getRed(),
                            foliageColor.getGreen(),
                            foliageColor.getBlue());

                    Color grassColor = ((JButton) MultipleInputs.get("grassColour")).getForeground();
                    grassTint.tint = String.format("#%02x%02x%02x", grassColor.getRed(),
                            grassColor.getGreen(),
                            grassColor.getBlue());

                    grassTint.type = "tint";

                    ((MapTints) val).grass = grassTint;

                } else if (rMapElement.Type == ReplaceBiomes.class) {
                    val = new ReplaceBiomes();
                    Replacement replacement = new Replacement();

                    replacement.amount = (int) ((JSpinner) MultipleInputs.get("replacementVal")).getValue();
                    replacement.noise_frequency_scale = ((Double) ((JSpinner) MultipleInputs.get("noiseVal"))
                            .getValue()).floatValue();
                    replacement.targets = (List<String>) ((RElementValue) MultipleInputs.get("targetsVal")).getValue();

                    ((ReplaceBiomes) val).replacements = replacement;
                } else if (rMapElement.Type == SurfaceBuilder.class) {
                    val = new SurfaceBuilder();
                    OverworldBuilder builder = new OverworldBuilder();

                    builder.sea_floor_depth = (int) ((JSpinner) MultipleInputs.get("seaDepthVal")).getValue();
                    builder.mid_material = ((RItemValue) MultipleInputs.get("midMaterialVal")).getItems().get(0).item;

                    builder.foundation_material = ((RItemValue) MultipleInputs.get("foundationMaterialVal"))
                            .getItems().get(0).item;

                    builder.sea_material = ((RItemValue) MultipleInputs.get("seaMaterialVal")).getItems().get(0).item;

                    builder.sea_floor_material = ((RItemValue) MultipleInputs.get("seaFloorMaterialVal")).getItems()
                            .get(0).item;

                    builder.top_material = ((RItemValue) MultipleInputs.get("surfaceMaterialVal")).getItems()
                            .get(0).item;

                    ((SurfaceBuilder) val).builder = builder;

                } else if (rMapElement.Type == Tags.class) {

                    val = ((RElementValue) InputField).getValue();

                } else if (rMapElement.Type == minecraftDamage.class) { // minecraft:damage
                    val = new minecraftDamage();
                    ((minecraftDamage) val).damage = (int) ((JSpinner) InputField).getValue();
                } else if (rMapElement.Type == minecraftDestructibleByMining.class) { // minecraft:destructible_by_mining
                    val = new minecraftDestructibleByMining();
                    ((minecraftDestructibleByMining) val).seconds_to_destroy = ((Double) ((JSpinner) InputField)
                            .getValue()).floatValue();
                } else if (rMapElement.Type == minecraftBlockPlacer.class) {
                    val = new minecraftBlockPlacer();
                    if (((RItemValue) InputField).getItems().isEmpty())
                        ((minecraftBlockPlacer) val).block = "minecraft:air";
                    else
                        ((minecraftBlockPlacer) val).block = ((RItemValue) InputField).getItems().get(0).item;
                } else if (rMapElement.Type == String.class) { // string
                    val = ((JTextField) InputField).getText();
                } else if (rMapElement.Type == Integer.class || rMapElement.Type == int.class
                        || rMapElement.Type == double.class || rMapElement.Type == Double.class) { // int
                    val = ((JSpinner) InputField).getValue();
                } else if (rMapElement.Type == Float.class || rMapElement.Type == float.class) { // float
                    val = ((JSpinner) InputField).getValue();
                } else if (rMapElement.Type.isArray()) { // array

                } else if (rMapElement.Type == Boolean.class || rMapElement.Type == boolean.class) { // bool
                    val = (((JComboBox<String>) InputField).getSelectedIndex() == 0);
                } else { // else
                    if (rMapElement.Type == null) {
                        InputField = new JLabel("Input type is null.");
                    } else {
                        InputField = new JLabel("Unknown input type:\n" + rMapElement.Type.getName());
                    }
                }
            }
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(this, "Failed to get value of Map Entry.", e.getMessage(), e);
            return null;
        }
        return new AbstractMap.SimpleEntry<>(rMapElement.ID, val);

    }

}
