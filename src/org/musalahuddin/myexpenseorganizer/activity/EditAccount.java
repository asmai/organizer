package org.musalahuddin.myexpenseorganizer.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.util.Utils;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class EditAccount extends FragmentActivity implements View.OnClickListener{
	
	private static final int SELECT_CATEGORY_REQUEST = 1;
	private static final int SELECT_FIELD_REQUEST = 2;
	
	private Long mAccountCatId = 0L;
	private Long mAccountDueDate = 0L;
	private String mAccountCatName; 
	private Calendar mCalendar = Calendar.getInstance();
	
	//fields
	private TextView mAccountNameText; 
	private TextView mAccountNumText;
	private TextView mAccountDescText;
	private TextView mAccountBalText;
	private TextView mAccountLimitText;
	private TextView mAccountPayText;
	private Button mAccountDueButton;
	private Button mAccountCatButton;
	private Button mAddFieldButton;
	
	//rows
	private TableRow mAccountNameRow; 
	private TableRow mAccountNumRow;
	private TableRow mAccountDescRow;
	private TableRow mAccountBalRow;
	private TableRow mAccountLimitRow;
	private TableRow mAccountPayRow;
	private TableRow mAccountDueRow;
	private TableRow mAccountCatRow;
	
	
	static final int DIALOG_DATE = 0;
	static final int DIALOG_FIELDS = 1;
	
	private ArrayList<Field> fields = new ArrayList<Field>();
	private Integer[] fieldIds;
	 
	
	protected class Field{
		int fieldId; 
		String fieldLabel;
		
		public Field(int id, String label){
			this.fieldId = id;
			this.fieldLabel = label;
		}
		
		public int getFieldId(){
			return fieldId;
		}
		
		public String getFieldLabel(){
			return fieldLabel;
		}
	}
	
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
		
		mAccountNameText = (EditText) findViewById(R.id.in_account_name);
		mAccountNumText = (EditText) findViewById(R.id.in_account_number);
	    mAccountDescText= (EditText) findViewById(R.id.in_account_description);
		mAccountBalText= (EditText) findViewById(R.id.in_account_balance);
		mAccountLimitText= (EditText) findViewById(R.id.in_account_limit);
		mAccountPayText= (EditText) findViewById(R.id.in_account_payment);
		mAccountCatButton = (Button) findViewById(R.id.in_account_category);
		mAccountDueButton = (Button) findViewById(R.id.in_account_due);
		mAddFieldButton = (Button) findViewById(R.id.add_field);
		
		
		mAccountNameRow = (TableRow) findViewById(R.id.row_account_name); 
		fields.add(new Field(mAccountNameRow.getId(),getString(R.string.account_name)));
		mAccountNumRow = (TableRow) findViewById(R.id.row_account_number);
		fields.add(new Field(mAccountNumRow.getId(),getString(R.string.account_number)));
	    mAccountDescRow = (TableRow) findViewById(R.id.row_account_description);
	    fields.add(new Field(mAccountDescRow.getId(),getString(R.string.account_description)));
		mAccountBalRow = (TableRow) findViewById(R.id.row_account_balance);
		fields.add(new Field(mAccountBalRow.getId(),getString(R.string.account_balance)));
		mAccountLimitRow = (TableRow) findViewById(R.id.row_account_limit);
		fields.add(new Field(mAccountLimitRow.getId(),getString(R.string.account_limit)));
		mAccountPayRow = (TableRow) findViewById(R.id.row_account_payment);
		fields.add(new Field(mAccountPayRow.getId(),getString(R.string.account_payment)));
		mAccountCatRow = (TableRow) findViewById(R.id.row_account_category);
		fields.add(new Field(mAccountCatRow.getId(),getString(R.string.account_category)));
		mAccountDueRow = (TableRow) findViewById(R.id.row_account_due);
		fields.add(new Field(mAccountDueRow.getId(),getString(R.string.account_due)));
		
		mAccountCatButton.setOnClickListener(this);
		mAccountDueButton.setOnClickListener(this);
		mAddFieldButton.setOnClickListener(this);
		
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
			if(saveState()){
				//Toast.makeText(this, "Success!!", Toast.LENGTH_LONG).show();
				save();
			}
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
			showDialog(DIALOG_DATE);
			break;
		case R.id.add_field:
			selectFields();
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
		case DIALOG_DATE:
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
		switch(requestCode){
		case SELECT_CATEGORY_REQUEST:
			if (intent != null) {
		      mAccountCatId = intent.getLongExtra("cat_id",0);
		      mAccountCatName = intent.getStringExtra("cat_name");
		      mAccountCatButton.setText(mAccountCatName);
		    }
			break;
		case SELECT_FIELD_REQUEST:
			if (intent != null) {
				Bundle b = intent.getExtras();
				displayFields(b);
			}
			break;
		}
		
	}
	
	/**
	 * sets date on date button
	 */
	private void setDate() {
		mAccountDueDate = mCalendar.getTimeInMillis();
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
	
	/*
	 * validate the form
	 */
	protected boolean saveState(){
		String name = mAccountNameText.getText().toString();
		String balance = mAccountBalText.getText().toString();
		String limit = mAccountLimitText.getText().toString();
		String pay = mAccountPayText.getText().toString();
		long catId  = mAccountCatId;
		
		if(catId == 0L){
			Toast.makeText(this, "Please select account category", Toast.LENGTH_LONG).show();
			return false;
		}
		if(name.equals("")){
			Toast.makeText(this, "Please enter account name", Toast.LENGTH_LONG).show();
			return false;
		}
		if(balance.equals(".") || limit.equals(".") || pay.equals(".")){
			Toast.makeText(this, "Invalid number entered", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	/*
	 * save the data
	 */
	protected void save(){
		boolean success;
		String name = mAccountNameText.getText().toString();
		int number = parseInt(mAccountNumText.getText().toString());
		String description = mAccountDescText.getText().toString();
		double balance = parseDouble(mAccountBalText.getText().toString());
		double limit = parseDouble(mAccountLimitText.getText().toString());
		double pay = parseDouble(mAccountPayText.getText().toString());
		long due = mAccountDueDate;
		long catId  = mAccountCatId;
		
		Log.i("credit limit is ",String.valueOf(limit));
		Log.i("due date is ",String.valueOf(due));
		
		success = AccountTable.create(name, number, description, balance, limit, pay, due, catId) != -1;
		
		if(!success){
			Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
		}
		
		finish();
		
	}
	
	protected Double parseDouble(String str){
		double number; 
		try{
			number =  Double.parseDouble(str);
		}catch(NumberFormatException e){
			number = 0; 
		}
		return number;
	}
	
	protected Integer parseInt(String str){
		int number; 
		try{
			number =  Integer.parseInt(str);
		}catch(NumberFormatException e){
			number = 0; 
		}
		return number;
	}
	
	protected Long parseLong(String str){
		long number; 
		try{
			number =  Long.parseLong(str);
		}catch(NumberFormatException e){
			number = 0; 
		}
		return number;
	}
	
	protected void selectFields(){
		
		//TableRow[] fieldRows;
		//String[] fieldLabels;
		
		
		ArrayList<String> tempLables = new ArrayList<String>();
		ArrayList<Integer> tempIds = new ArrayList<Integer>();
		//ArrayList<Boolean> tempChecks = new ArrayList<Boolean>();
		
		TableRow row;
		
		for(Field field: fields){
			row = (TableRow) findViewById(field.getFieldId());
			if(row.getVisibility() == View.GONE){
				//System.out.println(field.getFieldLabel());
				tempIds.add(field.getFieldId());
				tempLables.add(field.getFieldLabel());
			}
		}
		// dynamically sizing arrays
		int size = tempIds.size();
		
		fieldIds = tempIds.toArray(new Integer[size]);
		String[] fieldLabels = tempLables.toArray(new String[size]);
		
		
		/*
		for(String label: fieldLabels){
			System.out.println("label is: " + label);
		}
		
		for(boolean check: fieldChecks){
			System.out.println("check is: " + check);
		}
		
		
		showDialog(DIALOG_FIELDS);
		*/
		
		Bundle b = new Bundle();
		b.putStringArray("fieldLabels",fieldLabels);
		Intent i = new Intent(this,AddField.class);
		i.putExtras(b);
	    //startActivity(i);
	    startActivityForResult(i,SELECT_FIELD_REQUEST);
		
	}
	
	
	protected void displayFields(Bundle bundle){
		boolean[] fieldChecks = bundle.getBooleanArray("fieldChecks");
		TableRow row; 
		for(int i=0; i<fieldChecks.length; i++){
			//System.out.println("check is: " + fieldChecks[i] );
			if(fieldChecks[i]){
				row = (TableRow) findViewById(fieldIds[i]);
				row.setVisibility(View.VISIBLE);
			}
		}
	}
	


}
