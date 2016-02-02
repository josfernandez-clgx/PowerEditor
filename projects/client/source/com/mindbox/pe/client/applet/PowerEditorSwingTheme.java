package com.mindbox.pe.client.applet;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

public final class PowerEditorSwingTheme extends DefaultMetalTheme {

	public static final int DIVIDER_SIZE = 9;

	// primary colors
	public static final ColorUIResource primary1 = new ColorUIResource(48, 56, 148);
	public static final ColorUIResource primary2 = new ColorUIResource(178, 190, 255);
	public static final ColorUIResource primary3 = new ColorUIResource(212, 224, 255);
	
	public static final ColorUIResource secondary1 = new ColorUIResource(62, 68, 140);//88, 88, 148);
	public static final ColorUIResource secondary2 = new ColorUIResource(128, 128, 128);
	public static final ColorUIResource secondary3 = new ColorUIResource(255, 255, 255);//255, 255, 255);
	public static final  ColorUIResource buttonBackgroundColor = new ColorUIResource(224, 232, 255);
	
	public static final ColorUIResource blackColor = new ColorUIResource(Color.black);
	public static final ColorUIResource whiteColor = new ColorUIResource(Color.white);
	
	public static final ColorUIResource shadowColor = new ColorUIResource(Color.lightGray);
	public static final ColorUIResource blueShadowColor = new ColorUIResource(204,212,255);
	public static final ColorUIResource darkBlueShadowColor = new ColorUIResource(180,188,255);
	
	public static final Font bannelFont = new Font("SansSerif", Font.BOLD, 14);
	
	public static final FontUIResource controltextfont = new FontUIResource("SansSerif", Font.PLAIN, 11);
	public static final FontUIResource menutextfont = new FontUIResource("SansSerif", Font.PLAIN, 12);
	public static final FontUIResource smalltextfont = new FontUIResource("SansSerif", Font.PLAIN, 10);
	public static final FontUIResource systemtextfont = new FontUIResource("SansSerif", Font.PLAIN, 11);
	public static final FontUIResource usertextfont = new FontUIResource("SansSerif", Font.PLAIN, 11);
	public static final FontUIResource windowtitlefont = new FontUIResource("SansSerif", Font.BOLD, 12);
	
	public static final Font bigTabFont = new Font("SansSerif", Font.BOLD, 14);
	public static final Font tabFont = new Font("SansSerif", Font.BOLD, 12);
	public static final Font smallTabFont = new Font("SansSerif", Font.BOLD, 11);
	public static final Font boldFont = new Font("SansSerif", Font.BOLD, 11);
	
	private static boolean isLookAndFeelSet = false;

	static void resetLookAndFeelSet() {
		isLookAndFeelSet = false;
	}
	
	private static final PowerEditorSwingTheme INSTANCE = new PowerEditorSwingTheme();

	public static PowerEditorSwingTheme getInstance() {
		return INSTANCE;
	}


	/**
	 * Sets the Swing user interface look and feel to that of the OS.
	 */
	public static void setLookAndFeelToMulti() {
		// setting the Windows look and feel
		try {
			if (!isLookAndFeelSet) {
				MetalLookAndFeel.setCurrentTheme(new PowerEditorSwingTheme());
				javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				isLookAndFeelSet = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the Swing user interface look and feel to that of the OS.
	 */
	public static void setLookAndFeelToOS() {
		// setting the Windows look and feel
		try {
			if (!isLookAndFeelSet) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				isLookAndFeelSet = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	private PowerEditorSwingTheme() {
	}

	public ColorUIResource getPrimary1() {
		return primary1;
	}

	public ColorUIResource getPrimary2() {
		return primary2;
	}

	public ColorUIResource getPrimary3() {
		return primary3;
	}

	public ColorUIResource getSecondary1() {
		return secondary1;
	}

	public ColorUIResource getSecondary2() {
		return secondary2;
	}

	public ColorUIResource getSecondary3() {
		return secondary3;
	}

	public ColorUIResource getBlack() {
		return blackColor;
	}

	protected ColorUIResource getWhite() {
		return whiteColor;
	}

	public FontUIResource getControlTextFont() {
		return controltextfont;
	}
	public FontUIResource getMenuTextFont() {
		return menutextfont;
	}

	public FontUIResource getSubTextFont() {
		return smalltextfont;
	}

	public FontUIResource getSystemTextFont() {
		return systemtextfont;
	}

	public FontUIResource getUserTextFont() {
		return usertextfont;
	}

	public FontUIResource getWindowTitleFont() {
		return windowtitlefont;
	}

	public ColorUIResource getControlHighlight() {
		return whiteColor;
	}
	
	public ColorUIResource getControlShadow() {
		return blueShadowColor;
	}

}
