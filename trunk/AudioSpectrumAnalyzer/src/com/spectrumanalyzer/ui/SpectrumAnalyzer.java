package com.spectrumanalyzer.ui;

import log.LOG;

import com.spectrumanalyzer.ui.R;

import dsp.AudioProcessing;
import dsp.AudioProcessingListener;
import dsp.SignalHelper.DebugSignal;
import fft.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class SpectrumAnalyzer extends Activity implements Button.OnClickListener, AudioProcessingListener {
	
	private static final String TAG = SpectrumAnalyzer.class.getSimpleName();
	
	private Spinner fs_spinner;
	private Spinner fft_spinner;
	private Button move_center_freq_to_left_button;
	private Button move_center_freq_to_right_button;
	private TextView peak_freq_text_view;
	private Panel spectrum_display;
	private EditText debug_signal_freq;
	private SeekBar noiseLevel;
	
	private AudioProcessing mAudioCapture;
	
	private AlertDialog mAlert;
	
	private double mSampleRateInHz = Constants.SAMPLING_FREQUENCY;
	private int mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS;
	
	private static boolean mRunAppInDebugMode;
	
	private int mOrientation;
	
	private static final int SET_FREQ_BUTTON_ID = 0x7f070100;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setSpectrumAnalyzer();
		mAlert = createAlertDialog();
	}
	
	void setDebugModeOptions() {
		LinearLayout debug_signal_freq_setting_group = (LinearLayout) findViewById(R.id.debug_signal_freq_setting_group);
		debug_signal_freq_setting_group.setBackgroundResource(R.drawable.debug_signal_settings_border);
		
		LinearLayout debug_signal_freq_setting_text = (LinearLayout) findViewById(R.id.debug_signal_freq_setting_text);
		
		TextView debug_signal_freq_title = new TextView(this);
		debug_signal_freq_title.setText("Debug Signal Settings");
		debug_signal_freq_title.setGravity(Gravity.CENTER);
		debug_signal_freq_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		debug_signal_freq_title.setTextColor(Color.WHITE);
		debug_signal_freq_setting_text.addView(debug_signal_freq_title);
		
		LinearLayout debug_signal_freq_setting = (LinearLayout) findViewById(R.id.debug_signal_freq_setting);
		
		debug_signal_freq = new EditText(this);
		debug_signal_freq.setText(Double.toString(Constants.FREQ_1KHz));
		debug_signal_freq.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		debug_signal_freq.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
		debug_signal_freq_setting.addView(debug_signal_freq);
		
		Button apply_freq = new Button(this);
		apply_freq.setId(SET_FREQ_BUTTON_ID);
		apply_freq.setText("Set Freq [Hz]");
		apply_freq.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 2f));
		debug_signal_freq_setting.addView(apply_freq);
		apply_freq.setOnClickListener(this);
		
		LinearLayout debug_signal_noise_and_two_senoids_setting = (LinearLayout) findViewById(R.id.debug_signal_noise_and_two_senoids_setting);
		
		CheckBox add_noise = new CheckBox(this);
		add_noise.setTextSize(14);
		add_noise.setText("Add Noise");
		add_noise.setTextColor(Color.WHITE);
		add_noise.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		add_noise.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		debug_signal_noise_and_two_senoids_setting.addView(add_noise);
		add_noise.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        if (((CheckBox) v).isChecked()) {
		            LOG.d(TAG,"Add noise");
		            DebugSignal.setAddNoise(true);
		            noiseLevel.setEnabled(true);
		        } else {
		        	LOG.d(TAG,"Don't add noise");
		        	DebugSignal.setAddNoise(false);
		        	noiseLevel.setEnabled(false);;
		        }
		    }
		});
		
		CheckBox two_sins = new CheckBox(this);
		two_sins.setTextSize(14);
		two_sins.setText("Add Sinusoid");
		two_sins.setTextColor(Color.WHITE);
		two_sins.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		two_sins.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		debug_signal_noise_and_two_senoids_setting.addView(two_sins);
		two_sins.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        if (((CheckBox) v).isChecked()) {
		            LOG.d(TAG,"Add a second sinusoid");
		            DebugSignal.setAddSecondSinusoid(true);
		        } else {
		        	LOG.d(TAG,"Use only one sinusoid");
		        	DebugSignal.setAddSecondSinusoid(false);
		        }
		    }
		});
		
		noiseLevel = new SeekBar(this);
		noiseLevel.setMax(Constants.MAX_LEVEL);
		noiseLevel.setProgress(Constants.DEFAULT_PROGRESS);
		noiseLevel.setEnabled(false);
		noiseLevel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
		debug_signal_freq_setting_group.addView(noiseLevel);
		noiseLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				LOG.d(TAG,"Progress: "+progress);
				DebugSignal.setNoiseLevel(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAlert.show();
	}
	
	private void setSpectrumAnalyzer() {
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
		
		// get the Surface view which is used to draw the spectrum.
		spectrum_display = (Panel) findViewById(R.id.SurfaceView01);
	}

	public class OnSamplingRateItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			double samplingRate;
			
			switch(pos){
			case 0:
				samplingRate = 8000.0;
				break;
			case 1:
				samplingRate = 16000.0;
				break;
			case 2:
				samplingRate = 22050.0;
				break;
			case 3:
				samplingRate = 44100.0;
				break;
			case 4:
				samplingRate = 48000.0;
				break;
			case 5:
				samplingRate = 96000.0;
				break;
			default:
				samplingRate = 8000.0;
				break;
			}
			onSamplingRateChanged(samplingRate);
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
	
	public class OnNumberOfFFTPointsItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			int numberOfFFTPoints;
			
			switch(pos){
			case 0:
				LOG.i(TAG,"Automatic");
				numberOfFFTPoints = 512;
				break;
			case 1:
				numberOfFFTPoints = 64;
				break;
			case 2:
				numberOfFFTPoints = 128;
				break;
			case 3:
				numberOfFFTPoints = 256;
				break;
			case 4:
				numberOfFFTPoints = 512;
				break;
			case 5:
				numberOfFFTPoints = 1024;
				break;
			case 6:
				numberOfFFTPoints = 2048;
				break;
			default:
				numberOfFFTPoints = 512;
				break;
			}
			onNumberOfFFTPointsChanged(numberOfFFTPoints);
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

	@Override
	public void onClick(View v) {
    	int buttonID;
    	buttonID = v.getId();
    	switch(buttonID){
    	case R.id.btn_shift_center_freq_to_left:
    		LOG.i(TAG,"Shift center freq to left");
    		break;
    		
    	case R.id.btn_shift_center_freq_to_right:
    		LOG.i(TAG,"Shift center freq to right");
    		break;
    		
    	case SET_FREQ_BUTTON_ID:
    		String freq = debug_signal_freq.getText().toString();
    		if(!"".equals(freq)) {
    			LOG.i(TAG,"Set frequency button pressed: "+Double.valueOf(freq).doubleValue());
    			DebugSignal.setDebugSignalFrequency(Double.valueOf(freq).doubleValue());
    		}
    		
    		break;
    		
    	default:
    		LOG.e(TAG,"Invalid Option!!!");
			break;
    	}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mAudioCapture.close();
		AudioProcessing.unregisterDrawableFFTSamplesAvailableListener();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        LOG.i(TAG,"Orientation changed: LANDSCAPE");
	        mOrientation = Configuration.ORIENTATION_LANDSCAPE;
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	LOG.i(TAG,"Orientation changed: PORTRAIT");
	    	mOrientation = Configuration.ORIENTATION_PORTRAIT;
	    }
	}
	
	private AlertDialog createAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Run app in debug mode?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mRunAppInDebugMode = true;
				resetDebugSignalSettings();
				mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints,true);
				AudioProcessing.registerDrawableFFTSamplesAvailableListener(SpectrumAnalyzer.this);
				setDebugModeOptions();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mRunAppInDebugMode = false;
				mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints,false);
				AudioProcessing.registerDrawableFFTSamplesAvailableListener(SpectrumAnalyzer.this);
			}
		});
		return builder.create();
	}
	
	private void resetDebugSignalSettings() {
		DebugSignal.setDebugSignalFrequency(Constants.FREQ_1KHz);
		DebugSignal.setAddSecondSinusoid(false);
		DebugSignal.setAddNoise(false);
		DebugSignal.setNoiseLevel(Constants.MIN_LEVEL);
	}
	
	private void onNumberOfFFTPointsChanged(int numberOfFFTPoints) {
		if(numberOfFFTPoints!=mNumberOfFFTPoints) {
			mNumberOfFFTPoints = numberOfFFTPoints;
			mAudioCapture.close();
			mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints,mRunAppInDebugMode);
		}
	}
	
	private void onSamplingRateChanged(double samplingRate) {
		if(samplingRate!=mSampleRateInHz) {
			mSampleRateInHz = samplingRate;
			mAudioCapture.close();
			mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints,mRunAppInDebugMode);
		}
	}

	@Override
	public void onDrawableFFTSignalAvailable(final double[] absSignal) {
		SpectrumAnalyzer.this.runOnUiThread(new Runnable() {
            public void run() {
        		spectrum_display.drawSpectrum(absSignal, mSampleRateInHz, mNumberOfFFTPoints, mAudioCapture.getMaxFFTSample());
        		peak_freq_text_view.setText(Double.toString(mAudioCapture.getPeakFrequency()));
            }
        });
	}
	
	public static boolean getRunAppInDebugMode() {
		return mRunAppInDebugMode;
	}
}
