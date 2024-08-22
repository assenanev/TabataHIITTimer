package com.xquadro.tabataTimer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class TabataMainActivity extends Activity implements OnClickListener {
	Button btnStart, btnReset;
	
	EditText etPrepare, etWork, etRest, etCount;
	CheckBox cbBeeps;
	
	int prepare, work, rest, count;
	boolean beeps;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnStart = (Button) findViewById(R.id.btnStart);
        btnReset = (Button) findViewById(R.id.btnReset);
        etPrepare = (EditText) findViewById(R.id.etPrepare);
        etWork = (EditText) findViewById(R.id.etWork);
        etRest = (EditText) findViewById(R.id.etRest);
        etCount = (EditText) findViewById(R.id.etCount);
        cbBeeps = (CheckBox) findViewById(R.id.cbTwoBeeps);

        btnStart.setOnClickListener(this);
        
        btnReset.setOnClickListener(this);
        
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putString("prepare", etPrepare.getText().toString());
		editor.putString("work", etWork.getText().toString());
		editor.putString("rest", etRest.getText().toString());
		editor.putString("count", etCount.getText().toString());
		editor.putBoolean("beeps", cbBeeps.isChecked());
		editor.commit();

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
		String prepareText = prefs.getString("prepare", null);
		if (prepareText != null) 
		{
			etPrepare.setText(prepareText);
		}
		
		String workText = prefs.getString("work", null);
		if (workText != null) 
		{
			etWork.setText(workText);
		}
		
		String restText = prefs.getString("rest", null);
		if (prepareText != null) 
		{
			etRest.setText(restText);
		}
		
		String countText = prefs.getString("count", null);
		if (countText != null) 
		{
			etCount.setText(countText);
		}
		
		Boolean twoBlips = prefs.getBoolean("beeps", false);
		cbBeeps.setChecked(twoBlips);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	        case R.id.btnStart: 
	         	startTimerActivity();
	        	break;
	        case R.id.btnReset:
	        	loadDefaults();
	        	break;
		}
		
	}
	
	private void startTimerActivity (){
		Intent intent = new Intent(TabataMainActivity.this,TimerActivity.class);
		
		try {
			prepare = Integer.parseInt(etPrepare.getText().toString());
			work = Integer.parseInt(etWork.getText().toString());
			rest = Integer.parseInt(etRest.getText().toString());
			count = Integer.parseInt(etCount.getText().toString());
			beeps = cbBeeps.isChecked();
			
		} catch(NumberFormatException nfe) {
			prepare = 10;
			work = 20;
			rest = 10;
			count = 8;
			beeps = false;
		} 
		
		
		
		Bundle bundle = new Bundle();
		bundle.putInt("prepare", prepare);
		bundle.putInt("work", work);
		bundle.putInt("rest", rest);
		bundle.putInt("count", count);
		bundle.putBoolean("beeps", beeps);

		intent.putExtras(bundle);
		
		TabataMainActivity.this.startActivity(intent);
	}
	
	private void loadDefaults(){
		etPrepare.setText("10");
		etWork.setText("20");
		etRest.setText("10");
		etCount.setText("8");
		cbBeeps.setChecked(false);
	}

}
