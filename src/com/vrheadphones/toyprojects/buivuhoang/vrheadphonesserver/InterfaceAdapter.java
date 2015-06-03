package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import java.util.List;

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl.Sound3D;

/***
 * This class puts an adapter layer between a user interface and internal functional classes. 
 * This removes the need to handle interface interactions via a loop.
 * @author Hoang
 *
 */
public abstract class InterfaceAdapter {
	
	/***
	 * Handles play button pressed action
	 */
	public abstract void playButtonPressed();
	
	/***
	 * Handles pause button pressed action
	 */
	public abstract void pauseButtonPressed();
	
	/***
	 * Handles stop button pressed action
	 */
	public abstract void stopButtonPressed();

	/***
	 * Get the duration of the longest sound in seconds
	 * @return the duration of the longest sound in seconds
	 */
	public abstract double getDurationInSeconds(List<Sound3D> soundList);
	
	/***
	 * Test function for sound playback position
	 */
	public abstract void setPlayPosition(int seconds);
	
}
