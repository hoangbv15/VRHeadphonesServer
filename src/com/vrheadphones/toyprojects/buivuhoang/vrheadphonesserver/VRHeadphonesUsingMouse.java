package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import org.lwjgl.input.Mouse;

public class VRHeadphonesUsingMouse {
	private float[] originalCubeCentre = { 0, 4, 0 };
	private float[] currentCubeCentre = { 0, 4, 0 };

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
			Mouse.setGrabbed(true);

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

		if (!SoundPlayer.isPlaying()) {
			SoundPlayer.play(null);
		}

		float x = originalCubeCentre[0];
		float y = originalCubeCentre[1];
		currentCubeCentre[0] = (float) (x * Math.cos(dX) - y
				* Math.sin(dX));
		currentCubeCentre[1] = (float) (x * Math.sin(dX) + y
				* Math.cos(dX));

		y = currentCubeCentre[1];
		float z = originalCubeCentre[2];
		currentCubeCentre[1] = y;
		currentCubeCentre[2] = z;

		x = currentCubeCentre[0];
		z = currentCubeCentre[2];
		currentCubeCentre[0] = (float) (x * Math.cos(dY) - z
				* Math.sin(dY));
		currentCubeCentre[2] = (float) (-x * Math.sin(dY) + z
				* Math.cos(dY));

		SoundPlayer.setSourcePosition(x, y, z);
		appMainView.updateRotationalData(x, y, z);
	}

	public static void main(String[] args) {
		VRHeadphonesUsingMouse app = new VRHeadphonesUsingMouse();
		app.start();
	}
}
