package fn10.bedrockr.addons.source.interfaces;

import fn10.bedrockr.addons.RMapElement;

/**
 * Everything implementing this SHOULD have a static method, <code>public static RMapElement[] getPickable()</code>
 */
public interface RMapElementProvider {

    RMapElement[] getPickable();

}
