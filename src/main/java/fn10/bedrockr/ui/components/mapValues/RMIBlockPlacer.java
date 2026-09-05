package fn10.bedrockr.ui.components.mapValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.supporting.BlockComponents;
import fn10.bedrockr.addons.element.supporting.ItemComponents;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.components.RItemValue;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import org.jspecify.annotations.NonNull;

import javax.naming.NameNotFoundException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RMIBlockPlacer extends RMapValue<ItemComponents.ItemBlockPlacer, RItemValue> {
    public RMIBlockPlacer(Window Ancestor, RMapElement RME) {
        super(Ancestor, RME);
    }

    @Override
    protected @NonNull RItemValue createInput() {
        return new RItemValue(RFileOperations.getCurrentWorkspace().WorkspaceName, RItemValue.TYPE_SINGLE, true);
    }

    @Override
    public ItemComponents.ItemBlockPlacer getValue() {
        try {
            ItemComponents.ItemBlockPlacer itemBlockPlacer = new ItemComponents.ItemBlockPlacer();
            itemBlockPlacer.block = InputField.getItem().item;
            return itemBlockPlacer;
        } catch (Exception e) {
            RLogUtils.exception(e);
            return null;
        }
    }

    @Override
    public void setValue(ItemComponents.ItemBlockPlacer val) {
        try {
            InputField.setItem(ItemInfo.getItemById(val.block, RFileOperations.getCurrentWorkspace().WorkspaceName, RFileOperations.getCurrentImgHandler()));
        } catch (IncorrectWorkspaceException | NameNotFoundException | IOException |
                 WorkspaceResources.WorkspaceUnsupportedException e) {
            RLogUtils.exception(e);
        }
    }

    @Override
    public boolean valid(boolean strict) {
        return InputField.valid(strict);
    }
}
