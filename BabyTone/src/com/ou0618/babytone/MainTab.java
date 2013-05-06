package com.ou0618.babytone;

import com.ou0618.babytone.R;
import com.ou0618.babytone.R.drawable;
import com.ou0618.babytone.R.id;
import com.ou0618.babytone.R.layout;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainTab extends ActivityGroup implements OnCheckedChangeListener {
	private TabHost mHost;
	private Intent newsIntent;
	private Intent myInfoIntent;
	
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.maintabs);
	    // 设置TabHost
	    initTabs();
	    initRadios();
	    setupIntent();
	  }
	 
	  private void initTabs() {
	    mHost = (TabHost) findViewById(R.id.tabhost);
	    mHost.setup(this.getLocalActivityManager());
	    newsIntent = new Intent(this, BabyToneActivity.class);
	    myInfoIntent = new Intent(this, SoundActivity.class);
	    }
	  
	private void initRadios() {
		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);

	}
	
	private void setupIntent() {

		
		mHost.addTab(buildTabSpec("studentOnline", "bbbbbb",
		        R.drawable.icon_0,myInfoIntent));
		mHost.addTab(buildTabSpec("courseRecords","aaaa",
		        R.drawable.icon_0,newsIntent));
		mHost.addTab(buildTabSpec("studentOnline", "bbbbbb",
		        R.drawable.icon_0,myInfoIntent));
		mHost.addTab(buildTabSpec("studentOnline", "bbbbbb",
		        R.drawable.icon_0,myInfoIntent));
		mHost.addTab(buildTabSpec("studentOnline", "bbbbbb",
		        R.drawable.icon_0,myInfoIntent));
		
		}
	  
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
		    switch (buttonView.getId()) {
		    case R.id.radio_button0:
		        this.mHost.setCurrentTabByTag("courseRecords");
		        break;
		    case R.id.radio_button1:
		        this.mHost.setCurrentTabByTag("studentOnline");
		        break;
		    }
		}
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, String resLabel, int resIcon,
		    final Intent content) {
		return this.mHost
		        .newTabSpec(tag)
		        .setIndicator(resLabel,
		                getResources().getDrawable(resIcon))
		        .setContent(content);
		}
	}