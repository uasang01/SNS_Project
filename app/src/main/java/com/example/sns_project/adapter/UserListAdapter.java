 package com.example.sns_project.adapter;

 import android.app.Activity;
 import android.media.Image;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.TextView;

 import com.bumptech.glide.Glide;
 import com.example.sns_project.FirebaseHelper;
 import com.example.sns_project.R;
 import com.example.sns_project.UserInfo;

 import java.util.ArrayList;

 import androidx.annotation.NonNull;
 import androidx.cardview.widget.CardView;
 import androidx.recyclerview.widget.RecyclerView;

 public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
     private ArrayList<UserInfo> userDataset;
     private Activity activity;
     private final static String TAG ="UserListAdapter";
     private FirebaseHelper firebaseHelper;

     static class UserViewHolder extends RecyclerView.ViewHolder {
         CardView cardView;
         UserViewHolder(CardView v) {
             super(v);
             cardView = v;
         }
     }


     // Provide a suitable constructor (depends on the kind of dataset)
     public UserListAdapter(Activity activity, ArrayList<UserInfo> userDataset) {
         this.userDataset = userDataset;
         this.activity = activity;

         firebaseHelper = new FirebaseHelper(activity);
     }

     // Create new views (invoked by the layout manager)
     @NonNull
     @Override
     public UserListAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         // create a new view
         CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
         final UserViewHolder userViewHolder = new UserViewHolder(cardView);

         cardView.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
             }
         });

         return userViewHolder;
     }

     @Override
     public int getItemViewType(int position){
         return position;
     }


     // Replace the contents of a view (invoked by the layout manager)
     @Override
     public void onBindViewHolder(final UserViewHolder holder, int position) {
         // - get element from your dataset at this position
         // - replace the contents of the view with that element
         ImageView profileImageView = holder.cardView.findViewById(R.id.profileImageView);
         TextView nameTextView = holder.cardView.findViewById(R.id.titleTextView);
         TextView addressTextView = holder.cardView.findViewById(R.id.addressTextView);

         UserInfo userInfo = userDataset.get(position);
         if(userDataset.get(position).getPhotoUrl() != null){
             Glide.with(activity).load(userDataset.get(position).getPhotoUrl()).centerCrop().override(500).into(profileImageView);
         }

         nameTextView.setText(userInfo.getName());
         addressTextView.setText(userInfo.getAddress());
     }

     // Return the size of your dataset (invoked by the layout manager)
     @Override
     public int getItemCount() {
         return userDataset.size();
     }

 }