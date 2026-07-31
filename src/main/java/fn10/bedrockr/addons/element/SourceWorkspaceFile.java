package fn10.bedrockr.addons.element;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.element.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

public class SourceWorkspaceFile extends ElementSource<WorkspaceFile> {

    public String workspaceName() {
        return getSerialized().WorkspaceName;
    }

    public SourceWorkspaceFile(WorkspaceFile obj) {
        super(obj);
    }

    @Override
    public @NonNull File getLocation(String workspace) throws IOException {
        return RFileOperations.getFileFromWorkspace(workspace, RFileOperations.WPFFILENAME);
    }

    @Override
    public String getFileExtension() {
        return "";
    }

    @Override
    public Class<WorkspaceFile> getSerilizedClass() {
        return WorkspaceFile.class;
    }
}
