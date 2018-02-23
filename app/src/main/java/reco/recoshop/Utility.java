package reco.recoshop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;


/**
 * The Utility class.
 */
public class Utility {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final DatabaseReference DB_REF = FirebaseDatabase.getInstance().getReference("Categories");
    public static final StorageReference STORAGE_REFERENCE = FirebaseStorage.getInstance().getReference();

    /**
     * checkPermission used to check if there's READ_EXTERNAL_STORAGE permission.
     * @param context
     * @return true if there's READ_EXTERNAL_STORAGE permission., else return false.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        // The currentAPIVersion is better then Marshmallow.
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            // The READ_EXTERNAL_STORAGE hasn't been granted.
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Ask for permission.
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();

                return false;
            } else {

                // The READ_EXTERNAL_STORAGE has been granted.
                return true;
            }
        } else {

            // Permission is automatically granted on sdk<23 upon installation.
            return true;
        }
    }

    /**
     * Upload data to DB.
     * @param activity
     * @param databaseRef
     * @param data
     * @param dataType
     */
    public static void uploadData(final Activity activity, DatabaseReference databaseRef, final Object data, final String dataType) {

        final ProgressDialog progressBarLogin = ProgressDialog.show(activity, "",
                "Saving " + dataType + "...", true);

        try {
            databaseRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    progressBarLogin.dismiss();
                    Toast.makeText(activity, "The " + dataType + " have been saved", Toast.LENGTH_SHORT).show();

                    // Close activity
                    activity.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // The task failed.
                    progressBarLogin.dismiss();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(activity, "Can't upload the " + dataType + ". " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


}


