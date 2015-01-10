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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
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
	private static String CHARSET = "US-ASCII";
	private boolean isFileLoaded = false;
	// File chooser for open/save scenario dialogs
	private JFileChooser chooser;
	private File currentFile;
	
	private boolean isScenarioModified = true;
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
		// Setting up 
		chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Scenario files", "scn");
		chooser.setFileFilter(filter);
		
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
		menuItem = new JMenuItem("Open Scenario", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Opens a saved scenario");
		menuItem.addActionListener(new OpenScenarioFileAction());
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save Scenario", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save a scenario");
		menuItem.addActionListener(new SaveScenarioFileAction());
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save Scenario As", KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save a scenario as");
		menuItem.addActionListener(new SaveScenarioAsFileAction());
		menu.add(menuItem);
		
		// Close
		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Closes the programme");
		menuItem.addActionListener(new ExitAppAction());
		menu.add(menuItem);

		frame.setJMenuBar(menuBar);
	}
	
	private class OpenScenarioFileAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	// Loads the scenario file from your file system
    		int returnVal = chooser.showOpenDialog(null);
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
    			
    			currentFile = chooser.getSelectedFile();
    			
    			String path = currentFile.getParentFile().getAbsolutePath();
    			Charset charset = Charset.forName(CHARSET);
    			try (BufferedReader reader = Files.newBufferedReader(currentFile.toPath(), charset)) {
    			    String line = null;
    			    List<Sound3D> soundList = new ArrayList<Sound3D>();
    			    while ((line = reader.readLine()) != null) {
    			    	// parse the scenario file
    			    	String[] splittedLine = line.split("\t+");
    			    	float x = Float.parseFloat(splittedLine[1]);
    			    	float y = Float.parseFloat(splittedLine[2]);
    			    	Sound3D newSound = new Sound3D(x, y, 0);
    			    	newSound.waveFile = new File(path + "\\" + splittedLine[0]);
    			    	soundList.add(newSound);
    			    }
    			    reader.close();
    			    positionFieldPanel.setSoundListInternal(soundList);
    			    // File loaded, set this to true to prevent save from opening a new dialog
    			    isFileLoaded = true;
    			    
    			    // refresh the new changes
    			    refresh();
    			} catch (IOException x) {
    			    System.err.format("IOException: %s%n", x);
    			}
    		}
        }
    }
	
	private File openSaveDialog() {
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
	
	private void saveToFile(File file) {
		String s = "";
    	for (Sound3D sound: positionFieldPanel.getSoundListInternal())
    		s += sound.waveFile.getName() + "\t" + sound.x + "\t" + sound.y + "\n";
    	Charset charset = Charset.forName(CHARSET);
    	try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
    	    writer.write(s, 0, s.length());
    	    writer.close();
    	} catch (IOException x) {
    	    System.err.format("IOException: %s%n", x);
    	}
	}
	
	private class SaveScenarioFileAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	if (!isScenarioModified)
        		return;
        	if (!isFileLoaded)
        		currentFile = openSaveDialog();
        	
        	saveToFile(currentFile);
        	isFileLoaded = true;
        }
    }
	
	private class SaveScenarioAsFileAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	currentFile = openSaveDialog();
        	saveToFile(currentFile);
        	isFileLoaded = true;
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
    
    private void refresh() {
    	isScenarioModified = true;
    	soundList = positionFieldPanel.getSoundList();
    }
    
    private class OKButtonAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	refresh();
//        	for (Sound3D pos: positionFieldPanel.getSoundList())
//        		System.out.println(pos.waveFile.getName() + ": " + pos.x + " " + pos.y);
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

	public boolean isScenarioModified() {
		if (isScenarioModified) {
			isScenarioModified = false;
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
//				appMainView.setIpAddress("127.0.0.1");
			}
		});
	}
}
