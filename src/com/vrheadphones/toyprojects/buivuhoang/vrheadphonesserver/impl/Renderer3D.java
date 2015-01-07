package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

/*
 *      This Code Was Created By Jeff Molofee 2000
 *      A HUGE Thanks To Fredric Echols For Cleaning Up
 *      And Optimizing The Base Code, Making It More Flexible!
 *      If You've Found This Code Useful, Please Let Me Know.
 *      Visit My Site At nehe.gamedev.net
 */

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import utility.BufferTools;
import utility.Camera;
import utility.EulerCamera;
import utility.Model;
import utility.OBJLoader;

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.Shape3D;

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
	
	private static Camera camera;
	private static int bunnyDisplayList;
	
    private static final String MODEL_LOCATION = "res/models/bunny.obj";
	
    private static float[] lightPosition = {-2.19f, 1.36f, 11.45f, 1f};
    
	private Object monitor;
    private boolean done = false;
    private static boolean fullscreen = false;
    private final static String windowTitle = "Cube";

    private DisplayMode displayMode;
    
    private float thetaX;
    private float thetaY;
    private float thetaZ;
    
    private Shape3D cube = new Cube3D();
    
    public Renderer3D(Object monitor) {
    	this.monitor = monitor;
    }
       
    public boolean render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

        GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix
        
        camera.setRotation((float)Math.toDegrees(thetaY), (float)Math.toDegrees(thetaX), (float)Math.toDegrees(thetaZ));
        camera.applyTranslations();
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glCallList(bunnyDisplayList);
        
        return true;
    }
    
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
                && d[i].getHeight() == 480
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        Display.create();
    }
    
    private static void setUpDisplayLists() {
        bunnyDisplayList = glGenLists(1);
        glNewList(bunnyDisplayList, GL_COMPILE);
        {
            Model m = null;
            try {
                m = OBJLoader.loadModel(new File(MODEL_LOCATION));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Display.destroy();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                Display.destroy();
                System.exit(1);
            }
            
            GL11.glColor3f(0.4f, 0.27f, 0.17f);
            glBegin(GL_TRIANGLES);
            for (Model.Face face : m.getFaces()) {
                Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                glNormal3f(n1.x, n1.y, n1.z);
                Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                glVertex3f(v1.x, v1.y, v1.z);
                Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                glNormal3f(n2.x, n2.y, n2.z);
                Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                glVertex3f(v2.x, v2.y, v2.z);
                Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                glNormal3f(n3.x, n3.y, n3.z);
                Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                glVertex3f(v3.x, v3.y, v3.z);
            }
            glEnd();
        }
        glEndList();
    }
    
    private static void setUpCamera() {
        camera = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f).setPosition(-1.38f, 1.36f, 20f).setFieldOfView(60).build();
        camera.applyOptimalStates();
        camera.applyPerspectiveMatrix();
    }
    
    private static void setUpLighting() {
    	GL11.glShadeModel(GL11.GL_SMOOTH);
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
    	GL11.glEnable(GL11.GL_LIGHTING);
    	GL11.glEnable(GL11.GL_LIGHT0);
    	GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, BufferTools.asFlippedFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
    	GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, BufferTools.asFlippedFloatBuffer(new float[]{0, 0, 0, 1}));
    	GL11.glEnable(GL11.GL_CULL_FACE);
    	GL11.glCullFace(GL11.GL_BACK);
    	GL11.glEnable(GL11.GL_COLOR_MATERIAL);
    	GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE);
    }
    
    public void init() throws Exception {
        createWindow();
        initGL();
        setUpDisplayLists();
        setUpCamera();
        setUpLighting();
    }

    private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
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
    
    public void cleanup() {
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
	            	monitor.wait();
	                render();
	                Display.update();
	            }
            }
            cleanup();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
	}

	public static boolean isCloseRequested() {
		return Display.isCloseRequested();
	}
}
