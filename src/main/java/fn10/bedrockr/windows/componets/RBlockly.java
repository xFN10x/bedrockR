package fn10.bedrockr.windows.componets;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class RBlockly extends JFXPanel {

    public WebView webView;
    public WebEngine webEngine;
    public Scene scene;

    public RBlockly() {
        Platform.runLater(() -> {
            webView = new WebView();
            scene = new Scene(webView);
            webEngine = webView.getEngine();
            setScene(scene);

            try {
                webEngine.loadContent(
                        Files.readString(Path.of(getClass().getResource("/blockly/blockly.html").toURI())));
            } catch (Exception e) {
                e.printStackTrace();
            }

            
        });
    }
}
