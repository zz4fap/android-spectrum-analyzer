package com.spectrumanalyzer.dsp;

import com.spectrumanalyzer.log.LOG;
import com.spectrumanalyzer.fft.FFTHelper;
import com.spectrumanalyzer.dsp.SignalHelper.DebugSignal;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioProcessing extends Thread {
	
	private static final String TAG = AudioProcessing.class.getSimpleName();
	
	private double mSampleRateInHz;
	private int mNumberOfFFTPoints;
	
	private AudioRecord mRecorder;
	private int mMinBufferSize;
	private int mBufferSize;
	
	private boolean mStopped;
	private boolean mRunInDebugMode;
	
	private static AudioProcessingListener mListener;
	
	private FFTHelper mFFT;
	
	private AudioProcessingException mAudioProcessingException;
	
	public AudioProcessing(double sampleRate, int numberOfFFTPoints) throws Exception {
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
		mBufferSize = 2*mNumberOfFFTPoints;
		mFFT = new FFTHelper(mSampleRateInHz,mNumberOfFFTPoints);
		if(!getInstanceOfAudioRecord()) {
			LOG.e(TAG,mAudioProcessingException.getMessage());
			throw mAudioProcessingException;
		}
		start();
	}
	
	public AudioProcessing(double sampleRate, int numberOfFFTPoints, boolean runInDebugMode) throws Exception {
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
		mBufferSize = 2*mNumberOfFFTPoints;
		mRunInDebugMode = runInDebugMode;
		mFFT = new FFTHelper(mSampleRateInHz,mNumberOfFFTPoints);
		if(mRunInDebugMode) {
			if(!getInstanceOfAudioRecord()) {
				LOG.e(TAG,mAudioProcessingException.getMessage());
				throw mAudioProcessingException;
			}
		}
		start();
	}
	
	@Override
	public void run() {
		if(mRunInDebugMode) {
			runWithSignalHelper();
		} else {
			runWithAudioRecord();
		}
	}
	
	private void runWithSignalHelper() { // DEBUG_MODE - simulated sinusoids signal
		int numberOfReadBytes = 0;
		double[] absNormalizedSignal;
		byte tempBuffer[] = new byte[mBufferSize]; // 2*Buffer size because it's a short variable into a array of bytes.

		while(!mStopped) {
			numberOfReadBytes = DebugSignal.read(tempBuffer,mBufferSize,mSampleRateInHz);
			if(numberOfReadBytes > 0) {
				if(mFFT!=null) {
					// Calculate captured signal's FFT.
					absNormalizedSignal = mFFT.calculateFFT(tempBuffer, numberOfReadBytes);
					notifyListenersOnFFTSamplesAvailableForDrawing(absNormalizedSignal);
				}
			} else {
				LOG.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
	}
	
	private boolean getInstanceOfAudioRecord() {
		mMinBufferSize = AudioRecord.getMinBufferSize((int)mSampleRateInHz,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		if(mMinBufferSize < 0) {
			mAudioProcessingException = new AudioProcessingException("Error when getting Minimum buffer: "+mMinBufferSize);
			return false;
		}
		
		try {
			mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
					(int)mSampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, 10*mMinBufferSize);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			mAudioProcessingException =  new AudioProcessingException("Audio Recording device was not initialized!!!");
			return false;
		}
		
		return true;
	}
	
	private void runWithAudioRecord() { // REAL_MODE - BUILT IN AUDIO DEVICE
		int numberOfReadBytes = 0;
		double[] absNormalizedSignal;
		byte tempBuffer[] = new byte[mBufferSize];

		mRecorder.startRecording();

		while(!mStopped) {
			numberOfReadBytes = mRecorder.read(tempBuffer,0,mBufferSize);
			if(numberOfReadBytes > 0) {
				if(mFFT!=null) {
					// Calculate captured signal's FFT.
					absNormalizedSignal = mFFT.calculateFFT(tempBuffer, numberOfReadBytes);
					notifyListenersOnFFTSamplesAvailableForDrawing(absNormalizedSignal);
				}
			} else {
				LOG.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
        
        mRecorder.stop();
        mRecorder.release();
	}
	
	public double getPeakFrequency() {
		return mFFT.getPeakFrequency();
	}
	
	public double getPeakFrequency(int[] absSignal) {
		return mFFT.getPeakFrequency(absSignal);
	}
	
	public double getMaxFFTSample() {
		return mFFT.getMaxFFTSample();
	}
	
	public int getPeakFrequencyPosition() {
		return mFFT.getPeakFrequencyPosition();
	}
	
	public void close() { 
		mStopped = true;
	}
	
	public static void registerDrawableFFTSamplesAvailableListener(AudioProcessingListener listener) {
		mListener = listener;
	}
	
	public static void unregisterDrawableFFTSamplesAvailableListener() {
		mListener = null;
	}
	
	public void notifyListenersOnFFTSamplesAvailableForDrawing(double[] absSignal) {
		if(!mStopped) {
			if(mListener!=null) {
				mListener.onDrawableFFTSignalAvailable(absSignal);
			}
		}
	}
	
	public class AudioProcessingException extends Exception {

		private static final long serialVersionUID = 990899067193042344L;

		public AudioProcessingException(String msg) {
			super(msg); 
		}
		
		public AudioProcessingException(String msg, Throwable t){ 
			super(msg,t); 
		} 
	}
	
}
