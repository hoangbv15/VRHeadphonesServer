package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import org.lwjgl.input.Mouse;

public class VRHeadphonesUsingMouse {
	private Position3D cubeCentre = new Position3D(0, 4, 0);

	private Renderer3D renderer;
	private float mouseSensitivity = 0.01f;
	private float dX = 0.0f;
	private float dY = 0.0f;
	private AppMainView appMainView;

	public void start() {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				appMainView = new AppMainView();
				appMainView.createAndShowGUI();
				appMainView.setIpAddress("None");
			}
		});

		try {
			renderer = new Renderer3D(this);
			Thread t = new Thread(renderer);
			t.start();
			Thread.sleep(1000);
			Mouse.setGrabbed(true);

			while (!Renderer3D.isCloseRequested()) {
				pollInput();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pollInput() {
		float dXNew = Mouse.getDX() * mouseSensitivity;
		float dYNew = Mouse.getDY() * mouseSensitivity;
		dX -= dXNew;
		dY += dYNew;

		renderer.setRotationAngle(dX, dY, 0);

		if (!SoundPlayer3D.isPlaying() || appMainView.isFileChanged()) {
			SoundPlayer3D.play(appMainView.getWaveFile());
		}

		cubeCentre.rotate(dX, dY, 0);
		SoundPlayer3D.setSourcePosition(cubeCentre.x, cubeCentre.y, cubeCentre.z);
		appMainView.updateRotationalData(cubeCentre.x, cubeCentre.y, cubeCentre.z);
	}

	public static void main(String[] args) {
		VRHeadphonesUsingMouse app = new VRHeadphonesUsingMouse();
		app.start();
	}
}
