package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.ExpenseChildCategory;
import org.musalahuddin.myexpenseorganizer.database.ExpenseParentCategory;
import org.musalahuddin.myexpenseorganizer.dialog.EditTextDialog;
import org.musalahuddin.myexpenseorganizer.dialog.EditTextDialog.EditTextDialogListener;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class SelectCategory extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,EditTextDialogListener,OnChildClickListener, OnGroupClickListener {

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
    
    boolean mManageOnly;
	
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
		            mManager.restartLoader(groupPos, bundle, SelectCategory.this);
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
		        mManager.initLoader(groupPos, bundle, SelectCategory.this);
		    }
		    return null;
		}

	
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState == null){
			Log.i("SelectCategory", "onCreate init");
		}
		setContentView(R.layout.categories_list2);
		Intent intent = getIntent();
		String action = intent.getAction();
        mManageOnly = action != null && action.equals("myexpenseorganizer.intent.manage.expensecategories");
	
		setTitle(mManageOnly ? R.string.pref_manage_expense_categories_title : R.string.select_expense_category);
		
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
	    lv.setOnChildClickListener(this);
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
			createCat(null);
		}
		return false;
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		long cat_id = info.id;
		String name =   ((TextView) info.targetView).getText().toString();
		
		Log.i("onCentexItemSelected","cat id is : " + cat_id + " and parent id : " + mAdapter.getGroupId(ExpandableListView.getPackedPositionGroup(info.packedPosition)));
		
		switch(item.getItemId()) {
		case CREATE_SUB_CAT:
			//Toast.makeText(this,"create sub category", Toast.LENGTH_LONG).show();
			createCat(cat_id);
			return true;
			
		case EDIT_CAT:
			//Toast.makeText(this,"edit categories", Toast.LENGTH_LONG).show();
			if(type==ExpandableListView.PACKED_POSITION_TYPE_CHILD){
				int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
				long parent_id = mAdapter.getGroupId(groupPos);
				editCat(name,cat_id,parent_id);
			}
			else{
				editCat(name,cat_id,null);
			}
			return true;
			
		case DELETE_CAT:
			if(type==ExpandableListView.PACKED_POSITION_TYPE_GROUP){
				//if(ExpenseChildCategory.countSub(cat_id) > 0){
					//can't delete parent category if has any subcategories
					//Toast.makeText(this,"Cannot be deleted, because it has subcategories", Toast.LENGTH_LONG).show();
				//}
				//else{
					//delete parent 
					if(!ExpenseParentCategory.delete(cat_id))
						Toast.makeText(this,"This category cannot be deleted", Toast.LENGTH_LONG).show();
				//}
			}
			else if(type==ExpandableListView.PACKED_POSITION_TYPE_CHILD){
				// delete child
				if(!ExpenseChildCategory.delete(cat_id))
					Toast.makeText(this,"This category cannot be deleted", Toast.LENGTH_LONG).show();
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
		String sortOrder;
		
		if(bundle == null){
			selection = null;
			selectionArgs=null;
			projection = new String[]{ExpenseParentCategory.COLUMN_ID, ExpenseParentCategory.COLUMN_NAME};
			sortOrder = "LOWER("+ExpenseParentCategory.COLUMN_NAME+")" + " ASC ";
			return new CursorLoader(MyApplication.getInstance(),MyExpenseOrganizerProvider.EXPENSE_PARENT_CATEGORIES_URI, projection,
			        selection,selectionArgs, sortOrder);
		}
		else {
			parentId = bundle.getLong("parentId");
			selection = "expense_parent_category_id = ?";
			selectionArgs = new String[]{String.valueOf(parentId)};
			projection = new String[]{ExpenseChildCategory.COLUMN_ID, ExpenseChildCategory.COLUMN_NAME};
			sortOrder = "LOWER("+ExpenseChildCategory.COLUMN_NAME+")" + " ASC ";
			
			return new CursorLoader(MyApplication.getInstance(),MyExpenseOrganizerProvider.EXPENSE_CHILD_CATEGORIES_URI, projection,
			        selection,selectionArgs, sortOrder);
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
      //Intent i = new Intent(this, CatImport.class);
      Intent i = new Intent("myexpenseorganizer.intent.import.expensecategories");
      startActivity(i);
    }
    
    /**
     * presents AlertDialog for adding a new category
     * if label is already used, shows an error
     * @param parent_id
     */
    public void createCat(Long parentId) {
	    Bundle args = new Bundle();
	    int dialogTitle;
	    if (parentId != null) {
	      args.putLong("parentId", parentId);
	      dialogTitle = R.string.menu_create_sub_cat;
	    } else
	      dialogTitle = R.string.menu_create_main_cat;
	    args.putString("dialogTitle", getString(dialogTitle));
	    EditTextDialog.newInstance(args).show(getSupportFragmentManager(), "CREATE_CATEGORY");
	}
    
    /**
     * presents AlertDialog for editing an existing category
     * if label is already used, shows an error
     * @param label
     * @param catId
     * @param parentId
     */
    public void editCat(String name, Long catId, Long parentId) {
        Bundle args = new Bundle();
        if (parentId != null) {
  	      args.putLong("parentId", parentId);
  	    }
        args.putLong("catId", catId);
        args.putString("dialogTitle", getString(R.string.menu_edit_cat));
        args.putString("value",name);
        EditTextDialog.newInstance(args).show(getSupportFragmentManager(), "EDIT_CATEGORY");
    }


	@Override
	public void onFinishEditDialog(Bundle args) {
		// TODO Auto-generated method stub
		//System.out.println(args.toString());
		Long catId,parentId;
		boolean success;
	    String value = args.getString("result");
	    catId = args.getLong("catId");
	    parentId = args.getLong("parentId");
	    if(catId != 0L){
	    	if (parentId == 0L){
		    	success = ExpenseParentCategory.update(value,catId) != -1;
		    }
		    else{
		    	success = ExpenseChildCategory.update(value, catId, parentId) != -1;
		    }
	    }
	    else{
	    	if (parentId == 0L){
		    	success = ExpenseParentCategory.create(value) != -1;
		    }
		    else{
		    	success = ExpenseChildCategory.create(value, parentId) != -1;
		    }
	    }
	    
	    if (!success) {
	    	Toast.makeText(SelectCategory.this,"Category already exists", Toast.LENGTH_LONG).show();
	    }
	
	}


	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		
		return true;
	}


	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		//(CharSequence) mAdapter.getGroup(groupPosition)
		
		if (mManageOnly)
		     return false;
		
		//group information
		View groupView = mAdapter.getGroupView(groupPosition, true, null, parent);
		String groupName = ((TextView) groupView).getText().toString();
		
		//child information
		String childName = ((TextView) v).getText().toString();
		
		//Toast.makeText(SelectCategory.this,"hello " + String.valueOf(mAdapter.getGroupId(groupPosition)), Toast.LENGTH_LONG).show();
		//Toast.makeText(SelectCategory.this,"hello " + groupName, Toast.LENGTH_LONG).show();
		
		Intent intent=new Intent();
		intent.putExtra("exp_cat_id",id);
		intent.putExtra("exp_cat_name", groupName+" : "+childName);
		setResult(RESULT_OK,intent);
		finish();
		
		return true;
	}
	
}