package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.util.List;

import org.lwjgl.input.Mouse;

public class VRHeadphonesUsingMouse {
	private Renderer3D renderer;
	private float mouseSensitivity = 0.01f;
	private float dX = 0.0f;
	private float dY = 0.0f;
	private PositionChooser appMainView;
	
	public void start() {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				appMainView = new PositionChooser();
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
		
		List<Sound3D> soundList = appMainView.getSoundList();
		
		if (!SoundPlayer3D.isPlaying() || appMainView.isFileChanged()) {
			SoundPlayer3D.play(soundList);
		}
		
		for (int i = 0; i < soundList.size(); i++) {
			Sound3D sound = soundList.get(i);
			sound.rotate(dX, dY, 0);
			SoundPlayer3D.setSourcePosition(i, sound.x, sound.y, sound.z);
		}
	}

	public static void main(String[] args) {
		VRHeadphonesUsingMouse app = new VRHeadphonesUsingMouse();
		app.start();
	}
}
