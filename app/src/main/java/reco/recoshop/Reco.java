package reco.recoshop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Reco implements Serializable {

    private String recoId;
    private String user;
    private String title;
    private String link;
    private String description;
    private String imageName;
    private int productRating;
    private int recoLikes;

    /**
     * Constructor for a reco with likes. Used for recos from DB.
     *
     * @param recoId
     * @param user
     * @param title
     * @param link
     * @param description
     * @param imageUri
     * @param productRating
     * @param recoLikes
     */
    public Reco(String recoId, String user, String title, String link, String description, String imageUri, int productRating, int recoLikes) {

        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "http://" + link;
        }

        this.recoId = recoId;
        this.user = user;
        this.title = title;
        this.link = link;
        this.description = description;
        this.imageName = imageUri;
        this.productRating = productRating;
        this.recoLikes = recoLikes;
    }

    /**
     * Constructor for a reco without likes.
     *
     * @param recoId
     * @param user
     * @param title
     * @param link
     * @param description
     * @param imageUri
     * @param productRating
     */
    public Reco(String recoId, String user, String title, String link, String description, String imageUri, int productRating) {
        this(recoId, user, title, link, description, imageUri, productRating, 0);
    }

    /**
     * Get title.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title.
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get link.
     */
    public String getLink()
    {
        return link;
    }

    /**
     * Get image Uri.
     *
     * @return
     */
    public String getImageUri() {
        return imageName;
    }

    /**
     * Get description.
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get user name.
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * Set user name.
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Get likes.
     *
     * @return
     */
    public int getLikes() {
        return recoLikes;
    }

    /**
     * Set likes.
     *
     * @param likes
     */
    public void setLikes(int likes) {
        this.recoLikes = likes;
    }

    /**
     * Get product's rating
     *
     * @return
     */
    public float getProductRate() {
        return productRating;
    }

    /**
     * Get id.
     *
     * @return
     */
    public String getRecoId() {
        return recoId;
    }

    /**
     * Reco to Map object. Used for upload reco to DB.
     *
     * @return Map<String, Object> recoMap.
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("user", user);
        result.put("title", title);
        result.put("link", link);
        result.put("description", description);
        result.put("image", imageName);
        result.put("productRating", productRating);
        result.put("recoLikes", recoLikes);

        return result;
    }

    /**
     * Get reco from DataSnapshot object. Used to get a reco from DB.
     *
     * @param dataSnapshot recoDataSnapshot.
     * @return Reco.
     */
    public static Reco toReco(DataSnapshot dataSnapshot) {
        try {
            String recoId = dataSnapshot.getKey().toString();
            HashMap<String, Object> mapProduct = (HashMap<String, Object>) dataSnapshot.getValue();

            String user = mapProduct.get("user").toString();
            String title = mapProduct.get("title").toString();
            String link = mapProduct.get("link").toString();
            String description = mapProduct.get("description").toString();
            String image = mapProduct.get("image").toString();
            int productRating = ((Long) mapProduct.get("productRating")).intValue();
            int recoLikes = ((Long) mapProduct.get("recoLikes")).intValue();

            return new Reco(recoId, user, title, link, description, image, productRating, recoLikes);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reco) {
            Reco recoObj = (Reco) obj;
            return (recoId.equals(recoObj.getRecoId()) && user.equals(recoObj.user) && title.equals(recoObj.title) && description.equals(recoObj.description));
        }
        return false;
    }
}
