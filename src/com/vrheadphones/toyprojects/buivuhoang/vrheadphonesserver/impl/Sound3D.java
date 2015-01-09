package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.io.File;

public class Sound3D {
	public File waveFile;
	
	// Current x, y, z
	public float x;
	public float y;
	public float z;
	
	// Original x, y, z
	private float oX;
	private float oY;
	private float oZ;
	
	public Sound3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		oX = x;
		oY = y;
		oZ = z;
	}
	
	public void rotate(float thetaX, float thetaY, float thetaZ) {
//		float x = originalCubeCentre[0];
//      float y = originalCubeCentre[1];
//      currentCubeCentre[0] = (float) (x * Math.cos(thetaX) - y * Math.sin(thetaX));
//      currentCubeCentre[1] = (float) (x * Math.sin(thetaX) + y * Math.cos(thetaX));
//
//      y = currentCubeCentre[1];
//      float z = originalCubeCentre[2];
//      currentCubeCentre[1] = (float) (y * Math.cos(thetaZ) - z * Math.sin(thetaZ));
//      currentCubeCentre[2] = (float) (y * Math.sin(thetaZ) + z * Math.cos(thetaZ));
//
//      x = currentCubeCentre[0];
//      z = currentCubeCentre[2];
//      currentCubeCentre[0] = (float) (  x * Math.cos(thetaY) - z * Math.sin(thetaY));
//      currentCubeCentre[2] = (float) (- x * Math.sin(thetaY) + z * Math.cos(thetaY));
		
        x = (float) (oX * Math.cos(thetaX) - oY * Math.sin(thetaX));
        y = (float) (oX * Math.sin(thetaX) + oY * Math.cos(thetaX));
        
        z = (float) (y * Math.sin(thetaZ) + oZ * Math.cos(thetaZ));
        y = (float) (y * Math.cos(thetaZ) - oZ * Math.sin(thetaZ));
        
        float tempZ = z;
        z = (float) (- x * Math.sin(thetaY) + z * Math.cos(thetaY));
        x = (float) (  x * Math.cos(thetaY) - tempZ * Math.sin(thetaY));
	}
}
