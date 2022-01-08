package com.example.uhfreader816ubt;
import com.example.uhfreader816ubt.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;

public class TabsActivity extends TabActivity {

	private String[] tableMenu = {"ISO15693","ISO14443A"};
	private Intent[] tableIntents;
	
	private TabHost myTabHost;

	public static final String EXTRA_MODE = "mode";
	public static final String TABLE_6B = "Command";
	public static final String TABLE_G2 = "EPCC1-G2";
	public static final String TABLE_GENERAL="ACTIVE";
	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tabs);
		
		myTabHost = getTabHost();
		Intent intent1 = new Intent(this,ScanModeGroup.class);
		intent1.putExtra(EXTRA_MODE, TABLE_G2);
		Intent intent2 = new Intent(this,Iso6BActivity.class);
		intent2.putExtra(EXTRA_MODE, TABLE_6B);
		
		Intent intent3 = new Intent(this,GetActive.class);
		intent2.putExtra(EXTRA_MODE, TABLE_GENERAL);
		TabHost.TabSpec tabSpec2 = myTabHost.newTabSpec(TABLE_6B).setIndicator(TABLE_6B).setContent(intent2);
		TabHost.TabSpec tabSpec1 = myTabHost.newTabSpec(TABLE_G2).setIndicator(TABLE_G2).setContent(intent1);
		TabHost.TabSpec tabSpec3 = myTabHost.newTabSpec(TABLE_GENERAL).setIndicator(TABLE_GENERAL).setContent(intent3);

		myTabHost.addTab(tabSpec2);
		myTabHost.addTab(tabSpec1);
		myTabHost.addTab(tabSpec3);
		myTabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tabs, menu);
		return true;
	}

}
