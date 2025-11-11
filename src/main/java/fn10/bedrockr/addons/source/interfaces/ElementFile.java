package fn10.bedrockr.addons.source.interfaces;

public interface ElementFile<T extends ElementSource<? extends ElementFile<T>>> extends SourcelessElementFile { // mostly for making functions better to read


    Class<T> getSourceClass();

    String getElementName();

    void setDraft(Boolean draft);

    Boolean getDraft();

}
