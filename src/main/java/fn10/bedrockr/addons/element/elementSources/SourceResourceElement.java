package fn10.bedrockr.addons.element.elementSources;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.element.elementFiles.ResourceFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

/**
 * @deprecated Use the new {@link fn10.bedrockr.addons.resource} package.
 */
@Deprecated(since = "a2.2", forRemoval = true)
public class SourceResourceElement extends ElementSource<ResourceFile> {
    
    public SourceResourceElement() {
        this(new ResourceFile());
    }

    @Override
    public ElementDetails getDetails() {
        return null;
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
                "resources", RFileOperations.RESOURCE_FILE_NAME);
    }

    @Override
    public String getFileExtension() {
        return "";
    }
}
