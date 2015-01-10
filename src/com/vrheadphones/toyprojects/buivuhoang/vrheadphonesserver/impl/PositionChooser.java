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
import java.util.List;

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
	private boolean isFileChanged = true;
	private List<Sound3D> soundList;
	
	public static String FRAME_TITLE = "VR Headphones Server";
	public static boolean RIGHT_TO_LEFT = false;

	private JButton okButton = new JButton("OK");
	private JButton deleteButton = new JButton("Delete");
	
	private SoundPositionPanel positionFieldPanel = new SoundPositionPanel();

	private JFrame appFrame;
	private JPanel buttonPanel = new JPanel();
	private JPanel instructionsPanel = new JPanel();
	
	public PositionChooser() {
		soundList = positionFieldPanel.getSoundList();
	}

	private void addComponentsToPane(Container pane) {
		okButton.addActionListener(new OKButtonAction());
		deleteButton.addActionListener(new DeleteButtonAction());
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		positionFieldPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		instructionsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}
		
		instructionsPanel.setLayout(new BoxLayout(instructionsPanel,
				BoxLayout.PAGE_AXIS));
		pane.add(instructionsPanel, BorderLayout.NORTH);
		pane.add(positionFieldPanel, BorderLayout.CENTER);
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel,
				BoxLayout.LINE_AXIS));
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
		menuBar.add(menu);

//		// Open wave file
//		menuItem = new JMenuItem("Open Mono Wave File", KeyEvent.VK_O);
//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
//				ActionEvent.ALT_MASK));
//		menuItem.getAccessibleContext().setAccessibleDescription(
//				"Opens a mono wave file to use as a 3D sound source");
//		menuItem.addActionListener(new OpenWaveFileAction());
//		menu.add(menuItem);
//		
//		// Close
//		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Closes the programme");
		menuItem.addActionListener(new ExitAppAction());
		menu.add(menuItem);

		frame.setJMenuBar(menuBar);
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
        	isFileChanged = true;
        	soundList = positionFieldPanel.getSoundList();
//        	for (Sound3D pos: positionFieldPanel.getSoundList())
//        		System.out.println(pos.x + " " + pos.y);
        }
    }
    
    private class DeleteButtonAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	positionFieldPanel.deleteSelected();
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

	public void updateRotationalData(float x, float y, float z) {}

	public void setIpAddress(String ipAddress) {
		instructionsPanel.removeAll();

		String[] instructions = {
				"The VRHeadphonesServer application is now running.", "",
				"Your IP address is: " + ipAddress, "",
				"Enter this IP address on the start screen of the",
				"VRHeadphones application on your phone to begin." };

		for (String s : instructions) {
			JLabel label = new JLabel(s);
			instructionsPanel.add(label);
		}
		appFrame.pack();
	}

	public boolean isFileChanged() {
		if (isFileChanged) {
			isFileChanged = false;
			return true;
		} 
		return false;
	}
	
	public List<Sound3D> getSoundList() {
		return soundList;
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PositionChooser appMainView = new PositionChooser();
				appMainView.createAndShowGUI();
				appMainView.setIpAddress("127.0.0.1");
			}
		});
	}
}
