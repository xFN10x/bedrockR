package fn10.bedrockr.utils.exception;

import fn10.bedrockr.windows.componets.RItemValue.Type;

public class WrongItemValueTypeException extends Exception {

    public WrongItemValueTypeException(String msg, Type expected, Type actually) {
        super(msg + 
        "\nThis operation is only on: " + expected.toString() + 
        "\nThis grid is actually: " + actually.toString());
    }
}
