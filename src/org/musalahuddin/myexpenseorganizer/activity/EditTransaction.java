package org.musalahuddin.myexpenseorganizer.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditTransaction extends FragmentActivity implements OnClickListener{
	
	boolean mAddOnly;
	
	private static final int SELECT_EXPENSE_CATEGORY_REQUEST = 1;
	private static final int SELECT_TRANSACTON_CATEGORY_REQUEST = 2;
	private static final int SELECT_FROM_ACCOUNT_REQUEST = 3;
	private static final int SELECT_TO_ACCOUNT_REQUEST = 4;
	
	static final int DIALOG_DATE = 0;
	static final int DIALOG_TIME = 1;
	
	private long transactionDate = 0L;
	private long transactionTime = 0L;
	private Long mExpenseCatId = 0L;
	private Long mTransactionCatId = 0L;
	private String mTransactionCatName;
	private String mExpenseCatName;
	
	
	private Calendar mCalendar = Calendar.getInstance();
	
	private Bitmap pendingTransactionImage = null;
	
	private Spinner mTransactionFrom, mTransactionTo;
	private Button mTransactionFromAccount, mTransactionToAccount;
	private EditText mTransactionFromOther, mTransactionToOther;
	private ImageButton mTransactionCamera;
	private Button mTransactionDate,mTransactionTime;
	private Button mTransactionCategory;
	private Button mTransactionType;
	
	
	private int cameraButton;
	private static int imageHeight=0;
	private static int imageWidth=0;
	
	public static Uri contentUri;
	
	private final java.text.DateFormat mDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
	private final java.text.DateFormat mTimeFormat = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT);
	
	private int getDefaultCameraButton() {
        return R.drawable.camera_button;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_transaction);
		Intent intent = getIntent();
		String action = intent.getAction();
        mAddOnly = action != null && action.equals("myexpenseorganizer.intent.add.transactions");
		
        setTitle(mAddOnly ? R.string.transaction_new_title : R.string.transaction_edit_title);
        
        cameraButton = getDefaultCameraButton();
        
        mTransactionFrom = (Spinner) findViewById(R.id.spinner_transaction_from);
        mTransactionFromAccount = (Button) findViewById(R.id.in_transaction_from_account);
        mTransactionFromOther = (EditText) findViewById(R.id.in_transaction_from_other);
        mTransactionTo = (Spinner) findViewById(R.id.spinner_transaction_to);
        mTransactionToAccount = (Button) findViewById(R.id.in_transaction_to_account);
        mTransactionToOther = (EditText) findViewById(R.id.in_transaction_to_other);
        mTransactionCamera = (ImageButton) findViewById(R.id.image_transaction_camera);
        mTransactionDate = (Button) findViewById(R.id.in_transaction_date);
        mTransactionTime = (Button) findViewById(R.id.in_transaction_time);
        mTransactionCategory = (Button) findViewById(R.id.in_transaction_category);
        mTransactionType = (Button) findViewById(R.id.in_transaction_type);
        
        ArrayList<String> list = new ArrayList<String>();
    	list.add("Accounts");
    	list.add("Other");
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    		android.R.layout.simple_spinner_item, list);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	mTransactionFrom.setAdapter(adapter);
    	mTransactionFrom.setOnItemSelectedListener(
    			new OnItemSelectedListener(){
	    			public void onItemSelected(AdapterView<?> parent, View view, int position,
	    					long id) {
	    				if(position == 0){
	    					mTransactionFromOther.setVisibility(View.GONE);
	    					mTransactionFromAccount.setVisibility(View.VISIBLE);
	    				}
	    				else{
	    					mTransactionFromAccount.setVisibility(View.GONE);
	    					mTransactionFromOther.setVisibility(View.VISIBLE);
	    					mTransactionFromOther.setText("");
	    					mTransactionFromOther.requestFocus();
	    				}
	    				
	    			}
	
	    			@Override
	    			public void onNothingSelected(AdapterView<?> parent) {
	    				// do nothing
	    			}
    			});
    	
    	mTransactionTo.setAdapter(adapter);
    	mTransactionTo.setOnItemSelectedListener(
    			new OnItemSelectedListener(){
	    			public void onItemSelected(AdapterView<?> parent, View view, int position,
	    					long id) {
	    				if(position == 0){
	    					mTransactionToOther.setVisibility(View.GONE);
	    					mTransactionToAccount.setVisibility(View.VISIBLE);
	    				}
	    				else{
	    					mTransactionToAccount.setVisibility(View.GONE);
	    					mTransactionToOther.setVisibility(View.VISIBLE);
	    					mTransactionToOther.setText("");
	    					mTransactionToOther.requestFocus();
	    				}
	    				
	    			}
	
	    			@Override
	    			public void onNothingSelected(AdapterView<?> parent) {
	    				// do nothing
	    				
	    			}
    			});
    	
    	mTransactionFromAccount.setOnClickListener(this);
    	mTransactionCamera.setOnClickListener(this);
    	mTransactionDate.setOnClickListener(this);
    	mTransactionTime.setOnClickListener(this);
    	mTransactionCategory.setOnClickListener(this);
    	mTransactionType.setOnClickListener(this);
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
			finish();
			break;
			
		case R.id.CANCEL_COMMAND:
			 finish();
			break;
			
		}
		
		return false;
	}
	
	final ClearImageCallback clearImage = new ClearImageCallback() {
        @Override
        public void clearImage() {
        	pendingTransactionImage = null;
        	mTransactionCamera.setImageResource(cameraButton);
        }
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		
		case R.id.in_transaction_from_account:
			startSelectAccount();
			break;
			
		case R.id.image_transaction_camera:
			
			if(imageHeight == 0){
			imageHeight = mTransactionCamera.getHeight();
			}
			if(imageWidth == 0){
			imageWidth = mTransactionCamera.getWidth();
			}
			
			Toast.makeText(EditTransaction.this,"getHeight = " + imageHeight, Toast.LENGTH_LONG).show();
			
			if (pendingTransactionImage != null)
                CameraModule.showPictureLauncher(this, clearImage);
            else
                CameraModule.showPictureLauncher(this, null);
			break;
			
		case R.id.in_transaction_date:
			showDialog(DIALOG_DATE);
			break;
			
		case R.id.in_transaction_time:
			showDialog(DIALOG_TIME);
			break;
			
		case R.id.in_transaction_category:
			startSelectExpenseCategory();
			break;
			
		case R.id.in_transaction_type:
			startSelectTransactionCategory();
			break;
		}
		
	}
	
	
	/**
	 * calls the activity for selecting (and managing) expense categories
	 */
    private void startSelectAccount(){
    	Intent i = new Intent(this, SelectAccount.class);
    	startActivityForResult(i,SELECT_FROM_ACCOUNT_REQUEST);
    }

	/**
	 * calls the activity for selecting (and managing) expense categories
	 */
    private void startSelectExpenseCategory(){
    	Intent i = new Intent(this, SelectCategory.class);
    	startActivityForResult(i,SELECT_EXPENSE_CATEGORY_REQUEST);
    }
	
	/**
	 * calls the activity for selecting (and managing) transaction types
	 */
    private void startSelectTransactionCategory(){
    	Intent i = new Intent(this, SelectTransactionCategory.class);
    	startActivityForResult(i,SELECT_TRANSACTON_CATEGORY_REQUEST);
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
	  
	  /**
	   * listens on changes in the time dialog and sets the time on hte button
	   */
	  private TimePickerDialog.OnTimeSetListener mTimeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	      mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
	      mCalendar.set(Calendar.MINUTE,minute);
	      setTime();
	    }
	  };
	  
	/**
	 * sets date on date button
	 */
	private void setDate() {
		mTransactionDate.setText(mDateFormat.format(mCalendar.getTime()));
	}
	
	/**
	 * sets date on date button
	 */
	private void setTime() {
		mTransactionTime.setText(mTimeFormat.format(mCalendar.getTime()));
	}
	
	/**
	 * helper for padding integer values smaller than 10 with 0
	 * @param c
	 * @return
	 */
	private static String pad(int c) {
	  if (c >= 10)
	     return String.valueOf(c);
	  else
	     return "0" + String.valueOf(c);
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DATE:
			return new DatePickerDialog(this,
					mDateSetListener,
					mCalendar.get(Calendar.YEAR),
					mCalendar.get(Calendar.MONTH),
					mCalendar.get(Calendar.DAY_OF_MONTH)
			);
		case DIALOG_TIME:
			return new TimePickerDialog(this,
					mTimeSetListener,
					mCalendar.get(Calendar.HOUR_OF_DAY),
					mCalendar.get(Calendar.MINUTE),
					android.text.format.DateFormat.is24HourFormat(this)
			);
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(requestCode == SELECT_EXPENSE_CATEGORY_REQUEST){
			if (intent != null) {
		      mExpenseCatId = intent.getLongExtra("exp_cat_id",0);
		      mExpenseCatName = intent.getStringExtra("exp_cat_name");
		      mTransactionCategory.setText(mExpenseCatName);
		      //Toast.makeText(EditTransaction.this,"hello " + mTransactionCatId, Toast.LENGTH_LONG).show();
		
		    }
		}
		else if(requestCode == SELECT_TRANSACTON_CATEGORY_REQUEST){
			
			if (intent != null) {
		      mTransactionCatId = intent.getLongExtra("trans_cat_id",0);
		      mTransactionCatName = intent.getStringExtra("trans_cat_name");
		      mTransactionType.setText(mTransactionCatName);
		      //Toast.makeText(EditTransaction.this,"hello " + mTransactionCatId, Toast.LENGTH_LONG).show();
		     
		    }
		}
		else{
			CameraResultCallback callback = new CameraResultCallback() {
	            @Override
	            public void handleCameraResult(Bitmap bitmap) {
	            	//Toast.makeText(EditTransaction.this,"getHeigth = " + mTransactionCamera.getHeight(), Toast.LENGTH_LONG).show();
	            	//Toast.makeText(EditTransaction.this,"getHeigth = " + EditTransaction.imageHeight, Toast.LENGTH_LONG).show();
	            	pendingTransactionImage = bitmap;
	            	mTransactionCamera.setMaxHeight(imageHeight);
	                mTransactionCamera.setMaxWidth(imageWidth);
	                //mTransactionCamera.setMaxHeight(150);
	                //mTransactionCamera.setMaxWidth(150);
	            	mTransactionCamera.setImageBitmap(pendingTransactionImage);
	                
	            }
	        };
	        
	        
	        if(CameraModule.activityResult(this,
	                requestCode, resultCode, intent, callback)){
	        	Log.i("success","with camera");
	        }
		}
        
	}
}
