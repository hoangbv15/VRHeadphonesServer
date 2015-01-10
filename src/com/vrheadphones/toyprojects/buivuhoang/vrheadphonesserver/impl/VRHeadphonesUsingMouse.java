package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class VRHeadphonesUsingMouse {
	private Renderer3D renderer;
	private static final float MOUSE_SENSITIVITY = 0.005f;
	private static final float KEYBOARD_SENSITIVITY = 0.05f;
	private float dX = 0.0f;
	private float dY = 0.0f;
	private PositionChooser appMainView;

	public void start() {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		appMainView = new PositionChooser();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				appMainView.createAndShowGUI();
//				appMainView.setIpAddress("None");
			}
		});

		try {
			renderer = new Renderer3D(this);
			Renderer3D.setSoundList(appMainView.getSoundList());
			Thread t = new Thread(renderer);
			t.start();
			Thread.sleep(1000);
//			Mouse.setGrabbed(true);
			while (!Renderer3D.isCloseRequested()) {
				pollInput();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pollInput() throws InterruptedException {
		// Use the mouse
		if (Mouse.isButtonDown(0)) {
			float dXNew = Mouse.getDX() * MOUSE_SENSITIVITY;
			float dYNew = Mouse.getDY() * MOUSE_SENSITIVITY;
			dX -= dXNew;
			dY += dYNew;
		}
		
		// Use keyboard
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			dX += KEYBOARD_SENSITIVITY;
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			dX -= KEYBOARD_SENSITIVITY;

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			dY += KEYBOARD_SENSITIVITY;
		else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			dY -= KEYBOARD_SENSITIVITY;
		
		renderer.setRotationAngle(dX, dY, 0);

		List<Sound3D> soundList = appMainView.getSoundList();

		if (!SoundPlayer3D.isPlaying() || appMainView.isScenarioModified()) {
			SoundPlayer3D.play(soundList);
			renderer.setRotationAngle(0, 0, 0);
			Thread.sleep(500); 
			Renderer3D.setSoundList(soundList);
			renderer.setRotationAngle(0, 0, 0);
			Thread.sleep(500);
			// Sleep to wait for the renderer to finish rendering the reseted camera angle
			// Fail to do this, and the coordinates will be messed up
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
