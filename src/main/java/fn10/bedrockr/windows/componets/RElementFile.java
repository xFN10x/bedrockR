package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.RWorkspace;
import fn10.bedrockr.windows.util.ErrorShower;

public class RElementFile extends RElement implements ActionListener {

    protected ElementFile<?> file;
    protected String filePath;
    protected RWorkspace wksp;

    public RElementFile(RWorkspace Workspace, ElementFile<?> File, String FilePath)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        super(File.getSourceClass(), null, (File.getDraft() ? Color.gray : Color.green));
        this.file = File;
        this.filePath = FilePath;
        this.wksp = Workspace;
        this.setName("RElementFile");
        Color clr = (File.getDraft() ? Color.gray : Color.green);

        Name.setText(File.getElementName());
        if (clr != Color.green) {
            Name.setText(File.getElementName() + " (DRAFT)");
            Name.setForeground(clr.brighter());
            Desc.setForeground(clr.brighter());
            this.setBackground(clr.darker().darker());
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
            Class<? extends ElementSource<?>> srczz = file.getSourceClass();
            ElementSource<?> newsrc = srczz.getConstructor(file.getClass()).newInstance(file); // make new elementsource with file
            ((RElementEditingScreen) srczz
                    .getMethod("getBuilderWindow", Window.class, ElementCreationListener.class, String.class)
                    .invoke(newsrc, wksp, wksp, ((WorkspaceFile) wksp.SWPF.getSerilized()).WorkspaceName)).setVisible(true);
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
        if (ac.equals("edit")) {
            openWindow();
        } else if (ac.equals("remove")) {
            File file = new File(filePath);
            file.delete();
            wksp.refreshElements();
            wksp.buildElements(true);
        } else if (ac.equals("draft")) {
            try {
                file.setDraft(true);
                ElementSource<?> src = file.getSourceClass().getConstructor(file.getClass()).newInstance(file);
                src.buildJSONFile(((WorkspaceFile) wksp.SWPF.getSerilized()).WorkspaceName);
                wksp.refreshElements();
            } catch (Exception e1) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                ErrorShower.showError(wksp, "Failed to create a new Source.", "Error", e1);
            }
            ;
        } else if (ac.equals("undraft")) {
            try {
                file.setDraft(false);
                ElementSource<?> src = file.getSourceClass().getConstructor(file.getClass()).newInstance(file);
                src.buildJSONFile(((WorkspaceFile) wksp.SWPF.getSerilized()).WorkspaceName);
                wksp.refreshElements();
            } catch (Exception e1) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                ErrorShower.showError(wksp, "Failed to create a new Source.", "Error", e1);
            }
        }
    }

}
