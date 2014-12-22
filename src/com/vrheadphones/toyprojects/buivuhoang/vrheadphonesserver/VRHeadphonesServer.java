package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.jar.JarFile;

public class VRHeadphonesServer {
	
	public static JarFile jar;
	public static String basePath = "";
	public static InetAddress localAddr;
	private String[] textLines = new String[6];
	private String sHost = "";
	
	public VRHeadphonesServer() {

		// get local IP
		try {
			localAddr = InetAddress.getLocalHost();
			if (localAddr.isLoopbackAddress()) {
				localAddr = LinuxInetAddress.getLocalHost();
			}
			sHost = localAddr.getHostAddress();
		} catch (UnknownHostException ex) {
			sHost = "Error finding local IP.";
		}
		
		try {
			URL fileURL = this.getClass().getProtectionDomain().getCodeSource()
					.getLocation();
			String sBase = fileURL.toString();
			if ("jar"
					.equals(sBase.substring(sBase.length() - 3, sBase.length()))) {
				jar = new JarFile(new File(fileURL.toURI()));

			} else {
				basePath = System.getProperty("user.dir") + "\\res\\";
			}
		} catch (Exception ex) {
			this.textLines[1] = "exception: " + ex.toString();

		}
		
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AppMainView appMainView = new AppMainView();
				appMainView.setIpAddress(sHost);
				appMainView.createAndShowGUI();

				appMainView.updateRotationalData(1, 2, 3);
			}
		});
		
	}
	
	public void start() {
		// TODO: implement the server routine
	}
	
	public static void main(String[] args) {
		
		VRHeadphonesServer server = new VRHeadphonesServer();
		server.start();
	}
}
