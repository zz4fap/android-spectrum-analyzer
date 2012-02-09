package com.spectrumanalyzer.log;

import com.spectrumanalyzer.ui.SpectrumAnalyzer;
import android.util.Log;

public class LOG {
	
	private static final String DEBUG_TAG = "SpectrumAnalyzer";
	
	private LOG(){}

	public static void d(String TAG, String msg){
		if(SpectrumAnalyzer.getRunAppInDebugMode()){
			Log.d(TAG,msg);
		}
	}
	
	public static void d(String msg){
		if(SpectrumAnalyzer.getRunAppInDebugMode()){
			Log.d(DEBUG_TAG,msg);
		}
	}
	
	public static void i(String TAG, String msg){
		if(SpectrumAnalyzer.getRunAppInDebugMode()){
			Log.i(TAG,msg);
		}
	}
	
	public static void i(String msg){
		if(SpectrumAnalyzer.getRunAppInDebugMode()){
			Log.i(DEBUG_TAG,msg);
		}
	}
	
	public static void e(String TAG, String msg){
		if(SpectrumAnalyzer.getRunAppInDebugMode()){
			Log.e(TAG,msg);
		}
	}
	
	public static void e(String msg){
		if(SpectrumAnalyzer.getRunAppInDebugMode()){
			Log.e(DEBUG_TAG,msg);
		}
	}
}
