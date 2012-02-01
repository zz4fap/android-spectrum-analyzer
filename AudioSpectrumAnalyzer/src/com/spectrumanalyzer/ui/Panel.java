package com.spectrumanalyzer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

    // private Context mContext;
    private int mWidth;
    private int mHeight;
    // private int mOrientation;
    // private Display mDisplay;
    private boolean isSurfaceCreated;
    private SurfaceHolder mSurfaceHolder;

    private static final int SHIFT_CONST = 10;
    private static final double SCALE_FACTOR = 100.0;

    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mSurfaceHolder = getHolder();
        setFocusable(true);
        // mContext = context;
        // mDisplay = ((WindowManager) mContext
        // .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public Panel(Context context) {
        super(context);
        getHolder().addCallback(this);
        mSurfaceHolder = getHolder();
        setFocusable(true);
        // mContext = context;
        // mDisplay = ((WindowManager) mContext
        // .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public void drawSpectrum(double[] absSignal, double samplingRate,
            int numberOfFFTPoints, double maxFFTSample) {
        if (isSurfaceCreated) {
            Canvas canvas;
            canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    canvas.drawColor(Color.BLACK);
                    drawSpectrumMarks(canvas, samplingRate, numberOfFFTPoints);
                    drawFFTSignal(canvas, absSignal, maxFFTSample);
                    drawCenterFrequencyMarkAndText(canvas, samplingRate / 4);
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

    private void drawSpectrumMarks(Canvas canvas, double samplingRate,
            int numberOfFFTPoints) {
        int freqStep = 1000;
        Paint p = new Paint();

        for (int freq = 0; freq <= (int) (samplingRate / 2); freq = freq
                + freqStep) {
            double point = freq
                    * (((double) numberOfFFTPoints) / (samplingRate));
            int pointInt = (int) point;

            pointInt = pointInt + SHIFT_CONST;// add 10 pixels in order to make
                                              // room for first freq string to
                                              // be totally written on the
                                              // screen.

            double freqDouble = ((double) freq) / 1000.0;

            p.setColor(Color.WHITE);
            canvas.drawText(Double.toString(freqDouble), (pointInt - 8),
                    (mHeight - 1), p);// plot frequencies

            p.setColor(Color.BLUE);
            canvas.drawLine(pointInt, (mHeight - 15), pointInt, (mHeight - 30),
                    p);// plot markers
        }
    }

    private void drawFFTSignal(Canvas canvas, double[] absSignal,
            double maxFFTSample) {
        int sampleValue, nextSampleValue;
        Paint p = new Paint();
        p.setColor(Color.RED);
        for (int i = 0; i < (absSignal.length - 1); i++) {
            sampleValue = (int) (SCALE_FACTOR * (absSignal[i] / maxFFTSample));
            nextSampleValue = (int) (SCALE_FACTOR * (absSignal[i + 1] / maxFFTSample));
            canvas.drawLine((i + SHIFT_CONST), ((mHeight - 30) - sampleValue),
                    (i + 1 + SHIFT_CONST), ((mHeight - 30) - nextSampleValue),
                    p);
        }
    }

    // private void drawPeakFrequencyMarkAndText(Canvas canvas, double peakFreq)
    // {
    // Paint p = new Paint();
    // p.setColor(Color.WHITE);
    // canvas.drawText("Peak Freq: "+peakFreq+" Hz",100,(mHeight-150),p);// plot
    // frequencies
    // }

    private void drawCenterFrequencyMarkAndText(Canvas canvas, double centerFreq) {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawText("Center Freq: " + centerFreq + " Hz", (mWidth / 2),
                (mHeight - 150), p);
    }

    private void getViewInfo() {
        mWidth = getWidth();
        mHeight = getHeight();
        // mOrientation = mDisplay.getOrientation();
        Log.i("ZZ4FAP: ", "Width: " + getWidth() + " - Height: " + getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        mWidth = width;
        mHeight = height;
        // mOrientation = mDisplay.getOrientation();
        Log.i("ZZ4FAP: ", "Surface Changed: new width: " + width
                + " - new height: " + height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("ZZ4FAP: ", "surfaceCreated");
        isSurfaceCreated = true;
        getViewInfo();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreated = false;
    }
}
