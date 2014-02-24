package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AccountTable extends Model{
	
	// URI 
	public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.ACCOUNTS_URI;
	// Table columns
	public static final String TABLE_ACCOUNT = "accounts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ACC0UNT_CATEGORY_ID = "account_category_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_INIT_BALANCE = "init_balance";
	public static final String COLUMN_CREDIT_LIMIT = "credit_limit";
	public static final String COLUMN_MONTHLY_PAYMENT = "monthly_payment";
	public static final String COLUMN_DUE_DATE = "due_date";
	public static final String COLUMN_CREATE_DATE = "create_date";
	public static final String COLUMN_MOD_DATE = "mod_date";
	public static final String COLUMN_DELETE_DATE = "delete_date";
	public static final String COLUMN_DELETED = "deleted";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_ACCOUNT
			+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ COLUMN_ACC0UNT_CATEGORY_ID + " INTEGER NOT NULL, "
			+ COLUMN_NAME + " TEXT," 
			+ COLUMN_NUMBER + " INTEGER, "
			+ COLUMN_DESCRIPTION + " TEXT, " 
			+ COLUMN_INIT_BALANCE + " INTEGER, "
			+ COLUMN_CREDIT_LIMIT + " INTEGER, "
			+ COLUMN_MONTHLY_PAYMENT + " INTEGER, "
			+ COLUMN_DUE_DATE + " INTEGER, "
			+ COLUMN_CREATE_DATE + " INTEGER, " 
			+ COLUMN_MOD_DATE + " INTEGER, "
			+ COLUMN_DELETE_DATE + " INTEGER, " 
			+ COLUMN_DELETED + " INTERGER DEFAULT 0 NOT NULL, "
			+ "FOREIGN KEY(" + COLUMN_ACC0UNT_CATEGORY_ID + ") REFERENCES "+AccountCategoryTable.TABLE_ACCOUNT_CATEGORY+"("+AccountCategoryTable.COLUMN_ID+")"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(AccountTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
		onCreate(database);
	}
	
	/**
	 * Creates a new Category
	 * @param name
	 * @return the row id of the newly inserted row, of -1 if category already exists
	 */
	public static long create(
			String name,
			int number,
			String description,
			double init_balance,
			double credit_limit,
			double monthly_payment,
			long due_date,
			long account_category_id
			){
		
		// convert amount to integer before storing it in database
		init_balance = init_balance*100;
		credit_limit = credit_limit*100;
		monthly_payment = monthly_payment*100;
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_NAME, name);
		initialValues.put(COLUMN_NUMBER, number);
		initialValues.put(COLUMN_DESCRIPTION, description);
		initialValues.put(COLUMN_INIT_BALANCE, (int)init_balance);
		initialValues.put(COLUMN_CREDIT_LIMIT, (int)credit_limit);
		initialValues.put(COLUMN_MONTHLY_PAYMENT, (int)monthly_payment);
		initialValues.put(COLUMN_DUE_DATE, due_date);
		initialValues.put(COLUMN_ACC0UNT_CATEGORY_ID, account_category_id);
		Uri uri;
		try{
			uri = cr().insert(CONTENT_URI,initialValues);
		}
		catch (SQLiteConstraintException e){
			return -1;
		}
		
		return Integer.valueOf(uri.getLastPathSegment());
		
	}
	

}