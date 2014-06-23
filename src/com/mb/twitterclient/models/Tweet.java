package com.mb.twitterclient.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

@Table(name="Tweets")
public class Tweet extends Model {
	
	@Column(name="Body")
	private String body;
	
	@Column(name="TweetId", unique=true, onUniqueConflict=Column.ConflictAction.REPLACE)
	private long tweetId;
	
	@Column(name="CreatedAt")
	private String createdAt;
	
	@Column(name="User")
	private User user;
	
	public Tweet() {
		super();
	}
	
	public String getBody() {
		return body;
	}
	
	public long getTweetId() {
		return tweetId;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}
	
	public User getUser() {
		return user;
	}
	
	public static Tweet fromJSON(JSONObject json) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = json.getString("text");
			tweet.tweetId = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.user = User.fromJSON(json.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				Tweet tweet = fromJSON(json);
				tweetsList.add(tweet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tweetsList;
	}
	
	public static List<Tweet> getTweets(long maxId) {
		From selectQuery = new Select().from(Tweet.class);
		if (maxId > 0) {
			selectQuery = selectQuery.where("tweetId <= ?", maxId);
		}
		return selectQuery.orderBy("CreatedAt DESC").limit(20).execute();
	}

}
