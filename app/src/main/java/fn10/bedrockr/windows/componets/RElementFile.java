package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.jsonClasses.ElementFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.windows.RElementCreationScreen;
import fn10.bedrockr.windows.RWorkspace;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RElementFile extends RElement implements ActionListener {

    protected ElementFile file;
    protected String filePath;
    protected RWorkspace wksp;

    public RElementFile(RWorkspace Workspace, ElementFile File, String FilePath)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        super(File.getSourceClass(), null);
        this.file = File;
        this.filePath = FilePath;
        this.wksp = Workspace;

        Name.setText(File.getElementName());

        var popup = new JPopupMenu(File.getElementName());

        var editItem = new JMenuItem("Edit Element...");
        var removeItem = new JMenuItem("Remove Element");
        var draftItem = new JMenuItem("Draft Element...");
        var buildItem = new JMenuItem("Build Element...");

        editItem.addActionListener(this);
        editItem.setActionCommand("edit");

        removeItem.addActionListener(this);
        removeItem.setActionCommand("remove");

        draftItem.addActionListener(this);
        draftItem.setActionCommand("draft");

        buildItem.addActionListener(this);
        buildItem.setActionCommand("build");

        popup.add(editItem);
        popup.add(removeItem);
        popup.add(draftItem);
        popup.addSeparator();
        popup.add(buildItem);

        this.setComponentPopupMenu(popup);
    }

    protected void openWindow() {
        try {
            var srczz = file.getSourceClass();
            var newsrc = srczz.getConstructor(file.getClass()).newInstance(file); // make new elementsource with file
            ((RElementCreationScreen) srczz.getMethod("getBuilderWindow", Frame.class, ElementCreationListener.class)
                    .invoke(newsrc, wksp, wksp)).setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(wksp, "Failed to open up window.", "Error", e);
        }
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
        var ac = e.getActionCommand();
        if (ac.equals("edit")) {
            openWindow();
        } else if (ac.equals("remove")) {
            var file = new File(filePath);
            file.delete();
            wksp.refreshElements();
        }
    }

}
