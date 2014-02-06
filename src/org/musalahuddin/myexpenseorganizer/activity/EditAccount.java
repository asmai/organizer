package org.musalahuddin.myexpenseorganizer.activity;

import java.util.Calendar;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

public class EditAccount extends FragmentActivity implements View.OnClickListener{
	
	private static final int SELECT_CATEGORY_REQUEST = 1;
	
	private Long mAccountCatId = null;
	private String mAccountCatName; 
	private Calendar mCalendar = Calendar.getInstance();
	
	private Button mAccountCatButton;
	private Button mAccountDueButton;
	
	static final int DATE_DIALOG_ID = 0;
	
	/**
	 * calls the activity for selecting (and managing) account categories
	 */
    private void startSelectAccountCategory(){
    	Intent i = new Intent(this, SelectAccountCategory.class);
    	startActivityForResult(i,SELECT_CATEGORY_REQUEST);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account);
		setTitle(R.string.account_new_title);
		Log.i("time: " , String.valueOf(System.currentTimeMillis()));
		mAccountCatButton = (Button) findViewById(R.id.in_account_category);
		mAccountDueButton = (Button) findViewById(R.id.in_account_due);
		
		mAccountCatButton.setOnClickListener(this);
		mAccountDueButton.setOnClickListener(this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);
		Intent i;
		switch (item.getItemId()) {
		case R.id.SAVE_COMMAND:
			
			break;
		case R.id.CANCEL_COMMAND:
			 finish();
			break;
		}
		
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.in_account_category:
			startSelectAccountCategory();
			break;
		case R.id.in_account_due:
			showDialog(DATE_DIALOG_ID);
			break;
		}
	}
	
	 /**
	   * listens on changes in the date dialog and sets the date on the button
	   */
	  private DatePickerDialog.OnDateSetListener mDateSetListener =
	    new DatePickerDialog.OnDateSetListener() {

		  	@Override
			public void onDateSet(DatePicker view, int year, 
					int monthOfYear, int dayOfMonth) {
		  			mCalendar.set(year, monthOfYear, dayOfMonth);
		  			setDate();
			}
	  };
	
    @Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,
					mDateSetListener,
					mCalendar.get(Calendar.YEAR),
					mCalendar.get(Calendar.MONTH),
					mCalendar.get(Calendar.DAY_OF_MONTH)
			); 
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == SELECT_CATEGORY_REQUEST && intent != null) {
	      mAccountCatId = intent.getLongExtra("cat_id",0);
	      mAccountCatName = intent.getStringExtra("cat_name");
	      mAccountCatButton.setText(mAccountCatName);
	    }
	}
	
	/**
	 * sets date on date button
	 */
	private void setDate() {
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		String suffix = getDateSuffix(day);
		
		mAccountDueButton.setText(String.valueOf(day)+suffix+" of every month");
	}
	
	/**
	 * return suffix based on the day of the month
	 */
	protected String getDateSuffix(int day) { 
        switch (day) {
            case 1: case 21: case 31:
                   return ("st");

            case 2: case 22: 
                   return ("nd");

            case 3: case 23:
                   return ("rd");

            default:
                   return ("th");
        }
	}


}
