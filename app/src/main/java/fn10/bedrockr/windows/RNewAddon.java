package fn10.bedrockr.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RFonts;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.base.RLoadingScreen;
import fn10.bedrockr.windows.componets.RAddon;

public class RNewAddon extends RDialog implements ActionListener, DocumentListener {

    protected final static String[] PICKABLE_VERSIONS = {
            "1.21.50",
            "1.21.60",
            "1.21.70",
            "1.21.80",
            "1.21.90"
    };
    protected ImageIcon ChosenIcon = ImageUtilites.ResizeImageByURL(getClass().getResource("/addons/DefaultIcon.png"),
            250, 250);
    protected JFileChooser file = new JFileChooser();
    protected String imageExtension = "png";

    protected JLabel AddonIcon = new JLabel(ChosenIcon);
    protected JTextArea DescInput = new JTextArea("My new addon, made in bedrockR");
    protected JTextField NameInput = new JTextField("New AddonR");
    protected JComboBox<String> MinimumEngineVersionSelection = new JComboBox<String>(PICKABLE_VERSIONS);
    protected JButton CreateButton = new JButton("Create Addon!");

    public RNewAddon(JFrame Parent) {
        super(Parent,
                DISPOSE_ON_CLOSE,
                "New Addon",
                new Dimension(459, 350));

        AddonIcon.setSize(new Dimension(300, 300));
        AddonIcon.setHorizontalAlignment(SwingConstants.CENTER);
        AddonIcon.setVerticalAlignment(SwingConstants.CENTER);
        AddonIcon.setBorder(BorderFactory.createLineBorder(getForeground(), 3));

        var NameInputText = new JLabel("Addon Name");

        NameInput.setPreferredSize(new Dimension(150, 25));
        NameInput.getDocument().addDocumentListener(this);

        var DescInputText = new JLabel("Addon Description");

        DescInput.setPreferredSize(new Dimension(150, 100));
        DescInput.setLineWrap(true);
        DescInput.setFont(RFonts.RegMinecraftFont.deriveFont(1, 10));
        DescInput.setWrapStyleWord(true);

        var MinimumEngineVersionSelectionText = new JLabel("Minimum Engine Version");

        MinimumEngineVersionSelection.setSize(new Dimension(150, 25));

        var PickIconButton = new JButton("Change Icon...");
        PickIconButton.addActionListener(this);
        PickIconButton.setActionCommand("changeIcon");

        CreateButton.addActionListener(this);
        CreateButton.setActionCommand("create");
        CreateButton.setPreferredSize(new Dimension(421, 30));

        

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

        // create button
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

        

        checkForError();
    }

    private static void showError(Component parent, String msg, String title, Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        JOptionPane.showMessageDialog(parent,
                msg + "\n" + sw.toString(),
                title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "changeIcon") {
            try {
                file.setDialogTitle("Choose Addon Icon");
                file.setFileFilter(
                        new FileNameExtensionFilter("Image Files (*.png,*.jpg)", "png", "jpg"));
                file.showOpenDialog(this);
                ChosenIcon = ImageUtilites.ResizeIcon(new ImageIcon(ImageIO.read(file.getSelectedFile())), 250, 250);
                AddonIcon.setIcon(ChosenIcon);
                Launcher.LOG.info(file.getSelectedFile().getName());
                imageExtension = file.getSelectedFile().getName().split("\\.")[1];
                Launcher.LOG.info("Icon extension will be: " + imageExtension);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand() == "create") {

            var name = NameInput.getText();
            Launcher.LOG.info("Making new addon: " + name);

            try {
                var loading = new RLoadingScreen((JFrame) getParent());

                SwingUtilities.invokeLater(() -> {
                    loading.setVisible(true);
                });

                RFileOperations.createWorkspace(loading,

                        new WPFile(NameInput.getText(),
                                MinimumEngineVersionSelection.getSelectedItem().toString(), DescInput.getText(),
                                imageExtension),

                        (ImageIcon) AddonIcon.getIcon());
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(this, "Failed to make new addon.", "Grrrr", ex);
                return;
            }

        } else {
            Launcher.LOG.warning("No action event! " + getClass().getName());
            throw new UnsupportedOperationException("Action event not handled.");
        }
    }

    private void checkForError() {
        var text = NameInput.getText();
        File proposedDir = new File(RFileOperations.getBaseDirectory(this) + "/workspace/" + NameInput.getText());

        Launcher.LOG.info(RFileOperations.getBaseDirectory(this) + "/workspace/" + NameInput.getText());

        SwingUtilities.invokeLater(() -> {
            if (text == "") {
                CreateButton.setToolTipText("You can't name your addon nothing... ");
                CreateButton.setEnabled(false);
            } else if (proposedDir.exists()) {
                CreateButton.setToolTipText("Addon already exists.");
                CreateButton.setEnabled(false);
            } else if (!RFileOperations.validFolderName(text)) {
                CreateButton.setToolTipText(
                        "STOP! You are violating the folder naming convensions! Pay the fine, or change your addon name...");
                CreateButton.setEnabled(false);
            } else {
                CreateButton.setToolTipText("You're all good!");
                CreateButton.setEnabled(true);
            }
        });
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        checkForError();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        checkForError();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkForError();
    }

}
