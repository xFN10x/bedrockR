package fn10.bedrockr.ui.components;


import com.formdev.flatlaf.util.ScaledImageIcon;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.addons.mcjson.behav.Item;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RLogUtils;
import fn10.bedrockr.utils.net.VanillaAtlases;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RItemButton extends JButton {
    
    private static final Executor textureExecutor = Executors.newFixedThreadPool(15);
    private final ItemInfo info;
    
    public RItemButton(ItemInfo info, Window ancestor, ActionListener onClick, boolean isBlock) throws IOException {
        super();
        this.info = info;
        setMargin(new Insets(2, 1, 2, 1));
        Dimension size = new Dimension(48, 48);
        setMinimumSize(size);
        setPreferredSize(size);
        setFont(getFont().deriveFont(8f));
        if (isBlock) {
            VanillaAtlases.Atlas blockAtlas = VanillaAtlases.Atlas.of("block");
            if (!blockAtlas.sizeMappings().containsKey(info.Id)) return;
            textureExecutor.execute(() -> {
                SwingUtilities.invokeLater(() -> {
                    ScaledImageIcon icon = ImageUtilities.toScaled(blockAtlas.slice(info.Id));
                    setIcon(icon);
                });
            });
        }
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
