package reco.recoshop.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import reco.recoshop.ImageManipulations;
import reco.recoshop.Product;
import reco.recoshop.R;
import reco.recoshop.Reco;
import reco.recoshop.Utility;

/**
 * The AddReco acticity, where users upload their recos.
 */
public class AddReco extends AppCompatActivity {

    public static final String PRODUCT = "Product";
    private DatabaseReference recosRef;
    private ImageView ivRecoImage;
    private Product currentProduct;
    private Button bSave;
    private TextView tvProductName;
    private EditText etTitle, etLink, etDescription;
    private RatingBar rb;
    private ImageManipulations imageManipulations;

    private boolean userChosenImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reco);

        // The product's field.
        tvProductName = findViewById(R.id.addReco_tvProduct);

        // The reco's fields.
        etTitle = findViewById(R.id.addReco_etTitle);
        etLink = findViewById(R.id.addReco_etLink);
        etDescription = findViewById(R.id.addReco_etDesc);
        ivRecoImage = findViewById(R.id.addReco_ivImage);
        rb = findViewById(R.id.addReco_rb);

        bSave = findViewById(R.id.addReco_bSave);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSaveButton();
            }
        };

        etTitle.addTextChangedListener(tw);
        etLink.addTextChangedListener(tw);
        etDescription.addTextChangedListener(tw);

        // Get the current product object.
        Intent iProduct = getIntent();
        currentProduct = (Product) iProduct.getSerializableExtra(PRODUCT);

        // Set product's name.
        tvProductName.setText(tvProductName.getText() + "\n" + currentProduct.getId());

        // Get recos' reference.
        recosRef = Utility.DB_REF.child(currentProduct.getCategory()).child(currentProduct.getId()).child("Recos");
    }


    /**
     * Enable/Unable the save button.
     */
    private void enableSaveButton() {

        if (etTitle.getText().length() > 0 &&
                etLink.getText().length() > 0 &&
                etDescription.getText().length() > 0 &&
                userChosenImage) {
            bSave.setEnabled(true);
        } else {
            bSave.setEnabled(false);
        }

    }

    /**
     * Called when the user clicks on the "Choose Image" button.
     *
     * @param view
     */
    public void chooseImage(View view) {

        imageManipulations = null;

        imageManipulations = new ImageManipulations(AddReco.this);
        imageManipulations.chooseImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageManipulations.REQUEST_CAMERA || requestCode == ImageManipulations.SELECT_IMAGE) {
                // Load the image into ivRecoImage.
                Glide.with(AddReco.this).load(data.getData()).into(ivRecoImage);
                userChosenImage = true;
                enableSaveButton();
            }
            else{
                userChosenImage = false;
                enableSaveButton();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                imageManipulations.onRequestPermissionsResult(grantResults);
                break;
            }
        }
    }

    /**
     * Called when the user clicks on "Save Reco".
     *
     * @param view
     */
    public void saveReco(View view) {

        // Create reco child.
        DatabaseReference databaseRef = recosRef.push();

        // Get reco's id.
        String recoId = databaseRef.getKey();

        String imageId = currentProduct.getId() + " " + recoId;

        // Upload image and get its uri.
        String imageUri = ImageManipulations.uploadImage(AddReco.this, imageId, ivRecoImage);

        if (!imageUri.isEmpty()) {
            try {

                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                final Reco reco = new Reco(recoId, user, etTitle.getText().toString(), etLink.getText().toString(), etDescription.getText().toString(), imageUri, (int)rb.getRating());

                Map<String, Object> recoMap = reco.toMap();

                // Upload recoMap.
                Utility.uploadData(AddReco.this, databaseRef, recoMap, "reco");

                // Update product's rating.
                updateProductRating(reco);
            } catch (Exception e) {
                Toast.makeText(AddReco.this, "Can't upload reco. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Update the product's rating.
     *
     * @param reco
     */
    private void updateProductRating(final Reco reco) {

        try {

            // Get the product's rating reference from firebase
            final DatabaseReference productRatingRef = Utility.DB_REF.child(currentProduct.getCategory()).child(currentProduct.getId()).child("rating");

            // Count product's recos.
            recosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final int count = ((Long) dataSnapshot.getChildrenCount()).intValue();

                    // Get the older product's rating.
                    productRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            float oldRating;

                            try {
                                // Rating is double.
                                oldRating = dataSnapshot.getValue(double.class).floatValue();
                            } catch (Exception ex) {
                                // Rating is long.
                                oldRating = dataSnapshot.getValue(long.class).floatValue();
                            }

                            // Calculate the new rating.
                            float newRating = (oldRating * (count - 1) + reco.getProductRate()) / count;

                            // Round up rating.
                            newRating = Float.parseFloat(String.format(
                                    "%.1f", newRating));

                            // Update the product's rating.
                            productRatingRef.setValue(newRating);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(AddReco.this, "Can't upload rating. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}