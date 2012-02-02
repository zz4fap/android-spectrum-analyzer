package dsp;

import log.LOG;
import dsp.SignalHelper.SignalGenerator;
import fft.Constants;
import fft.FFTHelper;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioProcessing extends Thread {
	
	private static final String TAG = AudioProcessing.class.getSimpleName();
	
	private double mSampleRateInHz;
	private int mNumberOfFFTPoints;
	
	private AudioRecord mRecorder;
	private int mMinBufferSize;
	
	private boolean mStopped;
	private boolean mRunInDebugMode;
	
	private static AudioProcessingListener mListener;
	
	private FFTHelper mFFT;
	
	public AudioProcessing(double sampleRate, int numberOfFFTPoints){
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
		mFFT = new FFTHelper(mSampleRateInHz,mNumberOfFFTPoints);
		start();
	}
	
	public AudioProcessing(double sampleRate, int numberOfFFTPoints, boolean runInDebugMode){
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
		mRunInDebugMode = runInDebugMode;
		mFFT = new FFTHelper(mSampleRateInHz,mNumberOfFFTPoints);
		start();
	}
	
	@Override
	public void run(){
		if(mRunInDebugMode){
			runWithSignalHelper();
		} else {
			runWithAudioRecord();
		}
	}
	
	private void runWithSignalHelper(){ // TESTE
		int numberOfReadBytes = 0, bufferSize = 2*mNumberOfFFTPoints;
		double[] absNormalizedSignal;

		while(!mStopped) {
			byte tempBuffer[] = new byte[bufferSize]; // 2*Buffer size because it's a short variable into a array of bytes.
			numberOfReadBytes = SignalGenerator.read(tempBuffer,1000,mNumberOfFFTPoints,mSampleRateInHz,true,true);
			if(numberOfReadBytes > 0){
				if(mFFT!=null){
					// Calculate captured signal's FFT.
					absNormalizedSignal = mFFT.calculateFFT(tempBuffer);
					notifyListenersOnFFTSamplesAvailableForDrawing(absNormalizedSignal);
				}
			} else {
				LOG.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
	}
	
	private void runWithAudioRecord(){
		int numberOfReadBytes = 0, bufferSize = 2*mNumberOfFFTPoints;
		double[] absNormalizedSignal;
		
		mMinBufferSize = AudioRecord.getMinBufferSize((int)mSampleRateInHz,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				(int)mSampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 10*mMinBufferSize);
		
		if(mRecorder==null) {
			throw new RuntimeException("Audio Recording Device was not initialized!!!");
		}

		mRecorder.startRecording();

		while(!mStopped) {
			byte tempBuffer[] = new byte[bufferSize];
			numberOfReadBytes = mRecorder.read(tempBuffer,0,bufferSize);
			if(numberOfReadBytes > 0){
				if(mFFT!=null){
					// Calculate captured signal's FFT.
					absNormalizedSignal = mFFT.calculateFFT(tempBuffer);
					notifyListenersOnFFTSamplesAvailableForDrawing(absNormalizedSignal);
				}
			} else {
				LOG.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
        
        mRecorder.stop();
        mRecorder.release();
	}
	
	public double getPeakFrequency(){
		return mFFT.getPeakFrequency();
	}
	
	public double getPeakFrequency(int[] absSignal){
		return mFFT.getPeakFrequency(absSignal);
	}
	
	public double getMaxFFTSample(){
		return mFFT.getMaxFFTSample();
	}
	
	public void close(){ 
		mStopped = true;
	}
	
	public static void registerDrawableFFTSamplesAvailableListener(AudioProcessingListener listener){
		mListener = listener;
	}
	
	public static void unregisterDrawableFFTSamplesAvailableListener(){
		mListener = null;
	}
	
	public void notifyListenersOnFFTSamplesAvailableForDrawing(double[] absSignal){
		if(mListener!=null)
			mListener.onDrawableFFTSignalAvailable(absSignal);
	}
}
