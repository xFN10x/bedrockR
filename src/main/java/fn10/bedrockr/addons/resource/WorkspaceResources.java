package fn10.bedrockr.addons.resource;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.element.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.resource.builders.ItemTextureBuilder;
import fn10.bedrockr.addons.resource.builders.ResourceBuilder;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class WorkspaceResources {
    public final WorkspaceFile wpf;
    public final HashMap<String, Class<? extends Resource>> resourcePaths = new HashMap<>();
    public final ArrayList<Resource> resources = new ArrayList<>();
    public final HashMap<Class<? extends Resource>, ResourceBuilder<? extends Resource>> builders = new HashMap<>(Map.of(
            ItemTextureResource.class, new ItemTextureBuilder()
    ));

    private WorkspaceResources(WorkspaceFile wpf) {
        this.wpf = wpf;
    }

    public void addNewResource(Resource res) {
        addNewResource(res, true);
    }

    public void addNewResource(Resource res, boolean save) {
        resourcePaths.put(res.getFolderPath(), res.getClass());
        resources.add(res);
        if (save) save();
    }

    public <T extends Resource> List<T> getResourcesOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<>();
        for (Resource res : resources) {
            if (type.isInstance(res)) {
                list.add(type.cast(res));
            }
        }
        return list;
    }

    public <T extends Resource> T getResourceFromID(String id, Class<T> type) {
        for (Resource res : resources) {
            if (res.ID.equals(id) && type.isInstance(res)) {
                return type.cast(res);
            }
        }
        return null;
    }

    private <T extends Resource> T loadResource(String path, Class<T> resClass) throws IOException {
        Path resFolder = getResourcesPath(wpf.WorkspaceName).resolve(path);
        Path jsonPath = resFolder.resolve("resource.json");
        T resource = RFileOperations.gson.fromJson(Files.readString(jsonPath), resClass);
        Path dataFile = resFolder.resolve("data." + resource.resourceDataExtension());
        resource.data = Files.readAllBytes(dataFile);
        addNewResource(resource, false);
        Launcher.LOG.info("Loaded resource: " + path);
        save();
        return resource;
    }

    public static WorkspaceResources load(String wpname) throws WorkspaceUnsupportedException, IOException {
        WorkspaceFile wpf = RFileOperations.getWorkspaceFile(wpname);
        int format = wpf.Format;
        if (format < 2) {
            throw new WorkspaceUnsupportedException(2, format);
        }

        WorkspaceResources res = new WorkspaceResources(wpf);

        HashMap<String, Class<? extends Resource>> resources = getResourcesJson(wpname);
        for (Map.Entry<String, Class<? extends Resource>> entry : resources.entrySet()) {
            res.loadResource(entry.getKey(), entry.getValue());
        }
        res.save();
        return res;
    }
    
    public void save() {
        try {
            String json = RFileOperations.gson.toJson(resourcePaths);
            RFileOperations.write(getResourcesJsonPath(wpf.WorkspaceName), json);

            for (Resource resource : resources) {
                resource.save(this);
            }
            Launcher.LOG.info("Saved resources.");
        } catch (IOException e) {
            Launcher.LOG.log(Level.SEVERE, "Failed to save resources.", e);
        }
    }

    private static @NonNull HashMap<String, Class<? extends Resource>> getResourcesJson(String workspace) throws IOException {
        Path prop = getResourcesJsonPath(workspace);
        if (!Files.exists(prop)) return new HashMap<>();
        HashMap<String,?> map = RFileOperations.gson.fromJson(Files.readString(prop), HashMap.class);
        HashMap<String, Class<? extends Resource>> actualMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                Class<?> clas;
                if (entry.getValue() instanceof String str) {
                    clas = Class.forName(str);
                    if (!clas.isAssignableFrom(Resource.class)) continue;
                } else if (entry.getValue() instanceof Class cls) {
                    clas = cls;
                } else continue;
                actualMap.put(entry.getKey(), (Class<? extends Resource>) clas);
            } catch (Exception e) {
            }
        }
        return actualMap;
    }

    private static @NonNull Path getResourcesJsonPath(String wpName) throws IOException {
        return getResourcesPath(wpName).resolve("resources.json");
    }

    public static Path getResourcesPath(String wpName) throws IOException {
        return RFileOperations.getFileFromWorkspace(wpName, "resources").toPath();
    }

    public void build(String resourceRootPath) {

    }

    public static class WorkspaceUnsupportedException extends Exception {
        public WorkspaceUnsupportedException(int expectedFormat, int gotFormat) {
            super("Expected workspace format: " + expectedFormat + ", got format: " + gotFormat);
        }
    }
}
