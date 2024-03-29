package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.TransactionCategoryTable;
import org.musalahuddin.myexpenseorganizer.dialog.EditTextDialog;
import org.musalahuddin.myexpenseorganizer.dialog.EditTextDialog.EditTextDialogListener;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectTransactionCategory extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,EditTextDialogListener,AdapterView.OnItemClickListener {

	private LoaderManager mManager;
	private SimpleCursorAdapter mAdapter;
	
	/**
     * edit the category label
     */
    private static final int EDIT_CAT = Menu.FIRST+1;
    /**
     * delete the category after checking if
     * there are mapped transactions or subcategories
     */
    private static final int DELETE_CAT = Menu.FIRST+2;
    
    boolean mManageOnly;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.transaction_categories_list);
		Intent intent = getIntent();
		String action = intent.getAction();
        mManageOnly = action != null && action.equals("myexpenseorganizer.intent.manage.transactioncategories");
		
		setTitle(R.string.pref_manage_transaction_categories_title);
		setTitle(mManageOnly ? R.string.pref_manage_transaction_categories_title : R.string.select_transaction_category);
		
		ListView lv = (ListView) findViewById(R.id.list);
		
		// Create an array to specify the fields we want to display in the list
	    String[] from = new String[]{TransactionCategoryTable.COLUMN_NAME};

	    // and an array of the fields we want to bind those fields to 
	    int[] to = new int[]{android.R.id.text1};
	    
	   // Now create a simple cursor adapter and set it to display
	    mAdapter = new SimpleCursorAdapter(MyApplication.getInstance(), 
	            android.R.layout.simple_list_item_1, null, from, to,0);
		
		mManager = getLoaderManager();
		
		mManager.initLoader(0, null, this);
		
		lv.setAdapter(mAdapter);
	    lv.setEmptyView(findViewById(R.id.empty));
	    lv.setOnItemClickListener(this);
	    registerForContextMenu(lv);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

        menu.add(0,DELETE_CAT,0,R.string.menu_delete);
        menu.add(0,EDIT_CAT,0,R.string.menu_edit_account_cat);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long cat_id = info.id;
		String name =  ((TextView) info.targetView).getText().toString();
		
		switch(item.getItemId()) {
		
		case EDIT_CAT:
			
			editCat(name,cat_id);
			
			return true;
			
		case DELETE_CAT:
			
			if(!TransactionCategoryTable.delete(cat_id))
				Toast.makeText(this,"This category cannot be deleted", Toast.LENGTH_LONG).show();
			
			
			return true;
		}
		
		return false;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.categories, menu);
      super.onCreateOptionsMenu(menu);
      return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.CREATE_COMMAND:
			createCat();
		}
		return false;
	}
	
	/**
     * presents AlertDialog for adding a new category
     * if label is already used, shows an error
     */
    public void createCat() {
	    Bundle args = new Bundle();
	    int dialogTitle;
	    
	    dialogTitle = R.string.menu_create_transaction_cat;
	    args.putString("dialogTitle", getString(dialogTitle));
	    EditTextDialog.newInstance(args).show(getSupportFragmentManager(), "CREATE_CATEGORY");
	}
    
    /**
     * presents AlertDialog for editing an existing category
     * if label is already used, shows an error
     * @param label
     * @param catId
     */
    public void editCat(String name, Long catId) {
        Bundle args = new Bundle();
        args.putLong("catId", catId);
        args.putString("dialogTitle", getString(R.string.menu_edit_transaction_cat));
        args.putString("value",name);
        EditTextDialog.newInstance(args).show(getSupportFragmentManager(), "EDIT_CATEGORY");
    }
	
    @Override
	public void onFinishEditDialog(Bundle args) {
		Long catId;
		boolean success;
	    String value = args.getString("result");
	    catId = args.getLong("catId");
	   
	    if(catId != 0L){
	    	
		    success = TransactionCategoryTable.update(value,catId) != -1;
		    
	    }
	    else{
	    	
		    success = TransactionCategoryTable.create(value) != -1;
		    
	    }
	    
	    if (!success) {
	    	Toast.makeText(SelectTransactionCategory.this,"Category already exists", Toast.LENGTH_LONG).show();
	    }
		
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		String selection;
		String[] selectionArgs, projection;
		String sortOrder;
		
		selection = null;
		selectionArgs=null;
		projection = new String[]{TransactionCategoryTable.COLUMN_ID, TransactionCategoryTable.COLUMN_NAME};
		sortOrder = "LOWER("+TransactionCategoryTable.COLUMN_NAME+")" + " ASC ";
		
		CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
		        TransactionCategoryTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
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
	
	/**
     * Callback from button
     * @param v
     */
    public void importCats(View v) {
      //Intent i = new Intent(this, CatImport.class);
      Intent i = new Intent("myexpenseorganizer.intent.import.transactioncategories");
      startActivity(i);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mManageOnly)
		     return;
		Intent intent=new Intent();
		String name =   ((TextView) view).getText().toString();
		intent.putExtra("trans_cat_id",id);
		intent.putExtra("trans_cat_name", name);
		setResult(RESULT_OK,intent);
		finish();
		
	}

		
}
