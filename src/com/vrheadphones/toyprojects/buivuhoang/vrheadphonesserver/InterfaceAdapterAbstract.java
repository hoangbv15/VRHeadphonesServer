package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

/***
 * This class puts an adapter layer between a user interface and internal functional classes. 
 * This removes the need to handle interface interactions via a loop.
 * @author Hoang
 *
 */
public abstract class InterfaceAdapterAbstract {
	
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
}
