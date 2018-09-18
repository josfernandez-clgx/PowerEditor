package com.mindbox.pe.client.pear;

import java.awt.Container;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ResearchLabelFont {

    private static void createAndShowGUI() {
        JFrame mainFrame = new JFrame();
        Container mainPane = mainFrame.getContentPane();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        JLabel labelA = new JLabel("LabelA", JLabel.CENTER);
        Font labelFont = labelA.getFont();
        String fontName = labelFont.getName();
        int fontSize = labelFont.getSize();
        Font newLabelFont = new Font(fontName, Font.BOLD, fontSize * 2);
        labelA.setFont(newLabelFont);

        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(labelA);
        mainPanel.add(Box.createHorizontalGlue());

        mainPane.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

    public static void main(String args[]) {
        System.out.println("Hello, world\n");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
