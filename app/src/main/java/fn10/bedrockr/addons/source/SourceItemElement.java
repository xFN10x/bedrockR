package fn10.bedrockr.addons.source;

import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;

public class SourceItemElement implements ElementSource {
    private final String Location = "/elements/items/";
    private Class<WPFile> serilizedClass = WPFile.class;
    private WPFile serilized;

    public static ElementDetails getDetails() {
        return new ElementDetails("Item ", "A basic item. Can be made as food, block placer, or entity spawner.",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Item.png")));
    }

    @Override
    public String getJSONString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getJSONString'");
    }

    @Override
    public Object getFromJSON(String jsonString) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFromJSON'");
    }

    @Override
    public File buildJSONFile(Component doingThis, String workspace) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buildJSONFile'");
    }

    @Override
    public Class getSerilizedClass() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSerilizedClass'");
    }

    @Override
    public Object getSerilized() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSerilized'");
    }

}
