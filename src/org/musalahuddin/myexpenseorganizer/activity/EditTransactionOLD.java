package org.musalahuddin.myexpenseorganizer.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditTransactionOLD extends FragmentActivity implements OnClickListener,OnItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>{
	
	boolean mAddOnly;
	
	private LoaderManager mManager;
	public static final int ACCOUNTS_CURSOR=1;
	
	private static final int SELECT_EXPENSE_CATEGORY_REQUEST = 1;
	private static final int SELECT_TRANSACTON_CATEGORY_REQUEST = 2;
	private static final int SELECT_FROM_ACCOUNT_REQUEST = 3;
	private static final int SELECT_TO_ACCOUNT_REQUEST = 4;
	
	static final int DIALOG_DATE = 0;
	static final int DIALOG_TIME = 1;
	
	private long transactionDateTime = 0L;
	private Long mExpenseCatId = 0L;
	private Long mTransactionCatId = 0L;
	private String mTransactionCatName;
	private String mExpenseCatName;
	
	
	private Calendar mCalendar = Calendar.getInstance();
	
	private Bitmap pendingTransactionImage = null;
	
	private Spinner mSpTransactionFrom, mSpTransactionTo;
	private Spinner mSpTransactionFromAccount, mSpTransactionToAccount;
	private EditText mEtTransactionFromOther, mEtTransactionToOther;
	private ImageButton mImgBtnTransactionCamera;
	private Button mBtnTransactionDate,mBtnTransactionTime;
	private Button mBtnTransactionCategory;
	private Button mBtnTransactionType;
	
	private SimpleCursorAdapter mAccountsAdapter;
	
	
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
        
        
        mManager = getLoaderManager();
        
        cameraButton = getDefaultCameraButton();
        
        mSpTransactionFrom = (Spinner) findViewById(R.id.spinner_transaction_from);
        mSpTransactionFromAccount = (Spinner) findViewById(R.id.in_transaction_from_account);
        mEtTransactionFromOther = (EditText) findViewById(R.id.in_transaction_from_other);
        mSpTransactionTo = (Spinner) findViewById(R.id.spinner_transaction_to);
        mSpTransactionToAccount = (Spinner) findViewById(R.id.in_transaction_to_account);
        mEtTransactionToOther = (EditText) findViewById(R.id.in_transaction_to_other);
        mImgBtnTransactionCamera = (ImageButton) findViewById(R.id.image_transaction_camera);
        mBtnTransactionDate = (Button) findViewById(R.id.in_transaction_date);
        mBtnTransactionTime = (Button) findViewById(R.id.in_transaction_time);
        mBtnTransactionCategory = (Button) findViewById(R.id.in_transaction_category);
        mBtnTransactionType = (Button) findViewById(R.id.in_transaction_type);
        
      
        ArrayList<String> list = new ArrayList<String>();
    	list.add("Accounts");
    	list.add("Other");
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    		android.R.layout.simple_spinner_item, list);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	mSpTransactionFrom.setAdapter(adapter);
    	mSpTransactionFrom.setOnItemSelectedListener(
    			new OnItemSelectedListener(){
	    			public void onItemSelected(AdapterView<?> parent, View view, int position,
	    					long id) {
	    				if(position == 0){
	    					mEtTransactionFromOther.setVisibility(View.GONE);
	    					mSpTransactionFromAccount.setVisibility(View.VISIBLE);
	    				}
	    				else{
	    					mSpTransactionFromAccount.setVisibility(View.GONE);
	    					mEtTransactionFromOther.setVisibility(View.VISIBLE);
	    					mEtTransactionFromOther.setText("");
	    					mEtTransactionFromOther.requestFocus();
	    				}
	    				
	    			}
	
	    			@Override
	    			public void onNothingSelected(AdapterView<?> parent) {
	    				// do nothing
	    			}
    			});
    	
    	mSpTransactionTo.setAdapter(adapter);
    	mSpTransactionTo.setOnItemSelectedListener(
    			new OnItemSelectedListener(){
	    			public void onItemSelected(AdapterView<?> parent, View view, int position,
	    					long id) {
	    				if(position == 0){
	    					mEtTransactionToOther.setVisibility(View.GONE);
	    					mSpTransactionToAccount.setVisibility(View.VISIBLE);
	    				}
	    				else{
	    					mSpTransactionToAccount.setVisibility(View.GONE);
	    					mEtTransactionToOther.setVisibility(View.VISIBLE);
	    					mEtTransactionToOther.setText("");
	    					mEtTransactionToOther.requestFocus();
	    				}
	    				
	    			}
	
	    			@Override
	    			public void onNothingSelected(AdapterView<?> parent) {
	    				// do nothing
	    				
	    			}
    			});
    	
    	
    	mAccountsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, new String[] {AccountTable.COLUMN_NAME}, new int[] {android.R.id.text1}, 0);
    	mAccountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	mSpTransactionFromAccount.setAdapter(mAccountsAdapter);
    	mSpTransactionFromAccount.setOnItemSelectedListener(
    			new OnItemSelectedListener(){
	    			public void onItemSelected(AdapterView<?> parent, View view, int position,
	    					long id) {
	    				
	    				Toast.makeText(EditTransactionOLD.this,String.valueOf(id), Toast.LENGTH_LONG).show();
	    			}
	
	    			@Override
	    			public void onNothingSelected(AdapterView<?> parent) {
	    				// do nothing
	    				
	    			}
    			});
    	
    	mSpTransactionToAccount.setAdapter(mAccountsAdapter);
    	mSpTransactionToAccount.setOnItemSelectedListener(
    			new OnItemSelectedListener(){
	    			public void onItemSelected(AdapterView<?> parent, View view, int position,
	    					long id) {
	    				
	    				Toast.makeText(EditTransactionOLD.this,String.valueOf(id), Toast.LENGTH_LONG).show();
	    			}
	
	    			@Override
	    			public void onNothingSelected(AdapterView<?> parent) {
	    				// do nothing
	    				
	    			}
    			});
    	
    	
    	mImgBtnTransactionCamera.setOnClickListener(this);
    	mBtnTransactionDate.setOnClickListener(this);
    	mBtnTransactionTime.setOnClickListener(this);
    	mBtnTransactionCategory.setOnClickListener(this);
    	mBtnTransactionType.setOnClickListener(this);
    	
    	mManager.initLoader(ACCOUNTS_CURSOR, null, this);
    	
    	//upon orientation change stored in instance state, since new splitTransactions are immediately persisted to DB
        if (savedInstanceState != null) {
        	//mCalendar = (Calendar) savedInstanceState.getSerializable("calendar");
        	//setDate();
            //setTime();
        }
    	
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("calendar", mCalendar);
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
        	mImgBtnTransactionCamera.setImageResource(cameraButton);
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
			imageHeight = mImgBtnTransactionCamera.getHeight();
			}
			if(imageWidth == 0){
			imageWidth = mImgBtnTransactionCamera.getWidth();
			}
			
			Toast.makeText(EditTransactionOLD.this,"getHeight = " + imageHeight, Toast.LENGTH_LONG).show();
			
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
		mBtnTransactionDate.setText(mDateFormat.format(mCalendar.getTime()));
	}
	
	/**
	 * sets date on date button
	 */
	private void setTime() {
		mBtnTransactionTime.setText(mTimeFormat.format(mCalendar.getTime()));
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
		      mBtnTransactionCategory.setText(mExpenseCatName);
		      //Toast.makeText(EditTransaction.this,"hello " + mTransactionCatId, Toast.LENGTH_LONG).show();
		
		    }
		}
		else if(requestCode == SELECT_TRANSACTON_CATEGORY_REQUEST){
			
			if (intent != null) {
		      mTransactionCatId = intent.getLongExtra("trans_cat_id",0);
		      mTransactionCatName = intent.getStringExtra("trans_cat_name");
		      mBtnTransactionType.setText(mTransactionCatName);
		      //Toast.makeText(EditTransaction.this,"hello " + mTransactionCatId, Toast.LENGTH_LONG).show();
		     
		    }
		}
		else{
			CameraResultCallback callback = new CameraResultCallback() {
	            @Override
	            public void handleCameraResult(Bitmap bitmap, String imagePath) {
	            	//Toast.makeText(EditTransaction.this,"getHeigth = " + mImgBtnTransactionCamera.getHeight(), Toast.LENGTH_LONG).show();
	            	//Toast.makeText(EditTransaction.this,"getHeigth = " + EditTransaction.imageHeight, Toast.LENGTH_LONG).show();
	            	pendingTransactionImage = bitmap;
	            	mImgBtnTransactionCamera.setMaxHeight(imageHeight);
	                mImgBtnTransactionCamera.setMaxWidth(imageWidth);
	                //mImgBtnTransactionCamera.setMaxHeight(150);
	                //mImgBtnTransactionCamera.setMaxWidth(150);
	            	mImgBtnTransactionCamera.setImageBitmap(pendingTransactionImage);
	                
	            }
	        };
	        
	        
	        if(CameraModule.activityResult(this,
	                requestCode, resultCode, intent, callback)){
	        	Log.i("success","with camera");
	        }
		}
        
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection;
		String[] selectionArgs, projection;
		String sortOrder;
		
		selection = null;
		selectionArgs=null;
		projection = new String[]{
				AccountTable.COLUMN_ID, 
				AccountTable.COLUMN_NAME,
				AccountTable.COLUMN_NUMBER
				};
		
		sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";
		
		CursorLoader cursorLoader = new CursorLoader(this,
				AccountTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		mAccountsAdapter.swapCursor(c);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAccountsAdapter.swapCursor(null);
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		
	}


}
