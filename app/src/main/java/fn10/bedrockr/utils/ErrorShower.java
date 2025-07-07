package fn10.bedrockr.utils;

import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class ErrorShower {
    public static void showError(Frame parent, String msg, String title, Exception ex) {

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        var trun = sw.toString().substring(0, 200);

        var message = msg+"\n"+ (trun.length() >= 1000 ? trun + "\n(And way more lines to go...)": trun);

        JOptionPane.showMessageDialog(parent,
                message,
                title, JOptionPane.ERROR_MESSAGE); 

        // var frame = new RDialog(parent, JFrame.DISPOSE_ON_CLOSE, "There was an
        // Execption Thrown; "+title, new Dimension(500, 300));

        // var icon = new JLabel(new
        // ImageIcon(ErrorShower.class.getResource("/ui/Error.png")));

        // frame.add(icon);

        // SwingUtilities.invokeLater(() -> {
        // frame.setModal(true);
        // frame.setVisible(true);
        // });
    }
}
