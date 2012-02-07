package com.spectrumanalyzer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

	private int mWidth; 
	private int mHeight;
	private int mOrientation;
	private Display mDisplay;
	private boolean isSurfaceCreated;
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
	
	public void drawSpectrum(double[] absSignal, double samplingRate, int numberOfFFTPoints, double maxFFTSample, int markFreqPos, int drawableArea) {
    	if(isSurfaceCreated){
    		Canvas canvas;
    		canvas = null;
    		try {
    			canvas = mSurfaceHolder.lockCanvas(null);
    			synchronized (mSurfaceHolder) {
    				canvas.drawColor(Color.BLACK);
    				drawBorderLine(canvas);
    				drawSpectrumMarks(canvas, samplingRate, drawableArea);
    				drawFFTSignal(canvas, absSignal, numberOfFFTPoints, maxFFTSample, drawableArea);
    				drawMarkFrequencyAndText(canvas, markFreqPos, samplingRate, drawableArea);
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
	
	private void drawSpectrumMarks(Canvas canvas, double samplingRate, int drawableArea) {
		int freqStep = 1000;
		Paint p = new Paint();

		for(int freq = freqStep; freq <= (int)(samplingRate/2); freq = freq + freqStep)
		{
			double point = (double)((((double)drawableArea)/2)*freq)/((samplingRate/4));
			int pointInt = (int)point;

			p.setColor(Color.GREEN);
			p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			canvas.drawText(Integer.toString(freq/1000)+" K",(pointInt-8),13,p);// plot frequencies
			
			// draw vertical dashed marks			
			for(int i = 0; i < mHeight; i=i+2*MARK_SIZE) {
				canvas.drawLine(pointInt,i,pointInt,(i+MARK_SIZE-1), p);
			}
					
			// draw horizontal dashed marks			
			for(int i = SPACE_BETWEEN_HORIZONTAL_MARKS; i < mHeight; i=i+SPACE_BETWEEN_HORIZONTAL_MARKS) {
				for(int j = 0; j < drawableArea; j=j+2*MARK_SIZE) {
					canvas.drawLine(j,i,(j+MARK_SIZE-1),i, p);
				}
			}
		}
	}
	
	private void drawFFTSignal(Canvas canvas, double[] absSignal, int numberOfFFTPoints, double maxFFTSample, int drawableArea) {
		int sampleValue;
		double pos;
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		for(int i = 0; i < (absSignal.length-1); i++){
			sampleValue = (int)(SCALE_FACTOR*(absSignal[i]/maxFFTSample));			
			pos = (double)(((double)(((double)drawableArea/2)*i))/(numberOfFFTPoints/4));
			canvas.drawLine((int)pos,(mHeight-2),(int)pos, ((mHeight-2)-sampleValue), p);
		}
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
	
	private void drawMarkFrequencyAndText(Canvas canvas, int markPixelPos, double samplingRate, int drawableArea) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(12);
		p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		double markFreq = (double)((double)(markPixelPos*(samplingRate/4))/(double)(drawableArea/2));
		canvas.drawText("Mark Freq: "+markFreq+" Hz",(mWidth/2),(mHeight-165),p);	
		p.setColor(Color.GREEN);
		canvas.drawLine(markPixelPos,0,markPixelPos,(mHeight-1),p);
	}
	
	private void drawBorderLine(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.GREEN);
		canvas.drawLine(0,0,0,(mHeight-1),p);
		canvas.drawLine(0,0,(mWidth-1),0,p);
		canvas.drawLine((mWidth-1),0,(mWidth-1),(mHeight-1),p);
		canvas.drawLine(0,(mHeight-1),(mWidth-1),(mHeight-1),p);
	}

	private void getViewInfo() {
		mWidth = getWidth();
		mHeight = getHeight();
		mOrientation = mDisplay.getOrientation();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
		mOrientation = mDisplay.getOrientation();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isSurfaceCreated = true;
		getViewInfo();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isSurfaceCreated = false;
	}
}   
