package com.spectrumanalyzer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	
	private int mWidth; 
	private int mHeight;
	private Display mDisplay;
	private boolean mIsSurfaceCreated;
	private SurfaceHolder mSurfaceHolder;

	private static final int MARK_SIZE = 7;
	private static final int SPACE_BETWEEN_HORIZONTAL_MARKS = 50;
	private static final double SCALE_FACTOR = 100.0;

	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		mSurfaceHolder = getHolder();
		setFocusable(true);
		mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	public Panel(Context context) {
		super(context);
		getHolder().addCallback(this);
		mSurfaceHolder = getHolder();
		setFocusable(true);
		mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
	public void drawSpectrum(double[] absSignal, double samplingRate, int numberOfFFTPoints, double maxFFTSample, int markFreqPos, int drawableArea, int pointToStartDrawing) {
    	if(mIsSurfaceCreated){
    		Canvas canvas;
    		canvas = null;
    		try {
    			canvas = mSurfaceHolder.lockCanvas(null);
    			synchronized (mSurfaceHolder) {
    				canvas.drawColor(Color.BLACK);
    				drawBorderLine(canvas);
    				drawSpectrumMarks(canvas, samplingRate, drawableArea, pointToStartDrawing);
    				drawFFTSignal(canvas, absSignal, numberOfFFTPoints, maxFFTSample, drawableArea, pointToStartDrawing);
    				drawMarkFrequencyAndText(canvas, markFreqPos, samplingRate, drawableArea, pointToStartDrawing);
    			}
    		} finally {
    			// do this in a finally so that if an exception is thrown
    			// during the above, we don't leave the Surface in an
    			// inconsistent state
    			if (canvas != null) {
    				mSurfaceHolder.unlockCanvasAndPost(canvas);
    			}
    		}	
    	}
	}
	
	public void drawEmptySpectrum(double samplingRate, int markFreqPos, int drawableArea, int pointToStartDrawing) {
    	if(mIsSurfaceCreated){
    		Canvas canvas;
    		canvas = null;
    		try {
    			canvas = mSurfaceHolder.lockCanvas(null);
    			synchronized (mSurfaceHolder) {
    				canvas.drawColor(Color.BLACK);
    				drawBorderLine(canvas);
    			}
    		} finally {
    			// do this in a finally so that if an exception is thrown
    			// during the above, we don't leave the Surface in an
    			// inconsistent state
    			if (canvas != null) {
    				mSurfaceHolder.unlockCanvasAndPost(canvas);
    			}
    		}	
    	}
	}
	
	private void drawSpectrumMarks(Canvas canvas, double samplingRate, int drawableArea, int pointToStartDrawing) {
		int freqStep = 1000;
		Paint p = new Paint();

		for(int freq = freqStep; freq <= (int)(samplingRate/2); freq = freq + freqStep) {
			
			// convert from frequency to pixel position
			int pointInt = convertFromFrequencyToPixel((double)freq, samplingRate, drawableArea);

			p.setColor(Color.GREEN);
			p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			canvas.drawText(Integer.toString(freq/1000)+" K",((pointInt-8)-pointToStartDrawing),13,p);// plot frequencies
			
			// draw vertical dashed marks			
			for(int i = 0; ((i < mHeight) && (pointInt >= pointToStartDrawing)); i=i+2*MARK_SIZE) {
				canvas.drawLine((pointInt-pointToStartDrawing),i,(pointInt-pointToStartDrawing),(i+MARK_SIZE-1), p);
			}
					
			// draw horizontal dashed marks			
			for(int i = SPACE_BETWEEN_HORIZONTAL_MARKS; i < mHeight; i=i+SPACE_BETWEEN_HORIZONTAL_MARKS) {
				for(int j = 0; j < drawableArea; j=j+2*MARK_SIZE) {
					canvas.drawLine(j,i,(j+MARK_SIZE-1),i, p);
				}
			}
		}
	}
	
	private void drawFFTSignal(Canvas canvas, double[] absSignal, int numberOfFFTPoints, double maxFFTSample, int drawableArea, int pointToStartDrawing) {
		int sampleValue;
		int pos;
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		for(int i = 0; i < absSignal.length; i++) {
			sampleValue = (int)(SCALE_FACTOR*(absSignal[i]/maxFFTSample));
			// convert from fft sample to pixel position
			pos = convertFromFFTSampleToPixel(i, numberOfFFTPoints, drawableArea);
			if(pos >= pointToStartDrawing) {
				canvas.drawLine((pos-pointToStartDrawing),(mHeight-2),(pos-pointToStartDrawing),((mHeight-2)-sampleValue), p);
			}
		}
	}
	
	private void drawMarkFrequencyAndText(Canvas canvas, int markPixelPos, double samplingRate, int drawableArea, int pointToStartDrawing) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(17);
		p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		//convert from pixel position to frequency.
		double markFreq = convertFromPixelToFrequency(markPixelPos, samplingRate, drawableArea);
		canvas.drawText("Mark Freq: "+markFreq+" Hz",((mWidth/2)+20),40,p);	
		p.setColor(Color.GREEN);
		if(markPixelPos >= pointToStartDrawing) {
			canvas.drawLine((markPixelPos-pointToStartDrawing),0,(markPixelPos-pointToStartDrawing),(mHeight-1),p);			
		}
	}
	
	private void drawBorderLine(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.GREEN);
		canvas.drawLine(0,0,0,(mHeight-1),p);
		canvas.drawLine(0,0,(mWidth-1),0,p);
		canvas.drawLine((mWidth-1),0,(mWidth-1),(mHeight-1),p);
		canvas.drawLine(0,(mHeight-1),(mWidth-1),(mHeight-1),p);
	}
	
	// Conversion from pixel point to FFT Sample
	public static int convertFromPixelToFFTSample(int pixel, int numberOfFFTPoints, int drawableArea) {
		double sample = Math.round((double)pixel*((numberOfFFTPoints/2)-1)/(double)(drawableArea-1));
		return (int)sample;	
	}

	// Conversion from frequency to pixel point
	public static int convertFromFrequencyToPixel(double frequency, double samplingRate, int drawableArea) {
		double pixel = Math.round((frequency*(drawableArea-1))/(samplingRate/2));
		return (int)pixel;
	}

	// Conversion from pixel point to frequency
	public static double convertFromPixelToFrequency(int pixel, double samplingRate, int drawableArea) {
		double freq = Math.round(pixel*(double)((samplingRate/2)/(drawableArea-1)));
		return freq;
	}

	// Conversion from FFT sample to pixel position
	public static int convertFromFFTSampleToPixel(int sample, int numberOfFFTPoints, int drawableArea) {
		double pixel = Math.round(sample*((double)(drawableArea-1)/((numberOfFFTPoints/2)-1)));
		return (int)pixel;
	}
	
	// Conversion from FFT Sample to frequency, note that it's an approximation of the real frequency value.
	public static double convertFromFFTSampleToFrequency(int sample, int numberOfFFTPoints, double samplingRate, int drawableArea) {
		int pixel = convertFromFFTSampleToPixel(sample, numberOfFFTPoints, drawableArea);
		return convertFromPixelToFrequency(pixel, samplingRate, drawableArea);
	}
	
	private void drawPeakFrequencyMarkAndText(Canvas canvas, double peakFreq) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(12);
		p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		canvas.drawText("Peak Freq: "+peakFreq+" Hz",100,(mHeight-150),p);// plot frequencies
	}
	
	private void drawCenterFrequencyMarkAndText(Canvas canvas, double centerFreq) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(12);
		p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		canvas.drawText("Center Freq: "+centerFreq+" Hz",(mWidth/2),(mHeight-165),p);
	}

	private void getViewInfo() {
		mWidth = getWidth();
		mHeight = getHeight();
	}
	
	public int getPanelWidth() {
		if(android.os.Build.VERSION.SDK_INT >= 13) {
	    	Point outSize = new Point();
	    	mDisplay.getSize(outSize);
	    	return outSize.x;
		} else {
			return mDisplay.getWidth();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		mIsSurfaceCreated = true;
		getViewInfo();
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		mIsSurfaceCreated = false;
	}
}   
