package fn10.bedrockr.addons.source.jsonClasses;

import fn10.bedrockr.addons.source.interfaces.ElementSource;

public interface ElementFile { //mostly for making functions better to read

    
    Class<? extends ElementSource> getSourceClass();

    String getElementName();

    void setDraft(Boolean draft);

    Boolean getDraft();

}
