package fn10.bedrockr.addons.resource.builders;

import fn10.bedrockr.addons.resource.Resource;

import java.nio.file.Path;

public interface ResourceBuilder<T extends Resource> {
    void build(Path root, T[] resources);
}
