package fn10.bedrockr.rendering;

import javax.swing.JButton;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeCanvasContext;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture.MagFilter;

public class RenderingTest extends SimpleApplication {

    private FrameBuffer buffer;
    private int size = 64;
    private Texture2D renderTarget = new Texture2D(size, size, Image.Format.ARGB8);
    private static JButton testButton = new JButton("Test Block");
    private Spatial model;
    private JmeCanvasContext ctx;
    private ScreenshotAppState screenShotState = new ScreenshotAppState();

    public static void main(String[] args) {
        new RenderingTest().run();
    }

    public void run() {
        setDisplayStatView(false);
        setDisplayFps(false);
        setShowSettings(false);
        start();
        renderToFile("minecraft.Stone");
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            changeModel(assetManager.loadModel("models/StandardBlock.obj"));
        }).start();
    }

    public void renderToFile(String name) {
        screenShotState.setFileName(name + ".");
        screenShotState.takeScreenshot();
    }

    public void changeModel(Spatial model) {
        rootNode.detachAllChildren();
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture("models/default.png");
        mat.setFloat("AlphaDiscardThreshold", 0.1f);
        tex.setMagFilter(MagFilter.Nearest);
        mat.setTexture("DiffuseMap", tex);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        model.setMaterial(mat);
        model.rotate(new Quaternion(0.0990458f, -0.8923991f, 0.2391176f, -0.3696438f));
        rootNode.attachChild(model);
        model.scale(0.3f);
        model.move(0, -0.25f, 0);
    }

    @Override
    public void simpleInitApp() {
        model = assetManager.loadModel("models/StandardBlock.obj");
        flyCam.setEnabled(false);

        buffer = new FrameBuffer(size, size, 1);

        buffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(renderTarget));

        ViewPort offView = renderManager.createMainView("offscreenView", cam);
        offView.setOutputFrameBuffer(buffer);
        offView.clearScenes();
        offView.attachScene(rootNode);

        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture("models/default.png");
        mat.setFloat("AlphaDiscardThreshold", 0.1f);
        tex.setMagFilter(MagFilter.Nearest);
        mat.setTexture("DiffuseMap", tex);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        model.setMaterial(mat);
        model.rotate(new Quaternion(0.09904568838473506f, 0.89239911809345f, 0.239117558506f, 0.369643827011f));
        rootNode.attachChild(model);
        model.scale(0.3f);
        model.center();
        cam.setParallelProjection(true);
        DirectionalLight light = new DirectionalLight(new Vector3f(-0.4f, -1.0f, -0.3f));

        rootNode.addLight(light);
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.5f, 0.5f, 0.5f, 1)));

    }

}
