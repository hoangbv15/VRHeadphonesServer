package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PositionChooser {
	
	private static final String TARGET_IMAGE = "res/imgs/target_large.png";
	private File waveFile;
	private boolean isFileChanged = true;
	
	public static String FRAME_TITLE = "VR Headphones Server";
	public static boolean RIGHT_TO_LEFT = false;

	private JLabel xLabel = new JLabel("x: ");
	private JLabel yLabel = new JLabel("y: ");
	private JLabel zLabel = new JLabel("z: ");

	private JLabel xValueLabel = new JLabel("0.0");
	private JLabel yValueLabel = new JLabel("0.0");
	private JLabel zValueLabel = new JLabel("0.0");

	private JLabel fileNameLabel = new JLabel("Chosen wave file: default");
	
	private JButton okButton = new JButton("OK");
	private JButton addButton = new JButton("Add");
	private JButton deleteButton = new JButton("Delete");
	
	private ImagePanel positionFieldPanel = new ImagePanel();

	private JFrame appFrame;
	private JPanel buttonPanel = new JPanel();

	private void addComponentsToPane(Container pane) {
		okButton.addActionListener(new OKButtonAction());
		
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		positionFieldPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}

		pane.add(positionFieldPanel, BorderLayout.CENTER);
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel,
				BoxLayout.LINE_AXIS));
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(okButton);
		
		pane.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addMenuBar(JFrame frame) {
		// Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		// Open wave file
		menuItem = new JMenuItem("Open Mono Wave File", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Opens a mono wave file to use as a 3D sound source");
		menuItem.addActionListener(new OpenWaveFileAction());
		menu.add(menuItem);
		
		// Close
		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Closes the programme");
		menuItem.addActionListener(new ExitAppAction());
		menu.add(menuItem);

		frame.setJMenuBar(menuBar);
	}
	
	// Opens a wave file to use as a 3D sound source
	private class OpenWaveFileAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	// Loads the wave file from your file system
    		JFileChooser chooser = new JFileChooser();
    		FileNameExtensionFilter filter = new FileNameExtensionFilter(
    				"WAVE sound files", "wav");
    		chooser.setFileFilter(filter);
    		int returnVal = chooser.showOpenDialog(null);
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
    			isFileChanged = true;
    			waveFile = chooser.getSelectedFile();
    			fileNameLabel.setText("Chosen wave file: " + waveFile.getName());
    		}
        }
    }
	
	// Exit app
    private static class ExitAppAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }
    
    private class OKButtonAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	System.out.println(positionFieldPanel.getPosX() + " " + positionFieldPanel.getPosY());
        }
    }

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public void createAndShowGUI() {
		// Create and set up the window.
		if (appFrame == null) {
			appFrame = new JFrame(FRAME_TITLE);
			appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addMenuBar(appFrame);
			// Set up the content pane.
			addComponentsToPane(appFrame.getContentPane());
			// Use the content pane's default BorderLayout. No need for
			// setLayout(new BorderLayout());
			// Display the window.
			appFrame.pack();
		}
		appFrame.setVisible(true);
	}

	public void updateRotationalData(float x, float y, float z) {
		xValueLabel.setText(x + "");
		yValueLabel.setText(y + "");
		zValueLabel.setText(z + "");
	}

	public void setIpAddress(String ipAddress) {
		buttonPanel.removeAll();

		String[] instructions = {
				"The VRHeadphonesServer application is now running.", "",
				"Your IP address is: " + ipAddress, "",
				"Enter this IP address on the start screen of the",
				"VRHeadphones application on your phone to begin." };

		for (String s : instructions) {
			JLabel label = new JLabel(s);
			buttonPanel.add(label);
		}
	}

	public File getWaveFile() {
		return waveFile;
	}
	
	public boolean isFileChanged() {
		if (isFileChanged) {
			isFileChanged = false;
			return true;
		} 
		return false;
	}
	
	private class ImagePanel extends JPanel {
		private BufferedImage image;
		
		private BufferedImage circle;
		
		private int rawX;
		private int rawY;
		private int calculatedX;
		private int calculatedY;
		
		private boolean positionSet = false;
		
	    public ImagePanel() {
	       try {                
	          image = ImageIO.read(new File(TARGET_IMAGE));
	          circle = ImageIO.read(new File("res/imgs/Circle.png"));
	          addMouseListener(new MouseListener() {
	        	    @Override
	        	    public void mouseClicked(MouseEvent e) {
	        	    	
	        	    	if (isWithinSetPosition(e.getX(), e.getY())) {
	        	    		return;
	        	    	}
	        	    	
	        	    	positionSet = true;
	        	    	rawX = e.getX();
	        	    	rawY = e.getY();
	        	  	
	        	    	calculatedX = rawX - image.getWidth()/2;
	        	    	calculatedY = -(rawY - image.getHeight()/2);
	        	        
	        	        repaint();
	        	    }

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {}

					@Override
					public void mouseExited(MouseEvent e) {}
	        	});
	       } catch (IOException ex) {
	            // handle exception...
	       }
	    }

	    protected boolean isWithinSetPosition(int x, int y) {
	    	if (Math.abs(x - rawX) < circle.getWidth()/2 && 
	    			Math.abs(y - rawY) < circle.getHeight()/2)
	    		return true;
			return false;
		}

		@Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters       
	        
	        if (positionSet) 
	        	g.drawImage(circle, rawX - circle.getWidth()/2, rawY - circle.getHeight()/2, null);
	    }

		@Override
		public Dimension getPreferredSize() {
			Dimension size = new Dimension(image.getWidth(), image.getHeight());
			return size;
		}
	
		public int getPosX() {
			return calculatedX;
		}
		
		public int getPosY() {
			return calculatedY;
		}
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PositionChooser appMainView = new PositionChooser();
				appMainView.createAndShowGUI();
			}
		});
	}
}
