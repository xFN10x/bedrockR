package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class SourceWorkspaceFile extends ElementSource<WorkspaceFile> {
    private final String Location = File.separator + RFileOperations.WPFFILENAME;

    public String workspaceName() {
        return getSerialized().WorkspaceName;
    }

    public SourceWorkspaceFile(WorkspaceFile obj) {
        super(obj);
    }

    public static ElementDetails getDetails() {
        try {
            return new ElementDetails("Workspace File", "dont use this cause it will break",
                    RFileOperations.readAllBytes(ElementSource.class.getResource("/addons"
                            + "/element" + "/Element.png").openStream()));
        } catch (IOException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
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
