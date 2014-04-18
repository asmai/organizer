package org.musalahuddin.myexpenseorganizer.camera;

import org.musalahuddin.myexpenseorganizer.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
//import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class CameraModule {

	protected static final int REQUEST_CODE_CAMERA = 100;
    protected static final int REQUEST_CODE_PICTURE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 1;
    
    // directory name to store captured images and videos
 	private static final String IMAGE_DIRECTORY_NAME = "My Expense Organizer";
    
    private static Uri fileUri; // file url to store image/video
    
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
                	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                	
                	fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    
            		activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                else if ((which == 1 && cameraAvailable) || (which == 0 && !cameraAvailable)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    activity.startActivityForResult(intent, REQUEST_CODE_PICTURE);
                }
                else{
                	clearImageOption.clearImage();
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
    
 
    /*
	 * Creating file uri to store image/video
	 */
	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				/*
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);
		*/
		Environment
		.getExternalStorageDirectory(),
		IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
    
    public static boolean activityResult(Activity activity, int requestCode, int resultCode, Intent data,
            CameraResultCallback cameraResult) {
        if(requestCode == CameraModule.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;
           
            // bimatp factory
 			BitmapFactory.Options options = new BitmapFactory.Options();

 			// downsizing image as it throws OutOfMemory Exception for larger images
 			options.inSampleSize = 8;
 			
 			Toast.makeText(activity, fileUri.getPath(), Toast.LENGTH_LONG).show();
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),options);
            if(bitmap != null) {
                activity.setResult(Activity.RESULT_OK);
                cameraResult.handleCameraResult(bitmap);
            }
            return true;
        } 
        else if(requestCode == CameraModule.REQUEST_CODE_PICTURE && resultCode == Activity.RESULT_OK) {
        	fileUri = data.getData();
        	Bitmap bitmap;
        	
        	String imagePath = getRealPathFromURI(fileUri,activity);
        	
        	 // bimatp factory
 			BitmapFactory.Options options = new BitmapFactory.Options();

 			// downsizing image as it throws OutOfMemory Exception for larger images
 			options.inSampleSize = 8;
 			
 			Toast.makeText(activity, imagePath, Toast.LENGTH_LONG).show();
 			
            bitmap = BitmapFactory.decodeFile(imagePath,options);
            if(bitmap != null) {
                activity.setResult(Activity.RESULT_OK);
                cameraResult.handleCameraResult(bitmap);
            }
            return true;
        }
        
        return false;
    }
    
    @SuppressWarnings("unchecked")
	public static void getRealPathFromURI_(Activity activity) {
    	//Bundle bundle;
    	//bundle.putParcelable("contentUri", contentUri);
    	activity.getLoaderManager().initLoader(0, null,(LoaderManager.LoaderCallbacks<Cursor>) activity);
    }
    
    public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = activity.managedQuery( contentUri, proj, null, null,null);
 
        if (cursor == null) return null;
 
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
 
        cursor.moveToFirst();
 
        return cursor.getString(column_index);
    }
		
}
