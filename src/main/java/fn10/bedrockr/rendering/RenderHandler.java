package fn10.bedrockr.rendering;

import jakarta.annotation.Nullable;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RDialog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.plugins.AWTLoader;

public class RenderHandler extends SimpleApplication {

    public static RenderHandler CurrentHandler;

    public static final Quaternion DOWN_ISO = new Quaternion(0.0990458f, -0.8923991f, 0.2391176f, -0.3696438f);
    public static final Quaternion NORMAL_ISO = new Quaternion(0.09904568838473506f, 0.89239911809345f, 0.239117558506f,
            0.369643827011f);

    private FrameBuffer buffer;
    private int size = 150;
    private Texture2D renderTarget = new Texture2D(size, size, Format.ARGB8);
    private Spatial model;
    private String sspath = RFileOperations.getBaseDirectory(null, "cache", "renders").getAbsolutePath();
    private ScreenshotAppState screenShotState = new ScreenshotAppState(
            sspath + File.separator);
    private boolean started = false;

    public void run() {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(150, 150);
        setSettings(settings);
        setDisplayStatView(false);
        setDisplayFps(false);
        setShowSettings(false);
        start(Type.OffscreenSurface);
    }

    public static Texture make6Sided(Image top, Image down, Image side) {
        return make6Sided(top, down, side, side, side, side);
    }

    public static Texture make6Sided(Image texture) {
        return make6Sided(texture, texture, texture);
    }

    public static Texture make6Sided(Image top, Image down, Image east, Image west, Image north, Image south) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        Dimension sideSize = new Dimension(16, 16);
        g.drawImage(ImageUtilites.ResizeImage(top, sideSize), 16, 0, null); // top
        g.drawImage(ImageUtilites.ResizeImage(down, sideSize), 32, 0, null); // bottom
        g.drawImage(ImageUtilites.ResizeImage(north, sideSize), 16, 16, null); // north
        g.drawImage(ImageUtilites.ResizeImage(south, sideSize), 48, 16, null); // south
        g.drawImage(ImageUtilites.ResizeImage(east, sideSize), 0, 16, null); // east
        g.drawImage(ImageUtilites.ResizeImage(west, sideSize), 32, 16, null); // west
        /*
         * try {
         * ImageIO.write(img, "png", new
         * File("C:\\Users\\mathd\\Pictures\\Untitled - Copy.jpg"));
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         */
        com.jme3.texture.Image JMEimg = new AWTLoader().load(img, true);
        Texture2D tex = new Texture2D(JMEimg);
        tex.setMagFilter(MagFilter.Nearest);
        return tex;
    }

    public Future<Path> renderBlock(String fullBlockId, Image texture) {
        return renderBlock(fullBlockId, texture, texture, texture, texture, texture, texture);
    }

    public Future<Path> renderBlock(String fullBlockId, Image top, Image side, Image down) {
        return renderBlock(fullBlockId, top, down, side, side, side, side);
    }

    public Future<Path> renderBlock(String fullBlockId, Image top, Image down, Image east, Image west, Image north,
            Image south) {
        return enqueue(() -> {
            CurrentHandler.changeModel(null, make6Sided(top, down, east, west, north, south));
            CurrentHandler.renderToFile(fullBlockId.replaceAll(":", "."));
            return null;
        });
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

    public void showPreviewWindow(Window parent, Texture tex) {
        RDialog dia = new RDialog(parent, RDialog.DISPOSE_ON_CLOSE, "Block Preview", new Dimension(500, 700), true);
        changeModel(null, tex);
        try {
            JLabel adding = new JLabel(new ImageIcon(ImageIO.read(renderToFile("previewUp").toFile())
                    .getScaledInstance(300, 300, BufferedImage.SCALE_AREA_AVERAGING)));
            adding.setMinimumSize(new Dimension(300, 300));
            adding.setMaximumSize(new Dimension(300, 300));
            dia.Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, adding, 0, SpringLayout.HORIZONTAL_CENTER,
                    dia.getContentPane());
            dia.Lay.putConstraint(SpringLayout.NORTH, adding, 5, SpringLayout.NORTH, dia.getContentPane());
            dia.add(adding);

        } catch (IOException e) {
            e.printStackTrace();
        }
        doNextFrame(() -> {
            changeDownModel(null, tex);
            try {
                JLabel adding = new JLabel(new ImageIcon(ImageIO.read(renderToFile("previewDown").toFile())
                        .getScaledInstance(300, 300, BufferedImage.SCALE_AREA_AVERAGING)));
                adding.setMinimumSize(new Dimension(300, 300));
                adding.setMaximumSize(new Dimension(300, 300));
                dia.Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, adding, 0, SpringLayout.HORIZONTAL_CENTER,
                        dia.getContentPane());
                dia.Lay.putConstraint(SpringLayout.SOUTH, adding, -5, SpringLayout.SOUTH, dia.getContentPane());
                dia.add(adding);
            } catch (IOException e) {
                e.printStackTrace();
            }
            dia.setVisible(true);
        });
    }

    public void doNextFrame(Runnable r) {
        viewPort.addProcessor(new com.jme3.post.SceneProcessor() {
            @Override
            public void postFrame(FrameBuffer out) {
                r.run();
                viewPort.removeProcessor(this);
            }

            @Override
            public void initialize(RenderManager rm, ViewPort vp) {
            }

            @Override
            public void reshape(ViewPort vp, int w, int h) {
            }

            @Override
            public void preFrame(float tpf) {
            }

            @Override
            public void cleanup() {
            }

            @Override
            public boolean isInitialized() {
                return true;
            }

            @Override
            public void postQueue(RenderQueue rq) {
            }

            @Override
            public void setProfiler(AppProfiler profiler) {
            }
        });
    }

    public void changeDownModel(@Nullable Spatial model, Texture tex) {
        changeModel(model, tex, false).rotate(DOWN_ISO);
    }

    public Spatial changeModel(@Nullable Spatial model, Texture tex) {
        return changeModel(model, tex, true);
    }

    public Spatial changeModel(@Nullable Spatial model, Texture tex, boolean rotate) {
        if (!started)
            return null;

        rootNode.detachAllChildren();
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("AlphaDiscardThreshold", 0.1f);
        mat.setTexture("DiffuseMap", tex);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        if (model == null)
            model = assetManager.loadModel("models/StandardBlock.obj");

        model.setMaterial(mat);
        if (rotate)
            model.rotate(NORMAL_ISO);
        rootNode.attachChild(model);
        model.scale(0.5f);
        model.center();
        return model;
    }

    @Override
    public void simpleInitApp() {
        model = assetManager.loadModel("models/StandardBlock.obj");
        System.out.println(cam.getAspect());

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
        model.rotate(NORMAL_ISO);
        rootNode.attachChild(model);
        model.scale(0.5f);
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
