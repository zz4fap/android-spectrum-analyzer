package com.helloandroid.canvastutorial;

import dsp.AudioProcessing;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Canvastutorial extends Activity implements  Button.OnClickListener{
	
	private static final String TAG = Canvastutorial.class.getSimpleName();
	
	private Spinner fs_spinner;
	private Spinner fft_spinner;
	private Button move_center_freq_to_left_button;
	private Button move_center_freq_to_right_button;
	private TextView peak_freq_text_view;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setSpectrumAnalyzer();
	}
	
	private void setSpectrumAnalyzer(){
		// Spinner code with the Available Sampling Frequencies. 
		fs_spinner = (Spinner) findViewById(R.id.sampling_rate_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.frequencies_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fs_spinner.setAdapter(adapter);
		fs_spinner.setOnItemSelectedListener(new OnSamplingRateItemSelectedListener());
		
		// Spinner code with the Available Number of FFt Points. 
		fft_spinner = (Spinner) findViewById(R.id.number_of_fft_points_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.fft_points_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fft_spinner.setAdapter(adapter);
		fft_spinner.setOnItemSelectedListener(new OnNumberOfFFTPointsItemSelectedListener());
		
		//center frequency buttons
		move_center_freq_to_left_button = (Button) findViewById(R.id.btn_shift_center_freq_to_left);
		move_center_freq_to_left_button.setOnClickListener(this);
		move_center_freq_to_right_button = (Button) findViewById(R.id.btn_shift_center_freq_to_right);
		move_center_freq_to_right_button.setOnClickListener(this);
		
		// get text view which is used to display the current peak frequency.
		peak_freq_text_view = (TextView) findViewById(R.id.txt_peak_freq);
	}

	public class OnSamplingRateItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			switch(pos){
			case 0:
				Log.i("ZZ4FAP: ","8000");
				break;
			case 1:
				Log.i("ZZ4FAP: ","16000");
				break;
			case 2:
				Log.i("ZZ4FAP: ","22050");
				break;
			case 3:
				Log.i("ZZ4FAP: ","44100");
				break;
			case 4:
				Log.i("ZZ4FAP: ","48000");
				break;
			case 5:
				Log.i("ZZ4FAP: ","96000");
				break;
			default:
				Log.e(TAG,"Invalid Option!!!");
				break;
			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
	
	public class OnNumberOfFFTPointsItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			switch(pos){
			case 0:
				Log.i("ZZ4FAP: ","Automatic");
				break;
			case 1:
				Log.i("ZZ4FAP: ","64");
				break;
			case 2:
				Log.i("ZZ4FAP: ","128");
				break;
			case 3:
				Log.i("ZZ4FAP: ","256");
				break;
			case 4:
				Log.i("ZZ4FAP: ","512");
				break;
			case 5:
				Log.i("ZZ4FAP: ","1024");
				break;
			case 6:
				Log.i("ZZ4FAP: ","2048");
				break;
			default:
				Log.e(TAG,"Invalid Option!!!");
				break;
			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	@Override
	public void onClick(View v) {
    	int buttonID;
    	buttonID = v.getId();
    	switch(buttonID){
    	case R.id.btn_shift_center_freq_to_left:
    		Log.i("ZZ4FAP: ","Shift center freq to left");
    		break;
    		
    	case R.id.btn_shift_center_freq_to_right:
    		Log.i("ZZ4FAP: ","Shift center freq to right");
    		break;
    		
    	default:
			Log.e(TAG,"Invalid Option!!!");
			break;
    	}
	}
}
