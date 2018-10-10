package com.mindbox.pe.client.pear;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

public class ErrorFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1711454031933484262L;
    private static final Logger LOG = Logger.getLogger(LoginFrame.class);

    private JLabel label;
    private JButton button;

    public ErrorFrame() {
        super("Login error");
        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        label = new JLabel("");
        add(label);

        button = new JButton("OK");
        button.addActionListener(this);
        add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (button == source) {
            this.setVisible(false);
        } else {
            LOG.error("actionPerformed(): event=" + e.toString() + ", source=" + source.toString());
        }
    }

    public void display(String message) {
        label.setText(message);
        pack();
        setVisible(true);
    }

}
