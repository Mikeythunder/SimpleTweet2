package com.codepath.apps.restclienttemplate.models;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet {

    @ColumnInfo
    @PrimaryKey
    public long id;

    //Each tweet needs a text body, time posted and the user that posted it
    @ColumnInfo
    public String body;
    @ColumnInfo
    public String createdAt;

    @Ignore
    public User user;

    @ColumnInfo
    public String media;

    @ColumnInfo
    public Long userId;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        try {
            JSONObject entities = jsonObject.getJSONObject("entities");
            JSONArray media = entities.getJSONArray("media");
            if (media.length() > 0) {
                    Log.d("DEBUG", "A tweet with media object");
                    tweet.media = media.getJSONObject(0).getString("media_url_https");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //fromJson is a method in the user class
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;

        tweet.userId = user.id;
        tweet.id = jsonObject.getLong("id");
        return tweet;
    }

    //Return a list of tweets from a jSON Array from the api
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

}
