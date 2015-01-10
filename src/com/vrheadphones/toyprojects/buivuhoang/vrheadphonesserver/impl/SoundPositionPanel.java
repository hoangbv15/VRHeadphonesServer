package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SoundPositionPanel extends JPanel {
	private static final long serialVersionUID = -5319234978792101719L;
	
	private static final String TARGET_IMAGE = "res/imgs/target_large.png";
	private static final String CIRCLE_IMAGE = "res/imgs/Circle.png";
	private static final int FONT_SIZE = 20;
	private static final int FILENAME_LENGTH = 15;
	private static final String FONT_FACE = "Arial";
	
	private BufferedImage image, circle;
	
	private List<Sound3D> rawList = new ArrayList<Sound3D>();
	private List<Sound3D> selected = new ArrayList<Sound3D>();
	
	public SoundPositionPanel() {
		try {
			image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(TARGET_IMAGE));
			circle = ImageIO.read(getClass().getClassLoader().getResourceAsStream(CIRCLE_IMAGE));
			ControlListener controlListener = new ControlListener();
			addMouseListener(controlListener);
			addMouseMotionListener(controlListener);
			addKeyListener(controlListener);
			setFocusable(true);
			addSampleSounds();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method is meant to be used internally only
	 */
	public void setSoundListInternal(List<Sound3D> rawList) {
		this.rawList = rawList;
		repaint();
	}
	
//	/**
//	 * This method is meant to be used internally only
//	 * x, y needs to be the circle's position on the picture, not the sound's position.
//	 */
//	public void addSoundInternal(File file, float x, float y) {
//		Sound3D defaultSound = new Sound3D(x, y, 0);
//		defaultSound.waveFile = file;
//		rawList.add(defaultSound);
//	}
	
	public void addSampleSounds() {
		// Add a default sound
		
		float x = image.getWidth() / 2 + 100;
		float y = -4 * 40 + image.getHeight() / 2;
		
		Sound3D defaultSound = new Sound3D(x, y, 0);
		rawList.add(defaultSound);
	}

	// Opens a wave file to use as a 3D sound source
	private void OpenWaveFileAction(Sound3D sound) {
		// Loads the wave file from your file system
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"WAVE sound files", "wav");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			sound.waveFile = chooser.getSelectedFile();
		}
	}
	
    private class ControlListener extends MouseInputAdapter implements KeyListener {
    	private boolean shift_hold = false;
		
		@Override
		public void mousePressed(MouseEvent e) {
			requestFocus();
			
			int index = isWithinSetPosition(e.getX(), e.getY());
	    	if (index < 0) {
				Sound3D newRawPos = new Sound3D(0, 0, 0);
				rawList.add(newRawPos);
				index = rawList.size() - 1;
				updatePosition(e, newRawPos);
	    	}
	    	
	    	Sound3D pos = rawList.get(index);
	    	
	    	// Double clicking
			if (e.getClickCount() == 2 && !e.isConsumed()) {
			     e.consume();
			     OpenWaveFileAction(pos);
			}
			else if (shift_hold)
				if (!selected.contains(pos))
					selected.add(pos);
				else
					selected.remove(pos);
			else {
				if (!selected.contains(pos)) {
					selected.clear();
					selected.add(pos);
				} else
					selected.clear();
			}
			repaint();
    		return;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			int index = isWithinSetPosition(e.getX(), e.getY());
			if (index >= 0) {
				updatePosition(e, rawList.get(index));
			}
		}
    	
		private void updatePosition(MouseEvent e, Sound3D rawPost) {
			rawPost.x = e.getX();
			rawPost.y = e.getY();
	        repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT || 
					e.getKeyCode() == KeyEvent.VK_CONTROL)
				shift_hold = true;
			else if (e.getKeyCode() == KeyEvent.VK_DELETE)
				deleteSelected();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT || 
					e.getKeyCode() == KeyEvent.VK_CONTROL)
				shift_hold = false;
		}

		@Override
		public void keyTyped(KeyEvent e) {}
    }

    protected int isWithinSetPosition(int x, int y) {
    	for (int i = 0; i < rawList.size(); i++) {
    		Sound3D pos = rawList.get(i);
	    	if (Math.abs(x - pos.x) < circle.getWidth()/2 && 
	    			Math.abs(y - pos.y) < circle.getHeight()/2)
	    		return i;
    	}
		return -1;
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);     
    	for (int i = 0; i < rawList.size(); i++) {
    		Sound3D pos = rawList.get(i);
    		int x = (int)pos.x - circle.getWidth()/2;
    		int y = (int)pos.y - circle.getHeight()/2;
    		g.setFont(new Font(FONT_FACE, Font.PLAIN, FONT_SIZE));
    		g.drawImage(circle, x, y, null);
    		g.drawString("" + (i + 1), (int)pos.x - FONT_SIZE/4, (int)pos.y + FONT_SIZE/3);
    		
    		if (pos.waveFile != null) {
    			g.drawString(truncateString(pos.waveFile.getName(), FILENAME_LENGTH), x - FONT_SIZE, y);
    		}
    		
    		// Draw a dashed rectangle around selected items
    		if (selected.contains(pos)) {
	    		Graphics2D g2D = (Graphics2D)g;  
	    		Rectangle2D rect = new Rectangle2D.Float(x, y, circle.getWidth(), circle.getHeight());  
	    		float[] dash = {5F, 5F};  
	    		Stroke dashedStroke = new BasicStroke(1F, BasicStroke.CAP_SQUARE,  
	    				BasicStroke.JOIN_MITER, 3F, dash, 0F);  
	    		g2D.fill( dashedStroke.createStrokedShape(rect));
    		}
    	}
    }
	
	public void deleteSelected() {
		for (Sound3D pos: selected) {
			rawList.remove(pos);
		}
		
		selected.clear();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(image.getWidth(), image.getHeight());
		return size;
	}
	
	public List<Sound3D> getSoundList() {
		List<Sound3D> calculatedList = new ArrayList<Sound3D>();
		
		for (Sound3D rawPost: rawList) {
			
//			System.out.println(rawPost.waveFile.getName() + "\t" + rawPost.x + "\t" + rawPost.y);
			
			float x = ((float)rawPost.x - image.getWidth()/2)/40;
			float y = -((float)rawPost.y - image.getHeight()/2)/40;
			Sound3D calculatedPos = new Sound3D(x, y, 0);
			calculatedPos.waveFile = rawPost.waveFile;
			calculatedList.add(calculatedPos);
		}
		
		return calculatedList;
	}
	
	public List<Sound3D> getSoundListInternal() {
		return rawList;
	}
	
	public static String truncateString(String s, int length) {
		String newString = s;
		
		if (newString.length() > length) {
			newString = newString.substring(0, length - 3);
			newString += "...";
		}
		
		return newString;
	}
}