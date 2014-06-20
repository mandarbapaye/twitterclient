package com.mb.twitterclient.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {
	
	private String body;
	private long id;
	private String createdAt;
	private User user;
	
	public String getBody() {
		return body;
	}
	
	public long getId() {
		return id;
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
			tweet.id = json.getLong("id");
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

}
