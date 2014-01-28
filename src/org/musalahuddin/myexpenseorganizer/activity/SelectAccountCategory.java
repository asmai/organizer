package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountCategoryTable;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TextView;

public class SelectAccountCategory extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,EditTextDialogListener {

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.account_categories_list);
		
		setTitle(R.string.pref_manage_account_categories_title);
		
		ListView lv = (ListView) findViewById(R.id.list);
		
		// Create an array to specify the fields we want to display in the list
	    String[] from = new String[]{AccountCategoryTable.COLUMN_NAME};

	    // and an array of the fields we want to bind those fields to 
	    int[] to = new int[]{android.R.id.text1};
	    
	   // Now create a simple cursor adapter and set it to display
	    mAdapter = new SimpleCursorAdapter(MyApplication.getInstance(), 
	            android.R.layout.simple_list_item_1, null, from, to,0);
		
		mManager = getLoaderManager();
		
		mManager.initLoader(0, null, this);
		
		lv.setAdapter(mAdapter);
	    lv.setEmptyView(findViewById(R.id.empty));
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
			
			if(!AccountCategoryTable.delete(cat_id))
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
	    
	    dialogTitle = R.string.menu_create_account_cat;
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
        args.putString("dialogTitle", getString(R.string.menu_edit_account_cat));
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
	    	
		    success = AccountCategoryTable.update(value,catId) != -1;
		    
	    }
	    else{
	    	
		    success = AccountCategoryTable.create(value) != -1;
		    
	    }
	    
	    if (!success) {
	    	Toast.makeText(SelectAccountCategory.this,"Category already exists", Toast.LENGTH_LONG).show();
	    }
		
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		String selection;
		String[] selectionArgs, projection;
		String sortOrder;
		
		selection = null;
		selectionArgs=null;
		projection = new String[]{AccountCategoryTable.COLUMN_ID, AccountCategoryTable.COLUMN_NAME};
		sortOrder = "LOWER("+AccountCategoryTable.COLUMN_NAME+")" + " ASC ";
		
		CursorLoader cursorLoader = new CursorLoader(MyApplication.getInstance(),
		        AccountCategoryTable.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
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
      Intent i = new Intent("myexpenseorganizer.intent.import.accountcategories");
      startActivity(i);
    }

		
}
