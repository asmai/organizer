package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AddField extends Activity implements DialogInterface.OnClickListener {
	
	private int[] fieldIds;
	private String[] fieldLabels;
	private boolean[] fieldChecks;
	
	static final int FIELD_DIALOG_ID = 1;
	
	private AlertDialog fieldDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		/*
		String[] temp =  b.getStringArray("fieldLabels");
		for(String val:temp){
			System.out.println("val id: " + val);
		}
		*/
		
		fieldLabels = b.getStringArray("fieldLabels");
		fieldChecks = new boolean[fieldLabels.length];
		
		//fieldLabels = new String[] {"Muhammad","Asmaee","Salahuddin"};
		//fieldChecks = new boolean[] {false,false,false};
		
		fieldDialog = new AlertDialog.Builder(AddField.this)
        .setTitle("Add Field")
        .setMultiChoiceItems(
        		fieldLabels,
        		fieldChecks,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton,
                            boolean isChecked) {

                        fieldChecks[whichButton] = isChecked;
                    }
                })
        .setPositiveButton(R.string.alert_dialog_ok,this)
        .setNegativeButton(R.string.alert_dialog_cancel,this)
        .create();
		
		showDialog(FIELD_DIALOG_ID);
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case FIELD_DIALOG_ID:
			return fieldDialog;
		}
		return null;
	}


	@Override
	public void onClick(DialogInterface dialog, int id) {
		switch(id){
		case AlertDialog.BUTTON_POSITIVE:
			Bundle b = new Bundle();
			Intent i=new Intent();
			
			b.putBooleanArray("fieldChecks", fieldChecks);
			i.putExtras(b);
			setResult(RESULT_OK,i);
			
			//dismissDialog(FIELD_DIALOG_ID);
			finish();
			break;
		case AlertDialog.BUTTON_NEGATIVE:
			//dismissDialog(FIELD_DIALOG_ID);
			finish();
			break;
		}
		
	}

	
}
