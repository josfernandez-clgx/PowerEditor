package com.mindbox.pe.client.pear;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

public class ConfirmExitFrame extends JFrame {

    private static final long serialVersionUID = 5248190751965341577L;
    private static Logger LOG = Logger.getLogger(ConfirmExitFrame.class);

    private boolean done = false;
    private boolean ok = false;

    public ConfirmExitFrame(String title) {
        super(title);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDone();
            }
        });

        JRootPane rootPane = getRootPane();
        rootPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                "Cancel");
        rootPane.getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setDone();
            }
        });

        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Confirm exit");
        label.setAlignmentX(CENTER_ALIGNMENT);
        add(label);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel panel = new JPanel();
        panel.setAlignmentX(CENTER_ALIGNMENT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        add(panel);

        JButton okButton = new JButton("OK");
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok = true;
                setDone();
            }
        });
        panel.add(okButton);

        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDone();
            }
        });
        panel.add(cancelButton);
    }

    public boolean ok() {
        return ok;
    }

    public void run() {
        pack();
        setVisible(true);
        waitUntilDone();
        setVisible(false);
    }

    private synchronized void setDone() {
        done = true;
        notifyAll();
        setVisible(false);
    }

    public synchronized void waitUntilDone() {
        while (!done) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
}
