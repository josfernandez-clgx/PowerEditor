package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPanel;

public class LinePanel extends JPanel {

	public Insets getInsets() {
		return new Insets(0, 0, 0, 0);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Dimension dimension = getSize();
		dimension.height -= 3;
		g.setPaintMode();
		g.setColor(Color.gray);
		g.drawLine(1, dimension.height, dimension.width, dimension.height);
		g.setColor(Color.red);
		dimension.height++;
		g.drawLine(1, dimension.height, dimension.width, dimension.height);
		g.setColor(Color.blue);
		dimension.height++;
		g.drawLine(1, dimension.height, dimension.width, dimension.height);
	}

}
