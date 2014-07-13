package org.musalahuddin.myexpenseorganizer.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionAccountTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.TransactionView;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class SelectTransaction extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private LoaderManager mManager;
	private SimpleCursorAdapter mAdapter;
	
	
	private static final int EDIT_TRANSACTION = Menu.FIRST+1;
    
    private static final int DELETE_TRANSACTION = Menu.FIRST+2;
	
	private long mAccountId = 0L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transactions_list);
		
		Bundle extras = getIntent().getExtras();
		mAccountId = extras.getLong("accountId");
		String title = extras.getString("title");
		
		setTitle(title);
		
		ListView lv = (ListView) findViewById(R.id.list);
		
		// Create an array to specify the fields we want to display in the list
	    String[] from = new String[]{TransactionView.COLUMN_TRANSACTION_CATEGORY_NAME};

	    // and an array of the fields we want to bind those fields to 
	    int[] to = new int[]{R.id.transaction_type};
	    //int[] to = new int[]{android.R.id.text1};
		
		// Now create a simple cursor adapter and set it to display
	    mAdapter = new SimpleCursorAdapter(MyApplication.getInstance(),R.layout.transaction_row,null,from,to,0){

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View row = super.getView(position, convertView, parent);
				Cursor c = getCursor();
				c.moveToPosition(position);
				
				DecimalFormat f = new DecimalFormat("0.00");
				
				//get cursor values
				String expense_category_parent_name = c.getString(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME));
				String expense_category_child_name = c.getString(c.getColumnIndex(TransactionView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME));
				long transaction_date = c.getLong(c.getColumnIndex(TransactionView.COLUMN_TRANSACTION_DATE));
				long seondary_account_id =  c.getLong(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_ID));
				String seondary_account_name =  c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_NAME));
				String seondary_account_description =  c.getString(c.getColumnIndex(TransactionView.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION));
				double transaction_amount = c.getDouble(c.getColumnIndex(TransactionView.COLUMN_AMOUNT))/100;
				int is_deposit = c.getInt(c.getColumnIndex(TransactionView.COLUMN_IS_DEPOSIT));
				
				//get views
				TextView tvTransactionAccount = (TextView) row.findViewById(R.id.transaction_account);
				TextView tvTransactionCategory = (TextView) row.findViewById(R.id.transaction_category);
				TextView tvTransactionDate = (TextView) row.findViewById(R.id.transaction_date);
				TextView tvTransactionAmount = (TextView) row.findViewById(R.id.transaction_amount);
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				
				cal.setTimeInMillis(transaction_date);
				
				// set account
				if(seondary_account_id == 1L)
				tvTransactionAccount.setText(seondary_account_description);
				else
				tvTransactionAccount.setText(seondary_account_name);
				
				//set category
				tvTransactionCategory.setText(expense_category_parent_name + " : " + expense_category_child_name);
				
				// set time
				tvTransactionDate.setText(sdf.format(cal.getTime()));
				
				//set amount
				if(is_deposit==1)
					tvTransactionAmount.setText("$"+f.format(Math.abs(transaction_amount)));
				else
					tvTransactionAmount.setText("-$"+f.format(Math.abs(transaction_amount)));
				
	
				return row;
			}
		
	    	
	    };
	    
	    mManager = getLoaderManager();
	    
	    mManager.initLoader(0, null, this);
	    lv.setAdapter(mAdapter);
	    registerForContextMenu(lv);
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		 menu.add(0,DELETE_TRANSACTION,0,R.string.menu_delete);
	     menu.add(0,EDIT_TRANSACTION,0,R.string.menu_edit_transaction);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long transaction_id = info.id;
		int position = info.position;
		
		switch(item.getItemId()) {
		case EDIT_TRANSACTION:
			Cursor cursor = mAdapter.getCursor();
			cursor.moveToPosition(position);
			
			editTransaction(cursor);
			return true;
		case DELETE_TRANSACTION:
			return true;
		}
		
		return false;
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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String selection;
		String[] selectionArgs, projection;
		String sortOrder;
		
		selection = TransactionView.COLUMN_PRIMARY_ACCOUNT_ID+"=?";
		selectionArgs = new String[]{String.valueOf(mAccountId)};
		projection = new String[]{
				TransactionView.COLUMN_ID, 
				TransactionView.COLUMN_TRANSACTION_CATEGORY_NAME,
				TransactionView.COLUMN_SECONDARY_ACCOUNT_ID,
				TransactionView.COLUMN_SECONDARY_ACCOUNT_NAME,
				TransactionView.COLUMN_SECONDARY_ACCOUNT_DESCRIPTION,
				TransactionView.COLUMN_AMOUNT,
				TransactionView.COLUMN_IS_DEPOSIT,
				TransactionView.COLUMN_EXPENSE_CATEGORY_PARENT_NAME,
				TransactionView.COLUMN_EXPENSE_CATEGORY_CHILD_NAME,
				TransactionView.COLUMN_TRANSACTION_DATE
				};
		
		sortOrder = "LOWER("+TransactionView.COLUMN_TRANSACTION_DATE+")" + " DESC ";
		
		CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
				TransactionView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
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
	
	public void editTransaction(Cursor c){
		
	}

	
}
