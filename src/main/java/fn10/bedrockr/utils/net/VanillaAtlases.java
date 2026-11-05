package fn10.bedrockr.utils.net;

import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.UnsupportedFormatException;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.Tag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static fn10.bedrockr.utils.RFileOperations.LOG;

public class VanillaAtlases {
    public static Path ATLAS_FOLDER = RFileOperations.getBaseDirectory("atlas").toPath();

    public record Atlas(HashMap<String, Pair<Integer, Integer>> posMappings,
                        HashMap<String, Pair<Integer, Integer>> sizeMappings, BufferedImage img) {
private final static HashMap<String, Atlas> cache = new HashMap<>();
        
        public BufferedImage slice(String id) {
            if (!posMappings.containsKey(id)) {
                LOG.warning("ID: " + id + " not found in atlas!");
                return null;
            }
            Pair<Integer, Integer> pos = posMappings.get(id);
            Pair<Integer, Integer> size = sizeMappings.get(id);
            Integer w = size.getLeft();
            Integer h = size.getRight();
            BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Integer y = pos.getRight();
            Integer x = pos.getLeft();
            target.createGraphics().drawImage(img, 0, 0, w,h, x,y, x + w, y + h, null);
            return target;
        }

        public static Atlas of(String name) throws IOException {
            if (cache.containsKey(name)) return cache.get(name);
            Path path = ATLAS_FOLDER.resolve(name + "-atlas.nbt");
            if (!path.toFile().exists()) {
                LOG.severe("Failed to find atlas at: " + path);
                throw new FileNotFoundException(path.toString());
            }
            CompoundTag nbt = (CompoundTag) NBTUtil.read(path.toFile(), true).getTag();
            int format = nbt.getInt("format");
            if (format != 1) {
                throw new UnsupportedFormatException("Invalid atlas format", format, 1);
            }
            CompoundTag posMappingTag = nbt.getCompoundTag("posMappings");
            HashMap<String, Pair<Integer, Integer>> posMapping = new HashMap<>();
            for (Map.Entry<String, Tag<?>> entry : posMappingTag) {
                if (entry.getValue() instanceof IntArrayTag iat)
                    posMapping.put(entry.getKey(), Pair.of(iat.getValue()[0], iat.getValue()[1]));
                else
                    LOG.warning("Unexcepted type of tag in pos mappings: " + entry.getValue().getClass().getSimpleName());
            }
            CompoundTag sizeMappingTag = nbt.getCompoundTag("sizeMappings");
            HashMap<String, Pair<Integer, Integer>> sizeMapping = new HashMap<>();
            for (Map.Entry<String, Tag<?>> entry : sizeMappingTag) {
                if (entry.getValue() instanceof IntArrayTag iat)
                    sizeMapping.put(entry.getKey(), Pair.of(iat.getValue()[0], iat.getValue()[1]));
                else
                    LOG.warning("Unexcepted type of tag in pos mappings: " + entry.getValue().getClass().getSimpleName());
            }

            byte[] imgData = nbt.getByteArray("imgData");
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgData));

            Atlas atlas = new Atlas(posMapping, sizeMapping, img);
            cache.put(name, atlas);
            return atlas;
        }

    }

    /**
     * Download an atlas from my api.
     *
     * @param name             the name of the atlas. e.g, "block"
     * @param progressCallback A callback called
     * @throws Exception
     */
    public static void downloadAtlas(String name, Consumer<Pair<Integer, Integer>> progressCallback) throws Exception {
        URI uri = new URI("https://api.xplate.dev/bedrockr/v1/textures/" + name);
        Triple<Integer, Integer, InputStream> req = RFileOperations.getCurrentNetHandler().getStream(uri);
        if (req.getLeft() == 200) {
            Path path = ATLAS_FOLDER.resolve(name + "-atlas.nbt");
            FileUtils.createParentDirectories(path.toFile());
            try (OutputStream output = FileUtils.newOutputStream(path.toFile(), false)) {
                InputStream input = req.getRight();
                // approx a megabyte
                int read = 0;
                int justRead;
                int readLength = 1024 * 1024;
                byte[] buf = new byte[readLength];
                while ((justRead = input.read(buf)) != -1) {
                    output.write(buf, 0, justRead);
                    read += justRead;
                    progressCallback.accept(Pair.of(read, req.getMiddle()));
                }
            }
        } else throw new IOException("Failed to download atlas: " + name);
    }

}
