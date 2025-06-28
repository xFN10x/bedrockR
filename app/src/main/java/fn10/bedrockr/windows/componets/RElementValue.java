package fn10.bedrockr.windows.componets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Type;
import javax.swing.border.LineBorder;
import fn10.bedrockr.Launcher;

public class RElementValue extends JPanel {

    private SpringLayout Lay = new SpringLayout();
    private JLabel Name = new JLabel();
    private JButton Help = new JButton(new ImageIcon(getClass().getResource("/ui/Help.png")));
    private Component Input;
    private JCheckBox EnableDis = new JCheckBox();

    private final static Dimension Size = new Dimension(335, 40);
    private String Target = "";

    public RElementValue(@Nonnull Type InputType, String TargetField, String DisplayName, Boolean Optional,
            @Nullable String HelpString) {
        super();

        this.Target = TargetField;
        
        setMaximumSize(Size);
        setPreferredSize(Size);
        setBorder(new LineBorder(Color.DARK_GRAY));
        setLayout(Lay);

        Help.setSize(6, 6);
        Help.putClientProperty("JButton.buttonType", "help" );
        //Help.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 32));

        Launcher.LOG.info(InputType.getTypeName());
        if (InputType.equals(Boolean.class)) {
            String[] vals = { "true", "false" };
            Input = new JComboBox<String>(vals);
        } else {
            Input = new JTextField();
        }

        EnableDis.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                EnableDis.setEnabled(e.getStateChange() == e.SELECTED ? true : false);
            }

        });

        Name.setText(DisplayName);

        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Name, 0, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.WEST, Name, 13, SpringLayout.WEST, this);

        Lay.putConstraint(SpringLayout.WEST, Input, 3, SpringLayout.EAST, Name);
        Lay.putConstraint(SpringLayout.NORTH, Input, 3, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.SOUTH, Input, -3, SpringLayout.SOUTH, this);
        Lay.putConstraint(SpringLayout.EAST, Input, -3, SpringLayout.WEST, EnableDis);

        Lay.putConstraint(SpringLayout.EAST, EnableDis, -3, SpringLayout.EAST, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, EnableDis, 0, SpringLayout.VERTICAL_CENTER, this);

        if (!Optional) {
            EnableDis.setEnabled(false);
        }    

        Launcher.LOG.info(Input.toString());
        add(Help);
        add(Name);
        add(Input);
        add(EnableDis);

        
            
    }
}
