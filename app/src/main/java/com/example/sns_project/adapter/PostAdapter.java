 package com.example.sns_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.sns_project.FirebaseHelper;
import com.example.sns_project.PostInfo;
import com.example.sns_project.R;
import com.example.sns_project.activity.PostActivity;
import com.example.sns_project.activity.WritePostActivity;
import com.example.sns_project.listener.OnPostListener;
import com.example.sns_project.view.ReadContentsView;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostInfo> postDataset;
    private Activity activity;
    private final static String TAG ="PostAdapter";
    private FirebaseHelper firebaseHelper;
    private ArrayList<ArrayList<SimpleExoPlayer>> videoArrayArrayList = new ArrayList<>();

    private final static int MORE_INDEX = 2;
    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Activity activity, ArrayList<PostInfo> postDataset) {
        this.postDataset = postDataset;
        this.activity = activity;

        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        firebaseHelper.setOnPostListener(onPostListener);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,PostActivity.class);
                intent.putExtra("postInfo", postDataset.get(postViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menuImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, postViewHolder.getAdapterPosition());
            }
        });
        return postViewHolder;
    }
    private int getAdapterPosition() {
        return 0;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView titleTextView = holder.cardView.findViewById(R.id.titleTextView);
        PostInfo postInfo = postDataset.get(position);
        titleTextView.setText(postInfo.getTitle());

        ReadContentsView readContentsView = holder.cardView.findViewById(R.id.readContentsView);

        LinearLayout contentsLayout = holder.cardView.findViewById(R.id.contentsLayout);

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(postInfo)) {
            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsView.setPostInit(postInfo);
            readContentsView.setMoreIndex(MORE_INDEX);

            ArrayList<SimpleExoPlayer> videoArrayList = readContentsView.getVideoArrayList();
            if(videoArrayList!=null){
                videoArrayArrayList.add(videoArrayList);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return postDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String id = postDataset.get(position).getId();
                switch (item.getItemId()) {
                    case R.id.modify:
                        startNewActivity(WritePostActivity.class, postDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.storageDelete(postDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

    private void startNewActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("postInfo", postInfo);
        activity.startActivity(intent);
    }

    public void videoStop(){
        for(int i=0;i<videoArrayArrayList.size();i++) {
            ArrayList<SimpleExoPlayer> videoArrayList = videoArrayArrayList.get(i);
            for(int j=0;j<videoArrayList.size();j++){
                SimpleExoPlayer player = videoArrayList.get(j);
                if(player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }
            }

        }
    }
}