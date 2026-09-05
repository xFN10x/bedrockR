package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RMBiMapTints extends RMapValue<BiomeComponents.MapTints, JPanel> {
    private final JPanel foliagePanel = new JPanel();
    private final JButton foliageColour = new JButton("Select Colour");
    private final JPanel grassPanel = new JPanel();
    private final JButton grassColour = new JButton("Select Colour");

    public RMBiMapTints(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
        Size.setSize(400, 150);
    }

    @Override
    protected @NonNull JPanel createInput() {
        var input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));

        foliagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        foliageColour.addActionListener(_ -> {
            foliageColour
                    .setForeground(JColorChooser.showDialog(foliageColour, "Select Foliage Colour", Color.green));
        });
        foliagePanel.add(new JLabel("Foliage Colour"));
        foliagePanel.add(foliageColour);
        input.add(foliagePanel);

        grassPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        grassColour.addActionListener(_ -> {
            grassColour.setForeground(JColorChooser.showDialog(grassColour, "Select Grass Colour", Color.green));
        });
        grassPanel.add(new JLabel("Grass Colour"));
        grassPanel.add(grassColour);
        input.add(grassPanel);

        Lay.putConstraint(SpringLayout.NORTH, input, 5, SpringLayout.SOUTH, IDNameLabel);
        return input;
    }

    @Override
    public BiomeComponents.MapTints getValue() {
        BiomeComponents.MapTints val = new BiomeComponents.MapTints();
        BiomeComponents.MapTints.GrassTint grassTint = new BiomeComponents.MapTints.GrassTint();

        Color foliageColor = foliageColour.getForeground();
        val.foliage = String.format("#%02x%02x%02x", foliageColor.getRed(),
                foliageColor.getGreen(),
                foliageColor.getBlue());

        Color grassColor = grassColour.getForeground();
        grassTint.tint = String.format("#%02x%02x%02x", grassColor.getRed(),
                grassColor.getGreen(),
                grassColor.getBlue());

        grassTint.type = "tint";

        val.grass = grassTint;
        return val;
    }

    @Override
    public void setValue(BiomeComponents.MapTints val) {
        foliageColour.setForeground(Color.decode(val.foliage));
        grassColour.setForeground(Color.decode(val.grass.tint));
    }

    @Override
    public boolean valid(boolean strict) {
        return true;
    }
}
