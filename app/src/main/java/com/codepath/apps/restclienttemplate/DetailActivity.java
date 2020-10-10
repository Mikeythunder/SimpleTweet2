package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class DetailActivity extends AppCompatActivity {

    Context context;
    TextView tvScreenName;
    TextView tvBody;
    TextView tvReleaseTime;
    ImageView ivProfile;
    ImageView mediaImage;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvReleaseTime = findViewById(R.id.tvReleaseTime);
        ivProfile = findViewById(R.id.ivProfile);
        mediaImage = findViewById(R.id.mediaImage);
        textView = findViewById(R.id.textView);

       String screenName = getIntent().getStringExtra("screenName");
       String body = getIntent().getStringExtra("body");
       String releaseTime = getIntent().getStringExtra("releaseTime");
       String imageUrl = getIntent().getStringExtra("profileUrl");
       String mediaUrl = getIntent().getStringExtra("mediaUrl");
       tvBody.setText(body);

        if (mediaUrl != null) {
            textView.setVisibility(View.INVISIBLE);
            Glide.with(DetailActivity.this)
                    .load(mediaUrl)
                    .fitCenter()
                    .into(mediaImage);
        }
       //DetailActivity.this in this case because loading into its own XML file
        int radius = 100;
        int margin = 100;
        Glide.with(DetailActivity.this).load(imageUrl).apply(new RequestOptions().fitCenter().transforms(new RoundedCorners(radius))).into(ivProfile);

       tvScreenName.setText(screenName);
       tvReleaseTime.setText(releaseTime);



    }
}