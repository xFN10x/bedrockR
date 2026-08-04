package fn10.bedrockr.ui.components;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.*;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.ui.RElementEditingScreen;
import fn10.bedrockr.ui.RWorkspace;
import fn10.bedrockr.ui.base.RDetailedButton;
import fn10.bedrockr.ui.laf.BedrockrDark;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RFileOperations;

public class RElementFileButton extends RDetailedButton implements ActionListener {

    protected ElementFile<?> file;
    protected RWorkspace wksp;

    public RElementFileButton(RWorkspace Workspace, ElementFile<?> File) {
        Color clr = (File.getDraft() ? Color.gray : BedrockrDark.BEDROCKR_GREEN);
        super(clr);

        this.file = File;
        this.wksp = Workspace;
        this.setName("RElementFile");

        Name.setText(File.getElementName());
        Desc.setText(File.getDescription());
        if (clr != BedrockrDark.BEDROCKR_GREEN) {
            Name.setText(File.getElementName());
            Name.setForeground(clr.brighter());
            Desc.setForeground(clr.brighter());
            this.setBackground(clr.darker().darker());
        }

        try {
            setIcon(
                    new ImageIcon(File.getNewSource()
                            .getIcon(Workspace.SWPF.getSerialized().getRes(), ImageUtilities.ImgHandler))
            );
        } catch (IOException e) {
            RFileOperations.LOG.log(Level.SEVERE, "Failed to get Element icon.", e);
        }

        JPopupMenu popup = new JPopupMenu(File.getElementName());

        JMenuItem editItem = new JMenuItem("Edit Element...");
        JMenuItem removeItem = new JMenuItem("Remove Element");
        JMenuItem draftItem = new JMenuItem("Draft Element...");
        draftItem.setEnabled(!file.getDraft());
        JMenuItem buildItem = new JMenuItem("Build Element...");

        editItem.addActionListener(this);
        editItem.setActionCommand("edit");

        removeItem.addActionListener(this);
        removeItem.setActionCommand("remove");

        draftItem.addActionListener(this);
        draftItem.setActionCommand(file.getDraft() ? "undraft" : "draft");

        popup.add(editItem);
        popup.add(removeItem);
        popup.add(draftItem);
        popup.addSeparator();
        popup.add(buildItem);

        this.setComponentPopupMenu(popup);
    }

    protected void openWindow() {
        try {
            ElementSource<?> newsrc = file.getNewSource(); // make new elementsource
                                                                                               // with file
            RElementEditingScreen screen = RElementEditingScreen.getElementsCreationScreen(newsrc, wksp, wksp,
                    wksp.SWPF.getSerialized().WorkspaceName);
            if (screen != null)
                screen.setVisible(true);
        } catch (Exception e) {
            RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(wksp, "Failed to open up window.", "Error", e);
        }
    }

    /**
     * Get the {@code ElementFile} that this was built with.
     * 
     * @return The corrisponding {@code ElementFile}
     */
    public ElementFile<?> getFile() {
        return file;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getButton() != MouseEvent.BUTTON1)
            return;

        selected = false;
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.green, 3, 16));

        openWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        switch (ac) {
            case "edit":
                openWindow();
                break;
            case "remove":
                try {
                    if (RFileOperations.getFileFromElementFile(wksp.SWPF.workspaceName(), file).toFile().delete()) {
                        wksp.refreshElements();
                        wksp.buildElements(true);
                    } else {
                        RFileOperations.LOG.warning("Couldn't delete element.");
                    }
                } catch (Exception ex) {
                    ErrorShower.exception(this, "Failed to remove element." ,ex);
                }
                break;
            case "undraft":
            case "draft":
                try {
                    file.setDraft(ac.equals("draft"));
                    ElementSource<?> src = file.getNewSource();
                    src.saveJSONFile(wksp.SWPF.getSerialized().WorkspaceName);
                    wksp.refreshElements();
                } catch (Exception e1) {
                    RFileOperations.LOG.log(Level.SEVERE, "Exception thrown", e1);
                    ErrorShower.showError(wksp, "Failed to create a new Source.", "Error", e1);
                }
                break;
        }
    }

}
