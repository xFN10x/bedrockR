package fn10.bedrockr.ui.components;


import com.formdev.flatlaf.util.ScaledImageIcon;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.addons.mcjson.behav.Item;
import fn10.bedrockr.ui.rendering.BlockTextures;
import fn10.bedrockr.utils.RLogUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RItemButton extends JButton {
    
    private static final Executor textureExecutor = Executors.newFixedThreadPool(5);
    private final ItemInfo info;
    
    public RItemButton(ItemInfo info, Window ancestor, ActionListener onClick) {
        super();
        this.info = info;
        setMargin(new Insets(2, 1, 2, 1));
        Dimension size = new Dimension(48, 48);
        setMinimumSize(size);
        setPreferredSize(size);
        setFont(getFont().deriveFont(8f));
        
        textureExecutor.execute(() -> {
            try {
                ScaledImageIcon icon = BlockTextures.getBlockTexture(ancestor, info.Id);
                if (icon != null)
                    setIcon(icon);
            } catch (IOException e) {
                RLogUtils.exception("Failed to load block texture", e);
            }
        });
        
        setText(info.Name);
        setToolTipText(info.Name + " (" + info.Id +
                ")");
        addActionListener(onClick);
//        addActionListener(_ -> {
//            selected = info.toReturnItemInfo();
//        });
    }
    
    public ItemInfo get() {
        return info;
    }
}
