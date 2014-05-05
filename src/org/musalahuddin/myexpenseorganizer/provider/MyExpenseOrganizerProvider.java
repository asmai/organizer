package org.musalahuddin.myexpenseorganizer.provider;

import org.musalahuddin.myexpenseorganizer.database.*;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MyExpenseOrganizerProvider extends ContentProvider{

	// database helper
	public static MyExpenseOrganizerDatabaseHelper dbhelper;
	
	private static final boolean debug = false;
	static final String TAG = "MyExpenseOrganizerProvider";
	
	// Used for the UriMatcher
	private static final UriMatcher URI_MATCHER;
	private static final int EXPENSE_PARENT_CATEGORIES = 1;
	private static final int EXPENSE_PARENT_CATEGORIES_ID = 2;
	private static final int EXPENSE_CHILD_CATEGORIES = 3;
	private static final int EXPENSE_CHILD_CATEGORIES_ID = 4;
	private static final int ACCOUNT_CATEGORIES = 5;
	private static final int ACCOUNT_CATEGORIES_ID = 6;
	private static final int TRANSACTION_CATEGORIES = 7;
	private static final int TRANSACTION_CATEGORIES_ID = 8;
	private static final int ACCOUNTS = 9;
	private static final int ACCOUNTS_ID = 10;
	private static final int VIEW_ACCOUNTS = 11;
	private static final int TRANSACTIONS = 12;
	private static final int TRANSACTIONS_ID = 13;
	private static final int TRANSACTIONS_ACCOUNTS = 14;
	private static final int TRANSACTIONS_ACCOUNTS_ID = 15;
	
	// Authority
	public static final String AUTHORITY = "org.musalahuddin.myexpenseorganizer";
		
	public static final Uri EXPENSE_PARENT_CATEGORIES_URI = 
			Uri.parse("content://" + AUTHORITY + "/expense_parent_categories");
	public static final Uri EXPENSE_CHILD_CATEGORIES_URI = 
			Uri.parse("content://" + AUTHORITY + "/expense_child_categories");
	public static final Uri ACCOUNT_CATEGORIES_URI = 
			Uri.parse("content://" + AUTHORITY + "/account_categories");
	public static final Uri TRANSACTION_CATEGORIES_URI = 
			Uri.parse("content://" + AUTHORITY + "/transaction_categories");
	public static final Uri ACCOUNTS_URI = 
			Uri.parse("content://" + AUTHORITY + "/accounts");
	public static final Uri VIEW_ACCOUNTS_URI = 
			Uri.parse("content://" + AUTHORITY + "/view_accounts");
	public static final Uri TRANSACTIONS_URI = 
			Uri.parse("content://" + AUTHORITY + "/transactions");
	public static final Uri TRANSACTIONS_ACCOUNTS_URI = 
			Uri.parse("content://" + AUTHORITY + "/transactions_accounts");
	
	static {
	    URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	    URI_MATCHER.addURI(AUTHORITY, "expense_parent_categories", EXPENSE_PARENT_CATEGORIES);
	    URI_MATCHER.addURI(AUTHORITY, "expense_parent_categories/#", EXPENSE_PARENT_CATEGORIES_ID);
	    URI_MATCHER.addURI(AUTHORITY, "expense_child_categories", EXPENSE_CHILD_CATEGORIES);
	    URI_MATCHER.addURI(AUTHORITY, "expense_child_categories/#", EXPENSE_CHILD_CATEGORIES_ID);
	    URI_MATCHER.addURI(AUTHORITY, "account_categories", ACCOUNT_CATEGORIES);
	    URI_MATCHER.addURI(AUTHORITY, "account_categories/#", ACCOUNT_CATEGORIES_ID);
	    URI_MATCHER.addURI(AUTHORITY, "transaction_categories", TRANSACTION_CATEGORIES);
	    URI_MATCHER.addURI(AUTHORITY, "transaction_categories/#", TRANSACTION_CATEGORIES_ID);
	    URI_MATCHER.addURI(AUTHORITY, "accounts", ACCOUNTS);
	    URI_MATCHER.addURI(AUTHORITY, "accounts/#", ACCOUNTS_ID);
	    URI_MATCHER.addURI(AUTHORITY, "view_accounts", VIEW_ACCOUNTS);
	    URI_MATCHER.addURI(AUTHORITY, "transactions", TRANSACTIONS);
	    URI_MATCHER.addURI(AUTHORITY, "transactions/#", TRANSACTIONS_ID);
	    URI_MATCHER.addURI(AUTHORITY, "transactions_accounts", TRANSACTIONS_ACCOUNTS);
	    URI_MATCHER.addURI(AUTHORITY, "transactions_accounts/#", TRANSACTIONS_ACCOUNTS_ID);
	}
	
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Log.i("MyExpenseOrganizerProvider", "oncreate");
		dbhelper = new MyExpenseOrganizerDatabaseHelper(getContext());
	    return true;
	}
	

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String groupBy = null;
		String orderBy = null;
		String having = null;
		
		switch (URI_MATCHER.match(uri)) {
		
		case EXPENSE_PARENT_CATEGORIES:
			qb.setTables(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY);
			break;
		
		case EXPENSE_PARENT_CATEGORIES_ID:
			qb.setTables(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY);
			qb.appendWhere(ExpenseParentCategory.COLUMN_ID + "=" + uri.getPathSegments().get(1));
			break;
		
		case EXPENSE_CHILD_CATEGORIES:
			qb.setTables(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY);
			break;
		
		case EXPENSE_CHILD_CATEGORIES_ID:
			qb.setTables(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY);
			qb.appendWhere(ExpenseChildCategory.COLUMN_ID + "=" + uri.getPathSegments().get(1));
			break;
			
		case ACCOUNT_CATEGORIES:
			qb.setTables(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY);
			break;
		
		case ACCOUNT_CATEGORIES_ID:
			qb.setTables(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY);
			qb.appendWhere(AccountCategoryTable.COLUMN_ID + "=" + uri.getPathSegments().get(1));
			break;
			
		case TRANSACTION_CATEGORIES:
			qb.setTables(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY);
			break;
		
		case TRANSACTION_CATEGORIES_ID:
			qb.setTables(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY);
			qb.appendWhere(TransactionCategoryTable.COLUMN_ID + "=" + uri.getPathSegments().get(1));
			break;
			
		case ACCOUNTS:
			qb.setTables(AccountTable.TABLE_ACCOUNT);
			break;
			
		case VIEW_ACCOUNTS:
			qb.setTables(AccountView.VIEW_ACCOUNT);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		if (debug){
			  String qs = qb.buildQuery(projection, selection, null, groupBy,
			      null, orderBy, null);
			  Log.d(TAG, "Query : " + qs);
		}
		
		if (!TextUtils.isEmpty(sortOrder)) {
	      orderBy = sortOrder;
	    }
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy,
		        having, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		int count = 0; 
		switch (URI_MATCHER.match(uri)) {
		case EXPENSE_PARENT_CATEGORIES:
			count = db.delete(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY, selection, selectionArgs);
			break;
		case EXPENSE_PARENT_CATEGORIES_ID:
			selection = ExpenseParentCategory.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			count = db.delete(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY, selection, selectionArgs);
			break;
		case EXPENSE_CHILD_CATEGORIES:
			count = db.delete(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY, selection, selectionArgs);
			break;
		case EXPENSE_CHILD_CATEGORIES_ID:
			selection = ExpenseChildCategory.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			count = db.delete(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY, selection, selectionArgs);
			break;
		case ACCOUNT_CATEGORIES:
			count = db.delete(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, selection, selectionArgs);
			break;
		case ACCOUNT_CATEGORIES_ID:
			selection = AccountCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			count = db.delete(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, selection, selectionArgs);
			break;
		case TRANSACTION_CATEGORIES:
			count = db.delete(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, selection, selectionArgs);
			break;
		case TRANSACTION_CATEGORIES_ID:
			selection = TransactionCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			count = db.delete(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, selection, selectionArgs);
			break;
		case ACCOUNTS:
			count = db.delete(AccountTable.TABLE_ACCOUNT, selection, selectionArgs);
			//notify the accounts view uri
			getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);
			break;
		case ACCOUNTS_ID:
			selection = AccountTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			count = db.delete(AccountTable.TABLE_ACCOUNT, selection, selectionArgs);
			//notify the accounts view uri
			getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);
			break;
			
		default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		long id = 0;
		String newUri;
		String name;
		Long parentId;
		String selection;
		String[] selectionArgs;
		Cursor mCursor;
		
		switch (URI_MATCHER.match(uri)) {
		
		case EXPENSE_PARENT_CATEGORIES:
			name = values.getAsString(ExpenseParentCategory.COLUMN_NAME);
			
			selection = ExpenseParentCategory.COLUMN_NAME + " = ?";
			selectionArgs = new String[]{name};
			/*
			mCursor = db.query(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY,
					new String []{ExpenseParentCategory.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			mCursor.close();
			*/
			
			id = db.insertOrThrow(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY, null, values);
			newUri = EXPENSE_PARENT_CATEGORIES + "/" + id;
			break;
			
		case EXPENSE_CHILD_CATEGORIES:
			name = values.getAsString(ExpenseChildCategory.COLUMN_NAME);
			parentId = values.getAsLong(ExpenseChildCategory.COLUMN_EXPENSE_PARENT_CATEGORY_ID);
			
			selection = ExpenseChildCategory.COLUMN_NAME + " = ? AND " + ExpenseChildCategory.COLUMN_EXPENSE_PARENT_CATEGORY_ID + " =?";
			selectionArgs = new String[]{name,String.valueOf(parentId)};
			
			/*
			mCursor = db.query(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY,
					new String []{ExpenseChildCategory.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			
			mCursor.close();
			*/
			id = db.insertOrThrow(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY, null, values);
			newUri = EXPENSE_CHILD_CATEGORIES + "/" + id;
			break;
			
		case ACCOUNT_CATEGORIES:
			
			id = db.insertOrThrow(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, null, values);
			newUri = ACCOUNT_CATEGORIES + "/" + id;
			break;
			
		case TRANSACTION_CATEGORIES:
			
			id = db.insertOrThrow(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, null, values);
			newUri = TRANSACTION_CATEGORIES + "/" + id;
			break;
			
		case ACCOUNTS:
			id = db.insertOrThrow(AccountTable.TABLE_ACCOUNT, null, values);
			//notify the accounts view uri
			getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);
			newUri = ACCOUNTS + "/" + id;
			break;
			
		 default:
		      throw new IllegalArgumentException("Unknown URI: " + uri);
		
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return id > 0 ? Uri.parse(newUri) : null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		int count = 0; 
		String name;
		Long parentId;
		Cursor mCursor;
		
		switch (URI_MATCHER.match(uri)) {
		
		case EXPENSE_PARENT_CATEGORIES:
			// bulk update should not be supported
			count = db.update(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY, values, selection, selectionArgs);
			break;
			
		case EXPENSE_PARENT_CATEGORIES_ID:
			
			name = values.getAsString(ExpenseParentCategory.COLUMN_NAME);
			
			selection = ExpenseParentCategory.COLUMN_NAME + " = ?";
			selectionArgs = new String[]{name};
			
			mCursor = db.query(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY,
					new String []{ExpenseParentCategory.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			
			mCursor.close();
			selection = ExpenseParentCategory.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			selectionArgs = null;
			count = db.update(ExpenseParentCategory.TABLE_EXPENSE_PARENT_CATEGORY, values, selection, selectionArgs);
			break;
			
		case EXPENSE_CHILD_CATEGORIES:
			// bulk update should not be supported
			count = db.update(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY, values, selection, selectionArgs);
			break;
			
		case EXPENSE_CHILD_CATEGORIES_ID:
			
			name = values.getAsString(ExpenseChildCategory.COLUMN_NAME);
			parentId = values.getAsLong(ExpenseChildCategory.COLUMN_EXPENSE_PARENT_CATEGORY_ID);
			
			selection = ExpenseChildCategory.COLUMN_NAME + " = ? AND " + ExpenseChildCategory.COLUMN_EXPENSE_PARENT_CATEGORY_ID + " =?";
			selectionArgs = new String[]{name,String.valueOf(parentId)};
			
			mCursor = db.query(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY,
					new String []{ExpenseChildCategory.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			
			mCursor.close();
			//remove parentId .. no need to update parentId
			values.remove(ExpenseChildCategory.COLUMN_EXPENSE_PARENT_CATEGORY_ID);
			selection = ExpenseChildCategory.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			selectionArgs = null;
			count = db.update(ExpenseChildCategory.TABLE_EXPENSE_CHILD_CATEGORY, values, selection, selectionArgs);
			break;
			
		case ACCOUNT_CATEGORIES:
			// bulk update should not be supported
			count = db.update(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, values, selection, selectionArgs);
			break;
			
		case ACCOUNT_CATEGORIES_ID:
			
			name = values.getAsString(AccountCategoryTable.COLUMN_NAME);
			
			selection = AccountCategoryTable.COLUMN_NAME + " = ?";
			selectionArgs = new String[]{name};
			
			mCursor = db.query(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY,
					new String []{AccountCategoryTable.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			
			mCursor.close();
			selection = AccountCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			selectionArgs = null;
			count = db.update(AccountCategoryTable.TABLE_ACCOUNT_CATEGORY, values, selection, selectionArgs);
			break;
			
		case TRANSACTION_CATEGORIES:
			// bulk update should not be supported
			count = db.update(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, values, selection, selectionArgs);
			break;
			
		case TRANSACTION_CATEGORIES_ID:
			
			name = values.getAsString(TransactionCategoryTable.COLUMN_NAME);
			
			selection = TransactionCategoryTable.COLUMN_NAME + " = ?";
			selectionArgs = new String[]{name};
			
			mCursor = db.query(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY,
					new String []{TransactionCategoryTable.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			
			mCursor.close();
			selection = TransactionCategoryTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			selectionArgs = null;
			count = db.update(TransactionCategoryTable.TABLE_TRANSACTION_CATEGORY, values, selection, selectionArgs);
			break;
			
		case ACCOUNTS:
			// bulk update should not be supported
			count = db.update(AccountTable.TABLE_ACCOUNT, values, selection, selectionArgs);
			break;
			
		case ACCOUNTS_ID:
			
			/*
			name = values.getAsString(AccountTable.COLUMN_NAME);
			
			selection = AccountTable.COLUMN_NAME + " = ?";
			selectionArgs = new String[]{name};
			
			mCursor = db.query(AccountTable.TABLE_ACCOUNT,
					new String []{AccountTable.COLUMN_ID},
					selection, selectionArgs, null, null, null);
			
			if(mCursor.getCount() != 0){
				mCursor.close();
		        throw new SQLiteConstraintException();
			}
			
			mCursor.close();
			*/
			selection = AccountTable.COLUMN_ID + " = " +  uri.getPathSegments().get(1);
			selectionArgs = null;
			count = db.update(AccountTable.TABLE_ACCOUNT, values, selection, selectionArgs);
			//notify the accounts view uri
			getContext().getContentResolver().notifyChange(VIEW_ACCOUNTS_URI, null);
			break;
			
		default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
