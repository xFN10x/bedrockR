package fn10.bedrockr.addons.element.interfaces;

import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.addons.element.elementFiles.ResourcePackBuilder;
import fn10.bedrockr.addons.element.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.RAnnotation;

import java.io.IOException;

public abstract class ElementFile<T extends ElementSource<? extends ElementFile<T>>> extends SourcelessElementFile {
    @RAnnotation.CantEditAfter
    @RAnnotation.Order()
    @RAnnotation.VeryImportant
    @RAnnotation.HelpMessage("The name of the element in bedrockR")
    @RAnnotation.FieldDetails(Optional = false, displayName = "Element Name", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public String ElementName; // mostly for making functions better to read
    
    public abstract Class<T> getSourceClass();
    
    public abstract T getNewSource();

    public abstract String getElementName();

    /**
     * Builds this ElementFile to the built BP/RP
     *
     * @param rootPath          - the path to the BP, e.g. {@code rootPath + "/items/"} would be where items go
     * @param workspaceFile     - the workspace file for which this element is being built under
     * @param rootResPackPath   - the path to the RP
     * @param globalResVariables - basicly the resource pack
     * @throws IOException If the build fails.
     */
    public abstract void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
                               ResourcePackBuilder globalResVariables, WorkspaceResources res)
            throws IOException;

    public String getDescription() {
        return getNewSource().getDescription();
    }
}
