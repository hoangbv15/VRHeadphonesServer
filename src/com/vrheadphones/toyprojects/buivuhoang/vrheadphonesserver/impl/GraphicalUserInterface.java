package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.AbstractResizeListener;
import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.InterfaceAdapter;
import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.SoundSliderPlayerLink;

public class GraphicalUserInterface {
	private static final String DEFAULT_SOUND_NAME = "default";
	private static final String DOT_EXT = ".";

	public static final ResourceBundle LANGRES = ResourceBundle.getBundle("res.localisation.lang");
	public static final ResourceBundle SYSRES = ResourceBundle.getBundle("res.localisation.system");
	
	public static String FRAME_TITLE = LANGRES.getString("FRAME_TITLE");
	public static final String SCENE_EXTENSION = SYSRES.getString("SCENE_EXTENSION");
	public static final String SCENE_DESCRIPTION = LANGRES.getString("SCENE_DESCRIPTION");
	private static String CHARSET = SYSRES.getString("CHARSET");
	
	private static final String INSTRUCTIONSLINE4 = LANGRES.getString("INSTRUCTIONSLINE4");
	private static final String INSTRUCTIONSLINE3 = LANGRES.getString("INSTRUCTIONSLINE3");
	private static final String INSTRUCTIONSLINE2 = LANGRES.getString("INSTRUCTIONSLINE2");;
	private static final String INSTRUCTIONSLINE1 = LANGRES.getString("INSTRUCTIONSLINE1");
	private static final String EXIT_DESCRIPTION = LANGRES.getString("MENUITEM_EXIT_DESCRIPTION");
	private static final String EXIT = LANGRES.getString("MENUITEM_EXIT");
	private static final String SAVE_SCENARIO_AS_DESCRIPTION = LANGRES.getString("MENUITEM_SAVESCENARIOAS_DESCRIPTION");
	private static final String SAVE_SCENARIO_AS = LANGRES.getString("MENUITEM_SAVESCENARIOAS");
	private static final String SAVE_SCENARIO_DESCRIPTION = LANGRES.getString("MENUITEM_SAVESCENARIO_DESCRIPTION");
	private static final String SAVE_SCENARIO = LANGRES.getString("MENUITEM_SAVESCENARIO");
	private static final String OPENS_SCENARIO_DESCRIPTION = LANGRES.getString("MENUITEM_OPENSCENARIO_DESCRIPTION");
	private static final String OPEN_SCENARIO = LANGRES.getString("MENUITEM_OPENSCENARIO");
	private static final String FILE = LANGRES.getString("MENU_FILE");
	private JButton okButton = new JButton(LANGRES.getString("OK_BUTTON"));
	private JButton deleteButton = new JButton(LANGRES.getString("DELETE_BUTTON"));
	private ImageIcon playIcon = new ImageIcon(getClass().getClassLoader().getResource(SYSRES.getString("PLAY_ICON")));
	private ImageIcon pauseIcon = new ImageIcon(getClass().getClassLoader().getResource(SYSRES.getString("PAUSE_ICON")));
	private ImageIcon stopIcon = new ImageIcon(getClass().getClassLoader().getResource(SYSRES.getString("STOP_ICON")));
	private JButton playButton = new JButton(playIcon);
	private JButton stopButton = new JButton(stopIcon);
	
	private volatile boolean isPlayed = false;
	
	private volatile boolean isFileLoaded = false;
	// File chooser for open/save scenario dialogs
	
	private volatile boolean isRefreshButtonPressed = true;
	
	private File currentFile;
	private JFileChooser fileChooser = new JFileChooser();
	
	private List<Sound3D> soundList;
	
	public static boolean RIGHT_TO_LEFT = false;

	private SoundPositionPanel positionFieldPanel = new SoundPositionPanel(fileChooser);
	private SoundSliderPanel soundSliderPanel;
	
	private JFrame appFrame;
	private JPanel buttonPanel = new JPanel();
	private JPanel instructionsPanel = new JPanel();
	
	private InterfaceAdapter interfaceAdapter;
	
	private double durationInSeconds = 0;
	
	private Timer sliderTimer;
	
	public GraphicalUserInterface(InterfaceAdapter interfaceAdapter) {
		// Setting up 
		soundList = positionFieldPanel.getSoundList();
		this.interfaceAdapter = interfaceAdapter;
		soundSliderPanel = new SoundSliderPanel(new SliderPlayerLink());
	}

	private void addComponentsToPane(Container pane) {
		if (interfaceAdapter != null)
			durationInSeconds = interfaceAdapter.getDurationInSeconds(soundList);
		
		okButton.addActionListener(new OKButtonAction());
		deleteButton.addActionListener(new DeleteButtonAction());
		playButton.addActionListener(new PlayButtonAction());
		stopButton.addActionListener(new StopButtonAction());
//		playButton.setBorder(BorderFactory.createEmptyBorder());
//		stopButton.setBorder(BorderFactory.createEmptyBorder());
//		playButton.setBorderPainted(false);
		
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		positionFieldPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		instructionsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		soundSliderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
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
		buttonPanel.add(playButton);
		buttonPanel.add(stopButton);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
		southPanel.add(buttonPanel);
		southPanel.add(soundSliderPanel);
		pane.add(southPanel, BorderLayout.SOUTH);
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
        	if (!positionFieldPanel.isScenarioModified())
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
    	isRefreshButtonPressed = true;
    	soundList = positionFieldPanel.getSoundList();
    	if (interfaceAdapter != null) 
    		durationInSeconds = interfaceAdapter.getDurationInSeconds(soundList);
    	stopPlayerAndRefreshControls();
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
    
    private class PlayButtonAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	if (!isPlayed) {
        		// Play
        		isPlayed = true;
        		playButton.setIcon(pauseIcon);
        		if (interfaceAdapter != null) {
        			interfaceAdapter.playButtonPressed();
        			// Start the timer for the slider
        			sliderTimer = new Timer();
        			sliderTimer.schedule(new DurationSliderTask(), 0, //initial delay
        		        1 * 1000); //subsequent rate
        		}
        	} else {
        		// Pause
        		if (interfaceAdapter != null) {
        			interfaceAdapter.pauseButtonPressed();
        			// Stop the timer for the slider
        			if (sliderTimer != null) {
        				sliderTimer.cancel();
        			}
        		}
        		resetMediaControls();
        	}
        }
    }
    
    private class StopButtonAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	stopPlayerAndRefreshControls();
        }
    }
    
    private void stopPlayerAndRefreshControls() {
    	// Stop
    	if (interfaceAdapter != null) {
			interfaceAdapter.stopButtonPressed();
			
			// Stop the timer for the slider and reset the slider
			if (sliderTimer != null) {
				sliderTimer.cancel();
			}
			soundSliderPanel.refreshSlider(durationInSeconds);
    	}
    	resetMediaControls();
    }
    
    private void resetMediaControls() {
    	isPlayed = false;
		playButton.setIcon(playIcon);
    }
    
    private class DurationSliderTask extends TimerTask {
		public void run() {
			int position = soundSliderPanel.getPosition();
			if (position < durationInSeconds) {
				soundSliderPanel.setSlider(++position);
			}
			// If the slider ends, stop the sound player and reset media controls
			if (position > (int)durationInSeconds) {
				stopPlayerAndRefreshControls();
			}
		}
	}
    
    private class SliderPlayerLink implements SoundSliderPlayerLink {
		@Override
		public void updateSoundPlayer() {
			int seconds = soundSliderPanel.getPosition();
			if (interfaceAdapter != null)
				interfaceAdapter.setPlayPosition(seconds);
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
			refresh();
		}
		appFrame.setVisible(true);
		appFrame.setResizable(true);
	}

	public void updateRotationalData(float thetaX) {
		positionFieldPanel.updateRotationAngle(thetaX);
	}

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

	public boolean isRefreshButtonPressed() {
		if (isRefreshButtonPressed) {
			isRefreshButtonPressed = false;
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
				GraphicalUserInterface appMainView = new GraphicalUserInterface(null);
				appMainView.createAndShowGUI();
//				appMainView.setIpAddress("127.0.0.1");
			}
		});
	}
}
