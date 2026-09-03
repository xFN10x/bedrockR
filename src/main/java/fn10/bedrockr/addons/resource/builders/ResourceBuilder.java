package fn10.bedrockr.addons.resource.builders;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.addons.resource.interfaces.TextureResource;
import fn10.bedrockr.utils.RFileOperations;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ResourceBuilder<T extends Resource> {
    protected Path root;
    protected WorkspaceResources wres;
    
    public ResourceBuilder(WorkspaceResources wres) {
        this.wres = wres;
    }
     
    public abstract Class<T> getResClass();
    protected abstract void buildTyped(Collection<T> resources) throws IOException;
    
    public void build(Path root, Collection<? extends Resource> resources) throws IOException {
        ArrayList<T> list = new ArrayList<>();
        this.root = root;
        for (Resource res : resources) {
            if (getResClass().isAssignableFrom(res.getClass())) {
                list.add(getResClass().cast(res));
            }
        }
        buildTyped(list);
    }
    
    protected Path f(String path) throws IOException {
        Path resolved = root.resolve(path);

        RFileOperations.make(resolved);
        
        return resolved;
    }
    
}
