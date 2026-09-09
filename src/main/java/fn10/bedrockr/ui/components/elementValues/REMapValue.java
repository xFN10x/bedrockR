package fn10.bedrockr.ui.components.elementValues;

import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.RMapValueAddingSelector;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.ui.base.validValues.RMapValue;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class REMapValue<V> extends RElementValue<Map<String, V>, JScrollPane> {
    protected final JPanel HashMapInnerPane;
    protected final JButton HashMapAdd;
    protected final HashMap<String, JPanel> addedMapValuePanels;
    protected final HashMap<String, RMapValue<?, ?>> addedMapValues;

    public REMapValue(Class<V> valueType, @Nullable Field TargetField, @NonNull Class<Map<String, V>> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        HashMapInnerPane = new JPanel();
        addedMapValuePanels = new HashMap<>();
        addedMapValues = new HashMap<>();
        HashMapAdd = new JButton(ImageUtilities.getIcon("/addons/workspace/New.png"));
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public @NonNull JScrollPane createInput() {
        var input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        input.getVerticalScrollBar().setUnitIncrement(18);

        // do things to the panels
        HashMapInnerPane.setLayout(new BoxLayout(HashMapInnerPane, BoxLayout.Y_AXIS));
        input.setBorder(new LineBorder(Color.DARK_GRAY));
        input.setBackground(getBackground().brighter());
        // get the RMapProvider

        // finally, get the annotation after getting the field
        final RAnnotation.MapFieldSelectables anno = getAnno(RAnnotation.MapFieldSelectables.class);

        List<RMapElement> picked = new ArrayList<>();

        // add the button
        HashMapAdd.addActionListener(_ -> {
            if (Target != null && anno != null)
                try {
                    RMapElement select = RMapValueAddingSelector.openSelector(null,
                            ((RMapElement[]) anno.value().getMethod("getPickable")
                                    .invoke(anno.value().getConstructor().newInstance())),
                            picked);
                    if (select == null)
                        return;
                    var toAdd = RMapValue.ofElement(null, select, thi -> removeREMV(thi.getKey()));
                    toAdd.setSize(HashMapInnerPane.getWidth() - 5,
                            Double.valueOf(toAdd.getSize().getHeight()).intValue());
                    toAdd.setAlignmentX(0.5f);

                    picked.add(select);

                    addREMV(toAdd);

                    // finish, and why this wasnt working before
                    HashMapInnerPane.revalidate();
                    HashMapInnerPane.repaint();

                } catch (Exception e1) {
                    ErrorShower.exception(this, "Failed to add a map element.", e1);
                }
        });
        add(HashMapAdd);

        Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, input);
        Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);

        revalidate();
        repaint();
        return input;
    }

    @Override
    public void setValueInternal(Map<String, V> value) {
        HashMapInnerPane.removeAll();
        try {
            for (Map.Entry<String, V> entry : value.entrySet()) {
                RMapElement rme = RMapElement.LookupMap.get(entry.getKey());
                V val;
                if (entry.getValue() instanceof LinkedTreeMap<?, ?> ltm) {
                    JsonElement jsonTree = RFileOperations.gson.toJsonTree(ltm);
                    val = (V) RFileOperations.gson.fromJson(jsonTree, rme.Type);
                } else {
                    val = entry.getValue();
                }
                RMapValue<V, ?> ToAdd = (RMapValue<V, ?>) RMapValue.ofElement(null, rme, thi -> removeREMV(thi.getKey()));
                ToAdd.setValue(val);

                addREMV(ToAdd);
            }
        } catch (Exception e) {
            ErrorShower.exception(null, e.getMessage(), e);
        }
    }
    
    private void removeREMV(String key) {
        HashMapInnerPane.remove(addedMapValuePanels.get(key));
        addedMapValuePanels.remove(key);
        addedMapValues.remove(key);
    }

    private void addREMV(RMapValue<?, ?> ToAdd) {
        JPanel panel = new JPanel();
        panel.add(Box.createRigidArea(new Dimension(100, 10)));
        panel.add(ToAdd);
        HashMapInnerPane.add(panel);
        addedMapValuePanels.put(ToAdd.getKey(), panel);
        addedMapValues.put(ToAdd.getKey(), ToAdd);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, V> getValueInternal(boolean shouldLog) {
        HashMap<String, V> building = new HashMap<>();
        for (Map.Entry<String, RMapValue<?, ?>> entry : addedMapValues.entrySet()) {
            Map.Entry<String, V> entr = (Map.Entry<String, V>) entry.getValue().getKeyAndVal();
            building.put(entr.getKey(), entr.getValue());
        }
        return building;
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        for (Map.Entry<String, RMapValue<?, ?>> entry : addedMapValues.entrySet()) {
            RMapValue<?, ?> remv = entry.getValue();
            Problem = "REMV " + remv.rMapElement.DisplayName + " isn't valid.";
            if (!remv.valid(strict)) return false;
        }
        Problem = "No problem here!";
        return true;
    }
}
