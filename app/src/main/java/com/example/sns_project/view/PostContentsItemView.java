package com.example.sns_project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;

import androidx.annotation.Nullable;

public class PostContentsItemView extends LinearLayout {
    private ImageView imageView;
    private EditText editText;

    public PostContentsItemView(Context context) {
        super(context);
        initView();
    }

    public PostContentsItemView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    private void initView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(layoutInflater.inflate(R.layout.view_contents_image,this,false));
        addView(layoutInflater.inflate(R.layout.view_contents_edit_text,this,false));

        imageView = findViewById(R.id.postContentImageView);
        editText = findViewById(R.id.postContentEditText);

    }

    public void setImage(String path){
        Glide.with(this).load(path).override(1000).into(imageView);
    }

    public void setText(String text){
        editText.setText(text);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        imageView.setOnClickListener(onClickListener);    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener){
        editText.setOnFocusChangeListener(onFocusChangeListener);
    }
}
