package com.spectrumanalyzer.dsp;

import com.spectrumanalyzer.dsp.SignalHelper.DebugSignal;
import com.spectrumanalyzer.fft.Constants;
import com.spectrumanalyzer.fft.FFTHelper;
import com.spectrumanalyzer.log.LOG;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SignalProcessingService extends Service {

	private static final String TAG = SignalProcessingService.class.getSimpleName();
	
	// string constants used to retrived extra data from the incoming intent.
	private static final String EXTRA_DATA_STRING_SAMPLING_RATE = "extra_data_string_sampling_rate";
	private static final String EXTRA_DATA_STRING_NUMBER_OF_FFT_SAMPLES = "extra_data_string_number_of_fft_samples";
	private static final String EXTRA_DATA_STRING_RUN_DEBUG_MODE = "extra_data_string_run_debug_mode";

	private double mSamplingRate; // Sampling rate in Hz.
	private int mNumberOfFFTPoints;
	
	private AudioRecord mRecorder;
	private int mMinBufferSize;
	private int mBufferSize;
	private double[] mAbsoluteNormalizedSignal; // result of the FFT over the audio signal.
	
	private boolean mStopped;
	private boolean mRunInDebugMode;
		
	private FFTHelper mFFT; // Helper used to work out the fourier's transform of the audio signal

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class SignalProcessingBinder extends Binder {
		SignalProcessingService getService() {
			return SignalProcessingService.this;
		}
	}

	@Override
	public void onCreate() {
		mFFT = new FFTHelper();
		
		// Create an audio device used to record a raw stream of audio from the device's microphone.
		mMinBufferSize = AudioRecord.getMinBufferSize((int)mSamplingRate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				(int)mSamplingRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 10*mMinBufferSize);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		
		// receive sampling rate from client within the intent.
		if(intent.hasExtra(EXTRA_DATA_STRING_SAMPLING_RATE)) {
			double samplingRate = intent.getDoubleExtra(EXTRA_DATA_STRING_SAMPLING_RATE, Constants.SAMPLING_FREQUENCY);
			setSamplingRate(samplingRate);
		}
		
		// receive number of fft points from client within the intent.
		if(intent.hasExtra(EXTRA_DATA_STRING_NUMBER_OF_FFT_SAMPLES)) {
			int numberOfFFTPoints = intent.getIntExtra(EXTRA_DATA_STRING_NUMBER_OF_FFT_SAMPLES, Constants.NUMBER_OF_FFT_POINTS);
			setNumberOfFFTPoints(numberOfFFTPoints);
		}
		
		// receive flag indicating if it's must run in debug mode or not from client.
		mRunInDebugMode = intent.getBooleanExtra(EXTRA_DATA_STRING_RUN_DEBUG_MODE, false);

		return mBinder;
	}
	
	synchronized public void setSamplingRate(double samplingRate) {
		mSamplingRate = samplingRate;
		mFFT.setSamplingRate(mSamplingRate);
	}
	
	synchronized public void setNumberOfFFTPoints(int numberOfFFTPoints) {
		mNumberOfFFTPoints = numberOfFFTPoints;
		mFFT.setNumberOfFFTPoints(mNumberOfFFTPoints);
		mBufferSize = 2*mNumberOfFFTPoints;
	}
	
	synchronized public void setRunInDebugMode(boolean runInDebugMode) {
		mRunInDebugMode = runInDebugMode;
	}

	// This is the object that receives interactions from clients.
	private final IBinder mBinder = new SignalProcessingBinder();
	
	public void readSignalAndCalculateFFTThread() {
		new Thread(new Runnable() {
			public void run() {
				if(mRunInDebugMode) {
					readDebugSignalAndCalculateFFT();
				} else {
					readAudioSignalAndCalculateFFT();
				}
			}
		}).start();
	}
	
	private void readDebugSignalAndCalculateFFT() { // DEBUG_MODE - simulated sinusoids signal
		int numberOfReadBytes = 0;
		byte tempBuffer[] = new byte[mBufferSize]; // 2*Buffer size because it's a short variable into a array of bytes.

		numberOfReadBytes = DebugSignal.read(tempBuffer,mBufferSize,mSamplingRate);
		if(numberOfReadBytes > 0) {
			if(mFFT!=null) {
				// Calculate captured signal's FFT.
				mAbsoluteNormalizedSignal = mFFT.calculateFFT(tempBuffer, numberOfReadBytes);
			}
		} else {
			LOG.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
		}
	}
	
	private void readAudioSignalAndCalculateFFT() { // REAL_MODE - BUILT IN AUDIO DEVICE
		int numberOfReadBytes = 0;
		byte tempBuffer[] = new byte[mBufferSize];
		
		if(mRecorder==null) {
			throw new RuntimeException("Audio Recording Device was not initialized!!!");
		}

		mRecorder.startRecording();

		numberOfReadBytes = mRecorder.read(tempBuffer,0,mBufferSize);
		if(numberOfReadBytes > 0) {
			if(mFFT!=null) {
				// Calculate captured signal's FFT.
				mAbsoluteNormalizedSignal = mFFT.calculateFFT(tempBuffer, numberOfReadBytes);
			}
		} else {
			LOG.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
		}

		mRecorder.stop();
		mRecorder.release();
	}
	
	synchronized public double[] getAbsoluteNormalizedSignal() {
		return mAbsoluteNormalizedSignal;
	}
}
