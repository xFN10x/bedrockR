package fn10.bedrockr.windows.componets;

import java.net.URL;


import javax.swing.JTextArea;

import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.util.ErrorShower;
import jakarta.annotation.Nullable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class RBlockly extends JFXPanel {

    private WebView webView;
    private WebEngine webEngine;
    private Scene scene;

    /**
     * this is here to stop the GC from collecting it
     */
    private final Bridge bridge;

    private final JTextArea preview;

    public static class Bridge {

        private JTextArea preview = null;

        public Bridge(JTextArea preview) {
            if (preview != null)
                this.preview = preview;
        }

        // this is called in javascript
        public void updatePreview(String code) {
            if (preview != null)
                preview.setText(code);
        }

    }

    public void dispose() {

        Platform.runLater(() -> {
            webView.getEngine().load(null);
        });
    }

    public void execute(URL scriptURL) {
        if (!Platform.isFxApplicationThread())
            Platform.runLater(() -> {
                try {
                    execute(new String(RFileOperations.readAllBytes(scriptURL.openConnection().getInputStream())));
                } catch (Exception e) {
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            });
        else
            try {
                execute(new String(RFileOperations.readAllBytes(scriptURL.openConnection().getInputStream())));
            } catch (Exception e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
    }

    public void execute(String script) {
        if (!Platform.isFxApplicationThread())
            Platform.runLater(() -> {
                webEngine.executeScript(script);
            });
        else
            webEngine.executeScript(script);
    }

    public String getJson() {
        return webEngine.executeScript("JSON.stringify(getSaveJSON())").toString();
    }

    public void loadJson(String json) {
        webEngine.executeScript("loadJson(JSON.parse(" + json + "))");
    }

    public RBlockly(JTextArea previewArea, @Nullable String loadFrom, @Nullable Runnable afterLoaded) {
        this.preview = previewArea;
        Bridge br = new Bridge(preview);
        this.bridge = br;

        Platform.runLater(() -> {

            Color bgcolour = Color.rgb(38, 38, 38);

            webView = new WebView();
            scene = new Scene(webView);
            webEngine = webView.getEngine();

            scene.setFill(bgcolour);
            webView.setPageFill(bgcolour);

            setScene(scene);

            webView.setContextMenuEnabled(false);

            try {
                webEngine.load(getClass().getResource("/blockly/blockly.html").toExternalForm());

                webEngine.getLoadWorker().stateProperty().addListener(
                        new ChangeListener<Worker.State>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends javafx.concurrent.Worker.State> observable,
                                    javafx.concurrent.Worker.State oldValue,
                                    javafx.concurrent.Worker.State newValue) {
                                if (newValue == Worker.State.SUCCEEDED) {
                                    // when its loaded, add a referance to javascript

                                    webEngine
                                            .executeScript(RFileOperations.readResourceAsString("/blockly/blockly.js"));
                                    @SuppressWarnings("removal")
                                    JSObject window = (JSObject) webEngine.executeScript("window");
                                    window.setMember("rblockly", bridge);

                                    if (loadFrom != null) {
                                        webEngine.executeScript("loadJson('" + loadFrom + "')");
                                    }
                                    if (afterLoaded != null) {
                                        afterLoaded.run();
                                    }
                                    // stop this from happening again
                                    webEngine.getLoadWorker().stateProperty().removeListener(this);

                                } else if (newValue == Worker.State.FAILED) {
                                    Exception ex = new Exception("Blockly pane failed to load.");

                                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                                    ErrorShower.showError(getParent(), "Blockly Pane failed to load.", ex);

                                } else {
                                    java.util.logging.Logger.getGlobal().info("Unknown state: " + newValue);
                                }
                            }
                        });
            } catch (Exception e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
    }

}
