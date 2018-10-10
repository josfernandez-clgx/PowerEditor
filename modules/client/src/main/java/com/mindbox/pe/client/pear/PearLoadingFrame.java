package com.mindbox.pe.client.pear;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

public class PearLoadingFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = -2593293260513465449L;
    private static final Logger LOG = Logger.getLogger(PearLoadingFrame.class);

    public PearLoadingFrame(String title) {
        super(title);

        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        JLabel label = new JLabel("Launching PowerEditor", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(300,25)); // Tries to prevent truncation of the window title
        pane.add(label);

        pack();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        System.exit(0);
    }
}
