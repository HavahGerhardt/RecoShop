package reco.recoshop.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import reco.recoshop.Product;
import reco.recoshop.R;

/**
 * The products list adapter. It's used by the listView at the Category activity.
 */
public class ProductsListAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Product> products;
    private ArrayList<Product> copyProducts;
    private Context context;
    private LayoutInflater inflater;

    public ProductsListAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        this.copyProducts = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listView = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.product, null);
        }

        // Get the current product.
        Product currentProduct = products.get(position);

        TextView tvName = listView.findViewById(R.id.product_tvName);
        final ImageView iv = listView.findViewById(R.id.product_ivProduct);
        TextView tvRating = listView.findViewById(R.id.product_tvRating);
        ImageView ivStar = listView.findViewById(R.id.product_ivStar);

        // If the currentProduct hasn't rated yet. Hide it.
        if (currentProduct.getRating() == 0) {
            tvRating.setVisibility(View.INVISIBLE);
            ivStar.setVisibility(View.INVISIBLE);
        }
        // Show the currentProduct's rating.
        else {
            tvRating.setVisibility(View.VISIBLE);
            ivStar.setVisibility(View.VISIBLE);
            tvRating.setText(String.valueOf(currentProduct.getRating()));
        }

        // Set product's name.
        tvName.setText(currentProduct.getName());

        String imageName = currentProduct.getImage();

        if (imageName != null && !imageName.isEmpty()) {

            // Get the image's url.
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(currentProduct.getImage());

            // Load the product's image (from firebase) into imageView.
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

        } else {
            // Load "no image available" into iv.
            Glide.with(context).load(R.drawable.no_image_available).into(iv);
        }

        return listView;
    }


    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                // Get the search word in lowercase.
                constraint = constraint.toString().toLowerCase();

                FilterResults result = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<Product> founded = new ArrayList<>();
                    for (Product product : copyProducts) {

                        // Found products that contain the search word/s.
                        if (product.getId().toString().toLowerCase().contains(constraint)) {
                            founded.add(product);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();
                } else {

                    result.values = copyProducts;
                    result.count = copyProducts.size();
                }

                return result;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                // Set the result into products list and notify the adapter.
                products = (ArrayList<Product>) results.values;
                notifyDataSetChanged();

            }

        };
    }
}
