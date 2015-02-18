package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.InterfaceAdapterAbstract;

/***
 * An interface adapter that plugs the button actions with the 3D sound player
 * @author Hoang
 *
 */
public class SoundPlayer3DInterfaceAdapter extends InterfaceAdapterAbstract {

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

}
