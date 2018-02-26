package reco.recoshop.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import reco.recoshop.Product;
import reco.recoshop.R;
import reco.recoshop.Reco;
import reco.recoshop.Utility;

public class RecoActivity extends AppCompatActivity {

    public static final String RECO = "Reco";
    public static final String PRODUCT = "Product";

    private Reco currentReco;
    private Product product;
    private TextView tvProductName, tvCompany, tvCategory, tvRating;
    private TextView tvTitle, tvUser, tvDescription, tvLikes;
    private ImageView ivProduct, ivReco, ivLike, ivStar;
    private Button bAddLike;

    private DatabaseReference productRef;
    private DatabaseReference recoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);

        Intent iCategory = getIntent();
        currentReco = (Reco) iCategory.getSerializableExtra(RECO);
        product = (Product) iCategory.getSerializableExtra(PRODUCT);

        tvProductName = findViewById(R.id.reco_activity_tvName);
        tvCompany = findViewById(R.id.reco_activity_tvCompany);
        tvCategory = findViewById(R.id.reco_activity_tvCategory);
        tvRating = findViewById(R.id.reco_activity_tvRating);
        ivProduct = findViewById(R.id.reco_activity_ivProduct);
        ivStar = findViewById(R.id.reco_activity_ivStar);

        fillProductFields();

        productRef = Utility.DB_REF.child(product.getCategory()).child(product.getId());
        recoRef = productRef.child("Recos").child(currentReco.getRecoId());

        tvTitle = findViewById(R.id.reco_activity_tvTitle);
        tvUser = findViewById(R.id.reco_activity_tvUser);
        tvDescription = findViewById(R.id.reco_activity_tvDescription);
        tvLikes = findViewById(R.id.reco_activity_tvLikes);
        ivReco = findViewById(R.id.reco_activity_ivReco);
        bAddLike = findViewById(R.id.reco_activity_bAddLike);
        ivLike = findViewById(R.id.reco_activity_ivLike);

        fillRecoFields();

        setProductAndRecoListener();

    }

    /**
     * Fill the reco's fields.
     */
    private void fillRecoFields() {

        // Fill reco's fields.
        tvTitle.setText(currentReco.getTitle());
        tvUser.setText(currentReco.getUser());
        tvDescription.setText(currentReco.getDescription());
        tvLikes.setText(String.valueOf(currentReco.getLikes()));

        // Get reco's image.
        Utility.STORAGE_REFERENCE.child(currentReco.getImageUri()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(RecoActivity.this).load(uri).into(ivReco);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(RecoActivity.this).load(R.drawable.no_image_available).into(ivReco);
            }
        });

        fillRecoLikes();

    }

    /**
     * Fill the product's fields.
     */
    private void fillProductFields() {

        // Fill product's fields.
        tvProductName.setText(product.getName());
        tvCompany.setText(product.getcompany());
        tvCategory.setText(product.getCategory());
        tvRating.setText(String.valueOf(product.getRating()));

        // Get product's image.
        Utility.STORAGE_REFERENCE.child(product.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(RecoActivity.this).load(uri).into(ivProduct);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(RecoActivity.this).load(R.drawable.no_image_available).into(ivProduct);
            }
        });

        fillProductRating();
    }


    /**
     * Fill the reco's likes.
     */
    private void fillRecoLikes() {
        // If the currentReco hasn't been liked yet. Hide it.
        if (currentReco.getLikes() == 0) {
            tvLikes.setVisibility(View.INVISIBLE);
            ivLike.setVisibility(View.INVISIBLE);
        }
        // Show the currentReco's likes.
        else {
            tvLikes.setVisibility(View.VISIBLE);
            ivLike.setVisibility(View.VISIBLE);
            tvLikes.setText(String.valueOf(currentReco.getLikes()));
        }
    }

    /**
     * Fill the product's rating.
     */
    private void fillProductRating() {
        // If the product hasn't rated yet. Hide it.
        if (product.getRating() == 0) {
            tvRating.setVisibility(View.INVISIBLE);
            ivStar.setVisibility(View.INVISIBLE);
        }
        // Show the product's rating.
        else {
            tvRating.setVisibility(View.VISIBLE);
            ivStar.setVisibility(View.VISIBLE);
            tvRating.setText(String.valueOf(product.getRating()));
        }
    }

    /**
     * Set the product's rating and reco's likes listener.
     */
    private void setProductAndRecoListener() {
        final DatabaseReference productRef = Utility.DB_REF.child(product.getCategory()).child(product.getId());
        DatabaseReference recoRef = productRef.child("Recos").child(currentReco.getRecoId());


        productRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("rating")) {
                    Object ratingObj = dataSnapshot.getValue();
                    float rating;
                    if (ratingObj instanceof Double) {
                        rating = ((Double) ratingObj).floatValue();
                    } else {
                        rating = ((Long) ratingObj).floatValue();
                    }

                    product.setRating(rating);

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

        recoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals("recoLikes")) {

                    int likes = ((Long) dataSnapshot.getValue()).intValue();

                    currentReco.setLikes(likes);
                    fillRecoLikes();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Add like.
     *
     * @param view
     */
    public void addLike(View view) {

        // Get likes reference.
        final DatabaseReference likesRef = Utility.DB_REF.child(product.getCategory()).child(product.getId()).child("Recos").child(currentReco.getRecoId()).child("recoLikes");
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    // Gets old like count.
                    int likes = dataSnapshot.getValue(Integer.class);
                    likes++;

                    // Update the new likes count.
                    likesRef.setValue(likes);

                    // Prevent adding more likes.
                    bAddLike.setEnabled(false);

                } catch (Exception e) {
                    Toast.makeText(RecoActivity.this, "Failed to add like: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Open the reco's link on browser.
     *
     * @param view
     */
    public void goShopping(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentReco.getLink()));
        startActivity(browserIntent);
    }
}
