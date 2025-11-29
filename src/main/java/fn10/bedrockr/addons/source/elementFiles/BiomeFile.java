package fn10.bedrockr.addons.source.elementFiles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Biome;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Biome.minecraftBiome;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Biome.minecraftBiome.description;
import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.supporting.BiomeComponents;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.MapFieldSelectables;
import fn10.bedrockr.utils.RAnnotation.StringDropdownField;
import fn10.bedrockr.utils.RAnnotation.VeryImportant;

public class BiomeFile implements ElementFile<SourceBiomeElement> {

    @CantEditAfter
    @VeryImportant
    @HelpMessage("The name of the element in bedrockR")
    public String ElementName;

    public boolean Draft = false;

    @StringDropdownField({"_VANILLABIOMES"})
    @HelpMessage("The ID of the biome. Used in /locate, and debugging. Make this ID a vanilla one, like \"plains\", to make it override that biome.")
    public String BiomeID;

    @MapFieldSelectables(BiomeComponents.class)
    public HashMap<String, Object> Comps = new HashMap<String, Object>();

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        Biome biome = new Biome();

        biome.format_version = "1.21.120";

        minecraftBiome biomeInner = new minecraftBiome();
        biome.biome = biomeInner;

        description desc = new description();
        desc.identifier = workspaceFile.Prefix + ":" + BiomeID;
        biomeInner.description = desc;

        biomeInner.components = Comps;

        FileUtils.writeStringToFile(new File(rootPath + "/biomes/" + BiomeID + ".json"), gson.toJson(biome),
                StandardCharsets.UTF_8);
    }

    @Override
    public Class<SourceBiomeElement> getSourceClass() {
        return SourceBiomeElement.class;
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

    @Override
    public void setDraft(Boolean draft) {
        this.Draft = draft;
    }

    @Override
    public Boolean getDraft() {
        return Draft;
    }

}
