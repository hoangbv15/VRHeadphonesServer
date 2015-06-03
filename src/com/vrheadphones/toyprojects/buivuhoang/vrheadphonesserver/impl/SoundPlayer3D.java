package com.vrheadphones.toyprojects.buivuhoang.vrheadphonesserver.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.WaveData;

public class SoundPlayer3D {
	private static final String DEFAULT_FILEDIR = "src/";
	private static final String DEFAULT_FILE = "Opera.wav";

	/** Maximum data buffers we will need. */
	public static int NUM_BUFFERS = 1;

	/** Maximum emissions we will need. */
	public static int NUM_SOURCES = 1;

	/** Buffers hold sound data. */
	private static IntBuffer buffer, source;

	/** Position of the source sound. */
	private static FloatBuffer sourceVel;

	/** Position of the listener. */
	private static FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3)
			.put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

	/** Velocity of the listener. */
	private static FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3)
			.put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	
	
	// test
//	private static double durationInSeconds = 0;
	
	/**
	 * Orientation of the listener. (first 3 elements are "at", second 3 are
	 * "up")
	 */
	private static FloatBuffer listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6).put(
			new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f }).rewind();

	public static void init() {
		// Initialize OpenAL and clear the error bit.
		try {
			initBuffers();
			AL.create();
			setListenerValues();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		AL10.alGetError();
	}

	private static void initBuffers() {
		/** Buffers hold sound data. */
		buffer = BufferUtils.createIntBuffer(NUM_BUFFERS);

		/** Sources are points emitting sound. */
		source = BufferUtils.createIntBuffer(NUM_BUFFERS);

		/** Velocity of the source sound. */
		sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3 * NUM_BUFFERS).rewind();
	}

	public static boolean isPlaying() {
		return AL.isCreated();
	}

	public static void setSourcePosition(int index, float x, float y, float z) {
		FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3)
				.put(new float[] { x, y, z }).rewind();
		AL10.alSource(source.get(index), AL10.AL_POSITION, sourcePos);
	}

	public static void loadSoundList(List<Sound3D> soundList) {
		clean();
		NUM_SOURCES = soundList.size();
		NUM_BUFFERS = NUM_SOURCES;
		init();
		// Load the wav data.
		if (loadALData(soundList) == AL10.AL_FALSE) {
			System.out.println("Error loading data.");
			return;
		}
	}
	
	public static void play() {
		for (int i = 0; i < NUM_SOURCES; i++)
			AL10.alSourcePlay(source.get(i));
		
//		for (int i = 0; i < NUM_SOURCES; i++) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			int sizeInBytes = AL10.alGetBufferi(source.get(i), AL10.AL_SIZE);
//			int channels = AL10.alGetBufferi(source.get(i), AL10.AL_CHANNELS);
//			int bits = AL10.alGetBufferi(source.get(i), AL10.AL_BITS);
//
//			double lengthInSamples = sizeInBytes * 8
//					/ (double) (channels * bits);
//
//			int frequency = AL10.alGetBufferi(source.get(i), AL10.AL_FREQUENCY);
//
//			durationInSeconds = lengthInSamples / (double) frequency;
//
//			System.out.println("sizeInBytes: " + sizeInBytes);
//			System.out.println("channels: " + channels);
//			System.out.println("bits: " + bits);
//			System.out.println("lengthInSamples: " + lengthInSamples);
//			System.out.println("frequency: " + frequency);
//			System.out.println("durationInSeconds: " + durationInSeconds);
//			System.out.println();
//		}
	}
	
	public static void pause() {
		for (int i = 0; i < NUM_SOURCES; i++)
			AL10.alSourcePause(source.get(i));
	}
	
	public static void stop() {
		for (int i = 0; i < NUM_SOURCES; i++)
			AL10.alSourceStop(source.get(i));
	}
	
	/***
	 * This simply sets the position to play the song in the number of seconds
	 * @param seconds
	 */
	public static void setPlayPosition(int seconds) {
		for (int i = 0; i < NUM_SOURCES; i++) {
			AL10.alSourcei(source.get(i), AL11.AL_SEC_OFFSET, seconds);
		}
	}

	public static void clean() {
		// AL10.alSourceStop(source.get(0));
		// killALData();
		AL.destroy();
	}

	/**
	 * boolean LoadALData()
	 *
	 * This function will load our sample data from the disk using the Alut
	 * utility and send the data into OpenAL as a buffer. A source is then also
	 * created to play that buffer.
	 */
	private static int loadALData(List<Sound3D> soundList) {
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);
//		durationInSeconds = 0;
		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		// Loads the wave file from your file system
		for (int i = 0; i < soundList.size(); i++) {
			Sound3D sound = soundList.get(i);
			File file = sound.waveFile;
			WaveData waveFile;
			java.io.FileInputStream fin = null;
			try {
				if (file == null)
					waveFile = WaveData.create(DEFAULT_FILE);
				else {
					fin = new java.io.FileInputStream(file);
					waveFile = WaveData.create(new BufferedInputStream(fin));
				}
			} catch (java.io.FileNotFoundException ex) {
				ex.printStackTrace();
				return AL10.AL_FALSE;
			}
			
//			// Get the largest duration
//			AudioInputStream audioInputStream;
//			try {
//				if (file == null)
//					file = new File(DEFAULT_FILEDIR + DEFAULT_FILE);
//				audioInputStream = AudioSystem.getAudioInputStream(file);
//				AudioFormat format = audioInputStream.getFormat();
//				long frames = audioInputStream.getFrameLength();
//				double duration = (frames + 0.0) / format.getFrameRate();
//				if (durationInSeconds < duration) {
//					durationInSeconds = duration;
//				}
//
//			} catch (UnsupportedAudioFileException | IOException e) {
//				e.printStackTrace();
//			}
			
			if (fin != null) {
				try {
					fin.close();
				} catch (java.io.IOException ex) {
				}
			}

			// Loads the wave file from this class's package in your classpath
			// WaveData waveFile = WaveData.create("Footsteps.wav");
			AL10.alBufferData(buffer.get(i), waveFile.format, waveFile.data,
					waveFile.samplerate);
			waveFile.dispose();
		}

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		for (int i = 0; i < soundList.size(); i++) {
			Sound3D sound = soundList.get(i);
			AL10.alSourcei(source.get(i), AL10.AL_BUFFER, buffer.get(i));
			AL10.alSourcef(source.get(i), AL10.AL_PITCH, 1.0f);
			AL10.alSourcef(source.get(i), AL10.AL_GAIN, 1.0f);
			setSourcePosition(i, sound.x, sound.y, sound.z);
			AL10.alSource(source.get(i), AL10.AL_VELOCITY, sourceVel);
			AL10.alSourcei(source.get(i), AL10.AL_LOOPING, AL10.AL_TRUE);
		}
		
		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR)
			return AL10.AL_TRUE;

		return AL10.AL_FALSE;
	}

	/**
	 * void setListenerValues()
	 *
	 * We already defined certain values for the Listener, but we need to tell
	 * OpenAL to use that data. This function does just that.
	 */
	private static void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	/**
	 * void killALData()
	 *
	 * We have allocated memory for our buffers and sources which needs to be
	 * returned to the system. This function frees that memory.
	 */
	private static void killALData() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}

	/***
	 * This function returns the duration of a scenario
	 * @param soundList - the list containing the sounds of a scenario
	 * @return the largest duration of a sound in the list
	 */
	public static double getDurationInSeconds(List<Sound3D> soundList) {
		AudioInputStream audioInputStream;
		double durationInSeconds = 0;
		for (int i = 0; i < soundList.size(); i++) {
			Sound3D sound = soundList.get(i);
			File file = sound.waveFile;
			try {
				if (file == null)
					file = new File(DEFAULT_FILEDIR + DEFAULT_FILE);
				audioInputStream = AudioSystem.getAudioInputStream(file);
				AudioFormat format = audioInputStream.getFormat();
				long frames = audioInputStream.getFrameLength();
				double duration = (frames + 0.0) / format.getFrameRate();
				if (durationInSeconds < duration) {
					durationInSeconds = duration;
				}
			} catch (UnsupportedAudioFileException | IOException e) {
				e.printStackTrace();
			}
		}
		return durationInSeconds;
	}
}
