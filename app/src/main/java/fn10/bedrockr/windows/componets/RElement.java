package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.addons.source.ElementDetails;

public class RElement extends JPanel {

    public RElement(ElementDetails details) {
        super();

        setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white,1,16));
        setPreferredSize(new Dimension(300,50));
    }
}
