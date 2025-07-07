package fn10.bedrockr.windows.componets;

import java.lang.reflect.InvocationTargetException;

import fn10.bedrockr.addons.source.jsonClasses.ElementFile;

public class RElementFile extends RElement {

    public RElementFile(ElementFile File)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        super(File.getSourceClass(), null);
            
        Name.setText(File.getElementName());
        
    }

}
