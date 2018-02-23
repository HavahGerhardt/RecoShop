package reco.recoshop.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import reco.recoshop.ImageManipulations;
import reco.recoshop.Product;
import reco.recoshop.R;
import reco.recoshop.Utility;

public class AddProduct extends AppCompatActivity {

    public static String CATEGORY_NAME = "Category Name";
    private String categoryName;
    private ImageView iv;
    private EditText etName, etCompany, etCategory;
    private Button bSaveProduct;
    private DatabaseReference productsRef;
    private ImageManipulations imageManipulations;

    private boolean userChosenImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        iv = findViewById(R.id.addProduct_iv);
        etName = findViewById(R.id.addProduct_etName);
        etCompany = findViewById(R.id.addProduct_etCompany);
        etCategory = findViewById(R.id.addProduct_etCategory);
        bSaveProduct = findViewById(R.id.addProduct_bSave);

        // Get category' name.
        Intent iCategory = getIntent();
        categoryName = iCategory.getStringExtra(CATEGORY_NAME);
        etCategory.setText(categoryName);

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

        etName.addTextChangedListener(tw);
        etCompany.addTextChangedListener(tw);

        // Get products' reference.
        productsRef = Utility.DB_REF.child(categoryName);
    }

    /**
     * Enable/Unable the save button
     */
    private void enableSaveButton() {

        if (etName.getText().length() > 0 &&
                etCompany.getText().length() > 0 &&
                etCategory.getText().length() > 0 &&
                userChosenImage) {
            bSaveProduct.setEnabled(true);
        } else {
            bSaveProduct.setEnabled(false);
        }

    }

    /**
     * Called when the user clicks on the "Choose Image" button.
     *
     * @param view
     */
    public void chooseImage(View view) {

        imageManipulations = new ImageManipulations(AddProduct.this);
        imageManipulations.chooseImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageManipulations.REQUEST_CAMERA || requestCode == ImageManipulations.SELECT_IMAGE) {
                // Load the image into iv.
                Glide.with(AddProduct.this).load(data.getData()).into(iv);

                userChosenImage = true;
                enableSaveButton();
            } else {
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
     * Called when the user clicks on "Save Product".
     *
     * @param view
     */
    public void saveProduct(View view) {

        String name = etName.getText().toString();
        String company = etCompany.getText().toString();
        String imageId = name + " " + company;

        // Upload image and get its uri.
        String imageUri = ImageManipulations.uploadImage(AddProduct.this, imageId, iv);

        if (!imageUri.isEmpty()) {

            DatabaseReference currentProductRef = productsRef.child(imageId);

            Product product = new Product(name, categoryName, company, imageUri);

            Map<String, Object> productMap = product.toMap();

            // Upload productMap.
            Utility.uploadData(AddProduct.this, currentProductRef, productMap, "product");
        }
    }


}

