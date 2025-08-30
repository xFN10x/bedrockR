package fn10.bedrockr.windows.componets;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class RBlockly extends JFXPanel {

    private WebView webView;
    private WebEngine webEngine;
    private Scene scene;

    private final JTextArea preview;

    public static class Bridge {

        private final JTextArea preview;

        public Bridge(JTextArea preview) {
            this.preview = preview;
        }

        // this is called in javascript
        public void updatePreview(String code) {
            preview.setText(code);
        }

    }

    public RBlockly(JTextArea previewArea) {
        this.preview = previewArea;
        Platform.runLater(() -> {
            webView = new WebView();
            scene = new Scene(webView);
            webEngine = webView.getEngine();
            setScene(scene);

            try {
                webEngine.loadContent(
                        Files.readString(Path.of(getClass().getResource("/blockly/blockly.html").toURI())));

                webEngine.getLoadWorker().stateProperty().addListener(
                        new ChangeListener<Worker.State>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends javafx.concurrent.Worker.State> observable,
                                    javafx.concurrent.Worker.State oldValue,
                                    javafx.concurrent.Worker.State newValue) {
                                if (newValue == Worker.State.SUCCEEDED) {
                                    System.out.println("loaded");
                                    // when its loaded, add a referance to javascript
                                    Bridge br = new Bridge(preview);
                                    JSObject window = (JSObject) webEngine.executeScript("window");
                                    window.setMember("rblockly", br);
                                }
                            }

                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
