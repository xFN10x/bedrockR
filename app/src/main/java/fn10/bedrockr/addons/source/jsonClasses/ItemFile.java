package fn10.bedrockr.addons.source.jsonClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Item;
import fn10.bedrockr.addons.source.*;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.items.ItemComponents;
import fn10.bedrockr.utils.MapUtilities;
import fn10.bedrockr.utils.RAnnotation.*;

public class ItemFile implements ElementFile {

    @HelpMessage(message = "The Name Of The Element")
    @CantEditAfter
    @VeryImportant
    @FieldDetails(Optional = false, displayName = "Element Name", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public String ElementName;

    @HelpMessage(message = "The name of the item. e.g. \"Diamond\", \"Coal\"...")
    @FieldDetails(Optional = false, displayName = "Item Name", Filter = FieldFilters.RegularStringFilter.class)
    public String Name;

    @HelpMessage(message = "The Unique ID for this item. It must be all lowercase, with no spaces. e.g. 'diamond_block', 'wooden_sword', 'grass'")
    @FieldDetails(Optional = false, displayName = "Item Idenifier", Filter = FieldFilters.IDStringFilter.class)
    public String ID;

    @HelpMessage(message = "Specfiys if this item hidden in commands.")
    @FieldDetails(Optional = false, displayName = "Hidden in Commands", Filter = FieldFilters.RegularStringFilter.class)
    public boolean Hidden;

    @HelpMessage(message = "The Creative Tab is this item on.")
    @FieldDetails(Optional = true, displayName = "Item Category", Filter = FieldFilters.CommonFilter1.class)
    @StringDropdownField({ "(none)", "construction", "equipment", "items", "nature" })
    public String Category;

    @HelpMessage(message = "The group that this item is put into. These groups ")
    @FieldDetails(Optional = true, displayName = "Creative Group", Filter = FieldFilters.CommonFilter1.class)
    // avalible groups 1.21.70
    @StringDropdownField({ "(none)", "itemGroup.name.anvil", "itemGroup.name.arrow", "itemGroup.name.axe",
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
    @HelpMessage(message = "Main parts of an item. Components are basicly the options for an item. Like duribility, if its a weapon, etc.")
    @FieldDetails(Optional = false, displayName = "Components", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public HashMap<String, Object> Components;

    @HelpMessage(message = "The texture for the item.")
    @ResourcePackResourceType(ResourceFile.ITEM_TEXTURE)
    @FieldDetails(Filter = FieldFilters.RegularStringFilter.class, Optional = false, displayName = "Item Texture")
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
        globalResVaribles.EnglishTexts.put("item." + workspaceFile.Prefix + ":" + ID, Name);

        // make item
        var item = new Item();
        item.format_version = "1.21.60";
        // catagory
        var cata = new Item.InnerItem.Description.MenuCategory();
        cata.hidden = Hidden;
        if (Category != null) // make sure to have null checks like this, since its optional
            if (!Category.equals("(none)"))
                cata.category = Category;
        if (Group != null)
            if (!Group.equals("(none)"))
                cata.group = Group;

        // description
        var desc = new Item.InnerItem.Description();
        desc.menu_category = cata;
        desc.identifier = workspaceFile.Prefix + ":" + ID;

        // inner item
        var inner = new Item.InnerItem();
        inner.description = desc;

        inner.components.put("minecraft:icon", globalResVaribles.addItemTexture(
                    MapUtilities.getKeyFromValue(globalResVaribles.Resource.ResourceIDs, TextureUUID.toString())));

        //inner.components = Components;
        // inner.components.put(ItemComponents.Components., workspaceFile)
        item.body = inner;
        // build file
        var json = gson.toJson(item);
        var path = new File(rootPath + File.separator + "items" + File.separator + ID + ".json").toPath();
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

}
