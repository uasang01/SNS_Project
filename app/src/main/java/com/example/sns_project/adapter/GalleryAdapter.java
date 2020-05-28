package com.example.sns_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.sns_project.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.sns_project.Util.INTENT_PATH;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private ArrayList<String> galleryDataset;
    private Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GalleryAdapter(Activity activity, ArrayList<String> galleryDataset) {
        this.galleryDataset = galleryDataset;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (galleryViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(INTENT_PATH, galleryDataset.get(galleryViewHolder.getAdapterPosition()));
                    activity.setResult(activity.RESULT_OK,resultIntent);
                    activity.finish();
                }
            }
        });

        ImageView imageView = cardView.findViewById(R.id.profileImageView);
        Glide.with(activity).load(galleryDataset.get(viewType)).centerCrop().override(500).into(imageView);

        return galleryViewHolder;
    }
    private int getAdapterPosition() {

        return 0;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element



//        Bitmap bmp = BitmapFactory.decodeFile(galleryDataset.get(position));
//        imageView.setImageBitmap(bmp);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return galleryDataset.size();
    }
}
