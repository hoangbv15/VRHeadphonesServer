package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.Timer;

public class VRHeadphonesServer {
	public static JarFile jar;
	public static String basePath = "";
	public static InetAddress localAddr;
	private String[] textLines = new String[6];
	private String sHost = "";
	
	private Timer timer;
	private MainUserInterface appMainView;
	private OSCWorld world;
	private Renderer3D renderer;
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
		appMainView = new MainUserInterface();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				appMainView.createAndShowGUI();
				appMainView.setIpAddress(sHost);
			}
		});
		
		try {
			renderer = new Renderer3D(this);
			Renderer3D.setSoundList(appMainView.getSoundList());
			Thread t = new Thread(renderer);
	        t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateRotationAngle(float thetaX, float thetaY, float thetaZ) {
		renderer.setRotationAngle(thetaX, thetaY, thetaZ);
				
		List<Sound3D> soundList = appMainView.getSoundList();
		
		if (!SoundPlayer3D.isPlaying() || appMainView.isScenarioModified()) {
			SoundPlayer3D.play(soundList);
			Renderer3D.setSoundList(soundList);
		}
		
		for (int i = 0; i < soundList.size(); i++) {
			Sound3D sound = soundList.get(i);
			sound.rotate(thetaX, thetaY, thetaZ);
			SoundPlayer3D.setSourcePosition(i, sound.x, sound.y, sound.z);
		}        
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
