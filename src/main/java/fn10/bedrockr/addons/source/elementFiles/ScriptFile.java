package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.windows.componets.RBlockly;
import fn10.bedrockr.windows.componets.RBlockly.Bridge;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class ScriptFile implements ElementFile<SourceScriptElement> {

    @UneditableByCreation
    @CantEditAfter
    @HelpMessage("The name of the element. This only shows up in the workspace view.")
    public String ElementName;

    @UneditableByCreation
    @HelpMessage("The ID of the script. Must be all lower case, like an item/block id")
    public String ScriptName;

    @UneditableByCreation
    /**
     * The JSON blockly content. NOT THE JAVASCRIPT
     */
    public String Content;

    @UneditableByCreation
    private boolean Draft;

    @UneditableByCreation
    private transient Bridge bridge = new RBlockly.Bridge(null);

    @UneditableByCreation
    /**
     * format with; string version, elementname, scriptname, version (= 1.0.0)
     */
    private transient String scriptHeader = "/*\r\n" + //
            "  This following code was generated with - bedrockR (https://github.com/xFN10x/bedrockR)\r\n" + //
            "  Version: %s\r\n" +
            "\r\n" +
            "  Details:\r\n" +
            "    ElementName = %s\r\n" +
            "    ScriptName = %s\r\n" +
            "    Version = %s\r\n" +
            "*/\r\n" +
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

            final String finishedCode = String.format(scriptHeader, Launcher.VERSION, ElementName,
                    ScriptName, "1.0.0") + code;
            try {
                Path path = workspaceFile.addScript(rootPath, ScriptName + ".js");
                FileUtils.writeStringToFile(
                        path.toFile(),
                        finishedCode, StandardCharsets.UTF_8,true);
            } catch (IOException e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(null, "Failed to write script.", e);
            }
        });
        try {
            latch.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }

}
