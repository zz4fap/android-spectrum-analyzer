package dsp;

import dsp.SignalHelper.SignalGenerator;
import fft.Constants;
import fft.FFTHelper;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioProcessing extends Thread {
	
	private static final String TAG = AudioProcessing.class.getSimpleName();
	
	private static double mSampleRateInHz = Constants.SAMPLING_FREQUENCY;
	private static int mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS;
	
	private AudioRecord mRecorder = null;
	private int mMinBufferSize;
	
	private boolean stopped = false;
	
	private static AudioProcessingListener mListener;
	
	private FFTHelper mFFT;
	
	private int mDrawableSignal[];
	
	public AudioProcessing(){
		mFFT = new FFTHelper();
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
		double[] absNormalizedSignal;
		
		SignalGenerator signalGenerator = new SignalGenerator();

		while(!stopped) {
			byte tempBuffer[] = new byte[Constants.BUFFER_SIZE]; // 2*Buffer size because it's a short variable into a array of bytes.
			double[] signal = new double[Constants.BUFFER_SIZE/2];
			numberOfReadBytes = signalGenerator.read(tempBuffer,500,getSampleRateInHz(),true,false);
			if(numberOfReadBytes > 0){
				for(int i = 0; i < Constants.BUFFER_SIZE/2; i++){
					signal[i] = (double)((tempBuffer[2*i] & 0xFF) | (tempBuffer[2*i+1] << 8)) / 32768.0F;
				}
				if(mFFT!=null){
					// Calculate captured signal's FFT.
					absNormalizedSignal = mFFT.calculateFFT(signal); 
					mDrawableSignal = SignalHelper.getDrawableFFTSignal(absNormalizedSignal);
					notifyListenersOnFFTSamplesAvailableForDrawing();
				}
			} else {
				Log.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
	}
	
	private void runWithAudioRecord(){
		int numberOfReadBytes = 0;
		double[] absNormalizedSignal;
		
		mMinBufferSize = AudioRecord.getMinBufferSize((int)mSampleRateInHz,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				(int)mSampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 10*mMinBufferSize);
		
		if(mRecorder==null)
			return;

		mRecorder.startRecording();

		while(!stopped) {
			byte tempBuffer[] = new byte[Constants.BUFFER_SIZE];
			double[] signal = new double[Constants.BUFFER_SIZE/2];
			numberOfReadBytes = mRecorder.read(tempBuffer,0,Constants.BUFFER_SIZE);
			if(numberOfReadBytes > 0){
				for(int i = 0; i < Constants.BUFFER_SIZE/2; i++){
					signal[i] = (double)((tempBuffer[2*i] & 0xFF) | (tempBuffer[2*i+1] << 8)) / 32768.0F;
				}
				if(mFFT!=null){
					// Calculate captured signal's FFT.
					absNormalizedSignal = mFFT.calculateFFT(signal); 
					mDrawableSignal = SignalHelper.getDrawableFFTSignal(absNormalizedSignal);
					notifyListenersOnFFTSamplesAvailableForDrawing();
				}
			} else {
				Log.e(TAG,"There was an error reading the audio device - ERROR: "+numberOfReadBytes);
			}
		}
        
        mRecorder.stop();
        mRecorder.release();
	}
	
	public static double getSampleRateInHz(){
		return mSampleRateInHz;
	}
	
	public void setSampleRateInHz(double fs){
		mSampleRateInHz = fs;
	}
	
	public static int getNumberOfFFTPoints(){
		return mNumberOfFFTPoints;
	}
	
	public void setNumberOfFFTPoints(int fftPoints){
		mNumberOfFFTPoints = fftPoints;
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
