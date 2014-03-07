package org.musalahuddin.myexpenseorganizer.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.database.AccountCategoryTable;
import org.musalahuddin.myexpenseorganizer.database.ExpenseChildCategory;
import org.musalahuddin.myexpenseorganizer.database.ExpenseParentCategory;
import org.musalahuddin.myexpenseorganizer.database.TransactionCategoryTable;
import org.musalahuddin.myexpenseorganizer.util.AccountCategory;
import org.musalahuddin.myexpenseorganizer.util.Category;
import org.musalahuddin.myexpenseorganizer.util.ExpenseCategory;
import org.musalahuddin.myexpenseorganizer.util.ExpenseSubCategory;
import org.musalahuddin.myexpenseorganizer.util.Result;
import org.musalahuddin.myexpenseorganizer.util.TransactionCategory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class CatImport extends Activity implements DialogInterface.OnClickListener {

	ProgressDialog mProgressDialog;
	private AlertDialog importDialog;
	private final boolean debug = false;
	static final int IMPORT_DIALOG_ID = 1;
	private MyAsyncTask task = null;
	
	// defining types of imports 
	static final int EXPENSE_CATEGORIES_IMPORT = 1;
	static final int TRANSACTION_CATEGORIES_IMPORT = 2;
	static final int ACCOUNT_CATEGORIES_IMPORT = 3;
	
	
	// setting default to be 0
	static int importType = 0; 

	public static class MyDefaultHandler extends DefaultHandler{
		String tempValue;
		
		ArrayList<ExpenseCategory> categories;
		ArrayList<AccountCategory> accountCategories;
		ArrayList<TransactionCategory> transactionCategories;
		
		ExpenseCategory category;
		ExpenseSubCategory subCategory;
		AccountCategory accountCategory; 
		TransactionCategory transactionCategory; 
		
		public MyDefaultHandler(){
			categories = new ArrayList<ExpenseCategory>();
			accountCategories = new ArrayList<AccountCategory>();
			transactionCategories = new ArrayList<TransactionCategory>();
		}
		
		public Result getResult(){
			if(categories.size() > 0){
				return new Result(true,0,categories);
			}
			else if(accountCategories.size() > 0){
				return new Result(true,0,accountCategories);
			}
			else if(transactionCategories.size() > 0){
				return new Result(true,0,transactionCategories);
			}
			else{
				return new Result(false,R.string.parse_error_no_data_found);
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			switch(importType) {
			case EXPENSE_CATEGORIES_IMPORT:
				// if end of category element add to list
				if(localName.equalsIgnoreCase("category")){
					categories.add(category);
				}
				// if end of sub category add to category
				else if(localName.equalsIgnoreCase("sub_category")){
					category.addSubCategory(subCategory);
				}
				break;
			case ACCOUNT_CATEGORIES_IMPORT:
				if(localName.equalsIgnoreCase("category")){
					accountCategories.add(accountCategory);
				}
				break;
			case TRANSACTION_CATEGORIES_IMPORT:
				if(localName.equalsIgnoreCase("category")){
					transactionCategories.add(transactionCategory);
				}
				break;
			}
		}


		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			super.startElement(uri, localName, qName, attributes);
			
			switch(importType) {
			case EXPENSE_CATEGORIES_IMPORT:
				if(localName.equalsIgnoreCase("category")){
					Log.i("cat: ", attributes.getValue("Na"));
					category = new ExpenseCategory();
					category.setId(attributes.getValue("Nb"));
					category.setName(attributes.getValue("Na"));
				}
				else if(localName.equalsIgnoreCase("sub_category")){
					Log.i("subcat: ", attributes.getValue("Na"));
					subCategory = new ExpenseSubCategory();
					subCategory.setId(attributes.getValue("Nb"));
					subCategory.setName(attributes.getValue("Na"));
					
				}
				break;
			case ACCOUNT_CATEGORIES_IMPORT:
				if(localName.equalsIgnoreCase("category")){
					Log.i("act cat: ", attributes.getValue("Na"));
					accountCategory = new AccountCategory();
					accountCategory.setName(attributes.getValue("Na"));
				}
				break;
			case TRANSACTION_CATEGORIES_IMPORT:
				if(localName.equalsIgnoreCase("category")){
					Log.i("trans cat: ", attributes.getValue("Na"));
					transactionCategory = new TransactionCategory();
					transactionCategory.setName(attributes.getValue("Na"));
				}
				break;
			}
		}
		
	}
	
	public static Result analyzeCatFileWithSAX(InputStream is){
		
		MyDefaultHandler handler = new MyDefaultHandler();
		try{
			Xml.parse(is, Xml.Encoding.UTF_8, handler);
		}catch(IOException e){
			return new Result(false,R.string.parse_error_other_exception,e.getMessage());
		}catch(SAXException e){
			return new Result(false,R.string.parse_error_parse_exception);
		}
		
		return handler.getResult();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final Intent intent = getIntent();
		
		final String action = intent.getAction();
		
		int dialogTitleId =0; 
		
		// looking up import type from the action used to start this activity 
		if(action.equalsIgnoreCase("myexpenseorganizer.intent.import.expensecategories")){
			importType = EXPENSE_CATEGORIES_IMPORT;
		}
		else if(action.equalsIgnoreCase("myexpenseorganizer.intent.import.transactioncategories")){
			importType = TRANSACTION_CATEGORIES_IMPORT;
		}
		else if(action.equalsIgnoreCase("myexpenseorganizer.intent.import.accountcategories")){
			importType = ACCOUNT_CATEGORIES_IMPORT;
		}
		else{
			finish();
		}
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setProgress(0);
		mProgressDialog.setCancelable(false);
		
		if(importType == EXPENSE_CATEGORIES_IMPORT){
			dialogTitleId = R.string.alert_import_expense_categories_title; 
			
		}
		else if(importType == TRANSACTION_CATEGORIES_IMPORT){
			dialogTitleId = R.string.alert_import_transaction_categories_title;
		}
		else if(importType == ACCOUNT_CATEGORIES_IMPORT){
			dialogTitleId = R.string.alert_import_account_categories_title;
		}
		
		importDialog = new AlertDialog.Builder(this)
		.setTitle(dialogTitleId)
		.setCancelable(false)
		.setPositiveButton(R.string.import_alert_ok,this)
		.setNegativeButton(R.string.import_alert_cancel,this)
		.create();
		
		showDialog(IMPORT_DIALOG_ID);
	
		/*
		importDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.import_alert_title)
		.setCancelable(false)
		.setPositiveButton(R.string.import_alert_ok,this)
		.setNegativeButton(R.string.import_alert_cancel,this)
		.create();
		showDialog(IMPORT_DIALOG_ID);
		*/
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case IMPORT_DIALOG_ID:
			return importDialog;
		}
		return null;
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mProgressDialog.dismiss();
	}
	
	void UpdateProgress(int progress){
		mProgressDialog.setProgress(progress);
	}
	
	void markAsDone(){
		mProgressDialog.dismiss();
		
		String msg;
		Result result = task.getResult();
		msg = getString(result.message, result.extra);
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		task = null;
		
		finish();
	}
	
	static class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		private CatImport activity;
		InputStream catXML;
		private int max, totalImportedCat;
		Result result;
		int progress = 0;
		
		private String title;
		private ArrayList<ExpenseCategory> categories;
		private ArrayList<AccountCategory> accountCategories;
		private ArrayList<TransactionCategory> transactionCategories;
		
		/**
	     * @param context
	     */
	    public MyAsyncTask(CatImport activity) {
	    	attach(activity);
	    }
	    
	    public void setTitle(String title){
	    	this.title = title;
	    }
	    
	    public String getTitle(){
	    	return title;
	    }
	    
	    public Integer getTotalImportedCat(){
	    	return totalImportedCat;
	    }
	    
	    public Integer getProgress(){
	    	return progress;
	    }
	    
	    public Integer getMax(){
	    	return max;
	    }
	    
	    public void setMax(int max){
	    	this.max = max;
	    }
	    
	    public void setResult(Result result){
	    	this.result = result;
	    }
	    
	    public Result getResult(){
	    	return result;
	    }
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if(!parseXML()){
				return null;
			}
			// initialize progress bar
			publishProgress(0);
			totalImportedCat=0;
			
			
			if(importType == EXPENSE_CATEGORIES_IMPORT){
				setMax(categories.size());
				activity.mProgressDialog.setMax(getMax());
				importCats();
			}
			else if(importType == ACCOUNT_CATEGORIES_IMPORT){
				setMax(accountCategories.size());
				activity.mProgressDialog.setMax(getMax());
				importAccountCats();
			}
			else if(importType == TRANSACTION_CATEGORIES_IMPORT){
				setMax(transactionCategories.size());
				activity.mProgressDialog.setMax(getMax());
				importTransactionCats();
			}
			
			setResult(new Result(true,
					R.string.import_categories_success,
					String.valueOf(getTotalImportedCat())
					));
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			// TODO Auto-generated method stub
			if(activity == null){
				Log.w("MyAsyncTask","onPostExecute() skipped -- no activity");
			}
			else{
				activity.markAsDone();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progress = values[0];
			if(activity == null){
				Log.w("MyAsyncTask","onProgressUpdate() skipped -- no activity");
			}
			else{
				activity.UpdateProgress(progress);
			}
		}
		
		/**
	     * return false upon problem or true
	     */
		protected boolean parseXML(){
			int rawId = 0; 
			Result result;
			switch(importType) {
			
			case ACCOUNT_CATEGORIES_IMPORT:
				catXML = activity.getResources().openRawResource(R.raw.account_categories);
				result = analyzeCatFileWithSAX(catXML);
				if(result.success){
					accountCategories = (ArrayList<AccountCategory>) result.extra[0];
					return true;
				}
				else{
					return false;
				}
			case EXPENSE_CATEGORIES_IMPORT:
				catXML = activity.getResources().openRawResource(R.raw.expense_categories);
				result = analyzeCatFileWithSAX(catXML);
				if(result.success){
					categories = (ArrayList<ExpenseCategory>) result.extra[0];
					return true;
				}
				else{
					return false;
				}
			case TRANSACTION_CATEGORIES_IMPORT:
				catXML = activity.getResources().openRawResource(R.raw.transaction_categories);
				result = analyzeCatFileWithSAX(catXML);
				if(result.success){
					transactionCategories = (ArrayList<TransactionCategory>) result.extra[0];
					return true;
				}
				else{
					return false;
				}
			default:
				return false;
			}
			
			
			//catXML = activity.getResources().openRawResource(rawId);
			//Result result = analyzeCatFileWithSAX(catXML);
		}
		
		public void importCats(){
			int count = 0; 
			String name;
			long main_id, sub_id;
		
			for(ExpenseCategory mainCategory : categories){
				//System.out.println(mainCategory.getId() + " = " + mainCategory.getName());
				name = mainCategory.getName();
				count++;
				main_id = ExpenseParentCategory.find(name);
				if(main_id != -1){
					Log.i("MyExpense","category with name "+ name + " already defined");
				}
				else{
					main_id = ExpenseParentCategory.create(name);
					if(main_id != -1){
						totalImportedCat++;
						//if(count % 10 == 0){
							//publishProgress(count);
						//}
					}
					else{
						//this should not happen
						Log.w("MyExpense","could neither retrieve nor store main category " + name);
						continue;
					}
				}
				
				publishProgress(count);
				
				for(ExpenseSubCategory subCategory: mainCategory.getSubCategories()){
					//System.out.println(">>> "+subCategory.getId() + " = " + subCategory.getName());
					name = subCategory.getName();
					sub_id = ExpenseChildCategory.find(name,main_id);
					if(sub_id != -1){
						Log.i("MyExpense","sub category with name "+ name + " already defined");
					}
					else{
						sub_id = ExpenseChildCategory.create(name,main_id);
						if(sub_id != -1){
							totalImportedCat++;
						}
						else{
							//this should not happen
							Log.w("MyExpense","could neither retrieve nor store sub category " + name);
							continue;
						}
					}
					
				}
			}
		}
		
		public void importAccountCats(){
			int count = 0;
			String name;
			long main_id;
			for(AccountCategory mainCategory : accountCategories){
				//System.out.println(mainCategory.getName());
				name = mainCategory.getName();
				count++;
				main_id = AccountCategoryTable.find(name);
				if(main_id != -1){
					Log.i("MyExpense","category with name "+ name + " already defined");
				}
				else{
					main_id = AccountCategoryTable.create(name);
					if(main_id != -1){
						totalImportedCat++;
						//if(count % 10 == 0){
							//publishProgress(count);
						//}
					}
					else{
						//this should not happen
						Log.w("MyExpense","could neither retrieve nor store main category " + name);
						continue;
					}
				}
				
				publishProgress(count);
			}
		}
		
		public void importTransactionCats(){
			int count = 0;
			String name;
			long main_id;
			for(TransactionCategory mainCategory : transactionCategories){
				//System.out.println(mainCategory.getName());
				name = mainCategory.getName();
				count++;
				main_id = TransactionCategoryTable.find(name);
				if(main_id != -1){
					Log.i("MyExpense","category with name "+ name + " already defined");
				}
				else{
					main_id = TransactionCategoryTable.create(name);
					if(main_id != -1){
						totalImportedCat++;
						//if(count % 10 == 0){
							//publishProgress(count);
						//}
					}
					else{
						//this should not happen
						Log.w("MyExpense","could neither retrieve nor store main category " + name);
						continue;
					}
				}
				
				publishProgress(count);
			}
		}
		
		void attach(CatImport activity2) {
	    	this.activity=activity2;
	    }
	    
	    void detach() {
	        activity=null;
	    }
		
		
	}

	@Override
	public void onClick(DialogInterface dialog, int id) {
		// TODO Auto-generated method stub
		switch(id){
		case AlertDialog.BUTTON_POSITIVE:
			//parseXML();
			String title = getString(R.string.import_parsing);
		    mProgressDialog.setTitle(title);
		    mProgressDialog.show();
		    task = new MyAsyncTask(CatImport.this);
		    task.setTitle(title);
			dismissDialog(IMPORT_DIALOG_ID);
			task.execute();
			break;
		case AlertDialog.BUTTON_NEGATIVE:
			//dismissDialog(IMPORT_DIALOG_ID);
			finish();
			break;
		
		}
		
	}
	
}
