package log;

import android.util.Log;
import fft.Constants;

public class LOG {
	
	private static final String DEBUG_TAG = "ZZ4FAP: ";
	
	private LOG(){}

	public static void d(String TAG, String msg){
		if(Constants.DEBUG_MODE){
			Log.d(TAG,msg);
		}
	}
	
	public static void d(String msg){
		if(Constants.DEBUG_MODE){
			Log.d(DEBUG_TAG,msg);
		}
	}
	
	public static void i(String TAG, String msg){
		if(Constants.DEBUG_MODE){
			Log.i(TAG,msg);
		}
	}
	
	public static void i(String msg){
		if(Constants.DEBUG_MODE){
			Log.i(DEBUG_TAG,msg);
		}
	}
	
	public static void e(String TAG, String msg){
		if(Constants.DEBUG_MODE){
			Log.e(TAG,msg);
		}
	}
	
	public static void e(String msg){
		if(Constants.DEBUG_MODE){
			Log.e(DEBUG_TAG,msg);
		}
	}
}
