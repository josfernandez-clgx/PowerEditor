// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RoundButton.java

package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class RoundButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public RoundButton(String s) {
		super(s);
		init();
	}

	public RoundButton(String s, ImageIcon imageicon) {
		super(s, imageicon);
		init();
	}

	public boolean contains(int i, int j) {
		if (shape == null || !shape.getBounds().equals(getBounds())) shape = new java.awt.geom.Ellipse2D.Float(0.0F, 0.0F, getWidth(), getHeight());
		return shape.contains(i, j);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
		g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
	}

	private void init() {
		Dimension dimension = getPreferredSize();
		dimension.width = dimension.height = Math.max(dimension.width, dimension.height);
		setContentAreaFilled(false);
	}

	protected void paintComponent(Graphics g) {
		if (getModel().isArmed())
			g.setColor(Color.lightGray);
		else
			g.setColor(getBackground());
		g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
		super.paintComponent(g);
	}

	Shape shape;
}
