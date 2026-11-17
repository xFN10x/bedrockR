package fn10.bedrockr.ui.components;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.addons.resource.interfaces.ResourcePointer;
import fn10.bedrockr.addons.resource.interfaces.ResourceTask;
import fn10.bedrockr.ui.base.RDetailedButton;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RFileOperations;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RResourceButton<R extends Resource> extends RDetailedButton {
    
    private final WorkspaceResources resources;
    private final R resource;

    public RResourceButton(R res, WorkspaceResources resources) {
        this(res, resources,_ -> {});
    }
    
    public RResourceButton(R res, WorkspaceResources resources, Consumer<RResourceButton<R>> onClick) {
        super();
        this.func = () -> onClick.accept(this);
        this.resources = resources;
        this.resource = res;
        Name.setText(res.Name);
        setIcon(res.getResourceIcon(), false);

        JPopupMenu menu = new JPopupMenu();
        menu.add("Delete").addActionListener(_ -> {
            if (ImageUtilities.confirm(this, "Are you SURE you want to delete this resource?", "Confirm Deletion?")) {
                try {
                    resources.remove(res);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    resources.save();
                }
            }
        });
        menu.addSeparator();
        HashMap<String, ResourceTask> tasks = new HashMap<>();
        res.getTasks(tasks);
        for (Map.Entry<String, ResourceTask> entry : tasks.entrySet()) {
            menu.add(entry.getKey()).addActionListener(_ -> {
                try {
                    entry.getValue().run(res);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    resources.save();
                }
            });
        }

        setComponentPopupMenu(menu);
    }
    
    public ResourcePointer<R> get() {
        return (ResourcePointer<R>) ResourcePointer.pointerOf(resource);
    }
}
