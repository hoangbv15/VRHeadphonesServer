package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;
import java.awt.Robot;
import java.net.InetAddress;

import jsera.util.World;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;

/**
 * 
 * @author jsera
 * 
 * 
 */

public class OSCWorld extends World {
	
	//
	private OSCPortIn receiver;
	//
	private Robot robot;
	//
	private DiscoverableThread discoverable;
	private VRHeadphonesServer server;
	public OSCWorld(VRHeadphonesServer server) {
		super();
		this.server = server;
	}

	public void onEnter() {
		try {
			this.robot = new Robot();
			this.robot.setAutoDelay(5);
			//
			InetAddress local = InetAddress.getLocalHost();
			if (local.isLoopbackAddress()) {
				this.receiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
			} else {
				this.receiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
			}
			OSCListener listener = new OSCListener() {
				public void acceptMessage(java.util.Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 3) {
						rotateEvent(Float.parseFloat(args[0].toString()), Float.parseFloat(args[1]
								.toString()), Float.parseFloat(args[2].toString()));
					}
				}
			};
			this.receiver.addListener("/rotate", listener);
			//
			this.receiver.startListening();
			// discoverable stuff
			this.discoverable = new DiscoverableThread(OSCPort.defaultSCOSCPort()+1);
			this.discoverable.start();
		} catch (Exception ex) {

		}
	}

	private void rotateEvent(float thetaX, float thetaY, float thetaZ) {
        server.updateRotationAngle(thetaX, thetaY, thetaZ);
	}
}