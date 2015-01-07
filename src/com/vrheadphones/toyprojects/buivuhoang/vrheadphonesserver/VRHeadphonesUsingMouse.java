package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import org.lwjgl.input.Mouse;

public class VRHeadphonesUsingMouse {
	private Position3D cubeCentre = new Position3D(0, 4, 0);

	private CubeRenderer cubeRenderer;
	private float mouseSensitivity = 0.02f;
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
			cubeRenderer = new CubeRenderer(this);
			Thread t = new Thread(cubeRenderer);
			t.start();
			Thread.sleep(1000);
//			Mouse.setGrabbed(true);

			while (!CubeRenderer.isCloseRequested()) {
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

		cubeRenderer.setRotationAngle(dX, dY, 0);

		if (!SoundPlayer.isPlaying() || appMainView.isFileChanged()) {
			SoundPlayer.play(appMainView.getWaveFile());
		}

		cubeCentre.rotate(dX, dY, 0);
		SoundPlayer.setSourcePosition(cubeCentre.x, cubeCentre.y, cubeCentre.z);
		appMainView.updateRotationalData(cubeCentre.x, cubeCentre.y, cubeCentre.z);
	}

	public static void main(String[] args) {
		VRHeadphonesUsingMouse app = new VRHeadphonesUsingMouse();
		app.start();
	}
}
