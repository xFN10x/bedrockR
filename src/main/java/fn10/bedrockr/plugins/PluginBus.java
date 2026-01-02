package fn10.bedrockr.plugins;

import java.util.logging.Logger;

import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

/**
 * An object of this class is passed to every plugin loaded. With this, you can
 * register custom ElementSources & ElementFiles.
 */
public class PluginBus {

    private static final Logger LOG = Logger.getGlobal();

    /**
     * Register a sourced-element, that automaticly adds it to selectable elements.
     * 
     * @param <T>        the ElementFile Class
     * @param source     the class of the ElementSource you wish to register
     * @param extenstion The extension of the ElementFile on disc.
     * @return This plugin bus.
     */
    public <T> PluginBus registerElement(Class<? extends ElementSource<? extends T>> source,
            String extenstion) {
                LOG.info("Registering element: " +  source.getClass().getSimpleName() + " (" + extenstion + ")");
        RFileOperations.ELEMENT_EXTENSION_CLASSES.put(extenstion, source);
        RFileOperations.ELEMENTS.add(source);
        return this;
    }
}
