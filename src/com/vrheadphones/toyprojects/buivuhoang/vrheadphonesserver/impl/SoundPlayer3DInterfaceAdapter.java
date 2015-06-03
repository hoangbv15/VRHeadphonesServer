package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.util.List;

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.InterfaceAdapter;

/***
 * An interface adapter that plugs the button actions with the 3D sound player
 * @author Hoang
 *
 */
public class SoundPlayer3DInterfaceAdapter extends InterfaceAdapter {

	@Override
	public void playButtonPressed() {
		SoundPlayer3D.play();
	}

	@Override
	public void pauseButtonPressed() {
		SoundPlayer3D.pause();
	}

	@Override
	public void stopButtonPressed() {
		SoundPlayer3D.stop();
	}

	@Override
	public double getDurationInSeconds(List<Sound3D> soundList) {
		return SoundPlayer3D.getDurationInSeconds(soundList);
	}

	@Override
	public void setPlayPosition(int seconds) {
		SoundPlayer3D.setPlayPosition(seconds);
	}
}
