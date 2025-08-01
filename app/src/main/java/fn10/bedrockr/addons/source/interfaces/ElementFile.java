package fn10.bedrockr.addons.source.interfaces;

import java.io.IOException;

import fn10.bedrockr.addons.source.jsonClasses.GlobalBuildingVaribles;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;

public interface ElementFile { //mostly for making functions better to read

    
    Class<? extends ElementSource> getSourceClass();

    String getElementName();

    void setDraft(Boolean draft);

    Boolean getDraft();

    void build(String rootPath, WPFile workspaceFile,String rootResPackPath,GlobalBuildingVaribles globalResVaribles) throws IOException;

}
