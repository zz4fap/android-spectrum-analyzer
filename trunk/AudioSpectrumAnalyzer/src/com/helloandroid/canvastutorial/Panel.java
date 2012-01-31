package com.helloandroid.canvastutorial;

import java.util.ArrayList;

import dsp.AudioProcessing;
import dsp.SignalHelper;
import fft.FFTHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	
	private CanvasDrawing mCanvasDrawing;

	private Context mContext;
	private int mWidth; 
	private int mHeight;
	private int mOrientation;
	private Display mDisplay = null;

	private int mDrawableSignal[];

	private static final int SHIFT_CONST = 10;
	
	private AudioProcessing mAudioCapture = null;

	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs); 
		getHolder().addCallback(this);
		mCanvasDrawing = new CanvasDrawing(getHolder(), this);
		setFocusable(true);
		mContext = context;
		mDisplay = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	public Panel(Context context) {
		super(context);
		getHolder().addCallback(this);
		mCanvasDrawing = new CanvasDrawing(getHolder(), this);
		setFocusable(true);
		mContext = context;
		mDisplay = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		drawSpectrumMarks(canvas);
		drawFFTSignal(canvas);
		drawPeakFrequencyMarkAndText(canvas);
	}

	void drawSpectrumMarks(Canvas canvas) {
		int freqStep = 1000;
		Paint p = new Paint();

		for(int freq = 0; freq <= (AudioProcessing.getSampleRateInHz()/2); freq = freq + freqStep)
		{
			double point = freq*(((double)AudioProcessing.getNumberOfFFTPoints())/(AudioProcessing.getSampleRateInHz()));
			int pointInt = (int)point;

			pointInt = pointInt + SHIFT_CONST;//add 10 pixels in order to make room for first freq string to be totally written on the screen.

			double freqDouble = ((double)freq)/1000.0;

			p.setColor(Color.WHITE);
			canvas.drawText(Double.toString(freqDouble),(pointInt-8),(mHeight-1),p);// plot frequencies

			p.setColor(Color.BLUE);
			canvas.drawLine(pointInt,(mHeight-15),pointInt,(mHeight-30), p);// plot markers
		}
	}

	void drawFFTSignal(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.RED);		
		mDrawableSignal = mAudioCapture.getDrawableSignal();
		for(int count=0;count<=(mDrawableSignal.length-4);count=count+2){
			canvas.drawLine((mDrawableSignal[count]+SHIFT_CONST), ((mHeight-30)-mDrawableSignal[count+1]), (mDrawableSignal[count+2]+SHIFT_CONST), ((mHeight-30)-mDrawableSignal[count+3]), p);
		}
	}

	void drawPeakFrequencyMarkAndText(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		double peakFreq = FFTHelper.getPeakFrequency();
		canvas.drawText("Peak Freq: "+peakFreq+" Hz",100,(mHeight-150),p);// plot frequencies
	}

	void getScreenInfo() {
		mWidth = mDisplay.getWidth();
		mHeight = mDisplay.getHeight();
		mOrientation = mDisplay.getOrientation();
		Log.i("ZZ4FAP: ","Width: "+mWidth+" - Height: "+mHeight+" - Orientation: "+mOrientation);
	}

	void getViewInfo() {
		mWidth = getWidth();
		mHeight = getHeight();
		mOrientation = mDisplay.getOrientation();
		Log.i("ZZ4FAP: ","Width: "+getWidth()+" - Height: "+getHeight());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
		mOrientation = mDisplay.getOrientation();
		Log.i("ZZ4FAP: ","Surface Changed: new width: "+width+" - new height: "+height);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mAudioCapture = new AudioProcessing();
		AudioProcessing.registerDrawableFFTSamplesAvailableListener(mCanvasDrawing);
		mCanvasDrawing.setIsSurfaceCreated(true);
		getViewInfo();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mAudioCapture.close();
		mCanvasDrawing.setIsSurfaceCreated(false);
		AudioProcessing.unregisterDrawableFFTSamplesAvailableListener();
	}
}   
