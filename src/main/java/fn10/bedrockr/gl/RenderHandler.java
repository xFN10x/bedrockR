package fn10.bedrockr.gl;

import jakarta.annotation.Nullable;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFileOperations;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

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
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.plugins.AWTLoader;

public class RenderHandler extends SimpleApplication {

    public static RenderHandler CurrentHandler;

    private FrameBuffer buffer;
    private int size = 64;
    private Texture2D renderTarget = new Texture2D(size, size, Format.ARGB8);
    private Spatial model;
    private String sspath = RFileOperations.getBaseDirectory(null, "cache", "testRenders").getAbsolutePath();
    private ScreenshotAppState screenShotState = new ScreenshotAppState(
            sspath + File.separator);
    private boolean started = false;

    public void run() {
        setDisplayStatView(false);
        setDisplayFps(false);
        setShowSettings(false);
        start(Type.OffscreenSurface);
    }

    protected static Texture make6Sided(Image top, Image down, Image east, Image west, Image north, Image south) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        Dimension sideSize = new Dimension(16, 16);
        g.drawImage(ImageUtilites.ResizeImage(top, sideSize), 16, 0, null); // top
        g.drawImage(ImageUtilites.ResizeImage(down, sideSize), 32, 0, null); // bottom
        g.drawImage(ImageUtilites.ResizeImage(north, sideSize), 16, 16, null); // north
        g.drawImage(ImageUtilites.ResizeImage(south, sideSize), 48, 16, null); // south
        g.drawImage(ImageUtilites.ResizeImage(east, sideSize), 0, 16, null); // east
        g.drawImage(ImageUtilites.ResizeImage(west, sideSize), 32, 16, null); // west

        com.jme3.texture.Image JMEimg = new AWTLoader().load(img, true);
        Texture2D tex = new Texture2D(JMEimg);
        tex.setMagFilter(MagFilter.Nearest);
        return tex;
    }

    public static Path renderBlock(String fullBlockId, Image texture) {
        return renderBlock(fullBlockId, texture, texture, texture, texture, texture, texture);
    }

    public static Path renderBlock(String fullBlockId, Image top, Image side, Image down) {
        return renderBlock(fullBlockId, top, down, side, side, side, side);
    }

    public static Path renderBlock(String fullBlockId, Image top, Image down, Image east, Image west, Image north,
            Image south) {
        CurrentHandler.changeModel(null, make6Sided(top, down, east, west, north, south));
        return CurrentHandler.renderToFile(fullBlockId.replaceAll(":", "."));
    }

    public static RenderHandler startup() {
        RenderHandler making = new RenderHandler();
        making.run();
        CurrentHandler = making;
        return making;
    }

    private Path renderToFile(String name) {
        if (!started)
            return null;
        screenShotState.setFileName(name);
        screenShotState.takeScreenshot();
        return Path.of(sspath, name + ".png");
    }

    public void changeModel(@Nullable Spatial model, Texture tex) {
        if (!started)
            return;

        rootNode.detachAllChildren();
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("AlphaDiscardThreshold", 0.1f);
        mat.setTexture("DiffuseMap", tex);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        if (model == null)
            model = assetManager.loadModel("models/StandardBlock.obj");

        model.setMaterial(mat);
        model.rotate(new Quaternion(0.0990458f, -0.8923991f, -0.2391176f, 0.3696438f));
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
        model.rotate(new Quaternion(0.0990458f, -0.8923991f, -0.2391176f, 0.3696438f));
        rootNode.attachChild(model);
        model.scale(0.3f);
        model.center();
        cam.setParallelProjection(true);
        DirectionalLight light = new DirectionalLight(new Vector3f(-0.4f, -1.0f, -0.3f));

        rootNode.addLight(light);
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.5f, 0.5f, 0.5f, 1)));

        screenShotState.setIsNumbered(false);
        stateManager.attach(screenShotState);
        
        started = true;
    }
}
