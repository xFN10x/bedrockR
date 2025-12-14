package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;

public class ScriptFile implements ElementFile<SourceScriptElement> {

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'build'");
    }

    @Override
    public Class<SourceScriptElement> getSourceClass() {
        return SourceScriptElement.class;
    }

    @Override
    public String getElementName() {
        return "Script";
    }

    @Override
    public void setDraft(Boolean draft) {
        return;
    }

    @Override
    public Boolean getDraft() {
        return true;
    }
/* sadly, scripts will not be in the next updates until i rework them
    @UneditableByCreation
    @CantEditAfter
    @HelpMessage("The name of the element. This only shows up in the workspace view.")
    public String ElementName;

    @UneditableByCreation
    @HelpMessage("The ID of the script. Must be all lower case, like an item/block id")
    public String ScriptName;

    @UneditableByCreation
    **
     * The JSON blockly content. NOT THE JAVASCRIPT
     *
    public String Content;

    @UneditableByCreation
    private boolean Draft;

    @UneditableByCreation
    private transient Bridge bridge = new RBlockly.Bridge(null);

    @UneditableByCreation
    
     format with; string version, elementname, scriptname, version (= 1.0.0)
     
    private transient String scriptHeader = "/*\r\n" + //
            "  This following code was generated with - bedrockR (https://github.com/xFN10x/bedrockR)\r\n" + //
            "  Version: %s\r\n" +
            "\r\n" +
            "  Details:\r\n" +
            "    ElementName = %s\r\n" +
            "    ScriptName = %s\r\n" +
            "    Version = %s\r\n" +
            "*\r\n" +
            "\r\n";

    @Override
    public Class<SourceScriptElement> getSourceClass() {
        return SourceScriptElement.class;
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

    @Override
    public void setDraft(Boolean draft) {
        Draft = draft;
    }

    @Override
    public Boolean getDraft() {
        return Draft;
    }

    @SuppressWarnings("removal")
    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        CountDownLatch latch = new CountDownLatch(0);
        Platform.runLater(() -> {
            WebEngine webEngine = Launcher.BLOCKLY_MINI_WEBENGINE;

            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("rblockly", bridge);

            if (Content != null) {
                webEngine.executeScript("loadJson('" + Content + "')");
            }
            final String code = webEngine.executeScript(
                    "javascript.javascriptGenerator.workspaceToCode(Blockly.getMainWorkspace())")
                    .toString();

            final String finishedCode = String.format(scriptHeader, RFileOperations.VERSION, ElementName,
                    ScriptName, "1.0.0") + code;
            try {
                Path path = workspaceFile.addScript(rootPath, ScriptName + ".js");
                FileUtils.writeStringToFile(
                        path.toFile(),
                        finishedCode, StandardCharsets.UTF_8,true);
            } catch (IOException e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        try {
            latch.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }
*/
}
