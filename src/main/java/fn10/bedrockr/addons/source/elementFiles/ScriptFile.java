package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;

import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;

public class ScriptFile implements ElementFile {

    @UneditableByCreation
    public String ElementName;

    @UneditableByCreation
    public String Content;

    @UneditableByCreation
    private boolean Draft;

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return SourceScriptElement.class;
    }

    @Override
    public String getElementName() {
        return "Script";
    }

    @Override
    public void setDraft(Boolean draft) {
        Draft = draft;
    }

    @Override
    public Boolean getDraft() {
        return Draft;
    }

    @Override
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'build'");
    }

}
