package fft;

import java.io.*;

import dsp.AudioProcessing;

public class FFTHelper
{
	private double mPeakFreq;
	private int mPeakPos;
	private double mSampleRateInHz;
	private int mNumberOfFFTPoints;
	
	public FFTHelper(double sampleRate, int numberOfFFTPoints){
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
	}
	
	public double[] calculateFFT(double[] signal)
	{			
		double max = 0.0;
		Complex[] y;
		Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
		double[] absSignal;
		
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
		
		absSignal = calculateAbsSignal(y);
		max = getMaxAbsSignal(absSignal);
		
		for(int i=0; i < (mNumberOfFFTPoints/2); i++)
		{
			 absSignal[i] = absSignal[i]/max;
		}
		
		return absSignal;
	}
	
	public double[] calculateFFT(byte[] signal)
	{			
		double max = 0.0, temp;
		Complex[] y;
		Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
		double[] absSignal;
		
		for(int i = 0; i < mNumberOfFFTPoints; i++){
			temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
			complexSignal[i] = new Complex(temp,0.0);
		}

		y = FFT.fft(complexSignal);
		
		absSignal = calculateAbsSignal(y);
		max = getMaxAbsSignal(absSignal);
		
		for(int i=0; i < (mNumberOfFFTPoints/2); i++)
		{
			 absSignal[i] = absSignal[i]/max;
		}
		
		return absSignal;
	}
	
	private double[] calculateAbsSignal(Complex[] y)
	{
		double[] absSignal = new double[mNumberOfFFTPoints/2];
		
		for(int i=0; i < (mNumberOfFFTPoints/2); i++)
		{
			 absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
		}
		
		return absSignal;
	}
	
	private double getMaxAbsSignal(double[] absSignal)
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
	
	public int getPeakPosition(){
		return mPeakPos;
	}
	
	public double getPeakFrequency(){
		mPeakFreq = mPeakPos*(mSampleRateInHz/mNumberOfFFTPoints);
		return mPeakFreq;
	}
	
	public double getPeakFrequency(int[] absSignal){
		
		int peakPos = 0, max = absSignal[0];
		
		for(int i=1; i < (mNumberOfFFTPoints/2); i++)
		{
			 if(absSignal[i] > max)
			 {
				 max = absSignal[i];
				 peakPos = i;
			 }
		}
		
		return peakPos*(mSampleRateInHz/mNumberOfFFTPoints);
	}
}