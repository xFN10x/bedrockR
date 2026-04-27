package fn10.bedrockr.windows;

import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.SettingsFile;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.util.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class RSettingsScreen extends RDialog {

    public final JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

    public RSettingsScreen(Window parent) {
        super(parent, JFrame.DISPOSE_ON_CLOSE, "Settings", new Dimension(565, 700));

        Lay.putConstraint(SpringLayout.SOUTH, tabs, 0, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, tabs, 0, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, tabs, 0, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, tabs, 0, SpringLayout.WEST, getContentPane());

        add(tabs);

        try {
            load();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //load the tabs
        final Field[] fields = SettingsFile.class.getFields();
        final ArrayList<RAnnotation.SettingsCategory.SettingsCategorys> categories = new ArrayList<>();
        final HashMap<RAnnotation.SettingsCategory.SettingsCategorys, JPanel> panels = new HashMap<>();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(RAnnotation.SettingsCategory.class)
                    || !field.isAnnotationPresent(RAnnotation.FieldDetails.class))
                continue;
            final RAnnotation.SettingsCategory category = field.getAnnotation(RAnnotation.SettingsCategory.class);
            final RAnnotation.FieldDetails details = field.getAnnotation(RAnnotation.FieldDetails.class);
            if (!categories.contains(category.value())) {
                categories.add(category.value());
                final JPanel panel = new JPanel();
                panel.setLayout(new WrapLayout(WrapLayout.CENTER, 0, 8));
                panels.putIfAbsent(category.value(), panel);
                tabs.addTab(category.value().Name, panel);
            }
            final JPanel panel = panels.get(category.value());
            final RElementValue elementV = new RElementValue(this, field.getType(), details.Filter().getConstructor().newInstance(), field.getName(), details.displayName(), details.Optional(), SettingsFile.class, null);
            panel.add(elementV);
        }
    }
}
