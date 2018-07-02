package com.mindbox.pe.client;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class PearLogin {

	private Map<String, String> loginMap = new HashMap<String, String>();

	/**
	 * user login logic
	 * @return Map<String, String> 
	 */
	public Map<String, String> usrLogin(String peUrl) {
		// instantiate login frame   
		PearLoginFrame pearLoginFrame = new PearLoginFrame();

		// set login frame properties
		pearLoginFrame.setTitle("PowerEditor Login");
		pearLoginFrame.setAlwaysOnTop(true);
		pearLoginFrame.setBounds(10, 10, 295, 200);
		pearLoginFrame.setLocation(500, 500);
		pearLoginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pearLoginFrame.setResizable(false);

		// pearLoginFrame.setPeUrl(peUrl);

		pearLoginFrame.setVisible(true);
		pearLoginFrame.requestFocus();

		// wait until login is triggered
		while (!pearLoginFrame.getDoLoginFlag()) {
		}

		loginMap.put("userid", pearLoginFrame.getUserID());
		loginMap.put("password", pearLoginFrame.getPassword());

		// shut down the login frame
		pearLoginFrame.setEnabled(false);
		pearLoginFrame.setVisible(false);

		// pearLoginFrame.dispose();
		pearLoginFrame = null;

		System.out.println("-- User Requested to Log In --");
		// send back loginInfo
		return loginMap;
	}
}
