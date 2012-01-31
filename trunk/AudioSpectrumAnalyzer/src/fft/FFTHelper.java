package fft;

import java.io.*;

import dsp.AudioProcessing;

public class FFTHelper
{
	private static double mPeakFreq = 0;
	private static int mPeakPos = 0;
	
	public double[] calculateFFT(double[] signal)
	{			
		double max = 0.0;
		Complex[] y;
		Complex[] complexSignal = new Complex[AudioProcessing.getNumberOfFFTPoints()];
		double[] absSignal = new double[AudioProcessing.getNumberOfFFTPoints()/2];
		
		for(int i=0; i < AudioProcessing.getNumberOfFFTPoints(); i++)
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
		
		absSignal = calculateAbsSignal(y);
		max = getMaxAbsSignal(absSignal);
		
		for(int i=0; i < (AudioProcessing.getNumberOfFFTPoints()/2); i++)
		{
			 absSignal[i] = absSignal[i]/max;
		}
		
		return absSignal;
	}
	
	private double[] calculateAbsSignal(Complex[] y)
	{
		double[] absSignal = new double[AudioProcessing.getNumberOfFFTPoints()/2];
		
		for(int i=0; i < (AudioProcessing.getNumberOfFFTPoints()/2); i++)
		{
			 absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
		}
		
		return absSignal;
	}
	
	private double getMaxAbsSignal(double[] absSignal)
	{
		double max = absSignal[0];
		mPeakPos = 0;
		
		for(int i=1; i < (AudioProcessing.getNumberOfFFTPoints()/2); i++)
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
		mPeakFreq = mPeakPos*(AudioProcessing.getSampleRateInHz()/AudioProcessing.getNumberOfFFTPoints());
		return mPeakFreq;
	}
	
	public static double getPeakFrequency(int[] absSignal){
		
		int peakPos = 0, max = absSignal[0];
		
		for(int i=1; i < (AudioProcessing.getNumberOfFFTPoints()/2); i++)
		{
			 if(absSignal[i] > max)
			 {
				 max = absSignal[i];
				 peakPos = i;
			 }
		}
		
		return peakPos*(AudioProcessing.getSampleRateInHz()/AudioProcessing.getNumberOfFFTPoints());
	}
}