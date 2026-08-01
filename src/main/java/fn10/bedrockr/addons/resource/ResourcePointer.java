package fn10.bedrockr.addons.resource;

public class ResourcePointer<T extends Resource> {
    private final String id;
    private final Class<T> resClass;
    
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
}
