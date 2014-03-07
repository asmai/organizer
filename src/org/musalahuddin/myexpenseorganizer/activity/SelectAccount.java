package org.musalahuddin.myexpenseorganizer.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.AccountView;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectAccount extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener{

	private LoaderManager mManager;
	private SimpleCursorAdapter mAdapter;
	
	/**
     * edit the category label
     */
    private static final int EDIT_ACCOUNT = Menu.FIRST+1;
    /**
     * delete the category after checking if
     * there are mapped transactions or subcategories
     */
    private static final int DELETE_ACCOUNT = Menu.FIRST+2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts_list);
		setTitle(R.string.account_overview_title);
		
		ListView lv = (ListView) findViewById(R.id.list);
		
		
		// Create an array to specify the fields we want to display in the list
	    String[] from = new String[]{AccountTable.COLUMN_ACCOUNT_CATEGORY_NAME};

	    // and an array of the fields we want to bind those fields to 
	    int[] to = new int[]{R.id.account_category};
	    //int[] to = new int[]{android.R.id.text1};
	    
	   
	    // Now create a simple cursor adapter and set it to display
	    mAdapter = new SimpleCursorAdapter(MyApplication.getInstance(),R.layout.account_row,null,from,to,0){

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View row = super.getView(position, convertView, parent);
				Cursor c = getCursor();
				c.moveToPosition(position);
				
				DecimalFormat f = new DecimalFormat("0.00");
				
				//get cursor values
				String name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
				int number = c.getInt(c.getColumnIndex(AccountTable.COLUMN_NUMBER));
				double balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_INIT_BALANCE))/100;
				long due = c.getLong(c.getColumnIndex(AccountTable.COLUMN_DUE_DATE));
				double payment = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_MONTHLY_PAYMENT))/100;
				double limit = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_CREDIT_LIMIT))/100;
				
				
				//get views
				TextView accountName = (TextView) row.findViewById(R.id.account_name);
				TextView accountBalance = (TextView) row.findViewById(R.id.account_balance);
				TextView accountPayment = (TextView) row.findViewById(R.id.account_payment);
				TextView accountLimit = (TextView) row.findViewById(R.id.account_limit);
				TextView accountDue = (TextView) row.findViewById(R.id.account_due);
				
				//set name
				accountName.setText(name);
				if(number > 0){
					accountName.setText(name+"(...."+number+")");
				}
				
				//set balance
				accountBalance.setText("$"+f.format(Math.abs(balance)));
				if(balance < 0){
					accountBalance.setTextColor(Color.RED);
				}
				else if(balance > 0){
					accountBalance.setTextColor(Color.GREEN);
				}
				
				//set payment
				accountPayment.setText("$"+f.format(Math.abs(payment)));
				
				//set limit
				accountLimit.setText("$"+f.format(Math.abs(limit)));
				
				//set due date
				if(due > 0L){
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					
					Calendar currCalendar = Calendar.getInstance();
					currCalendar.setTimeInMillis(System.currentTimeMillis());
					
					Calendar dueCalendar = Calendar.getInstance();
					dueCalendar.setTimeInMillis(due);
					
					if(dueCalendar.getTimeInMillis() >= currCalendar.getTimeInMillis()){
						accountDue.setText(sdf.format(dueCalendar.getTime()));
					}
					else{
						Calendar currCal = Calendar.getInstance();
						currCal.set(currCalendar.get(currCalendar.YEAR),currCalendar.get(currCalendar.MONTH),currCalendar.get(currCalendar.DAY_OF_MONTH));
						
						Calendar dueCal = Calendar.getInstance();
						if(dueCalendar.get(dueCalendar.DAY_OF_MONTH) > currCalendar.getActualMaximum(currCalendar.DAY_OF_MONTH)){
							dueCal.set(currCalendar.get(currCalendar.YEAR),currCalendar.get(currCalendar.MONTH),currCalendar.get(currCalendar.DAY_OF_MONTH));		
						}
						else{
							dueCal.set(currCalendar.get(currCalendar.YEAR),currCalendar.get(currCalendar.MONTH),dueCalendar.get(dueCalendar.DAY_OF_MONTH));
						}
						
						if(dueCal.getTimeInMillis() < currCal.getTimeInMillis()){
							dueCal.add(Calendar.MONTH, 1);
						}
						accountDue.setText(sdf.format(dueCal.getTime()));
						
						//accountDue.setText(String.valueOf(due));
					}
				}
				
				
				return row;
			}
	    	
	    };
	    
	    /*
	    mAdapter = new SimpleCursorAdapter(MyApplication.getInstance(), 
	            android.R.layout.simple_list_item_1, null, from, to,0);
	    */
	    
	    mManager = getLoaderManager();
	    
	    mManager.initLoader(0, null, this);
	    lv.setAdapter(mAdapter);
	    lv.setOnItemClickListener(this);
	    registerForContextMenu(lv);
	    
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		 menu.add(0,DELETE_ACCOUNT,0,R.string.menu_delete);
	     menu.add(0,EDIT_ACCOUNT,0,R.string.menu_edit_account);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long account_id = info.id;
		int position = info.position;
		
		switch(item.getItemId()) {
		case EDIT_ACCOUNT:
			
			//View account_view = info.targetView;
			
			Cursor cursor = mAdapter.getCursor();
			cursor.moveToPosition(position);
			
			//TextView accountName = (TextView) account_view.findViewById(R.id.account_name);
			
			//Toast.makeText(SelectAccount.this, "Edit "+accountName.getText().toString() , Toast.LENGTH_SHORT).show();
			//Toast.makeText(SelectAccount.this, "Edit "+cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME)) , Toast.LENGTH_SHORT).show();
			editAccount(cursor);
			
			return true;
			
		case DELETE_ACCOUNT:
			
			
			//Toast.makeText(SelectAccount.this, "Delete" , Toast.LENGTH_SHORT).show();
			if(!AccountTable.delete(account_id))
				Toast.makeText(this,"This Account cannot be deleted", Toast.LENGTH_LONG).show();
			
			
			return true;
		}
		
		return false;
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
			/*
			i = new Intent(this, EditAccount.class);
			startActivityForResult(i, 0);
			*/
			i = new Intent("myexpenseorganizer.intent.add.accounts");
			startActivity(i);
			 
			break;
		}
		
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String selection;
		String[] selectionArgs, projection;
		String sortOrder;
		
		selection = null;
		selectionArgs=null;
		projection = new String[]{
				AccountTable.COLUMN_ID, 
				AccountTable.COLUMN_NAME,
				AccountTable.COLUMN_DESCRIPTION,
				AccountTable.COLUMN_ACCOUNT_CATEGORY_NAME, 
				AccountTable.COLUMN_NUMBER, 
				AccountTable.COLUMN_INIT_BALANCE,
				AccountTable.COLUMN_MONTHLY_PAYMENT,
				AccountTable.COLUMN_CREDIT_LIMIT,
				AccountTable.COLUMN_DUE_DATE};
		
		sortOrder = "LOWER("+AccountTable.COLUMN_NAME+")" + " ASC ";
		
		CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
				AccountView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
		    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		mAdapter.swapCursor(c);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> c) {
		mAdapter.swapCursor(null);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView accountName = (TextView) view.findViewById(R.id.account_name);
		String name = accountName.getText().toString();
		Toast.makeText(SelectAccount.this, "You have chosen: " + " " + name , Toast.LENGTH_SHORT).show();
	}
	
	//public void editAccount(View view, Long accountId){
	public void editAccount(Cursor c){
		
		long accountId = c.getLong(c.getColumnIndex(AccountTable.COLUMN_ID));
		String name = c.getString(c.getColumnIndex(AccountTable.COLUMN_NAME));
		int number = c.getInt(c.getColumnIndex(AccountTable.COLUMN_NUMBER));
		double balance = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_INIT_BALANCE))/100;
		long due = c.getLong(c.getColumnIndex(AccountTable.COLUMN_DUE_DATE));
		double payment = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_MONTHLY_PAYMENT))/100;
		double limit = c.getDouble(c.getColumnIndex(AccountTable.COLUMN_CREDIT_LIMIT))/100;
		String description = c.getString(c.getColumnIndex(AccountTable.COLUMN_DESCRIPTION));
		String categoryName = c.getString(c.getColumnIndex(AccountTable.COLUMN_ACCOUNT_CATEGORY_NAME));
		
		Bundle b = new Bundle();
		b.putLong(AccountTable.COLUMN_ID,accountId);
		b.putString(AccountTable.COLUMN_NAME,name);
		b.putInt(AccountTable.COLUMN_NUMBER,number);
		b.putString(AccountTable.COLUMN_DESCRIPTION,description);
		b.putDouble(AccountTable.COLUMN_INIT_BALANCE,balance);
		b.putDouble(AccountTable.COLUMN_DUE_DATE,due);
		b.putDouble(AccountTable.COLUMN_MONTHLY_PAYMENT,payment);
		b.putDouble(AccountTable.COLUMN_CREDIT_LIMIT, limit);
		b.putString(AccountTable.COLUMN_ACCOUNT_CATEGORY_NAME,categoryName);
		b.putLong(AccountTable.COLUMN_DUE_DATE,due);
		
		Intent i = new Intent(this,EditAccount.class);
		i.putExtras(b);
		startActivity(i);
	}
}
