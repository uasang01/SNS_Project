package com.example.sns_project;

import android.app.Activity;
import android.util.Log;

import com.example.sns_project.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.example.sns_project.Util.isStorageUrl;
import static com.example.sns_project.Util.showToast;
import static com.example.sns_project.Util.storageUriToName;

public class FirebaseHelper {
    private String TAG = "FirebaseHelper";
    private int successCount;
    private Activity activity;
    private OnPostListener onPostListener;
    public FirebaseHelper(Activity activity){
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public void storageDelete(final PostInfo postInfo) {
        final String id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContent();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        for (int i = 0; i < contentsList.size(); i++) {
            String content = contentsList.get(i);
            if (isStorageUrl(content)) {
                successCount++;
                StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUriToName(content));
                Log.e("hhi", storageUriToName(content));

                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        successCount--;
                        storeDelete(id, postInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        showToast(activity, TAG + " ERROR");
                    }
                });
            }
        }
        storeDelete(id, postInfo);
    }

    private void storeDelete(final String id, final PostInfo postInfo){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if(successCount == 0){
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onPostListener.onDelete(postInfo);
                            showToast(activity, "게시글을 삭제하였습니다");
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity, "게시글 삭제하지 못하였습니다");
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }
}
