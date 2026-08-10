package fn10.bedrockr.ui.components;

import fn10.bedrockr.utils.RAnnotation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RHelpButton extends JButton implements ActionListener {
    private String message = "", title = "";
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RHelpButton(String message, String title) {
        this();
        this.message = message;
        this.title = title;
    }
    public RHelpButton() {
        putClientProperty("JButton.buttonType", "help");
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(this,
                    getMessage(),
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);

    }
}
