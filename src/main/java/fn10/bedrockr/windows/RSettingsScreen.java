package fn10.bedrockr.windows;

import fn10.bedrockr.Launcher;
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
import java.util.List;
import java.util.logging.Level;

public class RSettingsScreen extends RDialog {

    public final JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
    private final SettingsFile settings = SettingsFile.load();

    private final HashMap<RElementValue, Boolean> changedNeedingRestart = new HashMap<>();
    private final ArrayList<RElementValue> vals = new ArrayList<>();

    public RSettingsScreen(Window parent) {
        super(parent, JFrame.DISPOSE_ON_CLOSE, "Settings", new Dimension(565, 600));

        Lay.putConstraint(SpringLayout.SOUTH, tabs, 0, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, tabs, 0, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, tabs, 0, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, tabs, 0, SpringLayout.WEST, getContentPane());

        JButton SaveButton = new JButton("Save");
        JButton CloseButton = new JButton("Close");
        JButton SaveCloseButton = new JButton("Save & Close");

        SaveButton.addActionListener(_ -> {
            vals.forEach(val -> {
                try {
                    if (!changedNeedingRestart.isEmpty()) {
                        List<String> names;
                        for (RElementValue rval : changedNeedingRestart.keySet()) {
                            names.add(rval.getDisplayName());
                        }
                        JOptionPane.showMessageDialog(this, "The following settings require a restart to take effect: " + String.join(names.toArray(new String[0]), ", "));
                    }
                    final Field field = SettingsFile.class.getField(val.getTarget());
                    field.set(settings, val.getValue());
                    settings.save();
                    load();
                } catch (Exception e) {
                    Launcher.LOG.log(Level.SEVERE, "Failed to save settings", e);
                }
            });
        });
        add(SaveButton);
        add(CloseButton);
        add(SaveCloseButton);
        add(tabs);

        Lay.putConstraint(SpringLayout.SOUTH, SaveButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, SaveButton, -15, SpringLayout.EAST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, CloseButton, 0, SpringLayout.SOUTH, SaveButton);
        Lay.putConstraint(SpringLayout.EAST, CloseButton, -6, SpringLayout.WEST, SaveButton);

        Lay.putConstraint(SpringLayout.SOUTH, SaveCloseButton, 0, SpringLayout.SOUTH, CloseButton);
        Lay.putConstraint(SpringLayout.EAST, SaveCloseButton, -6, SpringLayout.WEST, CloseButton);

        try {
            load();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //load the tabs
        final Field[] fields = SettingsFile.class.getFields();
        final ArrayList<RAnnotation.SettingsCategory.SettingsCategorys> categories = new ArrayList<>();
        final HashMap<RAnnotation.SettingsCategory.SettingsCategorys, JPanel> panels = new HashMap<>();
        tabs.removeAll();

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
            RAnnotation.RequiresRestart requiresRestartAnno;
            if (field.isAnnotationPresent(RAnnotation.RequiresRestart.class)) {
                requiresRestartAnno = field.getAnnotation(RAnnotation.RequiresRestart.class);
            } else {
                requiresRestartAnno = null;
            }
            elementV.setChangedStatusChangedListener(v -> {
                if (v) {
                    changedNeedingRestart.putIfAbsent(elementV, requiresRestartAnno != null);
                } else {
                    changedNeedingRestart.remove(elementV);
                }
            });

            try {
                elementV.setValue(field.get(settings));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            vals.add(elementV);
            panel.add(elementV);
        }
    }
}
