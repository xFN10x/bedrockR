package fn10.bedrockr.utils.net;

import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;
import fn10.bedrockr.utils.exception.UnsupportedFormatException;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.Tag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class VanillaAtlases {
    public static Path ATLAS_FOLDER = RFileOperations.getBaseDirectory("atlas").toPath();
    
    public record Atlas(HashMap<String, Pair<Integer, Integer>> posMappings, HashMap<String, Pair<Integer, Integer>> sizeMappings, BufferedImage img) {
        public static Atlas of(String name) throws IOException {
            Path path = ATLAS_FOLDER.resolve(name + "-atlas.nbt");
            if (!path.toFile().exists()) {
                RFileOperations.LOG.severe("Failed to find atlas at: " + path);
                throw new FileNotFoundException(path.toString());
            }
            CompoundTag nbt = (CompoundTag) NBTUtil.read(path.toFile(), true).getTag();
            int format = nbt.getInt("format");
            if (format != 1) {
                throw new UnsupportedFormatException("Invaild atlas format", format, 1);
            }
            CompoundTag posMappingTag = nbt.getCompoundTag("posMappings");
            HashMap<String, Pair<Integer, Integer>> posMapping = new HashMap<>();
            for (Map.Entry<String, Tag<?>> entry : posMappingTag) {
                if (entry.getValue() instanceof IntArrayTag iat) 
                    posMapping.put(entry.getKey(), Pair.of(iat.getValue()[0],iat.getValue()[1]));
                else 
                    RFileOperations.LOG.warning("Unexcepted type of tag in pos mappings: " + entry.getValue().getClass().getSimpleName());
            }
            CompoundTag sizeMappingTag = nbt.getCompoundTag("sizeMappings");
            HashMap<String, Pair<Integer, Integer>> sizeMapping = new HashMap<>();
            for (Map.Entry<String, Tag<?>> entry : sizeMappingTag) {
                if (entry.getValue() instanceof IntArrayTag iat)
                    sizeMapping.put(entry.getKey(), Pair.of(iat.getValue()[0],iat.getValue()[1]));
                else
                    RFileOperations.LOG.warning("Unexcepted type of tag in pos mappings: " + entry.getValue().getClass().getSimpleName());
            }

            byte[] imgData = nbt.getByteArray("imgData");
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgData));

            return new Atlas(posMapping, sizeMapping, img);
        }
        
    }
    
    public static void downloadAtlas(String name) {
        try {
            URI uri = new URI("https://api.xplate.dev/bedrockr/v1/textures/" + name);
            Pair<Integer, byte[]> req = RFileOperations.getCurrentNetHandler().getBytes(uri);
            if (req.getLeft() == 200) {
                Path path = ATLAS_FOLDER.resolve(name + "-atlas.nbt");
                FileUtils.createParentDirectories(path.toFile());
                Files.write(path, req.getRight(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
