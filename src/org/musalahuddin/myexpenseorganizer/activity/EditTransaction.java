package org.musalahuddin.myexpenseorganizer.activity;

import java.util.ArrayList;
import java.util.List;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.app.LoaderManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class EditTransaction extends FragmentActivity implements OnClickListener,LoaderManager.LoaderCallbacks<Cursor>{
	
	boolean mAddOnly;
	
	private Bitmap pendingTransactionImage = null;
	
	private Spinner from,to; 
	private ImageButton mTransactionCamera;
	
	private int cameraButton;
	private static int imageHeight, imageWidth;
	
	public static Uri contentUri;

	private int getDefaultCameraButton() {
        return R.drawable.camera_button;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_transaction);
		Intent intent = getIntent();
		String action = intent.getAction();
        mAddOnly = action != null && action.equals("myexpenseorganizer.intent.add.transactions");
		
        setTitle(mAddOnly ? R.string.transaction_new_title : R.string.transaction_edit_title);
        
        cameraButton = getDefaultCameraButton();
        
        from = (Spinner) findViewById(R.id.spinner_transaction_from);
        to = (Spinner) findViewById(R.id.spinner_transaction_to);
        mTransactionCamera = (ImageButton) findViewById(R.id.image_transaction_camera);
        
        
        ArrayList<String> list = new ArrayList<String>();
    	list.add("Accounts");
    	list.add("Other");
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    		android.R.layout.simple_spinner_item, list);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	from.setAdapter(adapter);
    	to.setAdapter(adapter);
    	mTransactionCamera.setOnClickListener(this);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);
		Intent i;
		switch (item.getItemId()) {
		
		case R.id.SAVE_COMMAND:
			finish();
			break;
			
		case R.id.CANCEL_COMMAND:
			 finish();
			break;
			
		}
		
		return false;
	}
	
	final ClearImageCallback clearImage = new ClearImageCallback() {
        @Override
        public void clearImage() {
        	pendingTransactionImage = null;
        	mTransactionCamera.setImageResource(cameraButton);
        }
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.image_transaction_camera:
			
			imageHeight = mTransactionCamera.getHeight();
			imageWidth = mTransactionCamera.getWidth();
			
			Toast.makeText(EditTransaction.this,"getHeigth = " + imageHeight, Toast.LENGTH_LONG).show();
			
			if (pendingTransactionImage != null)
                CameraModule.showPictureLauncher(this, clearImage);
            else
                CameraModule.showPictureLauncher(this, null);
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		CameraResultCallback callback = new CameraResultCallback() {
            @Override
            public void handleCameraResult(Bitmap bitmap) {
            	//Toast.makeText(EditTransaction.this,"getHeigth = " + mTransactionCamera.getHeight(), Toast.LENGTH_LONG).show();
            	//Toast.makeText(EditTransaction.this,"getHeigth = " + EditTransaction.imageHeight, Toast.LENGTH_LONG).show();
            	pendingTransactionImage = bitmap;
            	mTransactionCamera.setMaxHeight(imageHeight);
                mTransactionCamera.setMaxWidth(imageWidth);
            	mTransactionCamera.setImageBitmap(pendingTransactionImage);
                
            }
        };
        
        
        if(CameraModule.activityResult(this,
                requestCode, resultCode, data, callback)){
        	Log.i("success","with camera");
        }
        
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
