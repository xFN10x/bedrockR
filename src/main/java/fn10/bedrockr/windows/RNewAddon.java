package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;

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

import org.apache.commons.lang3.ArrayUtils;

import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.FieldFilters.FileNameLikeStringFilter;
import fn10.bedrockr.addons.source.FieldFilters.IDStringFilter;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.util.ErrorShower;
import fn10.bedrockr.windows.util.ImageUtilites;
import fn10.bedrockr.windows.util.RFonts;

public class RNewAddon extends RDialog implements ActionListener, DocumentListener {

    // make sure these are valid versions from here
    // https://github.com/PrismarineJS/minecraft-data/blob/master/data/dataPaths.json
    
    protected Byte[] ChosenIcon = ArrayUtils.toObject(ImageUtilites.ImageToBytes(ImageUtilites.ResizeImage(
            new ImageIcon(getClass().getResource("/addons/DefaultIcon.png")).getImage(),
            250, 250)));
    protected JFileChooser fileChooser = new JFileChooser();
    protected String imageExtension = "png";

    protected JLabel AddonIcon = new JLabel(new ImageIcon(ArrayUtils.toPrimitive(ChosenIcon)));
    protected JTextArea DescInput = new JTextArea("My new addon, made in bedrockR");
    protected JTextField NameInput = new JTextField("New AddonR");
    protected JTextField ModPrefixInput = new JTextField("my_mod");
    protected JComboBox<String> MinimumEngineVersionSelection = new JComboBox<String>(RFileOperations.PICKABLE_VERSIONS);
    protected JButton CreateButton = new JButton("Create Addon!");

    public RNewAddon(JFrame Parent) {
        super(Parent,
                DISPOSE_ON_CLOSE,
                "New Addon",
                new Dimension(459, 380));

        AddonIcon.setSize(new Dimension(300, 300));
        AddonIcon.setHorizontalAlignment(SwingConstants.CENTER);
        AddonIcon.setVerticalAlignment(SwingConstants.CENTER);
        AddonIcon.setBorder(BorderFactory.createLineBorder(getForeground(), 3));

        var NameInputText = new JLabel("Addon Name");

        NameInput.setPreferredSize(new Dimension(150, 25));
        NameInput.getDocument().addDocumentListener(this);

        var ModPrefixText = new JLabel("Addon Prefix");

        ModPrefixInput.setPreferredSize(new Dimension(150, 25));
        ModPrefixInput.getDocument().addDocumentListener(this);

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

        // prefix input
        Lay.putConstraint(SpringLayout.SOUTH, ModPrefixText, -1, SpringLayout.NORTH, ModPrefixInput);
        Lay.putConstraint(SpringLayout.WEST, ModPrefixText, 13, SpringLayout.EAST, AddonIcon);

        Lay.putConstraint(SpringLayout.WEST, ModPrefixInput, 13, SpringLayout.EAST, AddonIcon);
        Lay.putConstraint(SpringLayout.NORTH, ModPrefixInput, 18, SpringLayout.SOUTH, NameInput);

        // desc input
        Lay.putConstraint(SpringLayout.SOUTH, DescInputText, -1, SpringLayout.NORTH, DescInput);
        Lay.putConstraint(SpringLayout.WEST, DescInputText, 13, SpringLayout.EAST, AddonIcon);

        Lay.putConstraint(SpringLayout.WEST, DescInput, 13, SpringLayout.EAST, AddonIcon);
        Lay.putConstraint(SpringLayout.NORTH, DescInput, 18, SpringLayout.SOUTH, ModPrefixInput);

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

        add(ModPrefixInput);
        add(ModPrefixText);

        checkForError();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "changeIcon") {
            try {
                fileChooser.setDialogTitle("Choose Addon's Icon");
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "png"));
                fileChooser.showOpenDialog(this);
                try {
                    File file = fileChooser.getSelectedFile();
                    ChosenIcon = ArrayUtils.toObject(Files.readAllBytes(file.toPath()));
                    AddonIcon.setIcon(new ImageIcon(ArrayUtils.toPrimitive(ChosenIcon)));
                    imageExtension = file.getName().split("\\.")[1];
                } catch (Exception e1) {
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                }

            } catch (Exception ex) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
            }
        } else if (e.getActionCommand() == "create") {

            if (ChosenIcon == null) {
                JOptionPane.showMessageDialog(this, "Please select a file", "No Addon icon selected",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name = NameInput.getText();
            java.util.logging.Logger.getGlobal().info("Making new addon: " + name);

            // try {
            RLoadingScreen loading = new RLoadingScreen((JFrame) getParent());
loading.changeText("Creating...");
            // new Thread(() -> {
            // try {
            // SwingUtilities.invokeLater(() -> {
            try {
                SourceWorkspaceFile workspace = RFileOperations.createWorkspace(

                        new WorkspaceFile(NameInput.getText(),
                                MinimumEngineVersionSelection.getSelectedItem().toString(), DescInput.getText(),
                                imageExtension, ModPrefixInput.getText()),

                        ChosenIcon);

                if (workspace != null) {
                    RWorkspace.openWorkspace(loading, workspace);
                    loading.dispose();
                    this.dispose();
                } else {
                    throw new Exception();
                }
            } catch (Exception ex) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                ErrorShower.showError(((Frame) getParent()), "Failed to make new addon.", "Grrrr", ex);
            }

        } else {
            java.util.logging.Logger.getGlobal().warning("No action event! " + getClass().getName());
            throw new UnsupportedOperationException("Action event not handled.");
        }
    }

    private void checkForError() {
        String text = NameInput.getText();
        File proposedDir = new File(
                RFileOperations.getBaseDirectory() + File.separator + "workspace" + File.separator
                        + NameInput.getText());

        SwingUtilities.invokeLater(() -> {
            if (text == "") {
                CreateButton.setToolTipText("You can't name your addon nothing... ");
                CreateButton.setEnabled(false);
            } else if (proposedDir.exists()) {
                CreateButton.setToolTipText("Addon already exists.");
                CreateButton.setEnabled(false);
            } else if (!new FileNameLikeStringFilter().getValid(text)) {
                CreateButton.setToolTipText(
                        "STOP! You are violating the folder naming convensions! Pay the fine, or change your addon name...");
                CreateButton.setEnabled(false);
            } else if (!new IDStringFilter().getValid(ModPrefixInput.getText())) {
                CreateButton.setToolTipText(
                        "STOP! You are violating the folder naming convensions! Pay the fine, or change your addon prefix...");
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
