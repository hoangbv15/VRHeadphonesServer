package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

/*
 *      This Code Was Created By Jeff Molofee 2000
 *      A HUGE Thanks To Fredric Echols For Cleaning Up
 *      And Optimizing The Base Code, Making It More Flexible!
 *      If You've Found This Code Useful, Please Let Me Know.
 *      Visit My Site At nehe.gamedev.net
 */

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

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
public class CubeRenderer implements Runnable {
	private Object monitor;
    private boolean done = false;
    private static boolean fullscreen = false;
    private final static String windowTitle = "Cube";

    private DisplayMode displayMode;
    
    private float thetaX;
    private float thetaY;
    private float thetaZ;
    
    public CubeRenderer(Object monitor) {
    	this.monitor = monitor;
    }
       
//    private void mainloop() {
//        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
//            done = true;
//        }
//        if(Display.isCloseRequested()) {                     // Exit if window is closed
//            done = true;
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_F1) && !f1) {    // Is F1 Being Pressed?
//            f1 = true;                                      // Tell Program F1 Is Being Held
//            switchMode();                                   // Toggle Fullscreen / Windowed Mode
//        }
//        if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) {          // Is F1 Being Pressed?
//            f1 = false;
//        }
//    }

    private void switchMode() {
        fullscreen = !fullscreen;
        try {
            Display.setFullscreen(fullscreen);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

        GL11.glLoadIdentity();                          // Reset The Current Modelview Matrix
        GL11.glTranslatef(0.0f, 0.0f,-7.0f);             // Move Right 1.5 Units And Into The Screen 6.0
        GL11.glRotatef((float)Math.toDegrees(thetaY),1.0f,0.0f,0.0f);               // Rotate The Quad
        GL11.glRotatef((float)Math.toDegrees(thetaX),0.0f,1.0f,0.0f);               // Rotate The Quad
        GL11.glRotatef((float)Math.toDegrees(thetaZ),0.0f,0.0f,1.0f);               // Rotate The Quad
        GL11.glColor3f(0.5f,0.5f,1.0f);                 // Set The Color To Blue One Time Only
        GL11.glBegin(GL11.GL_QUADS);                        // Draw A Quad
        // x, z, y
            GL11.glColor3f(0.0f,1.0f,0.0f);             // Set The Color To Green
            GL11.glVertex3f( 1.0f, 1.0f,-3.0f);         // Top Right Of The Quad (Top)
            GL11.glVertex3f(-1.0f, 1.0f,-3.0f);         // Top Left Of The Quad (Top)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);         // Bottom Left Of The Quad (Top)
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);         // Bottom Right Of The Quad (Top)
            GL11.glColor3f(1.0f,0.5f,0.0f);             // Set The Color To Orange
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);         // Top Right Of The Quad (Bottom)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Top Left Of The Quad (Bottom)
            GL11.glVertex3f(-1.0f,-1.0f,-3.0f);         // Bottom Left Of The Quad (Bottom)
            GL11.glVertex3f( 1.0f,-1.0f,-3.0f);         // Bottom Right Of The Quad (Bottom)
            GL11.glColor3f(1.0f,0.0f,0.0f);             // Set The Color To Red
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);         // Top Right Of The Quad (Front)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);         // Top Left Of The Quad (Front)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Bottom Left Of The Quad (Front)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);         // Bottom Right Of The Quad (Front)
            GL11.glColor3f(1.0f,1.0f,0.0f);             // Set The Color To Yellow
            GL11.glVertex3f( 1.0f,-1.0f,-3.0f);         // Bottom Left Of The Quad (Back)
            GL11.glVertex3f(-1.0f,-1.0f,-3.0f);         // Bottom Right Of The Quad (Back)
            GL11.glVertex3f(-1.0f, 1.0f,-3.0f);         // Top Right Of The Quad (Back)
            GL11.glVertex3f( 1.0f, 1.0f,-3.0f);         // Top Left Of The Quad (Back)
            GL11.glColor3f(0.0f,0.0f,1.0f);             // Set The Color To Blue
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);         // Top Right Of The Quad (Left)
            GL11.glVertex3f(-1.0f, 1.0f,-3.0f);         // Top Left Of The Quad (Left)
            GL11.glVertex3f(-1.0f,-1.0f,-3.0f);         // Bottom Left Of The Quad (Left)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);         // Bottom Right Of The Quad (Left)
            GL11.glColor3f(1.0f,0.0f,1.0f);             // Set The Color To Violet
            GL11.glVertex3f( 1.0f, 1.0f,-3.0f);         // Top Right Of The Quad (Right)
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);         // Top Left Of The Quad (Right)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);         // Bottom Left Of The Quad (Right)
            GL11.glVertex3f( 1.0f,-1.0f,-3.0f);         // Bottom Right Of The Quad (Right)
        GL11.glEnd();                                       // Done Drawing The Quad

//        rquad-=0.01f;                                   // Decrease The Rotation Variable For The Quad     ( NEW )
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
    
    public void init() throws Exception {
        createWindow();
        initGL();
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
