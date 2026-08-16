package fn10.bedrockr.ui.components.elementValues;

public interface RELimitableValue<N extends Number> {
    N getMax();
    N getMin();
    
    void setMax(N val);
    void setMin(N val);
}
