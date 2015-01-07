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
	private Position3D cubeCentre = new Position3D(0, 4, 0);
	
	public static JarFile jar;
	public static String basePath = "";
	public static InetAddress localAddr;
	private String[] textLines = new String[6];
	private String sHost = "";
	
	private Timer timer;
	private static AppMainView appMainView;
	private OSCWorld world;
	private CubeRenderer cubeRenderer;
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
			}
		});
		
	}
	
	public void updateRotationAngle(float thetaX, float thetaY, float thetaZ) {
		if (!SoundPlayer.isPlaying() || appMainView.isFileChanged()) {
			SoundPlayer.play(appMainView.getWaveFile());
		}
		
		cubeCentre.rotate(thetaX, thetaY, thetaZ);
        
//        appMainView.updateRotationalData(x, y, z);
        appMainView.updateRotationalData(thetaX, thetaY, thetaZ);
        
        cubeRenderer.setRotationAngle(thetaX, thetaY, thetaZ);
        
        SoundPlayer.setSourcePosition(cubeCentre.x, cubeCentre.y, cubeCentre.z);
        
	}
	
	public void start() {
//		SoundPlayer.init();
		
		try {
			cubeRenderer = new CubeRenderer(this);
			Thread t = new Thread(cubeRenderer);
	        t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
