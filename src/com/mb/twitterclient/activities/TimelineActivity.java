package com.mb.twitterclient.activities;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.R.id;
import com.mb.twitterclient.R.layout;
import com.mb.twitterclient.adapters.TweetAdapter;
import com.mb.twitterclient.models.Tweet;

public class TimelineActivity extends Activity {
	
	TwitterRestClient restClient;
	
	ArrayList<Tweet> tweetsList;
	TweetAdapter tweetsAdapter;
	ListView lvTweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		restClient = TwitterApplication.getRestClient();
		
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweetsList = new ArrayList<Tweet>();
		tweetsAdapter = new TweetAdapter(this, tweetsList);
		lvTweets.setAdapter(tweetsAdapter);
		
		populateTimeline();
	}

	private void populateTimeline() {
		restClient.getTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray tweets) {
				tweetsAdapter.addAll(Tweet.fromJSONArray(tweets));
			}
			
			@Override
			public void onFailure(Throwable e, String str) {
				Log.d("error", e.getMessage());
			}
		});
	}
}
