package com.helloandroid.canvastutorial;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import dsp.AudioProcessingListener;

public class CanvasDrawing implements AudioProcessingListener {
	
    private SurfaceHolder _surfaceHolder;
    private Panel _panel;
    private boolean isSurfaceCreated = false;
    
    public CanvasDrawing(SurfaceHolder surfaceHolder, Panel panel) {
        _surfaceHolder = surfaceHolder;
        _panel = panel;
    }

    @Override
    public void onDrawableFFTSignalAvailable() {
    	if(isSurfaceCreated){
    		Canvas c;
    		c = null;
    		try {
    			c = _surfaceHolder.lockCanvas(null);
    			synchronized (_surfaceHolder) {
    				_panel.onDraw(c);
    			}
    		} finally {
    			// do this in a finally so that if an exception is thrown
    			// during the above, we don't leave the Surface in an
    			// inconsistent state
    			if (c != null) {
    				_surfaceHolder.unlockCanvasAndPost(c);
    			}
    		}	
    	}
    }
    
    public void setIsSurfaceCreated(boolean status){
    	isSurfaceCreated = status;
    }
}
