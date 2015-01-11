package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

/*
 *      This Code Was Created By Jeff Molofee 2000
 *      A HUGE Thanks To Fredric Echols For Cleaning Up
 *      And Optimizing The Base Code, Making It More Flexible!
 *      If You've Found This Code Useful, Please Let Me Know.
 *      Visit My Site At nehe.gamedev.net
 */

import static org.lwjgl.opengl.GL11.glCallList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import utility.BufferTools;
import utility.Camera;
import utility.EulerCamera;
import utility.OBJLoader;

/**
 * @author Mark Bernard
 * date:    16-Nov-2003
 *
 * Port of NeHe's Lesson 5 to LWJGL
 * Title: 3D Shapes
 * Uses version 0.8alpha of LWJGL http://www.lwjgl.org/
 *
 * Be sure that the LWJGL libraries are in your classpath
 *
 * Ported directly from the C++ version
 *
 * 2004-05-08: Updated to version 0.9alpha of LWJGL.
 *             Changed from all static to all instance objects.
 * 2004-09-22: Updated to version 0.92alpha of LWJGL.
 */
public class Renderer3D implements Runnable {
	private final static boolean VSYNC = true;
    private final static int FRAME_RATE = 60;
	
	private static List<Sound3D> soundList;
	private static boolean refresh = false;
	
	private static Camera camera;
//	private static float[] camPosition = {-1.38f, 1.36f, 10f};
	private static float[] camPosition = {0.5f, 0.5f, 3f};
	private static List<Integer> displayList;
//	private static int shaderProgram;
	
    private static final String MODEL_LOCATION = "res/models/cube.obj";
//    private static final String VERTEX_SHADER_LOCATION = "res/shaders/vertex_phong_lighting.vs";
//    private static final String FRAGMENT_SHADER_LOCATION = "res/shaders/vertex_phong_lighting.fs";
    
    private static float[] lightPosition = {-2.19f, 1.36f, 11.45f, 1f};
    
	private Object monitor;
    private static boolean done = false;
    private static boolean fullscreen = false;
    private final static String WINDOW_TITLE = "Sound Position in 3D";
    
    private DisplayMode displayMode;
    
    private float thetaX;
    private float thetaY;
    private float thetaZ;
    
    public Renderer3D(Object monitor) {
    	this.monitor = monitor;
    
    }
    
	private void inputPoll() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			done = true;
		}
		if (Display.isCloseRequested()) { // Exit if window is closed
			done = true;
		}
	}
       
    public boolean render() throws Exception {
    	if (needsRefresh()) {
    		setUpDisplayLists();
    	}
    	
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

        GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix
        
        
        
        camera.setRotation((float)Math.toDegrees(thetaY), (float)Math.toDegrees(thetaX), (float)Math.toDegrees(thetaZ));
        camera.applyTranslations();
//        GL20.glUseProgram(shaderProgram);
        
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glColor3f(0.1f, 0.2f, 0.5f);
        for (Integer display: displayList)
        	glCallList(display);
        
//        GL20.glUseProgram(0);
        Display.sync(FRAME_RATE);
        return true;
    }
    
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 1280
                && d[i].getHeight() == 720
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle(WINDOW_TITLE);
        Display.setVSyncEnabled(VSYNC);
        Display.create();
    }
    
    public static void setSoundList(List<Sound3D> newSoundList) {
    	soundList = newSoundList;
    	refresh = true;
    }
    
    private static boolean needsRefresh() {
    	if (refresh) {
    		refresh = false;
    		return true;
    	}
    	return false;
    }
        
    private static void setUpDisplayLists() {
    	try {
    		displayList = new ArrayList<Integer>(soundList.size());
    		for (int i = 0; i < soundList.size(); i++) {
    			Sound3D sound = soundList.get(i);
    			float x = - sound.x;
    			float y = sound.y;
    			float z = sound.z;
    			int display = OBJLoader.createDisplayList(OBJLoader.loadModel(
    					sound.getClass().getClassLoader().getResourceAsStream(MODEL_LOCATION)), x, z, y);
    			displayList.add(display);
    		}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cleanup();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            cleanup();
            System.exit(1);
        }
    }
    
    private static void setUpCamera() {
        camera = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(camPosition[0], camPosition[1], camPosition[2])
                .setFieldOfView(60).build();
        camera.applyOptimalStates();
        camera.applyPerspectiveMatrix();
    }
    
    private static void setUpLighting() {
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, BufferTools.asFlippedFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, BufferTools.asFlippedFloatBuffer(lightPosition));
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE);
        GL11.glColor3f(0.4f, 0.27f, 0.17f);
    }
    
//    private static void setUpShaders() {
//        shaderProgram = ShaderLoader.loadShaderPair(VERTEX_SHADER_LOCATION, FRAGMENT_SHADER_LOCATION);
//    }
    
    public void init() throws Exception {
        createWindow();
        initGL();
//        setUpDisplayLists();
        setUpCamera();
//        setUpShaders();
        setUpLighting();
    }

    private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
          45.0f,
          (float) displayMode.getWidth() / (float) displayMode.getHeight(),
          0.1f,
          100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }
    
    public static void cleanup() {
//    	GL20.glDeleteProgram(shaderProgram);
    	if (displayList != null)
	    	for (Integer display: displayList)
	    		GL11.glDeleteLists(display, 1);
        Display.destroy();
    }
    
    public synchronized void setRotationAngle(float thetaX, float thetaY, float thetaZ) {
    	synchronized(monitor) {
    		this.thetaX = thetaX;
        	this.thetaY = thetaY;
        	this.thetaZ = thetaZ;
    		monitor.notify();
    	}
    }
    
	@Override
	public void run() {
		try {
			init();
            synchronized(monitor) {
	            while (!done) {
	            	inputPoll();
	            	monitor.wait();
	                render();
	                Display.update();
	            }
            }
            cleanup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		finally {
			System.exit(0);
		}
	}

	public static boolean isCloseRequested() {
		return done;
	}
}
