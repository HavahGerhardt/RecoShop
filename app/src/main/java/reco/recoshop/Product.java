package reco.recoshop;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class Product implements Serializable {

    private String name;
    private String category;
    private String company;
    private String imageUri;
    private String id;
    private float rating;

    /**
     * Constructor for a product with rating. Used for products from DB.
     * @param name
     * @param category
     * @param company
     * @param imageUri
     * @param rating
     */
    public Product (String name, String category, String company, String imageUri, float rating)
    {
        this.name = name;
        this.category = category;
        this.company = company;
        this.imageUri = imageUri;
        this.rating = rating;

        id = name + " " + company;
    }

    /**
     * Constructor for a product without rating.
     * @param name
     * @param category
     * @param company
     * @param imageUri
     */
    public Product (String name, String category, String company, String imageUri)
    {
        this(name,category,company,imageUri,0.0f);
    }

    /**
     * Get name.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get category.
     * @return
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set category.
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Get company.
     * @return
     */
    public String getcompany() {
        return company;
    }

    /**
     * Get Image uri.
     * @return
     */
    public String getImage() {
        return imageUri;
    }

    /**
     * Set image uri.
     * @param imageName
     */
    public void setImage(String imageName) {
        this.imageUri = imageName;
    }

    /**
     * Get id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Get rating.
     * @return
     */
    public float getRating() {
        return rating;
    }

    /**
     * Set rating.
     * @param rating
     * @return
     */
    public void setRating(float rating)
    {
        this.rating = rating;
    }

    /**
     * Product to Map object. Used for upload product to DB.
     * @return Map<String, Object> productMap.
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("category", category);
        result.put("company",company);
        result.put("image", imageUri);
        result.put("rating",rating);

        return result;
    }

    /**
     * Get a product from DataSnapshot object. Used to get a product from DB.
     * @param dataSnapshot productDataSnapshot
     * @return
     */
    public static Product toProduct(DataSnapshot dataSnapshot)
    {
        try {
            HashMap<String, String> mapProduct = (HashMap<String, String>) dataSnapshot.getValue();

            String name = mapProduct.get("name").toString();
            String category = mapProduct.get("category").toString();
            String company = mapProduct.get("company").toString();
            String image = mapProduct.get("image").toString();
            Object ratingObj = mapProduct.get("rating");

            float rating;
            if (ratingObj instanceof Double) {
                rating = ((Double) ratingObj).floatValue();
            } else {
                rating = ((Long) ratingObj).floatValue();
            }

            return new Product(name, category, company, image, rating);
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Product)
        {
            Product oProduct = (Product)obj;
            return (id.equals(oProduct.getId()));
        }
        return false;
    }


}
