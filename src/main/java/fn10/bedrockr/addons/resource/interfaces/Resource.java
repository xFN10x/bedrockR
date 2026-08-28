package fn10.bedrockr.addons.resource.interfaces;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public abstract class Resource {

    public final Integer Format = 0;
    public final Integer CreatedInVersion = RFileOperations.NUM_VERSION;
    public final String Name;
    public final String ID;
    protected transient byte[] data;

    public Resource(String name, String id, byte[] data) {
        this.Name = name;
        this.ID = id;
        this.data = data;
    }

    public Resource(String name, String id) {
        this(name, id, new byte[0]);
    }

    public Resource(String name, String id, File data) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(data.toPath());
        } catch (IOException e) {
            RLogUtils.exception("Failed to read resource data.", e);
            bytes = new byte[0];
        }
        this(name, id, bytes);
    }

    public void save(WorkspaceResources res) throws IOException {
        Path savingPath = Files.createDirectories(WorkspaceResources.getResourcesPath(res.wpf.WorkspaceName).resolve(getFolderPath()));
        Path dataPath = savingPath.resolve(getDataName());
        Path serializedPath = savingPath.resolve("resource.json");
        String json = RFileOperations.gson.toJson(this);

        Files.writeString(serializedPath, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Files.write(dataPath, data);
        RFileOperations.LOG.info("Saved resource: " + Name);
    }

    public String getFolderPath() {
        return getResourceTypeFolder() + "/" + ID;
    }

    /// Do not start this with a / or it tries to save absolutely.
    public String getDataName() {
        return "data." + getDataExtension();
    }

    public abstract String getDataExtension();

    public abstract String getResourceTypeFolder();
    
    public abstract String getResourceCategory();

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public byte[] getResourceIcon() {
        return RFileOperations.readAllOfResource("/resource/"+ getResourceIconName() + ".png");
    }
    
    public abstract String getResourceIconName();
    
    public void getTasks(HashMap<String, ResourceTask> map) {
    }
}
