package com.xquadro.tabataTimer;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerActivity extends Activity {
	// controlls
	TextView tvCDT;
	TextView tvIntervals;
	TextView tvType;
	RelativeLayout rlBG;
	ImageView soundView;
	ImageView bigSmallView;
	ImageView pauseView;

	// model
	TabataModel2 t;
	TabataCount counter;

	// input
	int prepare = 10;
	int work = 20;
	int rest = 10;
	int count = 8;
	boolean beeps = false;

	// sounds
	SoundPool soundPool;
	int effordSoundId = -1;
	int restSoundId = -1;

	// state
	boolean smallFont;
	boolean soundEnabled;
	boolean isPaused;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
		isPaused = false;
				
		Bundle bundle = getIntent().getExtras();
		prepare = bundle.getInt("prepare");
		work = bundle.getInt("work");
		rest = bundle.getInt("rest");
		count = bundle.getInt("count");
		beeps = bundle.getBoolean("beeps");

		setContentView(R.layout.timer);

		initUI();

		prepareSounds();

		t = new TabataModel2(prepare, work, rest, count, beeps);
	}
	
	private void initUI(){
		tvCDT = (TextView) findViewById(R.id.tvCDT);
		tvIntervals = (TextView) findViewById(R.id.tvIntervals);
		tvType = (TextView) findViewById(R.id.tvType);
		rlBG = (RelativeLayout) findViewById(R.id.rlBG);
		soundView = (ImageView) findViewById(R.id.soundView);
		bigSmallView = (ImageView) findViewById(R.id.bigSmallView);
		pauseView = (ImageView) findViewById(R.id.pauseView);
		
		soundView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleSound();
				saveSettings();
			}
		});

		bigSmallView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleSize();
				saveSettings();
			}
		});

		pauseView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				togglePause();
			}
		});
	}

	private void togglePause() {
		isPaused = !isPaused;
		setPlayImage();
		if (isPaused) {
			pause();
		} else {
			play();
		}
	}

	
	private void setSoundImage() {
		soundView.setImageResource(soundEnabled ? R.drawable.ic_sound_on
				: R.drawable.ic_sound_off);
		
	}
	
	private void toggleSound() {
		soundEnabled = !soundEnabled;
		setSoundImage();
	}

	private void toggleSize() {
		smallFont = !smallFont;
		setSize(smallFont);
	}
	
	private void setSize(boolean small){
		bigSmallView.setImageResource(small ? R.drawable.ic_size_small
				: R.drawable.ic_size_big);
		
		if (small) {
			tvCDT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 120);
		} else {
			tvCDT.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 240);
		}
		
	}

	private void pause() {
		counter.cancel();
	}

	private void play() {		
		counter = new TabataCount(t.getTimeLeft() * 1000 + 2000, 1000);
		counter.start();
	}
	
	private void setPlayImage(){
		if(isPaused) {
			pauseView.setImageResource(R.drawable.ic_play);
		} else {
			pauseView.setImageResource(R.drawable.ic_pause);
		}
	}



	@Override
	protected void onPause() {
		super.onPause();
		soundPool.unload(effordSoundId);
		soundPool.unload(restSoundId);
		saveSettings();
		pause();
	}
	
	private void saveSettings(){
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putBoolean("sounds", soundEnabled);
		editor.putBoolean("small", smallFont);
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		soundEnabled = prefs.getBoolean("sounds", true);
		setSoundImage();
		smallFont = prefs.getBoolean("small", true);
		setSize(smallFont);
		play();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.timer);
		initUI();
		updateUI();
		setSize(smallFont);
		setSoundImage();
		setPlayImage();
	}

	public void updateUI() {
		tvIntervals.setText("" + t.getCurrentRound() + "/" + t.getRounds());
		switch (t.getActiveIntervalType()) {
		case 1:
			tvType.setText("Prepare");
			rlBG.setBackgroundColor(0xFF267226);
			break;
		case 2:
			tvType.setText("Work");
			rlBG.setBackgroundColor(0xFF14099F);
			break;
		case 3:
			tvType.setText("Rest");
			rlBG.setBackgroundColor(0xFF267226);
			break;
		case 4:
			tvType.setText("End");
			rlBG.setBackgroundColor(0xFF267226);
			break;

		default:
			break;
		}

		tvCDT.setText("" + t.getActiveIntervalTime());
		playSound(t.hasSound());

	}

	public void prepareSounds() {

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		try {
			AssetManager assetManager = getAssets();
			AssetFileDescriptor descriptor = assetManager.openFd("beep2.ogg");
			effordSoundId = soundPool.load(descriptor, 1);
			descriptor = assetManager.openFd("end.ogg");
			restSoundId = soundPool.load(descriptor, 1);
		} catch (IOException e) {
			// handle no sound
		}

	}

	public void playSound(int soundType) {
		if (soundEnabled == true) {
			switch (soundType) {
			case 1:
				if (effordSoundId != -1)
					soundPool.play(effordSoundId, 1, 1, 0, 0, 1);
				break;
			case 2:
				if (restSoundId != -1)
					soundPool.play(restSoundId, 1, 1, 0, 0, 1);
				break;

			default:
				break;
			}
		}
	}

	public class TabataCount extends CountDownTimer {

		public TabataCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			TimerActivity.this.finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			t.onTick();
			updateUI();
		}

	}

	
}