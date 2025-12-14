package fn10.bedrockr.rendering;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo;
import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo.BlockJsonEntry;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.SettingsFile;
import fn10.bedrockr.windows.*;

public class BlockTextures {
    private static final Gson gson = new GsonBuilder().create();

    private static HttpClient client = HttpClient.newBuilder().build();
    private static Map<String, ImageIcon> preloadedIcons = new HashMap<String, ImageIcon>();
    private static Map<String, Map<String, Object>> blocksJson = null;
    private static Map<String, Map<String, Map<String, Object>>> terrianTextureJson = null;
    private static URI blocksJsonUrl = URI.create(
            "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/blocks.json");
    private static URI terrianJsonUrl = URI.create(
            "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/textures/terrain_texture.json");

    @SuppressWarnings("unchecked")
    protected static Map<String, Map<String, Object>> getBlocksJson() throws IOException, InterruptedException {
        HttpRequest dataPathsReq = HttpRequest.newBuilder()
                .uri(blocksJsonUrl)
                .version(HttpClient.Version.HTTP_2).GET().build();
        HttpResponse<String> response = client.send(dataPathsReq, BodyHandlers.ofString());

        blocksJson = gson.fromJson(response.body(), HashMap.class);
        return blocksJson;
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Map<String, Map<String, Object>>> getTerrianTextureJson()
            throws IOException, InterruptedException {
        HttpRequest dataPathsReq = HttpRequest.newBuilder()
                .uri(terrianJsonUrl)
                .version(HttpClient.Version.HTTP_2).GET().build();
        HttpResponse<String> response = client.send(dataPathsReq, BodyHandlers.ofString());

        terrianTextureJson = gson.fromJson(response.body(), HashMap.class);
        return terrianTextureJson;
    }

    public static ImageIcon getBlockTexture(Window doingThis, String name) throws IOException {
        if (preloadedIcons.containsKey(name))
            return preloadedIcons.get(name);

        File proposedRender = Path
                .of(RFileOperations.getBaseDirectory("cache", "renders").getAbsolutePath(),
                        name + ".png")
                .toFile();
        if (proposedRender.exists()) {
            preloadedIcons.put(name, new ImageIcon(ImageIO.read(proposedRender).getScaledInstance(45, 45,
                    BufferedImage.SCALE_AREA_AVERAGING)));
            return getBlockTexture(doingThis, name);
        } else {
            return null;
        }
    }

    public static CountDownLatch downloadAllBlockTextures(Window doingThis) {
        if (ReturnItemInfo.vanillaBlocks == null)
            ReturnItemInfo.downloadVanillaBlocks();
        CountDownLatch latch = new CountDownLatch(1);
        RLoadingScreen loading = new RLoadingScreen(doingThis);
        SwingUtilities.invokeLater(() -> {
            loading.setVisible(true);
        });
        SwingUtilities.invokeLater(() -> {
            loading.Steps = ReturnItemInfo.vanillaBlocks.length;
            new Thread(() -> {
                for (BlockJsonEntry vanillaItems : ReturnItemInfo.vanillaBlocks) {
                    String name = vanillaItems.name.split(":")[1];
                    try {
                        loading.increaseProgressBySteps("Downloading " + name + "'s textures...");
                    } catch (IllegalAccessException e) {
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    }
                    renderBlock(name);
                }
                latch.countDown();
                SettingsFile settings = SettingsFile.load();
                HttpRequest latestVerReq = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.github.com/repos/PrismarineJS/minecraft-data/releases/latest"))
                        .version(HttpClient.Version.HTTP_2).GET().build();
                try {
                    HttpResponse<String> response = client.send(latestVerReq, BodyHandlers.ofString());
                    settings.LastTimeBlockTexturesCachedPrismarineJSMCDataVersionID = ((Double) gson
                            .fromJson(response.body(), LinkedTreeMap.class).get("id")).longValue();
                } catch (IOException | InterruptedException e) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    return;
                }
                settings.save();
                SwingUtilities.invokeLater(() -> {
                    loading.setVisible(false);
                });
            }).start();
        });
        return latch;
    }

    /**
     * Download a texture, process it and add it to the list
     * 
     * @param id
     * @throws InterruptedException
     * @throws IOException
     */
    public static Image downloadTexture(String id) throws IOException, InterruptedException {
        if (!id.startsWith("textures/blocks")) {
            id = "textures/blocks/" + id;
        }
        URI loc = URI.create(
                "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/"
                        + id + ".png");
        HttpRequest dataPathsReq = HttpRequest.newBuilder()
                .uri(loc)
                .version(HttpClient.Version.HTTP_2).GET().build();
        HttpResponse<InputStream> response = client.send(dataPathsReq, BodyHandlers.ofInputStream());
        // System.out.println(
        // "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/"
        // + id + ".png");
        return ImageIO.read(response.body());
    }

    @SuppressWarnings("unchecked")
    public static void renderBlock(String blockId) {
        if (blocksJson == null) {
            try {
                getBlocksJson();
                getTerrianTextureJson();
            } catch (IOException | InterruptedException e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        }

        try {
            if (!blocksJson.containsKey(blockId))
                return;
            Object textures = blocksJson.get(blockId).get("textures");
            if (textures == null) {
                return;
            }
            if (textures instanceof String) {
                // System.out.println("texture name: " + textures);
                Object texId = terrianTextureJson.get("texture_data").get(textures).get("textures");
                // System.out.println("texture file name: " + texId.toString());
                if (texId instanceof String) {
                    try {
                        RenderHandler.CurrentHandler.renderBlock(blockId, downloadTexture(texId.toString()));
                    } catch (IOException | InterruptedException e) {
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                    }
                }
            } else {
                if (((LinkedTreeMap<String, String>) textures).containsKey("side")) {
                    Object texIdTop = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("up")).get("textures");
                    // System.out.println(texIdTop.getClass().getSimpleName());
                    if (List.class.isAssignableFrom(texIdTop.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdTop);
                        texIdTop = list.get(0);
                    }
                    Object texIdSide = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("side")).get("textures");
                    if (List.class.isAssignableFrom(texIdSide.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdSide);
                        texIdSide = list.get(0);
                    }
                    Object texIdDown = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("down")).get("textures");
                    if (List.class.isAssignableFrom(texIdDown.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdDown);
                        texIdDown = list.get(0);
                    }

                    RenderHandler.CurrentHandler.renderBlock(blockId, downloadTexture(texIdTop.toString()),
                            downloadTexture(texIdSide.toString()), downloadTexture(texIdDown.toString()));

                } else if (((LinkedTreeMap<String, String>) textures).containsKey("east")) {
                    Object texIdTop = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("up")).get("textures");
                    // System.out.println(texIdTop.getClass().getSimpleName());
                    if (List.class.isAssignableFrom(texIdTop.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdTop);
                        texIdTop = list.get(0);
                    }
                    Object texIdEast = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("east")).get("textures");
                    if (List.class.isAssignableFrom(texIdEast.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdEast);
                        texIdEast = list.get(0);
                    }

                    Object texIdWest = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("west")).get("textures");
                    if (List.class.isAssignableFrom(texIdWest.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdWest);
                        texIdWest = list.get(0);
                    }

                    Object texIdSouth = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("south")).get("textures");
                    if (List.class.isAssignableFrom(texIdSouth.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdSouth);
                        texIdSouth = list.get(0);
                    }

                    Object texIdNorth = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("north")).get("textures");
                    if (List.class.isAssignableFrom(texIdNorth.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdNorth);
                        texIdNorth = list.get(0);
                    }

                    Object texIdDown = terrianTextureJson.get("texture_data")
                            .get(((LinkedTreeMap<String, String>) textures).get("down")).get("textures");
                    if (List.class.isAssignableFrom(texIdDown.getClass())) {
                        List<String> list = ((ArrayList<String>) texIdDown);
                        texIdDown = list.get(0);
                    }

                    RenderHandler.CurrentHandler.renderBlock(blockId, downloadTexture(texIdTop.toString()),
                            downloadTexture(texIdDown.toString()), downloadTexture(texIdEast.toString()),
                            downloadTexture(texIdWest.toString()), downloadTexture(texIdNorth.toString()),
                            downloadTexture(texIdSouth.toString()));

                }
            }
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }
}
