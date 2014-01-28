package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MyExpenseOrganizer extends Activity{

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case R.id.SETTINGS_COMMAND:
			Intent i = new Intent(MyApplication.getInstance(),Preference.class);
			startActivity(i);
			break;
		}
		
		return false;
	}
}
