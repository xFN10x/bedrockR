package fn10.bedrockr.windows.componets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;

import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

public class RElementValue extends JPanel {

    private SpringLayout Lay = new SpringLayout();
    private JLabel Name = new JLabel();
    private JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));

    private final static Dimension Size = new Dimension(320, 60);

    public RElementValue(Type InputType, String TargetField, String DisplayName,  Boolean Optional, @Nullable String HelpString ) {
        super();
        setMaximumSize(Size);
        setPreferredSize(Size);

        // for testing
        setBorder(new LineBorder(Color.white));

        Help.setSize(30, 30);
        
        Name.setText(DisplayName);

        add(Name);
        add(Help);
    }
}
