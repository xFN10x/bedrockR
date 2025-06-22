package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.RFileOperations;

public class RAddon extends JPanel {

    private final static Color BGC = ColorFunctions.darken(new Color(30, 30, 30), 0.01f);
    private BoxLayout Lay = new BoxLayout((Container) this, BoxLayout.Y_AXIS);

    public RAddon(String WPName) throws IOException { //TODO: to tired to add try catch rn
        super();

        //dirty line of code coming up... "varibles? never hear of 'er"
        SourceWPFile WPFile =
            new SourceWPFile(Files.readString(RFileOperations
            .getFileFromWorkspace(this, WPName, "/workspace.RWP",true)
            .toPath()));
        WPFile WPF = (WPFile)WPFile.getSerilized();

        setLayout(Lay);
        setPreferredSize(new Dimension(50, 50));
        setBackground(BGC);
        setBorder(new FlatLineBorder(new Insets(16, 16, 16, 16), Color.WHITE, 1, 16));

        var iconFile = RFileOperations.getFileFromWorkspace(this,WPName,"/icon."+WPF.IconExtension,true);
        iconFile.setReadable(true);
        Launcher.LOG.info(iconFile.getAbsolutePath());

        var Icon = new JLabel(new ImageIcon(ImageIO.read(iconFile)));
        Icon.setAlignmentX(CENTER_ALIGNMENT);

        var Name = new JLabel(WPName);
        Icon.setAlignmentX(CENTER_ALIGNMENT);

        var Div = new JSeparator();
        Div.setSize(new Dimension(getWidth(), 3));
        Div.setAlignmentX(CENTER_ALIGNMENT);

        add(Icon);
        
        add(Div);

        add(Name);
    }

}
