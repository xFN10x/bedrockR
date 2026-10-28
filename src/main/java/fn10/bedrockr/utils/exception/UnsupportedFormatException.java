package fn10.bedrockr.utils.exception;

import java.util.Arrays;

public class UnsupportedFormatException extends RuntimeException {
    public final int[] expected;
    public final int got;
    public UnsupportedFormatException(String message, int got, int... expected) {
        super(message + " (expected: " + Arrays.toString(expected) + ", got: " + got + ")");
        this.got = got;
        this.expected = expected;
    }
}
