package reco.recoshop.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import reco.recoshop.*;
import reco.recoshop.Adapters.CategoriesGridAdapter;
import reco.recoshop.Category;

/**
 * The Categories activity. It presents all categories names and images.
 */
public class Categories extends AppCompatActivity {

    private DatabaseReference categoriesRef;

    private GridView gv;
    private CategoriesGridAdapter categoriesGridAdapter;

    private ArrayList<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        gv = findViewById(R.id.categories_gv);

        // Create and set the CategoriesGridAdapter.
        categoriesGridAdapter = new CategoriesGridAdapter(Categories.this, categories);
        gv.setAdapter(categoriesGridAdapter);

        // Get categories names and images reference from firebase.
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("Categories img");

        setCategoryEventListener();

        setCategoryOnClickListener();
    }

    /**
     * Set the category's firebase event listener.
     */
    private void setCategoryEventListener() {

        // Called when a child of categoriesRef has been added, changed or removed from firebase.
        categoriesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // A new category has been added.

                // Add the new category to categories list and notify the categoriesGridAdapter.
                categories.add(Category.toCategory(dataSnapshot));
                categoriesGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // A category has been changed.

                // Get the changed category's index.
                int childIndex = getCategoryIndex(dataSnapshot);

                // Update the categories list and notify the categoriesGridAdapter.
                categories.set(childIndex, Category.toCategory(dataSnapshot));
                categoriesGridAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // A category has been removed.

                // Get the removed category.
                int childIndex = getCategoryIndex(dataSnapshot);

                // Remove the category from categories list and notify the categoriesGridAdapter.
                categories.remove(childIndex);
                categoriesGridAdapter.notifyDataSetChanged();

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
     * Set the category onClick listener.
     */
    private void setCategoryOnClickListener() {

        // Called when the user clicks on a category.
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Get the selected category.
                Category selectedCategory = categories.get(position);

                // Start the category activity
                Intent iCategory = new Intent(Categories.this, CategoryActivity.class);
                iCategory.putExtra(CategoryActivity.CATEGORY, selectedCategory);
                startActivity(iCategory);
            }
        });
    }

    /**
     * Get the category's index from the categories list.
     *
     * @param dataSnapshot
     * @return
     */
    private int getCategoryIndex(DataSnapshot dataSnapshot) {
        return categories.indexOf(Category.toCategory(dataSnapshot));
    }

    @Override
    public void onBackPressed() {
        // Try to sign out.
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
        }

        finish();

    }
}
