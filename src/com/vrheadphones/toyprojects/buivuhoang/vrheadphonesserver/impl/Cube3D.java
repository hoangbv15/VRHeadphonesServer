package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import org.lwjgl.opengl.GL11;

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.Shape3D;

public class Cube3D extends Shape3D {
	private static final Integer TYPE = GL11.GL_QUADS;
	private float[][] vertices = {
			{ 1.0f, 1.0f,-1.0f},         // Top Right Of The Quad (Top)
            {-1.0f, 1.0f,-1.0f},         // Top Left Of The Quad (Top)
            {-1.0f, 1.0f, 1.0f},         // Bottom Left Of The Quad (Top)
            { 1.0f, 1.0f, 1.0f},         // Bottom Right Of The Quad (Top)
            
            { 1.0f,-1.0f, 1.0f},         // Top Right Of The Quad (Bottom)
            {-1.0f,-1.0f, 1.0f},         // Top Left Of The Quad (Bottom)
            {-1.0f,-1.0f,-1.0f},         // Bottom Left Of The Quad (Bottom)
            { 1.0f,-1.0f,-1.0f},         // Bottom Right Of The Quad (Bottom)

            { 1.0f, 1.0f, 1.0f},         // Top Right Of The Quad (Front)
            {-1.0f, 1.0f, 1.0f},         // Top Left Of The Quad (Front)
            {-1.0f,-1.0f, 1.0f},         // Bottom Left Of The Quad (Front)
            { 1.0f,-1.0f, 1.0f},         // Bottom Right Of The Quad (Front)

            { 1.0f,-1.0f,-1.0f},         // Bottom Left Of The Quad (Back)
            {-1.0f,-1.0f,-1.0f},         // Bottom Right Of The Quad (Back)
            {-1.0f, 1.0f,-1.0f},         // Top Right Of The Quad (Back)
            { 1.0f, 1.0f,-1.0f},         // Top Left Of The Quad (Back)

            {-1.0f, 1.0f, 1.0f},         // Top Right Of The Quad (Left)
            {-1.0f, 1.0f,-1.0f},         // Top Left Of The Quad (Left)
            {-1.0f,-1.0f,-1.0f},         // Bottom Left Of The Quad (Left)
            {-1.0f,-1.0f, 1.0f},         // Bottom Right Of The Quad (Left)

            { 1.0f, 1.0f,-1.0f},         // Top Right Of The Quad (Right)
            { 1.0f, 1.0f, 1.0f},         // Top Left Of The Quad (Right)
            { 1.0f,-1.0f, 1.0f},         // Bottom Left Of The Quad (Right)
            { 1.0f,-1.0f,-1.0f},
	};
	
	private float[][] colours = {
			{0.0f,1.0f,0.0f}, 
            {1.0f,0.5f,0.0f},             // Set The Color To Orange
            {1.0f,0.0f,0.0f},             // Set The Color To Red
            {1.0f,1.0f,0.0f},             // Set The Color To Yellow
            {0.0f,0.0f,1.0f},             // Set The Color To Blue
            {1.0f,0.0f,1.0f},             // Set The Color To Violet
	};
	
	private int[] indices = {
			0, 4, 8, 12, 16, 20
	};

	@Override
	public int getType() {
		return TYPE;
	}

	@Override
	public float[][] getVertices() {
		return vertices;
	}

	@Override
	public float[][] getColours() {
		return colours;
	}
	
	@Override
	public int[] getIndices() {
		return indices;
	}
}
