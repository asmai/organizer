package org.musalahuddin.myexpenseorganizer.activity;

import org.musalahuddin.myexpenseorganizer.R;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.CameraResultCallback;
import org.musalahuddin.myexpenseorganizer.camera.CameraModule.ClearImageCallback;

import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditTransactionCopy extends FragmentActivity implements View.OnClickListener{

	

	private ImageButton pictureButton;
	private Bitmap pendingCommentPicture = null;
	
	private int cameraButton;
	
	
	private int getDefaultCameraButton() {
        return R.drawable.camera_button;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_transaction_copy);
		
		cameraButton = getDefaultCameraButton();
		
		pictureButton = (ImageButton) findViewById(R.id.picture);
		pictureButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.picture:
			//System.out.println("hello");
			if (pendingCommentPicture != null)
                CameraModule.showPictureLauncher(this, clearImage);
            else
                CameraModule.showPictureLauncher(this, null);
			break;
		}
	}
	
	final ClearImageCallback clearImage = new ClearImageCallback() {
        @Override
        public void clearImage() {
            pendingCommentPicture = null;
            pictureButton.setImageResource(cameraButton);
        }
    };
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		CameraResultCallback callback = new CameraResultCallback() {
            @Override
            public void handleCameraResult(Bitmap bitmap) {
                
            	
                pendingCommentPicture = bitmap;
                pictureButton.setImageBitmap(pendingCommentPicture);
                
            }
        };
        
        if(CameraModule.activityResult(this,
                requestCode, resultCode, data, callback)){
        	Log.i("success","with camera");
        }
	}

	

}
