package fn10.bedrockr.addons.source.elementFiles;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Block;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Block.InnerItem;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Block.InnerItem.Description;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Block.InnerItem.Description.MenuCategory;
import fn10.bedrockr.addons.addon.jsonClasses.RP.BlockJSONEntry;
import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.interfaces.ItemLikeElement;
import fn10.bedrockr.addons.source.supporting.BlockComponents;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.MapUtilities;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.FieldDetails;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.MapFieldSelectables;
import fn10.bedrockr.utils.RAnnotation.ResourcePackResourceType;
import fn10.bedrockr.utils.RAnnotation.SpecialField;
import fn10.bedrockr.utils.RAnnotation.StringDropdownField;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.RAnnotation.VeryImportant;

public class BlockFile implements ElementFile, ItemLikeElement {

    @HelpMessage("The Name Of The Element")
    @CantEditAfter
    @VeryImportant
    @FieldDetails(Optional = false, displayName = "Element Name", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public String ElementName;

    @HelpMessage("The name of the block. e.g. \"Diamond\", \"Coal\"...")
    @FieldDetails(Optional = false, displayName = "Block Name", Filter = FieldFilters.RegularStringFilter.class)
    public String Name;

    @HelpMessage("The Unique ID for this block. It must be all lowercase, with no spaces. e.g. 'diamond_block', 'wooden_sword', 'grass'\nThis is the name of the block in commands, and it will look like this in commands: (addon prefix):(idenifier)")
    @FieldDetails(Optional = false, displayName = "Block Idenifier", Filter = FieldFilters.IDStringFilter.class)
    public String ID;

    @HelpMessage("Specfiys if this block hidden in commands.")
    @FieldDetails(Optional = false, displayName = "Hidden in Commands", Filter = FieldFilters.RegularStringFilter.class)
    public boolean Hidden;

    @HelpMessage("The Creative Tab is this block on.")
    @FieldDetails(Optional = false, displayName = "Category", Filter = FieldFilters.CommonFilter1.class)
    @StringDropdownField({ "construction", "equipment", "items", "nature" })
    public String Category;

    @HelpMessage("The group that this block is put into. These groups are not in all categorys, so make sure you select a group in the category.")
    @FieldDetails(Optional = true, displayName = "Creative Group", Filter = FieldFilters.CommonFilter1.class)
    // avalible groups 1.21.70
    @StringDropdownField({ "itemGroup.name.anvil", "itemGroup.name.arrow", "itemGroup.name.axe",
            "itemGroup.name.banner", "itemGroup.name.banner_pattern", "itemGroup.name.bed", "itemGroup.name.boat",
            "itemGroup.name.boots", "itemGroup.name.bundles", "itemGroup.name.buttons", "itemGroup.name.candles",
            "itemGroup.name.chalkboard", "itemGroup.name.chest", "itemGroup.name.chestboat",
            "itemGroup.name.chestplate", "itemGroup.name.compounds", "itemGroup.name.concrete",
            "itemGroup.name.concretePowder", "itemGroup.name.cookedFood", "itemGroup.name.copper",
            "itemGroup.name.coral", "itemGroup.name.coral_decorations", "itemGroup.name.crop", "itemGroup.name.door",
            "itemGroup.name.dye", "itemGroup.name.enchantedBook", "itemGroup.name.fence", "itemGroup.name.fenceGate",
            "itemGroup.name.firework", "itemGroup.name.fireworkStars", "itemGroup.name.flower", "itemGroup.name.glass",
            "itemGroup.name.glassPane", "itemGroup.name.glazedTerracotta", "itemGroup.name.goatHorn",
            "itemGroup.name.grass", "itemGroup.name.hanging_sign", "itemGroup.name.helmet", "itemGroup.name.hoe",
            "itemGroup.name.horseArmor", "itemGroup.name.leaves", "itemGroup.name.leggings",
            "itemGroup.name.lingeringPotion", "itemGroup.name.log", "itemGroup.name.minecart",
            "itemGroup.name.miscFood", "itemGroup.name.mobEgg", "itemGroup.name.monsterStoneEgg",
            "itemGroup.name.mushroom", "itemGroup.name.netherWartBlock", "itemGroup.name.ominousBottle",
            "itemGroup.name.ore", "itemGroup.name.permission", "itemGroup.name.pickaxe", "itemGroup.name.planks",
            "itemGroup.name.potion", "itemGroup.name.potterySherds", "itemGroup.name.pressurePlate",
            "itemGroup.name.products", "itemGroup.name.rail", "itemGroup.name.rawFood", "itemGroup.name.record",
            "itemGroup.name.sandstone", "itemGroup.name.sapling", "itemGroup.name.sculk", "itemGroup.name.seed",
            "itemGroup.name.shovel", "itemGroup.name.shulkerBox", "itemGroup.name.sign", "itemGroup.name.skull",
            "itemGroup.name.slab", "itemGroup.name.smithing_templates", "itemGroup.name.splashPotion",
            "itemGroup.name.stainedClay", "itemGroup.name.stairs", "itemGroup.name.stone", "itemGroup.name.stoneBrick",
            "itemGroup.name.sword", "itemGroup.name.trapdoor", "itemGroup.name.walls", "itemGroup.name.wood",
            "itemGroup.name.wool", "itemGroup.name.woolCarpet" })
    public String Group;

    @SpecialField
    @MapFieldSelectables(BlockComponents.class)
    @HelpMessage("Defining parts of a block. This is were you would specify ")
    @FieldDetails(Optional = false, displayName = "Components", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public HashMap<String, Object> Components;

    @HelpMessage("<html>The texture for the block.<br><br><b>As of a1.1, you can only make blocks with 1 texture.</b></html>")
    @ResourcePackResourceType(ResourceFile.BLOCK_TEXTURE)
    @FieldDetails(Filter = FieldFilters.RegularStringFilter.class, Optional = false, displayName = "Block Texture")
    public UUID TextureUUID;

    @HelpMessage("The sounds that the block makes. This defines the step, break, and hit sounds for the block.")
    @FieldDetails(Optional = true, displayName = "Block Sounds", Filter = FieldFilters.CommonFilter1.class)
    // avalible groups 1.21.70
    @StringDropdownField({ "amethyst_block", "amethyst_cluster", "ancient_debris", "anvil", "azalea", "azalea_leaves",
            "bamboo", "bamboo_sapling", "bamboo_wood", "bamboo_wood_hanging_sign", "basalt", "big_dripleaf",
            "bone_block", "calcite", "candle", "cave_vines", "chain", "cherry_leaves", "cherry_wood",
            "cherry_wood_hanging_sign", "chiseled_bookshelf", "cloth", "comparator", "copper", "copper_bulb",
            "copper_grate", "coral", "creaking_heart", "decorated_pot", "deepslate", "deepslate_bricks",
            "dirt_with_roots", "dripstone_block", "eyeblossom", "frog_spawn", "froglight", "fungus", "glass",
            "glow_lichen", "grass", "gravel", "hanging_roots", "hanging_sign", "heavy_core", "honey_block", "iron",
            "itemframe", "ladder", "lantern", "large_amethyst_bud", "lever", "lodestone", "mangrove_roots",
            "medium_amethyst_bud", "metal", "mob_spawner", "moss_block", "moss_carpet", "mud", "mud_bricks",
            "muddy_mangrove_roots", "nether_brick", "nether_gold_ore", "nether_sprouts", "nether_wart", "nether_wood",
            "nether_wood_hanging_sign", "netherite", "netherrack", "nylium", "packed_mud", "pale_hanging_moss",
            "pink_petals", "pointed_dripstone", "polished_tuff", "powder_snow", "resin", "resin_brick", "roots", "sand",
            "scaffolding", "sculk", "sculk_catalyst", "sculk_sensor", "sculk_shrieker", "sculk_vein", "shroomlight",
            "slime", "small_amethyst_bud", "snow", "soul_sand", "soul_soil", "sponge", "spore_blossom", "stem", "stone",
            "suspicious_gravel", "suspicious_sand", "sweet_berry_bush", "trial_spawner", "tuff", "tuff_bricks",
            "turtle_egg", "vault", "vines", "web", "weeping_vines", "wet_sponge", "wood" })
    public String Sound;

    @UneditableByCreation
    public Boolean isDraft = Boolean.FALSE;

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return SourceBlockElement.class;
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

    @Override
    public void setDraft(Boolean draft) {
        this.isDraft = draft;
    }

    @Override
    public Boolean getDraft() {
        return isDraft == null ? Boolean.FALSE : isDraft;
    }

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        globalResVaribles.EnglishTexts.put("block." + workspaceFile.Prefix + ":" + ID, Name);

        globalResVaribles.BlockRPEntrys.put(workspaceFile.Prefix + ":" + ID,
                new BlockJSONEntry(Sound,
                        globalResVaribles.addBlockTexture(MapUtilities
                                .getKeyFromValue(globalResVaribles.Resource.ResourceIDs, TextureUUID.toString())),
                        null, null));

        // make item
        Block item = new Block();
        item.format_version = "1.21.100";
        // catagory
        MenuCategory cata = new MenuCategory();
        cata.hidden = Hidden;
        cata.category = Category;

        if (Group != null)
            cata.group = "minecraft:" + Group;

        // description
        Description desc = new Description();
        desc.menu_category = cata;
        desc.identifier = workspaceFile.Prefix + ":" + ID;

        // inner item
        InnerItem inner = new InnerItem();
        inner.description = desc;

        inner.components = Components;

        item.body = inner;

        // build file
        var json = gson.toJson(item);
        var path = new File(rootPath + File.separator + "blocks" + File.separator + ID + ".json").toPath();
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    @Override
    public Image getTexture(WorkspaceFile workspace) {
        try {
            ResourceFile resFile = RFileOperations.getResources(null, workspace.WorkspaceName).Serilized;
            BufferedImage img = ImageIO.read(resFile.getResourceFile(null, workspace.WorkspaceName, MapUtilities
                    .getKeyFromValue(resFile.ResourceIDs, TextureUUID.toString()), ResourceFile.BLOCK_TEXTURE));
            return img;
        } catch (IllegalAccessError | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getItemId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return Name;
    }
}
