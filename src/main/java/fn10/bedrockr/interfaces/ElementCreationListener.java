package fn10.bedrockr.interfaces;

import fn10.bedrockr.addons.source.interfaces.ElementSource;

public interface ElementCreationListener {

    /**
     * This function is called when the element has passed all checks, and it is ready to be serilized.
     */
    void onElementCreate(ElementSource<?> element);

    void onElementDraft(ElementSource<?> element);

    void onElementCancel();

}
