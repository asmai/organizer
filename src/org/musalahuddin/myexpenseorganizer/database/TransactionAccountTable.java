package org.musalahuddin.myexpenseorganizer.database;

import org.musalahuddin.myexpenseorganizer.provider.MyExpenseOrganizerProvider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class TransactionAccountTable extends Model{

	// URI 
	public static final Uri CONTENT_URI = MyExpenseOrganizerProvider.TRANSACTIONS_ACCOUNTS_URI;
	// Table columns
	public static final String TABLE_TRANSACTION_ACCOUNT = "transactions_accounts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TRANSACTION_ID = "transaction_id";
	public static final String COLUMN_PRIMARY_ACCOUNT_ID = "primary_account_id";
	public static final String COLUMN_SECONDARY_ACCOUNT_ID = "secondary_account_id";
	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_IS_DEPOSIT = "is_deposit";
	
	
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
	+ TABLE_TRANSACTION_ACCOUNT
	+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
	+ COLUMN_TRANSACTION_ID + " INTEGER NOT NULL, " 
	+ COLUMN_PRIMARY_ACCOUNT_ID + " INTEGER NOT NULL, "
	+ COLUMN_SECONDARY_ACCOUNT_ID + " INTEGER NOT NULL, "
	+ COLUMN_AMOUNT + " INTEGER, "
	+ COLUMN_IS_DEPOSIT + " INTEGER, "
	+ "FOREIGN KEY(" + COLUMN_TRANSACTION_ID + ") REFERENCES "+TransactionTable.TABLE_TRANSACTION+"("+TransactionTable.COLUMN_ID+"), "
	+ "FOREIGN KEY(" + COLUMN_PRIMARY_ACCOUNT_ID + ") REFERENCES "+AccountTable.TABLE_ACCOUNT+"("+AccountTable.COLUMN_ID+"), "
	+ "FOREIGN KEY(" + COLUMN_SECONDARY_ACCOUNT_ID + ") REFERENCES "+AccountTable.TABLE_ACCOUNT+"("+AccountTable.COLUMN_ID+")"
	+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TransactionAccountTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_ACCOUNT);
		onCreate(database);
	}
}
