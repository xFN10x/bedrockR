package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.supporting.ItemComponents;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.FieldDetails;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.MapFieldSelectables;
import fn10.bedrockr.utils.RAnnotation.ResourcePackResourceType;
import fn10.bedrockr.utils.RAnnotation.SpecialField;
import fn10.bedrockr.utils.RAnnotation.StringDropdownField;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.RAnnotation.VeryImportant;

public class BlockFile implements ElementFile {

        @HelpMessage(message = "The Name Of The Element")
    @CantEditAfter
    @VeryImportant
    @FieldDetails(Optional = false, displayName = "Element Name", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public String ElementName;

    @HelpMessage(message = "The name of the block. e.g. \"Diamond\", \"Coal\"...")
    @FieldDetails(Optional = false, displayName = "Block Name", Filter = FieldFilters.RegularStringFilter.class)
    public String Name;

    @HelpMessage(message = "The Unique ID for this block. It must be all lowercase, with no spaces. e.g. 'diamond_block', 'wooden_sword', 'grass'")
    @FieldDetails(Optional = false, displayName = "Block Idenifier", Filter = FieldFilters.IDStringFilter.class)
    public String ID;

    @HelpMessage(message = "Specfiys if this block hidden in commands.")
    @FieldDetails(Optional = false, displayName = "Hidden in Commands", Filter = FieldFilters.RegularStringFilter.class)
    public boolean Hidden;

    @HelpMessage(message = "The Creative Tab is this block on.")
    @FieldDetails(Optional = true, displayName = "Category", Filter = FieldFilters.CommonFilter1.class)
    @StringDropdownField({"construction", "equipment", "items", "nature" })
    public String Category;

    @HelpMessage(message = "The group that this block is put into. These groups ")
    @FieldDetails(Optional = true, displayName = "Creative Group", Filter = FieldFilters.CommonFilter1.class)
    // avalible groups 1.21.70
    @StringDropdownField({"itemGroup.name.anvil", "itemGroup.name.arrow", "itemGroup.name.axe",
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
    @MapFieldSelectables(ItemComponents.class)
    @HelpMessage(message = "Defining parts of a block. This is were you would specify ")
    @FieldDetails(Optional = false, displayName = "Components", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public HashMap<String, Object> Components;

    @HelpMessage(message = "The texture for the block.")
    @ResourcePackResourceType(ResourceFile.ITEM_TEXTURE)
    @FieldDetails(Filter = FieldFilters.RegularStringFilter.class, Optional = false, displayName = "Block Texture")
    public UUID TextureUUID;

    @UneditableByCreation
    public Boolean isDraft = Boolean.FALSE;

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return SourceItemElement.class;
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
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'build'");
    }
}
