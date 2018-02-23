package reco.recoshop.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import reco.recoshop.Category;
import reco.recoshop.R;

/**
 * The categories grid adapter. It's used by the gridView at the Categories activity.
 */
public class CategoriesGridAdapter extends BaseAdapter {

    private ArrayList<Category> categories;
    private Context context;
    private LayoutInflater inflater;

    public CategoriesGridAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.category, null);
        }

        // Get the current category.
        Category currentCategory = categories.get(position);

        final ImageView iv = gridView.findViewById(R.id.category_iv);
        TextView tv = gridView.findViewById(R.id.category_tv);

        // Set the category's name.
        tv.setText(currentCategory.getName());

        String imageName = currentCategory.getImage();

        if(imageName != null && !imageName.isEmpty()) {

            // Get the image reference from firebase's storage.
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(currentCategory.getImage());

            // Get the image's url.
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // The task has been succeed.

                    // Load the image's url into iv.
                    Glide.with(context).load(uri).into(iv);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // The task failed.

                    // Load "no image available" into iv.
                    Glide.with(context).load(R.drawable.no_image_available).into(iv);
                }
            });
        }
        else {
            // Load "no image available" into iv.
            Glide.with(context).load(R.drawable.no_image_available).into(iv);
        }

        return gridView;
    }

}
