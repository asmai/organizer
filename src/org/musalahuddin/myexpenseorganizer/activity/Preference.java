package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preference extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("Preference", "oncreate");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setTitle("Settings");
	}
	

}
