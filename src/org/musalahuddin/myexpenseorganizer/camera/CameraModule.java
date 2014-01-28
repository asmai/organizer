package org.musalahuddin.myexpenseorganizer.camera;

import org.musalahuddin.myexpenseorganizer.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class CameraModule {

	protected static final int REQUEST_CODE_CAMERA = 1;
    protected static final int REQUEST_CODE_PICTURE = 2;
    
    private static File tempFile = null;
    
    public interface ClearImageCallback {
        public void clearImage();
    }
    
    public interface CameraResultCallback {
        public void handleCameraResult(Bitmap bitmap);
    }
    
    public static void showPictureLauncher(final Activity activity, final ClearImageCallback clearImageOption) {
        ArrayList<String> options = new ArrayList<String>();

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = activity.getPackageManager();

        final boolean cameraAvailable = pm.queryIntentActivities(cameraIntent, 0).size() > 0;
        if(cameraAvailable)
            options.add(activity.getString(R.string.picture_camera));
        options.add(activity.getString(R.string.picture_gallery));

        if (clearImageOption != null)
            options.add(activity.getString(R.string.picture_clear));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_dropdown_item, options.toArray(new String[options.size()]));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @SuppressWarnings("nls")
            @Override
            public void onClick(DialogInterface d, int which) {
                if(which == 0 && cameraAvailable) {
                	tempFile = getTempFile(activity);
                	if(tempFile != null){
                		//Log.i("file Name", tempFile.getName());
                		Toast.makeText(activity,"file Name is not null", Toast.LENGTH_LONG).show();
                	}
                	else{
                	   //Log.i("file", "not found");
                	   Toast.makeText(activity,"file Name is null", Toast.LENGTH_LONG).show();
                	}
                    //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   
                   // activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } 
                /*else if ((which == 1 && cameraAvailable) || (which == 0 && !cameraAvailable)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    activity.startActivityForResult(Intent.createChooser(intent,
                            activity.getString(R.string.actfm_TVA_tag_picture)), REQUEST_CODE_PICTURE);
                } else {
                    if (clearImageOption != null)
                        clearImageOption.clearImage();
                }
                */
            }
        };

        // show a menu of available options
        new AlertDialog.Builder(activity)
        .setAdapter(adapter, listener)
        .show().setOwnerActivity(activity);
    }
    
 
    private static File getTempFile(Activity activity) {
        try {
            String storageState = Environment.getExternalStorageState();
            if(storageState.equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getName() + File.separatorChar + "Android/data/" + activity.getPackageName() + "/files/";
                File photoFile = File.createTempFile("comment_pic_" + System.currentTimeMillis(), ".jpg", new File(path));
                return photoFile;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
    
    public static boolean activityResult(Activity activity, int requestCode, int resultCode, Intent data,
            CameraResultCallback cameraResult) {
        if(requestCode == CameraModule.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;
            bitmap = data.getParcelableExtra("data"); //$NON-NLS-1$
            if(bitmap != null) {
                activity.setResult(Activity.RESULT_OK);
                cameraResult.handleCameraResult(bitmap);
            }
            return true;
        } 
        /*else if(requestCode == CameraModule.REQUEST_CODE_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Bitmap bitmap = bitmapFromUri(activity, uri);
            if(bitmap != null) {
                activity.setResult(Activity.RESULT_OK);
                cameraResult.handleCameraResult(bitmap);
            }
            return true;
        }
        */
        return false;
    }
		
}
