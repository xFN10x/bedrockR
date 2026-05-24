package fn10.bedrockr.addons.source.interfaces;

public abstract class ElementFile<T extends ElementSource<? extends ElementFile<T>>> extends SourcelessElementFile { // mostly for making functions better to read


    public abstract Class<T> getSourceClass();

    public abstract String getElementName();

}
