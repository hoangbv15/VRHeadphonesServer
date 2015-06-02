package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SoundPositionPanel extends JPanel {

	private static final long serialVersionUID = -5319234978792101719L;
	
	public static final String WAV_EXTENSION = GraphicalUserInterface.SYSRES.getString("WAV_EXTENSION");
	public static final String WAV_DESCRIPTION = GraphicalUserInterface.LANGRES.getString("WAV_DESCRIPTION");
	private static final String TARGET_IMAGE = GraphicalUserInterface.SYSRES.getString("TARGET_IMAGE");
	private static final String CIRCLE_IMAGE = GraphicalUserInterface.SYSRES.getString("CIRCLE_IMAGE");
	private static final String ARROW_IMAGE = GraphicalUserInterface.SYSRES.getString("ARROW_IMAGE");
	private static final int FONT_SIZE = Integer.parseInt(GraphicalUserInterface.SYSRES.getString("FONT_SIZE"));
	private static final int FILENAME_LENGTH = Integer.parseInt(GraphicalUserInterface.SYSRES.getString("FILENAME_LENGTH"));
	private static final String FONT_FACE = GraphicalUserInterface.SYSRES.getString("FONT_FACE");

	private BufferedImage targetBoard, circle, arrow;

	private List<Sound3D> rawList = new ArrayList<Sound3D>();
	private List<Sound3D> selected = new ArrayList<Sound3D>();

	private JFileChooser waveChooser;
	
	// The degree in which the arrow in the middle of the image should rotate
	private volatile float thetaX = 0;
		
	// variables to control mouse dragging
	private Sound3D currentSound;
	
	private boolean isScenarioModified = false;

	public SoundPositionPanel(JFileChooser waveChooser) {
		try {
			targetBoard = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream(TARGET_IMAGE));
			circle = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream(CIRCLE_IMAGE));
			arrow = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream(ARROW_IMAGE));
			
			this.waveChooser = waveChooser;
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

	// /**
	// * This method is meant to be used internally only
	// * x, y needs to be the circle's position on the picture, not the sound's
	// position.
	// */
	// public void addSoundInternal(File file, float x, float y) {
	// Sound3D defaultSound = new Sound3D(x, y, 0);
	// defaultSound.waveFile = file;
	// rawList.add(defaultSound);
	// }

	public void addSampleSounds() {
		// Add a default sound

		float x = 0.75f;
		float y = 0.5f;

		Sound3D defaultSound = new Sound3D(x, y, 0);
		rawList.add(defaultSound);
	}

	// Opens a wave file to use as a 3D sound source
	private void OpenWaveFileAction(Sound3D sound) {
		// Loads the wave file from your file system
		FileNameExtensionFilter wavFilter = new FileNameExtensionFilter(WAV_DESCRIPTION, WAV_EXTENSION);
		waveChooser.setFileFilter(wavFilter);
		int returnVal = waveChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			sound.waveFile = waveChooser.getSelectedFile();
		}
	}

	private class ControlListener extends MouseInputAdapter implements
			KeyListener {
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

			currentSound = rawList.get(index);

			// Double clicking
			if (e.getClickCount() == 2 && !e.isConsumed()) {
				e.consume();
				OpenWaveFileAction(currentSound);
			} else if (shift_hold)
				if (!selected.contains(currentSound))
					selected.add(currentSound);
				else
					selected.remove(currentSound);
			else {
				if (!selected.contains(currentSound)) {
					selected.clear();
					selected.add(currentSound);
				} else
					selected.clear();
			}
			repaint();
			return;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			updatePosition(e, currentSound);
		}

		private void updatePosition(MouseEvent e, Sound3D rawPost) {
			// Calculate the relative position of the source to the centre of the image
			rawPost.x = 1 - e.getX() * 2/(float)getWidth();
			rawPost.y = 1 - e.getY() * 2/(float)getHeight();
			repaint();
			isScenarioModified = true;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT
					|| e.getKeyCode() == KeyEvent.VK_CONTROL)
				shift_hold = true;
			else if (e.getKeyCode() == KeyEvent.VK_DELETE)
				deleteSelected();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT
					|| e.getKeyCode() == KeyEvent.VK_CONTROL)
				shift_hold = false;
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}

	protected int isWithinSetPosition(int x, int y) {
		for (int i = 0; i < rawList.size(); i++) {
			Sound3D pos = rawList.get(i);
			int posx = (int) ((1.0f - pos.x) * getWidth() / 2.0f);
			int posy = (int) ((1.0f - pos.y) * getHeight() / 2.0f);
			if (Math.abs(x - posx) < circle.getWidth() / 2
					&& Math.abs(y - posy) < circle.getHeight() / 2)
				return i;
		}
		return -1;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(targetBoard, 0, 0, this.getWidth(), this.getHeight(), null);
		
		for (int i = 0; i < rawList.size(); i++) {
			Sound3D pos = rawList.get(i);
			// Calculate the absolute position of each sound source 
			int x = (int) ((1.0f - pos.x) * getWidth() / 2.0f) - circle.getWidth() / 2;
			int y = (int) ((1.0f - pos.y) * getHeight() / 2.0f) - circle.getHeight() / 2;
			g.setFont(new Font(FONT_FACE, Font.PLAIN, FONT_SIZE));
			
			// Draw a circle representing a sound source
			g.drawImage(circle, x, y, null);
			// Draw the numbering of the sound source
			g.drawString("" + (i + 1), (int) x - FONT_SIZE / 4 + circle.getWidth() / 2, (int) y	+ FONT_SIZE / 3 + circle.getHeight() / 2);

			// Draw the name of the sound source
			if (pos.waveFile != null) {
				g.drawString(
						truncateString(pos.waveFile.getName(), FILENAME_LENGTH),
						x - FONT_SIZE, y);
			}

			// Draw a dashed rectangle around selected items
			if (selected.contains(pos)) {
				Graphics2D g2D = (Graphics2D) g;
				Rectangle2D rect = new Rectangle2D.Float(x, y,
						circle.getWidth(), circle.getHeight());
				float[] dash = { 5F, 5F };
				Stroke dashedStroke = new BasicStroke(1F,
						BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3F,
						dash, 0F);
				g2D.fill(dashedStroke.createStrokedShape(rect));
			}
		}
		
		AffineTransform identity = new AffineTransform();
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform trans = new AffineTransform();
		trans.setTransform(identity);
		trans.translate(this.getWidth()/2 - arrow.getWidth()/2, this.getHeight()/2 - arrow.getHeight()/2);
		trans.rotate( thetaX, arrow.getWidth()/2, arrow.getHeight()/2);
		g2d.drawImage(arrow, trans, this);
	}
	
	public void updateRotationAngle(float thetaX) {
		this.thetaX = thetaX;
		repaint();
	}

	public void deleteSelected() {
		for (Sound3D pos : selected) {
			rawList.remove(pos);
		}

		selected.clear();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(targetBoard.getWidth(), targetBoard.getHeight());
		return size;
	}

	public List<Sound3D> getSoundList() {
		List<Sound3D> calculatedList = new ArrayList<Sound3D>();

		for (Sound3D rawPost : rawList) {
			float x = -rawPost.x * 5;
			float y = rawPost.y * 5;
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
	
	public boolean isScenarioModified() {
		return isScenarioModified;
	}

	public void setScenarioModified(boolean isScenarioModified) {
		this.isScenarioModified = isScenarioModified;
	}
}
