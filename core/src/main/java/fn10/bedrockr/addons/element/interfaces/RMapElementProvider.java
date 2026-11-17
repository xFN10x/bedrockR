package fn10.bedrockr.addons.element.interfaces;

import fn10.bedrockr.addons.element.RMapElement;

/**
 * Everything implementing this SHOULD have a static method, {@code public static RMapElement[] getPickable()}
 */
public interface RMapElementProvider {

    RMapElement[] getPickable();

}
