package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.addons.source.jsonClasses.ElementFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.windows.RElementCreationScreen;
import fn10.bedrockr.windows.RWorkspace;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RElementFile extends RElement {

    protected ElementFile file;
    protected RWorkspace wksp;

    public RElementFile(RWorkspace Workspace, ElementFile File)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        super(File.getSourceClass(), null);
        this.file = File;
        this.wksp = Workspace;

        Name.setText(File.getElementName());

        var popup = new JPopupMenu(File.getElementName());
        popup.add(new JMenuItem("Edit Element..."));
        popup.add(new JMenuItem("Remove Element"));
        popup.add(new JMenuItem("Edit Element..."));

        this.setComponentPopupMenu(popup);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        selected = false;
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.green, 3, 16));

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

}
