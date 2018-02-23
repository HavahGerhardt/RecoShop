package reco.recoshop.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompatSideChannelService;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

import reco.recoshop.Product;
import reco.recoshop.R;
import reco.recoshop.Reco;

public class RecosListAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Reco> recos;
    private ArrayList<Reco> copyRecos;
    private Context context;
    private LayoutInflater inflater;

    public RecosListAdapter(Context context, ArrayList<Reco> recos) {
        this.context = context;
        this.recos = recos;
        this.copyRecos = recos;
    }

    @Override
    public int getCount() {
        return recos.size();
    }

    @Override
    public Object getItem(int position) {
        return recos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.reco, null);
        }

        // Get the current reco.
        Reco currentReco = recos.get(position);

        final ImageView imageView = gridView.findViewById(R.id.reco_ivImage);
        TextView tvTitle = gridView.findViewById(R.id.reco_tvText);
        TextView tvUser = gridView.findViewById(R.id.reco_tvUser);
        TextView tvLikes = gridView.findViewById(R.id.reco_tvLikes);
        ImageView ivLikes = gridView.findViewById(R.id.reco_ivLikes);

        // Get the reco.
        StorageReference recoStorageRef = FirebaseStorage.getInstance().getReference().child(currentReco.getImageUri());
        recoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(context).load(R.drawable.no_image_available).into(imageView);
            }
        });

        tvTitle.setText(currentReco.getDescription());
        tvUser.setText(currentReco.getUser());

        int likes = currentReco.getLikes();

        // If the currentReco hasn't liked yet. Hide it.
        if(likes < 1) {
            tvLikes.setVisibility(View.INVISIBLE);
            ivLikes.setVisibility(View.INVISIBLE);
        }
        // Show the currentReco's likes.
        else {
            tvLikes.setVisibility(View.VISIBLE);
            ivLikes.setVisibility(View.VISIBLE);
            tvLikes.setText(Integer.toString(likes));
        }

        return gridView;
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
                    ArrayList<Reco> founded = new ArrayList<>();
                    for (Reco reco : copyRecos) {

                        // Found recos that contain the search word in their title or user name./s.
                        if (reco.getTitle().toString().toLowerCase().contains(constraint) ||
                                reco.getUser().toString().toLowerCase().contains(constraint)) {
                            founded.add(reco);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();
                } else {

                    result.values = copyRecos;
                    result.count = copyRecos.size();
                }

                return result;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                // Set the result into recos list and notify the adapter.
                recos = (ArrayList<Reco>) results.values;
                notifyDataSetChanged();

            }

        };
    }
}
