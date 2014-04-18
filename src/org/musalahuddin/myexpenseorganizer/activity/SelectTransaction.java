package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SelectTransaction extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transactions_list);
		
		Bundle extras = getIntent().getExtras();
		String title = extras.getString("title");
		setTitle(title);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.transactions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		
		case R.id.CREATE_COMMAND:
			/*
			i = new Intent(this, EditAccount.class);
			startActivityForResult(i, 0);
			*/
			i = new Intent("myexpenseorganizer.intent.add.transactions");
			startActivity(i);
			 
			break;
		}
		
		return false;
	}

	
}
