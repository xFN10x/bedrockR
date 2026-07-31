package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class SourceResourceElement extends ElementSource<ResourceFile> {


    public SourceResourceElement() {
        this(new ResourceFile());
    }

    public SourceResourceElement(@Nonnull ResourceFile resourceFile) {
        super(resourceFile);
    }
    
    public SourceResourceElement(String json) {
        super(getFromJSON(json, ResourceFile.class));
    }

    @Override
    public Class<ResourceFile> getSerilizedClass() {
        return ResourceFile.class;
    }
    
    @Override
    public @NonNull File getLocation(String workspace) throws IOException {
        return RFileOperations.getFileFromWorkspace(workspace,
                File.separator + "resources" + File.separator + RFileOperations.RESOURCE_FILE_NAME);
    }

    @Override
    public String getFileExtension() {
        return "";
    }
}
