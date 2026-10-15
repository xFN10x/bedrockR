package fn10.bedrockr.addons.element.elementSources;

import fn10.bedrockr.addons.element.elementFiles.BiomeFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;

public class SourceBiomeElement extends ElementSource<BiomeFile> {

    @Override
    public ElementDetails getDetails() {
        return new ElementDetails("Biome", "A simple biome. Configured through components.");
    }

    public SourceBiomeElement(BiomeFile obj) {
        super(obj);
    }

    @Override
    public String getFileExtension() {
        return ".biomeref";
    }

    public SourceBiomeElement() {
        this(new BiomeFile());
    }
    

    @Override
    public Class<BiomeFile> getSerilizedClass() {
        return BiomeFile.class;
    }
}
