package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

public class MainUserInterface {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final String DOT_EXT = ".";

	public static final ResourceBundle RESOURCE = ResourceBundle.getBundle("res.localisation.lang");
	
	public static String FRAME_TITLE = RESOURCE.getString("FRAME_TITLE");
	public static final String SCENE_EXTENSION = RESOURCE.getString("SCENE_EXTENSION");
	public static final String SCENE_DESCRIPTION = RESOURCE.getString("SCENE_DESCRIPTION");
	private static String CHARSET = RESOURCE.getString("CHARSET");
	
	private static final String INSTRUCTIONSLINE4 = RESOURCE.getString("INSTRUCTIONSLINE4");
	private static final String INSTRUCTIONSLINE3 = RESOURCE.getString("INSTRUCTIONSLINE3");
	private static final String INSTRUCTIONSLINE2 = RESOURCE.getString("INSTRUCTIONSLINE2");;
	private static final String INSTRUCTIONSLINE1 = RESOURCE.getString("INSTRUCTIONSLINE1");
	private static final String EXIT_DESCRIPTION = RESOURCE.getString("MENUITEM_EXIT_DESCRIPTION");
	private static final String EXIT = RESOURCE.getString("MENUITEM_EXIT");
	private static final String SAVE_SCENARIO_AS_DESCRIPTION = RESOURCE.getString("MENUITEM_SAVESCENARIOAS_DESCRIPTION");
	private static final String SAVE_SCENARIO_AS = RESOURCE.getString("MENUITEM_SAVESCENARIOAS");
	private static final String SAVE_SCENARIO_DESCRIPTION = RESOURCE.getString("MENUITEM_SAVESCENARIO_DESCRIPTION");
	private static final String SAVE_SCENARIO = RESOURCE.getString("MENUITEM_SAVESCENARIO");
	private static final String OPENS_SCENARIO_DESCRIPTION = RESOURCE.getString("MENUITEM_OPENSCENARIO_DESCRIPTION");
	private static final String OPEN_SCENARIO = RESOURCE.getString("MENUITEM_OPENSCENARIO");
	private static final String FILE = RESOURCE.getString("MENU_FILE");
	private JButton okButton = new JButton(RESOURCE.getString("OK_BUTTON"));
	private JButton deleteButton = new JButton(RESOURCE.getString("DELETE_BUTTON"));
	
	private boolean isFileLoaded = false;
	// File chooser for open/save scenario dialogs
	private File currentFile;
	private JFileChooser fileChooser = new JFileChooser();
	
	private boolean isScenarioModified = true;
	private List<Sound3D> soundList;
	
	public static boolean RIGHT_TO_LEFT = false;

	private SoundPositionPanel positionFieldPanel = new SoundPositionPanel(fileChooser);

	private JFrame appFrame;
	private JPanel buttonPanel = new JPanel();
	private JPanel instructionsPanel = new JPanel();
	
	public MainUserInterface() {
		// Setting up 
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
		menu = new JMenu(FILE);
		menu.setMnemonic(KeyEvent.VK_A);
		menuBar.add(menu);

//		// Open wave file
		menuItem = new JMenuItem(OPEN_SCENARIO, KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				OPENS_SCENARIO_DESCRIPTION);
		menuItem.addActionListener(new OpenScenarioFileAction());
		menu.add(menuItem);
		
		menuItem = new JMenuItem(SAVE_SCENARIO, KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				SAVE_SCENARIO_DESCRIPTION);
		menuItem.addActionListener(new SaveScenarioFileAction());
		menu.add(menuItem);
		
		menuItem = new JMenuItem(SAVE_SCENARIO_AS, KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				SAVE_SCENARIO_AS_DESCRIPTION);
		menuItem.addActionListener(new SaveScenarioAsFileAction());
		menu.add(menuItem);
		
		// Close
		menu.addSeparator();

		menuItem = new JMenuItem(EXIT, KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				EXIT_DESCRIPTION);
		menuItem.addActionListener(new ExitAppAction());
		menu.add(menuItem);

		frame.setJMenuBar(menuBar);
	}
	
	private class OpenScenarioFileAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	FileNameExtensionFilter sceneFilter = new FileNameExtensionFilter(
    				SCENE_DESCRIPTION, SCENE_EXTENSION);
        	fileChooser.setFileFilter(sceneFilter);
        	// Loads the scenario file from your file system
    		int returnVal = fileChooser.showOpenDialog(null);
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
    			
    			currentFile = fileChooser.getSelectedFile();
    			
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
    			    	if (!DEFAULT_SOUND_NAME.equals(splittedLine[0]))
    			    		newSound.waveFile = new File(path + File.separator + splittedLine[0]);
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
		FileNameExtensionFilter sceneFilter = new FileNameExtensionFilter(
				SCENE_DESCRIPTION, SCENE_EXTENSION);
    	fileChooser.setFileFilter(sceneFilter);
		int returnVal = fileChooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (!sceneFilter.accept(selectedFile)) {
				selectedFile = new File(selectedFile.getPath() + DOT_EXT + SCENE_EXTENSION);
			}
			return selectedFile;
		}
		return null;
	}
	
	private void saveToFile(File file) {
		String s = "";
    	for (Sound3D sound: positionFieldPanel.getSoundListInternal()) {
    		String soundName = DEFAULT_SOUND_NAME;
    		if (sound.waveFile != null)
    			soundName = sound.waveFile.getName();
    		s += soundName + "\t" + sound.x + "\t" + sound.y + "\n";
    	}
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
		appFrame.setResizable(false);
	}

	public void updateRotationalData(float x, float y, float z) {}

	public void setIpAddress(String ipAddress) {
		instructionsPanel.removeAll();

		String[] instructions = {
				INSTRUCTIONSLINE1, "",
				INSTRUCTIONSLINE2 + ipAddress, "",
				INSTRUCTIONSLINE3,
				INSTRUCTIONSLINE4 };

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
				MainUserInterface appMainView = new MainUserInterface();
				appMainView.createAndShowGUI();
//				appMainView.setIpAddress("127.0.0.1");
			}
		});
	}
}
