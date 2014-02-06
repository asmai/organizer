package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SelectAccount extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle(R.string.account_overview_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accounts, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);
		Intent i;
		switch (item.getItemId()) {
		case R.id.SETTINGS_COMMAND:
			i = new Intent(this,Preference.class);
			startActivity(i);
			break;
		case R.id.CREATE_COMMAND:
			 i = new Intent(this, EditAccount.class);
			 startActivityForResult(i, 0);
			break;
		}
		
		return false;
	}
}
