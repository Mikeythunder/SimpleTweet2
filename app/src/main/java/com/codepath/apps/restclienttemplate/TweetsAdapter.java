package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

//The Tweets Adapter Class needs to extend the default RecyclerView Adapter, and this time it takes a specific Tweet Adapter
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    //The following methods are auto implemented by android studio

    //pass in context and list of tweets
        //The following constructor will pass in the context and the tweets
        //Context is the information passed in
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    //for each row inflate a layout
        //The onCreateViewHolder method inflates the layout
        //inflate means to save the viewHolder in memory
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Pass in the context that will be inflated into the item_tweet layout
            //Create a view that will be passed into a viewHolder and then shown on the recyclerView
                //The recycler view takes in a series of viewHolders and displays them
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }
    //Bind values based on the position
        //This onBindViewHolder binds the position of the data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data
            //get the individual tweet from the list of tweets
        Tweet tweet = tweets.get(position);

        //Bind the data to the viewHolder
            //Uses a user Defined method called bind() which takes in a tweet and binds the data to the viewHolder called on it
        holder.bind(tweet);
    }


    @Override
    //The size of the data it just the size of the tweet list
    public int getItemCount() {
        return tweets.size();
    }

    //THESE METHODS WERE INCLUDED TO USE HE SWIPE REFERSER METHOD:

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    //Define a view holder to hold the views
            //This needs to extend the default recycler view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        //This ViewHolder class needs fields depending on the display view
            //The view contains an ImageView for profile picture, TextView the tweet body and TextView for the users name
        RelativeLayout container;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        //Default constructor for the viewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //need to set those fields with the id in the display
                //Do this with findViewById
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            container = itemView.findViewById(R.id.container);
            tvName = itemView.findViewById(R.id.tvName);
        }

        //Bind needs to set all the fields in the View with their respective information from thw tweet
            //In this case it needs to set the Views text for body as the tweets body
            //the screen name would be from the user classes screen name field
            //the image Url needs to be loaded in using glide
                //Put the correct include files to use glide
        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvName.setText(tweet.user.name);
            //GLide will be used to load the image into the image field in the view
                //Must specify what is being loaded and into what
            int radius = 100;
            int margin = 100;


            Glide.with(context).load(tweet.user.profileImageUrl).apply(new RequestOptions().fitCenter().transforms(new RoundedCorners(radius))).into(ivProfileImage);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("body", tweet.body);
                    i.putExtra("screenName", tweet.user.screenName);
                    i.putExtra("releaseTime", tweet.createdAt);
                    i.putExtra("profileUrl", tweet.user.profileImageUrl);
                    i.putExtra("mediaUrl", tweet.media);
                    context.startActivity(i);
                }
            });
        }
    }

}
