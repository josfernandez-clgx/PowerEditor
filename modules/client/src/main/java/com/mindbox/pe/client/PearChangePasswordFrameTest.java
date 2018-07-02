/**
 * 
 */
package com.mindbox.pe.client;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PearChangePasswordFrameTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		showNewChangePasswordUI();
	}


	private void showNewChangePasswordUI() {
		// instantiate Change Password frame   
		PearChangePasswordFrame pearChangePasswordFrame = new PearChangePasswordFrame();

		// set login frame properties
		pearChangePasswordFrame.setTitle("Change Password");
		pearChangePasswordFrame.setAlwaysOnTop(true);
		pearChangePasswordFrame.setLocation(500, 500);
		pearChangePasswordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pearChangePasswordFrame.setResizable(false);

		// pearLoginFrame.setPeUrl(peUrl);

		pearChangePasswordFrame.setVisible(true);
		pearChangePasswordFrame.requestFocus();

		// wait until submit or cancel is triggered
		while (!pearChangePasswordFrame.getDoSubmitFlag()) {
		}

		// shut down the change password frame
		pearChangePasswordFrame.setEnabled(false);
		pearChangePasswordFrame.setVisible(false);

		pearChangePasswordFrame.dispose();
		pearChangePasswordFrame = null;

	}

}
