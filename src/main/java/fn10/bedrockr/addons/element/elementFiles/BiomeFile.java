package fn10.bedrockr.addons.element.elementFiles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.mcjson.behav.Biome;
import fn10.bedrockr.addons.mcjson.behav.Biome.minecraftBiome;
import fn10.bedrockr.addons.mcjson.behav.Biome.minecraftBiome.description;
import fn10.bedrockr.addons.element.FieldFilters;
import fn10.bedrockr.addons.element.elementSources.SourceBiomeElement;
import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.supporting.BiomeComponents;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.FieldDetails;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.MapFieldSelectables;
import fn10.bedrockr.utils.RAnnotation.Order;
import fn10.bedrockr.utils.RAnnotation.VeryImportant;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class BiomeFile extends ElementFile<SourceBiomeElement> {

    @CantEditAfter
    @Order(0)
    @VeryImportant
    @HelpMessage("The name of the element in bedrockR")
    @FieldDetails(Optional = false, displayName = "Element Name", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public String ElementName;

    @HelpMessage("The ID of the biome. Used in /locate, and debugging.")
    @FieldDetails(Optional = false, displayName = "Biome ID", Filter = FieldFilters.IDStringFilter.class)
    @Order(1)
    public String BiomeID;

    @MapFieldSelectables(BiomeComponents.class)
    @Order(2)
    public HashMap<String, Object> Comps = new HashMap<String, Object>();

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
                      ResourcePackBuilder globalResVaribles, WorkspaceResources res) throws IOException {
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
    public SourceBiomeElement getNewSource() {
        return new SourceBiomeElement(this);
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

}
