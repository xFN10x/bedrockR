package fn10.bedrockr.addons.source.jsonClasses;

import fn10.bedrockr.addons.source.FieldFilters;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
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
@UneditableByCreation
    public String Category;

    @UneditableByCreation
    public Boolean isDraft;

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return SourceItemElement.class;
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

}
