package cn.edu.xidian.robot.talk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

public class SettingsActivity extends Activity implements 
             OnItemSelectedListener,OnSeekBarChangeListener,
             RadioGroup.OnCheckedChangeListener{ 
	private SeekBar seekBarSpeed = null;
	private ArrayAdapter<CharSequence> adapter; 
	private Spinner spinSet = null;
	private RadioGroup radioGroup = null;
	private int selected = 0;
	private int barValue = 0;
	private int check = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.set);
		 Intent intent = this.getIntent();
		 Bundle bundle = intent.getExtras();
	     selected=bundle.getInt("NAME_POS");
	     barValue=bundle.getInt("SPEED");
	     check = bundle.getInt("CHECK");
	     
		 adapter = ArrayAdapter.createFromResource( 
	        		this, R.array.spinnerItem, android.R.layout.simple_spinner_item); 
	     spinSet=(Spinner)findViewById(R.id.spinner);
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	     spinSet.setAdapter(adapter);
	     spinSet.setOnItemSelectedListener(this);
	     spinSet.setSelection(selected);
	     
	     seekBarSpeed=(SeekBar)findViewById(R.id.seekBar_speed);
	     seekBarSpeed.setOnSeekBarChangeListener(this);
		 seekBarSpeed.setProgress(barValue);	
		 
		 radioGroup = (RadioGroup)findViewById(R.id.tts_manner);
		 radioGroup.check(check);
		 radioGroup.setOnCheckedChangeListener(this);
		 
	}
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("SPEED", barValue);
		bundle.putInt("NAME_POS", selected);
		bundle.putInt("CHECK", check);
		intent.putExtras(bundle);
		setResult(1,intent);
        
		finish();
	}
	
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		barValue=arg1;
	}

	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		selected=arg2;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
	    check = checkedId;        
   }
}
