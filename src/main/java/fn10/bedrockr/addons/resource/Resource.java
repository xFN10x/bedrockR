package fn10.bedrockr.addons.resource;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.utils.RFileOperations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

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
            Launcher.LOG.log(Level.SEVERE, "Failed to read resource data.", e);
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
        Launcher.LOG.info("Saved resource: " + Name);
    }

    public String getFolderPath() {
        return resourceTypeName() + "/" + ID;
    }

    /// Do not start this with a / or it tries to save absolutely.
    public String getDataName() {
        return "data." + resourceDataExtension();
    }

    public abstract String resourceDataExtension();

    public abstract String resourceTypeName();
}
