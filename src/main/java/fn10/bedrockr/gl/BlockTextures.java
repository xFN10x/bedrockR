package fn10.bedrockr.gl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Image;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.windows.base.RFrame;

public class BlockTextures {
    private static final Gson gson = new GsonBuilder().create();

    private static HttpClient client = HttpClient.newBuilder().build();
    private static Map<String, Map<String, Object>> blocksJson = null;
    private static URI blocksJsonUrl = URI.create(
            "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/blocks.json");

    @SuppressWarnings("unchecked")
    protected static Map<String, Map<String, Object>> getBlocksJson() throws IOException, InterruptedException {
        HttpRequest dataPathsReq = HttpRequest.newBuilder()
                .uri(blocksJsonUrl)
                .version(HttpClient.Version.HTTP_2).GET().build();
        HttpResponse<String> response = client.send(dataPathsReq, BodyHandlers.ofString());

        blocksJson = gson.fromJson(response.body(), HashMap.class);
        return blocksJson;
    }

    public static void main(String[] args) {
        RenderHandler.startup();
        RFrame test = new RFrame(JFrame.EXIT_ON_CLOSE, "Test", new Dimension(300, 300), false);
        JButton button = new JButton("load");

        button.addActionListener(ac -> {
            try {
                String imgpath = RenderHandler
                        .renderBlock("minecraft.stone", ImageIO.read(new File("C:\\Users\\mathd\\Pictures\\pfp.png")))
                        .toString();
                test.add(new JLabel(new ImageIcon(imgpath)));
                System.out.println(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        test.add(button);
        test.setVisible(true);
    }

    /**
     * Download a texture, process it and add it to the list
     * 
     * @param id
     * @throws InterruptedException
     * @throws IOException
     */
    public static Image downloadTexture(String id) throws IOException, InterruptedException {
        URI loc = URI.create(
                "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/textures/blocks/"
                        + id + ".png");
        HttpRequest dataPathsReq = HttpRequest.newBuilder()
                .uri(loc)
                .version(HttpClient.Version.HTTP_2).GET().build();
        HttpResponse<InputStream> response = client.send(dataPathsReq, BodyHandlers.ofInputStream());
        System.out.println(
                "https://raw.githubusercontent.com/Mojang/bedrock-samples/refs/heads/main/resource_pack/textures/blocks/"
                        + id + ".png");
        return ImageIO.read(response.body());
    }

    // i have brain fog; each icon grabs an image from here by id
}
