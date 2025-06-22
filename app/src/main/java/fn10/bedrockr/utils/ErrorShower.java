package fn10.bedrockr.utils;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class ErrorShower {
    public static void showError(Component parent, String msg, String title, Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        JOptionPane.showMessageDialog(parent,
                msg + "\n" + sw.toString(),
                title, JOptionPane.ERROR_MESSAGE);
    }
}
