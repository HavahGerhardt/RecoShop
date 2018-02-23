package reco.recoshop;


import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;

public class Category implements Serializable {

    private String name;
    private String imageUri;

    public Category(String name, String image) {
        this.name = name;
        this.imageUri = image;
    }

    /**
     * Get name.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get image's uri.
     * @return
     */
    public String getImage() {
        return imageUri;
    }

    /**
     * Get a category from DataSnapshot object. Used to get a category from DB.
     * @param dataSnapshot
     * @return
     */
    public static Category toCategory(DataSnapshot dataSnapshot)
    {
        return new Category(dataSnapshot.getKey().toString(),dataSnapshot.getValue().toString());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Object)
        {
            Category objCategory = (Category)obj;
            return name.equals(objCategory.getName());
        }
        return false;
    }
}
