package fn10.bedrockr.ui.components;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.ui.base.RDetailedButton;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RFileOperations;

import javax.swing.*;
import java.io.IOException;

public class RResourceButton extends RDetailedButton {
    public final Runnable onClick = () -> {
    };
    
    private final WorkspaceResources resources;

    public RResourceButton(Resource res, WorkspaceResources resources) {
        super();
        this.resources = resources;
        Name.setText(res.Name);
        setIcon(new ImageIcon(res.getResourceIcon()), false);

        JPopupMenu menu = new JPopupMenu();
        menu.add("Delete").addActionListener(_ -> {
            if (ImageUtilities.confirm(this, "Are you SURE you want to delete this resource?", "Confirm Deletion?")) {
                try {
                    resources.remove(res);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        
        setComponentPopupMenu(menu);
    }
}
