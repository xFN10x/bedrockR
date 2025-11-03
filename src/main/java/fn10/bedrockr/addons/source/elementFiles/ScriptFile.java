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
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.componets.RBlockly;
import fn10.bedrockr.windows.componets.RBlockly.Bridge;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class ScriptFile implements ElementFile {

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
            "  This script was generated with - bedrockR (https://github.com/xFN10x/bedrockR)\r\n" + //
            "  Version: %s\r\n" +
            "\r\n" +
            "  Details:\r\n" +
            "    ElementName = %s\r\n" +
            "    ScriptName = %s\r\n" +
            "    Version = %s\r\n" +
            "*/\r\n" +
            "\r\n";

    @Override
    public Class<? extends ElementSource> getSourceClass() {
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
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        try {
            Platform.startup(() -> {
            });
        } catch (Exception e) {
            // this throws if javafx is already started. idk how to check if it is
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            WebEngine webEngine = new WebEngine();
            webEngine.load(getClass().getResource("/blockly/blockly-minimized.html").toExternalForm());
            webEngine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(
                                ObservableValue<? extends javafx.concurrent.Worker.State> observable,
                                javafx.concurrent.Worker.State oldValue,
                                javafx.concurrent.Worker.State newValue) {
                            if (newValue == Worker.State.SUCCEEDED) {
                                // when its loaded, add a referance to java

                                webEngine
                                        .executeScript(RFileOperations.readResourceAsString("/blockly/blockly.js"));
                                JSObject window = (JSObject) webEngine.executeScript("window");
                                window.setMember("rblockly", bridge);

                                if (Content != null) {
                                    webEngine.executeScript("loadJson('" + Content + "')");
                                }
                                // stop this from happening again
                                webEngine.getLoadWorker().stateProperty().removeListener(this);
                                final String code = webEngine.executeScript(
                                        "javascript.javascriptGenerator.workspaceToCode(Blockly.getMainWorkspace())")
                                        .toString();

                                final String finishedCode = String.format(scriptHeader, Launcher.VERSION, ElementName,
                                        ScriptName, "1.0.0") + code;
                                try {
                                    Path path = workspaceFile.addScript(rootPath, ScriptName + ".js");
                                    FileUtils.writeStringToFile(
                                            path.toFile(),
                                            finishedCode, StandardCharsets.UTF_8);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    ErrorShower.showError(null, "Failed to write script.", e);
                                }
                                latch.countDown();
                            } else if (newValue == Worker.State.FAILED) {
                                Exception ex = new Exception("Blockly pane failed to load.");

                                ex.printStackTrace();
                                ErrorShower.showError(null, "Blockly Pane failed to load.", ex);

                            } else {
                                Launcher.LOG.info("Unknown state: " + newValue);
                            }

                        }
                    });

        });
        try {
            latch.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
