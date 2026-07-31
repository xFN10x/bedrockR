package fn10.bedrockr.interfaces;

import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.interfaces.ElementSource;

public interface ElementCreationListener {

    /**
     * This function is called when the element has passed all checks, and it is ready to be serilized.
     */
    <T extends ElementFile<? extends ElementSource<T>>> void onElementCreate(ElementSource<T> element);

    <T extends ElementFile<? extends ElementSource<T>>> void onElementDraft(ElementSource<T> element);

    default void onElementCancel() {}

}
