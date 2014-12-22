package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.vrheadphones.toyprojects.buivuhoang.utilities.SpringUtilities;

public class AppMainView {
	public static String FRAME_TITLE = "VR Headphones Server";
	public static boolean RIGHT_TO_LEFT = false;
    
	private JLabel xLabel = new JLabel();
	private JLabel yLabel = new JLabel();
	private JLabel zLabel = new JLabel();
	
	private JLabel xValueLabel = new JLabel();
	private JLabel yValueLabel = new JLabel();
	private JLabel zValueLabel = new JLabel();
	
	private JPanel rotationalDataPanel = new JPanel(new SpringLayout());
	
	private static int numPairs = 3;
	
	private JFrame appFrame;
	private JPanel instructionsPanel = new JPanel();
    
    
	public void addComponentsToPane(Container pane) {
         
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
         
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }
        
        xLabel.setText("x: ");
        yLabel.setText("y: ");
        zLabel.setText("z: ");
        
        xValueLabel.setText("0.0");
        yValueLabel.setText("0.0");
        zValueLabel.setText("0.0");
        
        xLabel.setLabelFor(xValueLabel);
        yLabel.setLabelFor(yValueLabel);
        zLabel.setLabelFor(zValueLabel);
        
        rotationalDataPanel.add(xLabel);
        rotationalDataPanel.add(xValueLabel);
        rotationalDataPanel.add(yLabel);
        rotationalDataPanel.add(yValueLabel);
        rotationalDataPanel.add(zLabel);
        rotationalDataPanel.add(zValueLabel);
        
        SpringUtilities.makeCompactGrid(rotationalDataPanel,
                numPairs, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        pane.setPreferredSize(new Dimension(350, 250));
        pane.add(rotationalDataPanel, BorderLayout.CENTER);
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.PAGE_AXIS));
        pane.add(instructionsPanel, BorderLayout.NORTH);
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public void createAndShowGUI() {
        //Create and set up the window.
    	if (appFrame == null) {
    		appFrame = new JFrame(FRAME_TITLE);
    		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		
    		//Set up the content pane.
            addComponentsToPane(appFrame.getContentPane());
            //Use the content pane's default BorderLayout. No need for
            //setLayout(new BorderLayout());
            //Display the window.
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
    		"The VRHeadphonesServer application is now running.",
    		"",
    		"Your IP address is: " + ipAddress,
    		"",
    		"Enter this IP address on the start screen of the",
    		"RemoteDroid application on your phone to begin."
		};
		
		for (String s: instructions) {
			JLabel label = new JLabel(s);
			instructionsPanel.add(label);
		}
	}
}
