package reco.recoshop.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import reco.recoshop.Adapters.ProductsListAdapter;
import reco.recoshop.Category;
import reco.recoshop.Product;
import reco.recoshop.R;
import reco.recoshop.Utility;

/**
 * The category activity. It presents a category and its products.
 */
public class CategoryActivity extends AppCompatActivity {

    public static final String CATEGORY = "Category";
    private Category currentCategory;

    private ArrayList<Product> products;
    private TextView tvCategoryName;
    private ImageView ivCategory;
    private ListView listView;
    private SearchView searchView;

    private ProductsListAdapter productsListAdapter;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        tvCategoryName = findViewById(R.id.category_tvName);
        ivCategory = findViewById(R.id.category_iv);
        listView = findViewById(R.id.category_lv);
        searchView = findViewById(R.id.category_sv);

        // Get the current category object.
        Intent iCategory = getIntent();
        currentCategory = (Category) iCategory.getSerializableExtra(CATEGORY);

        // Set the category's name.
        tvCategoryName.setText(currentCategory.getName());

        loadCategoryImage();

        products = new ArrayList<>();

        // Create and set the productsListAdapter.
        productsListAdapter = new ProductsListAdapter(CategoryActivity.this, products);
        listView.setAdapter(productsListAdapter);

        // Get the category's products reference from firebase.
        productsRef = Utility.DB_REF.child(currentCategory.getName());

        setProductEventListener();

        setProductOnClickListener();

        setOnQueryTextListener();
    }

    /**
     * Load the category's image from firebase storage.
     */
    private void loadCategoryImage() {

        String imageName = currentCategory.getImage();

        if (imageName != null && !imageName.isEmpty()) {

            // Get the image reference from firebase's storage.
            StorageReference categoryImageRef = FirebaseStorage.getInstance().getReference().child(imageName);

            // Get image's url.
            categoryImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // The task has been succeed.

                    // Load the image's uri into ivCategory.
                    Glide.with(getApplicationContext()).load(uri).into(ivCategory);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // The task failed.

                    // Load "no image available" into ivCategory.
                    Glide.with(getApplicationContext()).load(R.drawable.no_image_available).into(ivCategory);
                }
            });
        } else {
            // Load "no image available" into ivCategory.
            Glide.with(getApplicationContext()).load(R.drawable.no_image_available).into(ivCategory);
        }
    }

    /**
     * Set the product's firebase event listener.
     */
    private void setProductEventListener() {

        // Called when a child of productsRef has been added, changed or removed from firebase.
        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // A new product has been added.

                Product product = Product.toProduct(dataSnapshot);

                if (product != null) {
                    // Add the new product to products list and notify the productsListAdapter.
                    products.add(product);

                    sortProductsByRating();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // A product has been changed.

                // Get the changed product's index.
                int childIndex = getProductIndex(dataSnapshot);

                if (childIndex >= 0) {

                    Product product = Product.toProduct(dataSnapshot);

                    if (product != null) {
                        // Update the products list.
                        products.set(childIndex, product);
                    } else {
                        // Product badly formatted. Remove it from recos list.
                        products.remove(childIndex);
                    }

                    sortProductsByRating();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // A product has been removed.

                // Get the removed product.
                int childIndex = getProductIndex(dataSnapshot);

                if (childIndex >= 0) {
                    // Remove the product from products list and notify the productsListAdapter.
                    products.remove(childIndex);

                    sortProductsByRating();
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
     * Sort products list by rating.
     */
    private void sortProductsByRating() {
        Collections.sort(products, new Comparator<Product>() {
            public int compare(Product p1, Product p2) {
                return (-1) * Float.compare(p1.getRating(), p2.getRating());
            }
        });

        productsListAdapter.notifyDataSetChanged();
    }

    /**
     * Set the searchView's OnQueryText listener.
     */
    private void setOnQueryTextListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productsListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Set the product's onClick listener.
     */
    private void setProductOnClickListener() {
        // Called when the user clicks on a product.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Get the selected product.
                Product clickedProduct = products.get(position);

                // Start the ProductActivity.
                Intent iProduct = new Intent(CategoryActivity.this, ProductActivity.class);
                iProduct.putExtra(ProductActivity.PRODUCT, clickedProduct);
                startActivity(iProduct);

            }
        });
    }

    /**
     * Get the product's index.
     *
     * @param dataSnapshot
     * @return
     */
    private int getProductIndex(DataSnapshot dataSnapshot) {
        Product product = Product.toProduct(dataSnapshot);
        if (product != null) {
            return products.indexOf(product);
        } else return -1;

    }

    /**
     * Called when the user clicks on the add product button (The plus icon). It opens the AddProduct activity.
     *
     * @param view
     */
    public void AddProduct(View view) {
        Intent iAddProduct = new Intent(this, AddProduct.class);
        iAddProduct.putExtra(AddProduct.CATEGORY_NAME, currentCategory.getName());
        startActivity(iAddProduct);
    }


}
