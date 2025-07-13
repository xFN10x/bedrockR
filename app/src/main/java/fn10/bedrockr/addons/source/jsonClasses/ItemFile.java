package fn10.bedrockr.addons.source.jsonClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Item;
import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.items.ItemComponents;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.FieldDetails;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;

public class ItemFile implements ElementFile {

    @HelpMessage(message = "The Name Of The Element")
    @CantEditAfter
    @FieldDetails(Optional = false, displayName = "ElementName", Filter = FieldFilters.FileNameLikeStringFilter.class)
    public String ElementName;

    @HelpMessage(message = "The name of the item. e.g. \"Diamond\", \"Coal\"...")
    @FieldDetails(Optional = false, displayName = "Item Name", Filter = FieldFilters.RegularStringFilter.class)
    public String Name;

    @HelpMessage(message = "The Unique ID for this item. It must be all lowercase, with no spaces. e.g. 'diamond_block', 'wooden_sword', 'grass'")
    @FieldDetails(Optional = false, displayName = "Item Idenifier", Filter = FieldFilters.IDStringFilter.class)
    public String ID;

    @HelpMessage(message = "Is this item hidden in commands?")
    @FieldDetails(Optional = false, displayName = "Item Idenifier", Filter = FieldFilters.IDStringFilter.class)
    public boolean Hidden;

    @UneditableByCreation
    public String Category;

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
    public void build(String rootPath, WPFile workspaceFile) throws IOException {
        // make item
        var item = new Item();
        item.format_version = "1.21.30";
        // catacory
        var cata = new Item.InnerItem.Description.MenuCategory();
        cata.hidden = Hidden;

        // description
        var desc = new Item.InnerItem.Description();
        desc.menu_category = cata;
        desc.identifier = workspaceFile.Prefix + ":" + ID;

        // inner item
        var inner = new Item.InnerItem();
        inner.description = desc;
        // inner.components.put(ItemComponents.Components., workspaceFile)
        item.body = inner;
        // build file
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(item);

        var path = new File(rootPath + "/items/" + ID + ".json").toPath();
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

}
