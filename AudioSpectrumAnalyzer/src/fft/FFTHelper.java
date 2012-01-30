package fft;

import java.io.*;

public class FFTHelper
{
	private Complex[] complexSignal;
	private Complex[] y;
	private static int mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS; // N
	private double[] absSignal;
	private static double mPeakFreq = 0;
	private static double mSamplingFrequency = Constants.SAMPLING_FREQUENCY; // 44100 Hz
	private static int mPeakPos = 0;
	
	public FFTHelper(int numberOfFFTPoints, double samplingFrequency){
		mNumberOfFFTPoints = numberOfFFTPoints;
		complexSignal = new Complex[numberOfFFTPoints];
		absSignal = new double[numberOfFFTPoints/2];
		mSamplingFrequency = samplingFrequency;
	}
	
	public FFTHelper(){
		mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS;
		complexSignal = new Complex[mNumberOfFFTPoints];
		absSignal = new double[mNumberOfFFTPoints/2];
		mSamplingFrequency = Constants.SAMPLING_FREQUENCY;
	}
	
	public double[] calculateFFT(double[] signal)
	{			
		double max = 0.0;
		
		for(int i=0; i < mNumberOfFFTPoints; i++)
		{
			if(i < signal.length)
			{
				complexSignal[i] = new Complex(signal[i],0.0);
			}
			else
			{
				complexSignal[i] = new Complex(0.0,0.0);
			}
		}

		y = FFT.fft(complexSignal);
		
		calculateAbsSignal();
		max = getMaxAbsSignal();
		
		for(int i=0; i < (mNumberOfFFTPoints/2); i++)
		{
			 absSignal[i] = absSignal[i]/max;
		}
		
		return absSignal;
	}
	
	public double[] getAbsFFTSignal()
	{
		return absSignal;
	}
	
	private void calculateAbsSignal()
	{
		for(int i=0; i < (mNumberOfFFTPoints/2); i++)
		{
			 absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
		}
	}
	
	private double getMaxAbsSignal()
	{
		double max = absSignal[0];
		mPeakPos = 0;
		
		for(int i=1; i < (mNumberOfFFTPoints/2); i++)
		{
			 if(absSignal[i] > max)
			 {
				 max = absSignal[i];
				 mPeakPos = i;
			 }
		}
		
		return max;
	}
	
	static public int getPeakPosition(){
		return mPeakPos;
	}
	
	static public double getPeakFrequency(){
		mPeakFreq = mPeakPos*((double)getSamplingFrequency()/getNumberOfFFTPoints());
		return mPeakFreq;
	}
	
	static public int getNumberOfFFTPoints(){
		return mNumberOfFFTPoints;
	}
	
	static public double getSamplingFrequency(){
		return mSamplingFrequency;
	}
	
	public static double getPeakFrequency(int[] absSignal){
		
		int peakPos = 0, max = absSignal[0];
		
		for(int i=1; i < (getNumberOfFFTPoints()/2); i++)
		{
			 if(absSignal[i] > max)
			 {
				 max = absSignal[i];
				 peakPos = i;
			 }
		}
		
		return peakPos*((double)getSamplingFrequency()/getNumberOfFFTPoints());
	}
}