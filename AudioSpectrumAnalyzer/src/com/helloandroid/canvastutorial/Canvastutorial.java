package com.helloandroid.canvastutorial;

import dsp.AudioProcessing;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Canvastutorial extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		// Spinner code with the Available Sampling Frequencies. 
		Spinner spinner = (Spinner) findViewById(R.id.sampling_rate_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.frequencies_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

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
			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
}
