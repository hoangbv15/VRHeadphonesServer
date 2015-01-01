package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vrheadphones.toyprojects.buivuhoang.utilities.SpringUtilities;

public class AppMainView {
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
	
	private JPanel rotationalDataPanel = new JPanel(new SpringLayout());

	private static int numPairs = 3;

	private JFrame appFrame;
	private JPanel instructionsPanel = new JPanel();

	private void addComponentsToPane(Container pane) {
		
		instructionsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		rotationalDataPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		fileNameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}

		xLabel.setLabelFor(xValueLabel);
		yLabel.setLabelFor(yValueLabel);
		zLabel.setLabelFor(zValueLabel);

		rotationalDataPanel.add(xLabel);
		rotationalDataPanel.add(xValueLabel);
		rotationalDataPanel.add(yLabel);
		rotationalDataPanel.add(yValueLabel);
		rotationalDataPanel.add(zLabel);
		rotationalDataPanel.add(zValueLabel);

		SpringUtilities.makeCompactGrid(rotationalDataPanel, numPairs, 2, // rows,
																			// cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		pane.setPreferredSize(new Dimension(350, 250));
		pane.add(rotationalDataPanel, BorderLayout.CENTER);
		instructionsPanel.setLayout(new BoxLayout(instructionsPanel,
				BoxLayout.PAGE_AXIS));
		pane.add(instructionsPanel, BorderLayout.NORTH);
		pane.add(fileNameLabel, BorderLayout.SOUTH);
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
}
