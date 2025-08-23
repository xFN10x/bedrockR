package fn10.bedrockr.utils;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;


public class ErrorShower {
    public static void showError(Component parent, String msg, String title, Exception ex) {

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        String trun;
        if (sw.toString().length() >= 1000)
            trun = sw.toString().substring(0, 1000);
        else
            trun = sw.toString();

        var message = msg + "\n" + (trun.length() >= 1000 ? trun + "\n(And way more lines to go...)" : trun);

        JOptionPane.showMessageDialog(parent,
                message,
                title, JOptionPane.ERROR_MESSAGE);

    }
}
