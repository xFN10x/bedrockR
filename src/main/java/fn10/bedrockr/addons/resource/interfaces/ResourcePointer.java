package fn10.bedrockr.addons.resource.interfaces;

import fn10.bedrockr.addons.resource.BlockTextureResource;
import fn10.bedrockr.addons.resource.WorkspaceResources;

public class ResourcePointer<T extends Resource> {
    private final String id;
    private final Class<T> resClass;

    public static <T extends Resource> ResourcePointer<T> empty(Class<T> ty) {
        return new ResourcePointer<T>("empty", ty);
    }

    public T get(WorkspaceResources res) {
        return res.getResourceFromID(id, resClass);
    }

    private ResourcePointer(String id, Class<T> resClass) {
        this.id = id;
        this.resClass = resClass;
    }
    
    public static <T extends Resource> ResourcePointer<? extends Resource> pointerOf(T res) {
        return new ResourcePointer<>(res.ID, res.getClass());
    }

    public boolean exists(WorkspaceResources wres) {
        return get(wres) != null;
    }

    public String getID() {
        return id;
    }
}
