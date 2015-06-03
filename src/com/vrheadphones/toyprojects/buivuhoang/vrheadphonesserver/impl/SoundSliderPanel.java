package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.InterfaceAdapter;
// TODO fix so that sound player doesn't loop the sounds and stops when all sounds finish
public class SoundSliderPanel extends JPanel {
	private InterfaceAdapter interfaceAdapter;
	private JSlider soundPositionSlider = new JSlider();
	
	private double durationInSeconds = 0;
	
	private JLabel durationLabel = new JLabel("0:00 / 0:00");
	private String formattedDuration = "0:00";
	
	private Timer timer;
	
	public SoundSliderPanel(InterfaceAdapter interfaceAdapter) {
		this.interfaceAdapter = interfaceAdapter;
		soundPositionSlider.setEnabled(false);
		soundPositionSlider.setMinimum(0);
		soundPositionSlider.setMaximum(1);
		soundPositionSlider.setValue(0);
		soundPositionSlider.addMouseListener(new SoundSliderMouseListener());
		soundPositionSlider.addKeyListener(new SoundSliderKeyListener());
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(soundPositionSlider);
		add(durationLabel);
	}
	
	public void refreshSlider(double durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
		soundPositionSlider.setMaximum((int)durationInSeconds);
		soundPositionSlider.setValue(0);
		soundPositionSlider.setEnabled(true);
		formattedDuration = formatDuration((int)durationInSeconds);
		durationLabel.setText("0:00 / " + formattedDuration);
	}
	
	private String formatDuration(int seconds) {
		int numOfMinutes = (int) (seconds / 60);
		int numOfOddSeconds = (int) (seconds - numOfMinutes*60);
		String formattedSeconds = "";
		if (numOfOddSeconds < 10)
			formattedSeconds += "0";
		formattedSeconds += numOfOddSeconds;
		
		String formattedDuration = numOfMinutes + ":" + formattedSeconds;
		return formattedDuration;
	}
	
	public void setSlider(int seconds) {
		soundPositionSlider.setValue(seconds);
		durationLabel.setText(formatDuration(seconds) + " / " + formattedDuration);
	}
	
	private class SoundSliderMouseListener extends MouseInputAdapter {
		@Override
		public void mouseReleased(MouseEvent arg0) {
			updateSoundPlayer();
		}
	}
	
	private class SoundSliderKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT
					|| e.getKeyCode() == KeyEvent.VK_LEFT
					|| e.getKeyCode() == KeyEvent.VK_UP
					|| e.getKeyCode() == KeyEvent.VK_DOWN
					|| e.getKeyCode() == KeyEvent.VK_PAGE_UP
					|| e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
				updateSoundPlayer();
			}
		}
	}
	
	private void updateSoundPlayer() {
		int seconds = soundPositionSlider.getValue();
		if (interfaceAdapter != null)
			interfaceAdapter.setPlayPosition(seconds);
	}
	
	public void startPlaying() {
		System.out.println("start");
		timer = new Timer();
	    timer.schedule(new DurationSliderTask(), 0, //initial delay
	        1 * 1000); //subsequent rate
	}
	
	public void stopPlaying() {
		if (timer != null) {
			timer.cancel();
			soundPositionSlider.setValue(0);
			formattedDuration = formatDuration((int)durationInSeconds);
			durationLabel.setText("0:00 / " + formattedDuration);
		}
	}
	
	public void pausePlaying() {
		if (timer != null) {
			timer.cancel();
		}
	}
	
	private class DurationSliderTask extends TimerTask {
		public void run() {
			int position = soundPositionSlider.getValue();
			if (position < durationInSeconds) {
				durationLabel.setText(formatDuration((int)position) + " / " + formattedDuration);
				soundPositionSlider.setValue(++position);
			}
		}
	}
}
