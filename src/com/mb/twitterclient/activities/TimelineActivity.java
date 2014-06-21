package com.mb.twitterclient.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.adapters.TweetAdapter;
import com.mb.twitterclient.fragments.ComposeTweetFragment;
import com.mb.twitterclient.fragments.ComposeTweetFragment.OnTweetComposedListener;
import com.mb.twitterclient.models.Tweet;

public class TimelineActivity extends FragmentActivity implements OnTweetComposedListener {
	
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
		
		setupHandlers();
		//loadTweets();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twitter_options_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void setupHandlers() {
		lvTweets.setOnScrollListener(new com.mb.twitterclient.EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				loadTweets();
			}
		});
	}

	private void loadTweets() {
		long maxId = tweetsAdapter.isEmpty() ? 0 : tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getId();
		// Reduce the value so that only tweets older than the one loaded are received (otherwise we get dup for last tweet)
		maxId--;
		
//		if (maxId > 0)
//			Log.d("debug", "Load tweets older than: " + tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getBody() + ", id: " + maxId);
		
		restClient.getTimeline(maxId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray tweets) {
//				Log.d("debug", "*********** NEW RESULTS.");
//				for (int i = 0; i < tweets.length(); i++) {
//					try {
//						Log.d("debug", "tweet body: " + tweets.getJSONObject(i).getString("text"));
//					} catch (Exception e) {}
//				}
				
				tweetsAdapter.addAll(Tweet.fromJSONArray(tweets));
			}
			
			@Override
			public void onFailure(Throwable e, String str) {
				Log.d("error", e.getMessage());
			}
		});
	}
	
	public void onComposeClicked(MenuItem item) {
		ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
		composeTweetFragment.show(getFragmentManager(), "ComposeTweet");
	}
	
	public void onTweetComposed(String tweetText) {
		restClient.postNewTweet(tweetText, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject tweetJson) {
				// below approach may not be correct as some other
				// twitter client might have added another tweet at
				// the same time which is not reflected here.

				tweetsAdapter.insert(Tweet.fromJSON(tweetJson), 0);
				lvTweets.smoothScrollToPosition(0);

				// is this a better approach then.
//				tweetsAdapter.clear();
//				loadTweets();
			}
			
			@Override
			public void onFailure(Throwable e, String str) {
				Log.d("error", e.getMessage());
			}
		});

	}
}
