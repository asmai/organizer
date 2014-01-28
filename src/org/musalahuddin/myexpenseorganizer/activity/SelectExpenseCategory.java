package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.ExpenseChildCategory;
import org.musalahuddin.myexpenseorganizer.database.ExpenseParentCategory;
import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;


//public class SelectExpenseCategory extends ExpandableListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
public class SelectExpenseCategory extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	private LoaderManager mManager;
	private MyExpandableListAdapter mAdapter;
	
	/**
     * create a new sub category
     */
    private static final int CREATE_SUB_CAT = Menu.FIRST+2;
    /**
     * return the main cat to the calling activity
     */
    private static final int SELECT_MAIN_CAT = Menu.FIRST+1;
    /**
     * edit the category label
     */
    private static final int EDIT_CAT = Menu.FIRST+3;
    /**
     * delete the category after checking if
     * there are mapped transactions or subcategories
     */
    private static final int DELETE_CAT = Menu.FIRST+4;
	
	public class MyExpandableListAdapter extends SimpleCursorTreeAdapter{

		public MyExpandableListAdapter(Context context, Cursor cursor,
				int groupLayout, String[] groupFrom, int[] groupTo,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
					childTo);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			// TODO Auto-generated method stub
			
			// Given the group, we return a cursor for all the children within that group
		    long expenseParentCategoryId = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(ExpenseParentCategory.COLUMN_ID));
		    Bundle bundle = new Bundle();
		    bundle.putLong("parentId", expenseParentCategoryId);
		    
		    int groupPos = groupCursor.getPosition();
		    if (mManager.getLoader(groupPos) != null && !mManager.getLoader(groupPos).isReset()) {
		    	try {
		            mManager.restartLoader(groupPos, bundle, SelectExpenseCategory.this);
		        }catch (NullPointerException e) {
		            // a NPE is thrown in the following scenario:
		            //1)open a group
		            //2)orientation change
		            //3)open the same group again
		            //in this scenario getChildrenCursor is called twice, second time leads to error
		            //maybe it is trying to close the group that had been kept open before the orientation change
		            e.printStackTrace();
		        }
		    }else {
		        mManager.initLoader(groupPos, bundle, SelectExpenseCategory.this);
		    }
		    return null;
		}

	
	}
	

	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories_list);
		
		mManager = getLoaderManager();
	    mManager.initLoader(-1, null, this);
	    
	    mAdapter = new MyExpandableListAdapter(
	    		MyApplication.getInstance(),
	    		null,
	    		android.R.layout.simple_expandable_list_item_1,
	    		new String[]{ExpenseParentCategory.COLUMN_NAME},
	    		new int[] {android.R.id.text1},
	    		android.R.layout.simple_expandable_list_item_1,
	    		new String[]{ExpenseChildCategory.COLUMN_NAME},
	    		new int[] {android.R.id.text1});
	    setListAdapter(mAdapter);
	    registerForContextMenu(getExpandableListView());
	    
	}
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories_list2);
		
		ExpandableListView lv = (ExpandableListView) findViewById(R.id.list);
		
		mManager = getLoaderManager();
	    mManager.initLoader(-1, null, this);
	    
	    mAdapter = new MyExpandableListAdapter(
	    		MyApplication.getInstance(),
	    		null,
	    		android.R.layout.simple_expandable_list_item_1,
	    		new String[]{ExpenseParentCategory.COLUMN_NAME},
	    		new int[] {android.R.id.text1},
	    		android.R.layout.simple_expandable_list_item_1,
	    		new String[]{ExpenseChildCategory.COLUMN_NAME},
	    		new int[] {android.R.id.text1});
	    lv.setAdapter(mAdapter);
	    lv.setEmptyView(findViewById(R.id.empty));
	    registerForContextMenu(lv);
	    
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        // Menu entries relevant only for the group
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
        	 menu.add(0,CREATE_SUB_CAT,0,R.string.menu_create_sub_cat);
        }
        menu.add(0,DELETE_CAT,0,R.string.menu_delete);
        menu.add(0,EDIT_CAT,0,R.string.menu_edit_cat);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		long cat_id = info.id;
		
		Log.i("onCentexItemSelected","cat id is : " + cat_id);
		
		switch(item.getItemId()) {
		case CREATE_SUB_CAT:
			Toast.makeText(this,"create sub category", Toast.LENGTH_LONG).show();
			return true;
			
		case EDIT_CAT:
			Toast.makeText(this,"edit categories", Toast.LENGTH_LONG).show();
			return true;
			
		case DELETE_CAT:
			if(type==ExpandableListView.PACKED_POSITION_TYPE_GROUP){
				if(ExpenseChildCategory.countSub(cat_id) > 0){
					//can't delete parent category if has any subcategories
					Toast.makeText(this,"Cannot be deleted, because it has subcategories", Toast.LENGTH_LONG).show();
				}
				else{
					//delete parent 
					ExpenseParentCategory.delete(cat_id);
				}
			}
			else{
				// delete child
				ExpenseChildCategory.delete(cat_id);
				
			}
			return true;
		}
		
		return false;
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		// TODO Auto-generated method stub
		long parentId;
		String selection;
		String[] selectionArgs, projection;
		
		if(bundle == null){
			selection = null;
			selectionArgs=null;
			projection = new String[]{ExpenseParentCategory.COLUMN_ID, ExpenseParentCategory.COLUMN_NAME};
			
			return new CursorLoader(MyApplication.getInstance(),MyExpenseOrganizerProvider.EXPENSE_PARENT_CATEGORIES_URI, projection,
			        selection,selectionArgs, null);
		}
		else {
			parentId = bundle.getLong("parentId");
			selection = "expense_parent_category_id = ?";
			selectionArgs = new String[]{String.valueOf(parentId)};
			projection = new String[]{ExpenseChildCategory.COLUMN_ID, ExpenseChildCategory.COLUMN_NAME};
			
			return new CursorLoader(MyApplication.getInstance(),MyExpenseOrganizerProvider.EXPENSE_CHILD_CATEGORIES_URI, projection,
			        selection,selectionArgs, null);
	    }
		
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		int id = loader.getId();
		if(id == -1){
			mAdapter.setGroupCursor(data);
		}
		else{
			//check if group still exists
		     if (mAdapter.getGroupId(id) != 0){
		    	 System.out.println("group id is selected: " + id);
		    	 mAdapter.setChildrenCursor(id, data);
		     }
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		int id = loader.getId();
		if(id != -1){
			// child cursor
	        try {
	            mAdapter.setChildrenCursor(id, null);
	        } catch (NullPointerException e) {
	            Log.w("TAG", "Adapter expired, try again on the next query: "
	                    + e.getMessage());
	        }
		
		}else{
			mAdapter.setGroupCursor(null);
		}
		
	}
	
	/**
     * Callback from button
     * @param v
     */
    public void importCats(View v) {
      Intent i = new Intent(this, CatImport.class);
      startActivity(i);
    }
	
	
}