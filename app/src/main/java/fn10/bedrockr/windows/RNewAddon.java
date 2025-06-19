package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.base.RLoadingScreen;
import fn10.bedrockr.windows.utils.ImageUtilites;
import fn10.bedrockr.windows.utils.RFonts;

public class RNewAddon extends RDialog implements ActionListener {

    protected final static String[] PICKABLE_VERSIONS = {
            "1.21.50",
            "1.21.60",
            "1.21.70",
            "1.21.80",
            "1.21.90"
    };
    private JLabel AddonIcon;
    private ImageIcon ChosenIcon = ImageUtilites.ResizeImageByURL(getClass().getResource("/addons/DefaultIcon.png"),
            250, 250);

    public RNewAddon(JFrame Parent) {
        super(Parent,
                DISPOSE_ON_CLOSE,
                "New Addon",
                new Dimension(459, 350));

        AddonIcon = new JLabel(ChosenIcon);
        AddonIcon.setSize(new Dimension(300, 300));
        AddonIcon.setHorizontalAlignment(SwingConstants.CENTER);
        AddonIcon.setVerticalAlignment(SwingConstants.CENTER);
        AddonIcon.setBorder(BorderFactory.createLineBorder(getForeground(), 3));

        var NameInputText = new JLabel("Addon Name");
        var NameInput = new JTextField("New AddonR");
        NameInput.setPreferredSize(new Dimension(150, 25));

        var DescInputText = new JLabel("Addon Description");
        var DescInput = new JTextArea("My new addon, made in bedrockR");
        DescInput.setPreferredSize(new Dimension(150, 100));
        DescInput.setLineWrap(true);
        DescInput.setFont(RFonts.RegMinecraftFont.deriveFont(1, 10));
        DescInput.setWrapStyleWord(true);

        var MinimumEngineVersionSelectionText = new JLabel("Minimum Engine Version");
        var MinimumEngineVersionSelection = new JComboBox<String>(PICKABLE_VERSIONS);
        MinimumEngineVersionSelection.setSize(new Dimension(150, 25));

        var PickIconButton = new JButton("Change Icon...");
        PickIconButton.addActionListener(this);
        PickIconButton.setActionCommand("changeIcon");

        var CreateButton = new JButton("Create Addon!");
        CreateButton.addActionListener(this);
        CreateButton.setActionCommand("create");
        CreateButton.setPreferredSize(new Dimension(421,30));

        // constraints: icon
        Lay.putConstraint(SpringLayout.NORTH, AddonIcon, 10, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, AddonIcon, 10, SpringLayout.WEST, this);
        
        // name input
        Lay.putConstraint(SpringLayout.SOUTH, NameInputText, -1, SpringLayout.NORTH, NameInput);
        Lay.putConstraint(SpringLayout.WEST, NameInputText, 13, SpringLayout.EAST, AddonIcon);

        Lay.putConstraint(SpringLayout.NORTH, NameInput, 25, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, NameInput, 13, SpringLayout.EAST, AddonIcon);
        
        // desc input
        Lay.putConstraint(SpringLayout.SOUTH, DescInputText, -1, SpringLayout.NORTH, DescInput);
        Lay.putConstraint(SpringLayout.WEST, DescInputText, 13, SpringLayout.EAST, AddonIcon);

        Lay.putConstraint(SpringLayout.WEST, DescInput, 13, SpringLayout.EAST, AddonIcon);
        Lay.putConstraint(SpringLayout.NORTH, DescInput, 18, SpringLayout.SOUTH, NameInput);
        
        // min engine input
        Lay.putConstraint(SpringLayout.SOUTH, MinimumEngineVersionSelectionText, -1, SpringLayout.NORTH,
                MinimumEngineVersionSelection);
        Lay.putConstraint(SpringLayout.WEST, MinimumEngineVersionSelectionText, 13, SpringLayout.EAST, AddonIcon);

        Lay.putConstraint(SpringLayout.WEST, MinimumEngineVersionSelection, 13, SpringLayout.EAST, AddonIcon);
        Lay.putConstraint(SpringLayout.NORTH, MinimumEngineVersionSelection, 18, SpringLayout.SOUTH, DescInput);
        
        // pick icon button
        Lay.putConstraint(SpringLayout.WEST, PickIconButton, 13, SpringLayout.EAST, AddonIcon);
        Lay.putConstraint(SpringLayout.NORTH, PickIconButton, 14, SpringLayout.SOUTH, MinimumEngineVersionSelection);

        //create button
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, CreateButton, 115, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, CreateButton, -6, SpringLayout.HORIZONTAL_CENTER, this);

        add(AddonIcon);
        add(NameInput);
        add(NameInputText);
        add(DescInputText);
        add(DescInput);
        add(MinimumEngineVersionSelectionText);
        add(MinimumEngineVersionSelection);
        add(PickIconButton);
        add(CreateButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "changeIcon") {
            try {
                var file = new JFileChooser();
                file.setDialogTitle("Choose Addon Icon");
                file.setFileFilter(
                        new FileNameExtensionFilter("Image Files (*.png,*.jpg/jpeg)", "png", "jpg", "jpeg", "tga"));
                file.showOpenDialog(this);
                ChosenIcon = ImageUtilites.ResizeIcon(new ImageIcon(ImageIO.read(file.getSelectedFile())), 250, 250);
                AddonIcon.setIcon(ChosenIcon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand() == "create") {

            var loading = new RLoadingScreen((JFrame)getParent());
            loading.setVisible(true);

        } else {
            Launcher.LOG.warning("No action event! "+getClass().getName());
        }
    }

}
