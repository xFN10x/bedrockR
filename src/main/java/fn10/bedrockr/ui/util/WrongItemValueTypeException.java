package fn10.bedrockr.ui.util;

public class WrongItemValueTypeException extends Exception {

    public WrongItemValueTypeException(String msg, int expected, int actually) {
        super(msg + "\n" +
        "\nThis operation is only on: " + expected + 
        "\nThis grid is actually: " + actually);
    }
}
