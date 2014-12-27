package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.jar.JarFile;

import javax.swing.Timer;

public class VRHeadphonesServer {
	private float[] originalCubeCentre = {0, 4, 0};
	private float[] currentCubeCentre = {0, 4, 0};
	
	public static JarFile jar;
	public static String basePath = "";
	public static InetAddress localAddr;
	private String[] textLines = new String[6];
	private String sHost = "";
	
	private Timer timer;
	private static AppMainView appMainView;
	private OSCWorld world;
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
		
		world = new OSCWorld(this);
		
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				appMainView = new AppMainView();
				appMainView.createAndShowGUI();
				appMainView.setIpAddress(sHost);
				appMainView.updateRotationalData(1, 2, 3);
			}
		});
		
	}
	
	public void updateRotationAngle(float thetaX, float thetaY, float thetaZ) {
		float x = originalCubeCentre[0];
        float y = originalCubeCentre[1];
        currentCubeCentre[0] = (float) (x * Math.cos(thetaX) - y * Math.sin(thetaX));
        currentCubeCentre[1] = (float) (x * Math.sin(thetaX) + y * Math.cos(thetaX));

        y = currentCubeCentre[1];
        float z = originalCubeCentre[2];
        currentCubeCentre[1] = (float) (y * Math.cos(thetaZ) - z * Math.sin(thetaZ));
        currentCubeCentre[2] = (float) (y * Math.sin(thetaZ) + z * Math.cos(thetaZ));

        x = currentCubeCentre[0];
        z = currentCubeCentre[2];
        currentCubeCentre[0] = (float) (  x * Math.cos(thetaY) - z * Math.sin(thetaY));
        currentCubeCentre[2] = (float) (- x * Math.sin(thetaY) + z * Math.cos(thetaY));
        
        appMainView.updateRotationalData(x, y, z);
//        appMainView.updateRotationalData(thetaX, thetaY, thetaZ);
	}
	
	public void start() {
		this.timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				world.onEnter();
				timer.stop();
			}
		});
		this.timer.start();
	}
	
	public static void main(String[] args) {
		VRHeadphonesServer server = new VRHeadphonesServer();
		server.start();
	}
}
