package fn10.bedrockr.windows.util;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class ErrorShower {

    /**
     * Shows an error message, and prints the stack trace to the log
     * 
     * @param parent - the component closet related to the process this is thrown
     *               from
     * @param ex     - the exception
     */
    public static void exception(Component parent, Exception ex) {
        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
        showError(parent, "", ex);
    }

    public static void showError(Component parent, String msg, Exception ex) {
        showError(parent, msg, ex.getMessage(), ex);
    }

    public static void showError(String msg, String title, Exception ex) {
        showError(null, msg, title, ex);
    }

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
