package reco.recoshop;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

/**
 * ImageManipulations class used for add an image to a new product or reco.
 */
public class ImageManipulations {

    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_IMAGE = 1;
    private String userChosenTask;
    private Activity activity;

    public ImageManipulations(Activity activity) {
        this.activity = activity;
    }

    /**
     * Choose image from gallery or take a photo.
     */
    public void chooseImage() {

        final String options[] = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(activity.getApplicationContext());

                if (result) {
                    userChosenTask = options[item];
                    switch (item) {
                        // User Clicked on "Take a Photo".
                        case 0: {
                            cameraIntent();
                            break;
                        }
                        // User Clicked on "Choose from Library".
                        case 1: {
                            galleryIntent();
                            break;
                        }
                        // User Clicked on "Cancel".
                        case 2: {
                            dialog.dismiss();
                            break;
                        }
                    }

                }
            }
        });
        builder.show();
    }

    /**
     * Open the camera.
     */
    private void cameraIntent() {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(iCamera, REQUEST_CAMERA);
    }

    /**
     * Open the gallery.
     */
    private void galleryIntent() {
        Intent iGallery = new Intent();
        iGallery.setType("image/*");
        iGallery.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(iGallery, "Select Image"), SELECT_IMAGE);
    }

    /**
     * Check the request permission result.
     *
     * @param grantResults
     */
    public void onRequestPermissionsResult(int[] grantResults) {

        // The permission has been granted.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (userChosenTask.equals("Take Photo"))
                cameraIntent();
            else if (userChosenTask.equals("Choose from Library"))
                galleryIntent();
        } else {
            // User denied the permission request.
            Toast.makeText(activity.getApplicationContext(), "You must confirm the permission request for uploading an image!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Upload an image to firebase and return its path.
     *
     * @param imageId
     * @return
     */
    public static String uploadImage(Context context, String imageId, ImageView iv) {

        try {
            StorageReference imageRef = Utility.STORAGE_REFERENCE.child(imageId);

            // Get the data from an ImageView as bytes
            iv.setDrawingCacheEnabled(true);
            iv.buildDrawingCache();
            Bitmap bitmap = iv.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // Save the image's bytes in DB.
            imageRef.putBytes(data);

            // Return the image's path
            return imageRef.getPath().toString();
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Can't upload image. " + e.getMessage(), Toast.LENGTH_LONG).show();
            return "";
        }
    }
}
