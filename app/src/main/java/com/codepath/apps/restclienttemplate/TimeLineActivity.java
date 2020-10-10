package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


//TIME LINE ACTIVITY IS LIKE THE MAIN ACTIVITY
    //This is the view with the recyclerView field
public class TimeLineActivity extends AppCompatActivity {

    public static final String TAG = "TimeLineActivity";

    TweetDao tweetDao;
    TwitterClient client;
    //This recycler view is made in the activity view main time line file
    RecyclerView rvTweets;

    //Must take in the information
    List<Tweet> tweets;

    //Must take in the adapter class that will be used
    TweetsAdapter adapter;

    SwipeRefreshLayout swipeContainer;

    EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

       client = TwitterApp.getRestClient(this);
       tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();



        swipeContainer = findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                populateHomeTimeline();
            }
        });
       //Find the recycler View
            //Done with a simple view
        rvTweets = findViewById(R.id.rvTweets);

        //Initialize the list of tweets in adapter
            //The tweets are just and arrayList of tweets
        tweets = new ArrayList<>();

        //The adapter class needs to take in context (View that will be used, the one with the recycler view)
            //It also takes in the list of tweets to get the data from
        adapter = new TweetsAdapter(this, tweets);

        // Recycler View setup:
            //Layout manager
            //the Adapter
                //This follwing code seys the recyclerView as a linear which takes in "this" as context
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
                //This code sets the adapter with the adapter field which is initialized
        rvTweets.setAdapter(adapter);

        //The follwing is for a strctch story:

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        //Adds scroll listener to Recycler View
        rvTweets.addOnScrollListener(scrollListener);

        //Query the existing tweets in the Data Base
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB =TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });



       populateHomeTimeline();
    }

    //This method is for a stretch story
    private void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "OnSuccess for loadMoreData " + json.toString());
                // 2. Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    // 3. Append the new data objects to the existing set of items inside the array of items
                    // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "OnFailure for loadMoreData " + throwable);
            }
        }, tweets.get(tweets.size()-1).id);

    }

    private void populateHomeTimeline() {
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            //This is testing the getHomeTimeLine in the TwitterClient class
                //Checking if the URL is correct, and can be found online
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //Now this will return the tweets that are on the API
                Log.i(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    //the correct way was this: bUt changed to impement the swipe refereser
//                    //If the information is correct and passed then addAll to the list of tweets
//                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
//                    //Notify that the data was saved and changed
//                    adapter.notifyDataSetChanged();
                    //New way with referser:
                    adapter.clear();
                    adapter.addAll(tweetsFromNetwork);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data into the database");
                            //Insert users first, the tweets
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));

                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                } {

                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                //Log.e signifies that the console will throw and error
                Log.e(TAG, "onFailure" + response, throwable);
            }
        });
    }
}