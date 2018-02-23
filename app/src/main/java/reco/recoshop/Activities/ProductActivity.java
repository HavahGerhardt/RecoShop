package reco.recoshop.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import reco.recoshop.Adapters.RecosListAdapter;
import reco.recoshop.Product;
import reco.recoshop.R;
import reco.recoshop.Reco;
import reco.recoshop.Utility;

/**
 * The product activity. It presents a product and its recos.
 */
public class ProductActivity extends AppCompatActivity {

    public static final String PRODUCT = "Product";
    private Product currentProduct;
    private ArrayList<Reco> recos;

    private TextView tvName, tvCategory, tvCompany, tvRating;
    private ImageView ivProduct, ivStar;
    private SearchView svRecos;

    private ListView lvRecos;
    private RecosListAdapter recosListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recos = new ArrayList<>();

        // Get the current product object.
        Intent iProduct = getIntent();
        currentProduct = (Product) iProduct.getSerializableExtra(PRODUCT);

        tvName = findViewById(R.id.product_activity_tvName);
        tvCategory = findViewById(R.id.product_activity_tvCategory);
        tvCompany = findViewById(R.id.product_activity_tvCompany);
        tvRating = findViewById(R.id.product_activity_tvRating);
        ivProduct = findViewById(R.id.product_activity_ivProduct);
        ivStar = findViewById(R.id.product_activity_ivStar);
        lvRecos = findViewById(R.id.product_activity_lv);
        svRecos = findViewById(R.id.product_activity_sv);

        fillProductFields();

        setProductRatingListener();

        // Create and set the recosListAdapter.
        recosListAdapter = new RecosListAdapter(ProductActivity.this, recos);
        lvRecos.setAdapter(recosListAdapter);

        setRecoEventListener();

        setRecoOnClickListener();

        setOnQueryTextListener();
    }

    /**
     * Fill the product's fields.
     */
    private void fillProductFields() {

        // Set product's fields.
        tvName.setText(currentProduct.getName());
        tvCategory.setText(currentProduct.getCategory());
        tvCompany.setText(currentProduct.getcompany());

        fillProductRating();

        loadProductImage();

    }

    /**
     * Fill the product's rating.
     */
    private void fillProductRating() {
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
    }

    /**
     * Set the product's rating listener.
     */
    private void setProductRatingListener() {

        // Get product's rating reference.
        DatabaseReference productRef = Utility.DB_REF.child(currentProduct.getCategory()).child(currentProduct.getId());

        productRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("rating")) {

                    Object ratingObj = dataSnapshot.getValue();

                    float rating;

                    // Rating is double.
                    if (ratingObj instanceof Double) {
                        rating = ((Double) ratingObj).floatValue();
                    } else // Rating is long.
                    {
                        rating = ((Long) ratingObj).floatValue();
                    }

                    currentProduct.setRating(rating);

                    fillProductRating();
                }
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     * Load the product's image from firebase storage.
     */
    private void loadProductImage() {

        String imageName = currentProduct.getImage();

        if (imageName != null && !imageName.isEmpty()) {

            // Get product storage reference from firebase.
            StorageReference productStorageRef = FirebaseStorage.getInstance().getReference().child(currentProduct.getImage());

            // Get image uri.
            productStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // The task has been succeed.

                    // Load the image's uri into ivProduct.
                    Glide.with(ProductActivity.this).load(uri).into(ivProduct);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // The task failed.

                    // Load "no image available" into ivProduct.
                    Glide.with(ProductActivity.this).load(R.drawable.no_image_available).into(ivProduct);
                }
            });

        } else {
            // Load "no image available" into ivCategory.
            Glide.with(getApplicationContext()).load(R.drawable.no_image_available).into(ivProduct);
        }
    }

    /**
     * Set the reco's firebase event listener.
     */
    public void setRecoEventListener() {
        // Get the product's recos reference from firebase.
        DatabaseReference recosRef = Utility.DB_REF.child(currentProduct.getCategory()).child(currentProduct.getId()).child("Recos");

        recosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // A new reco has been added.

                Reco reco = Reco.toReco(dataSnapshot);

                if (reco != null) {
                    // Add the new reco to recos list and notify the recosListAdapter.
                    recos.add(reco);

                    sortRecosByLikes();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // A reco has been changed.

                // Get the changed reco's index.
                int childIndex = getRecoIndex(dataSnapshot);
                if (childIndex >= 0) {
                    Reco reco = Reco.toReco(dataSnapshot);

                    if (reco != null) {
                        // Update the recos list.
                        recos.set(childIndex, reco);
                    } else {
                        // Reco badly formatted. Remove it from recos list.
                        recos.remove(childIndex);
                    }

                    sortRecosByLikes();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // A reco has been removed.

                // Get the removed reco's index.
                int childIndex = getRecoIndex(dataSnapshot);

                if (childIndex >= 0) {

                    // Remove the reco from recos list and notify the recosListAdapter.
                    recos.remove(childIndex);

                    sortRecosByLikes();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Sort recos list by likes.
     */
    private void sortRecosByLikes() {
        Collections.sort(recos, new Comparator<Reco>() {
            public int compare(Reco r1, Reco r2) {
                return r2.getLikes() - r1.getLikes();
            }
        });

        recosListAdapter.notifyDataSetChanged();
    }

    /**
     * Set the searchView's OnQueryText listener.
     */
    private void setOnQueryTextListener() {
        svRecos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recosListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Set the reco's onClick listener.
     */
    private void setRecoOnClickListener() {
        lvRecos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Get the selected reco.
                Reco clickedReco = recos.get(position);

                // Start the RecoActivity.
                Intent iReco = new Intent(ProductActivity.this, RecoActivity.class);
                iReco.putExtra(RecoActivity.RECO, clickedReco);
                iReco.putExtra(RecoActivity.PRODUCT, currentProduct);
                startActivity(iReco);

            }
        });
    }

    /**
     * Get the reco's index from the recos list.
     *
     * @param dataSnapshot
     * @return
     */
    private int getRecoIndex(DataSnapshot dataSnapshot) {
        Reco reco = Reco.toReco(dataSnapshot);

        if (reco != null) {
            return recos.indexOf(reco);
        } else return -1;
    }

    /**
     * Called when the user clicks on the add reco button (The plus icon). It opens the AddReco activity.
     *
     * @param view
     */
    public void AddReco(View view) {
        Intent iAddReco = new Intent(this, AddReco.class);
        iAddReco.putExtra(AddReco.PRODUCT, currentProduct);
        startActivity(iAddReco);
    }
}
