package dsp;

import dsp.SignalHelper.Signal;
import fft.Constants;
import fft.FFTHelper;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioProcessing extends Thread {
	
	private static final String TAG = AudioProcessing.class.getSimpleName();
	
	private int mSampleRateInHz = (int)Constants.SAMPLING_FREQUENCY;
	private AudioRecord mRecorder = null;
	private int mMinBufferSize;
	private double mSignal[];
	
	private boolean stopped = false;
	
	private static AudioProcessingListener mListener;
	
	private FFTHelper mFFT;
	
	private int mDrawableSignal[];
	
	public AudioProcessing(){
		mSampleRateInHz = (int)Constants.SAMPLING_FREQUENCY;
		mFFT = new FFTHelper();
		start();
	}
	
	public AudioProcessing(int sampleRateInHz){
		mSampleRateInHz = sampleRateInHz;
		start();
	}
	
	@Override
	public void run(){
		if(Constants.DEBUG_MODE){
			runWithSignalHelper();
		} else {
			runWithAudioRecord();
		}
	}
	
	private void runWithSignalHelper(){ // TESTE
		int numberOfReadBytes = 0;
		
		Signal signal = new Signal();
		signal.setFrequency(1000);

		while(!stopped) {
			byte tempBuffer[] = new byte[Constants.BUFFER_SIZE]; // 2*Buffer size because it's a short variable into a array of bytes.
			mSignal = new double[Constants.BUFFER_SIZE/2];
			//numberOfReadBytes = signal.read(tempBuffer,0,Constants.BUFFER_SIZE,2);
			numberOfReadBytes = signal.read(tempBuffer,2);
			if(numberOfReadBytes > 0){
				for(int i = 0; i < Constants.BUFFER_SIZE/2; i++){
					mSignal[i] = (double)((tempBuffer[2*i] & 0xFF) | (tempBuffer[2*i+1] << 8)) / 32768.0F;
				}
				// Calculate captured signal's FFT.
				mFFT.calculateFFT(mSignal); 
				mDrawableSignal = SignalHelper.getDrawableFFTSignal(mFFT.getAbsFFTSignal());
				notifyListenersOnFFTSamplesAvailableForDrawing();
			} else {
				Log.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
	}
	
	private void runWithAudioRecord(){
		int numberOfReadBytes = 0;
		
		mMinBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHz,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 10*mMinBufferSize);
		
		if(mRecorder==null)
			return;

		mRecorder.startRecording();

		while(!stopped) {
			byte tempBuffer[] = new byte[Constants.BUFFER_SIZE];
			mSignal = new double[Constants.BUFFER_SIZE/2];
			numberOfReadBytes = mRecorder.read(tempBuffer,0,Constants.BUFFER_SIZE);
			if(numberOfReadBytes > 0){
				for(int i = 0; i < Constants.BUFFER_SIZE/2; i++){
					mSignal[i] = (double)((tempBuffer[2*i] & 0xFF) | (tempBuffer[2*i+1] << 8)) / 32768.0F;
				}
				// Calculate captured signal's FFT.
				mFFT.calculateFFT(mSignal); 
				mDrawableSignal = SignalHelper.getDrawableFFTSignal(mFFT.getAbsFFTSignal());
				notifyListenersOnFFTSamplesAvailableForDrawing();
			} else {
				Log.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
        
        mRecorder.stop();
        mRecorder.release();
	}
	
	public int getSampleRateInHz(){
		return mSampleRateInHz;
	}
	
	public void close(){ 
		stopped = true;
	}
	
	public static void registerDrawableFFTSamplesAvailableListener(AudioProcessingListener listener){
		mListener = listener;
	}
	
	public static void unregisterDrawableFFTSamplesAvailableListener(){
		mListener = null;
	}
	
	public void notifyListenersOnFFTSamplesAvailableForDrawing(){
		if(mListener!=null)
			mListener.onDrawableFFTSignalAvailable();
	}
	
	public int[] getDrawableSignal(){
		return mDrawableSignal;
	}
}
