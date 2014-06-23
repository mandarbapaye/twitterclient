package com.mb.twitterclient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.adapters.TweetAdapter;
import com.mb.twitterclient.fragments.ComposeTweetFragment;
import com.mb.twitterclient.fragments.ComposeTweetFragment.OnTweetComposedListener;
import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.util.Util;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity implements OnTweetComposedListener {
	
	TwitterRestClient restClient;
	
	ArrayList<Tweet> tweetsList;
	TweetAdapter tweetsAdapter;
	PullToRefreshListView  lvTweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_timeline);
		restClient = TwitterApplication.getRestClient();
		
		lvTweets = (PullToRefreshListView ) findViewById(R.id.lvTweets);
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
		
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadNewerTweets();
			}
		});
	}

	private void loadTweets() {
		
		long maxId = tweetsAdapter.isEmpty() ? 0 : tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getTweetId();
		// Reduce the value so that only tweets older than the one loaded are received (otherwise we get dup for last tweet)
		maxId--;
		
//		if (maxId > 0)
//			Log.d("debug", "Load tweets older than: " + tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getBody() + ", id: " + maxId);
		
		if (!Util.isNetworkConnected(this)) {
			Toast.makeText(this, "Loading from databse", Toast.LENGTH_SHORT).show();
			List<Tweet> tweetsList = loadTweetsFromDatabase(maxId);
			tweetsAdapter.addAll(tweetsList);
			return;
		}
		
		restClient.getTimeline(maxId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray tweets) {
//				Log.d("debug", "*********** NEW RESULTS.");
//				for (int i = 0; i < tweets.length(); i++) {
//					try {
//						Log.d("debug", "tweet body: " + tweets.getJSONObject(i).getString("text"));
//					} catch (Exception e) {}
//				}
				
				ArrayList<Tweet> tweetsList = Tweet.fromJSONArray(tweets);
				tweetsAdapter.addAll(tweetsList);
				saveToDatabase(tweetsList);
			}
			
			@Override
			public void onFailure(Throwable e, String str) {
				Log.d("error", e.getMessage());
			}
		});
	}
	
	private void loadNewerTweets() {
		if (tweetsAdapter.isEmpty()) {
			lvTweets.onRefreshComplete();
		} else {
			long sinceId = tweetsAdapter.getItem(0).getTweetId();
			restClient.getNewerTweets(sinceId, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray tweets) {
					ArrayList<Tweet> tweetsList = Tweet.fromJSONArray(tweets);
					if (tweetsList.isEmpty()) {
						// this is so as to refresh the timestamps in case there
						// are not tweets.
						Tweet tempTweet = new Tweet();
						tweetsAdapter.add(tempTweet);
						tweetsAdapter.remove(tempTweet);
					} else {
						for (int i = tweetsList.size() - 1; i >= 0; i--) {
							tweetsAdapter.insert(tweetsList.get(i), 0);
						}
						saveToDatabase(tweetsList);
					}
					
//					lvTweets.onRefreshComplete();
				}
				
				@Override
				public void onFailure(Throwable e, String str) {
					Log.d("error", e.getMessage());
//					lvTweets.onRefreshComplete();
				}
				
				@Override
				public void onFinish() {
					lvTweets.onRefreshComplete();
					super.onFinish();
				}
			});

		}
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
	
	private void saveToDatabase(ArrayList<Tweet> tweetsList) {
		ActiveAndroid.beginTransaction();
		for (Tweet tweet : tweetsList) {
			Long userSave = tweet.getUser().save();
			Long tweetSave = tweet.save();
			Log.d("debug", "User: " + userSave + ", Tweet: " + tweetSave);
		}
		Toast.makeText(this, "New Tweets are stored to DB", Toast.LENGTH_SHORT).show();
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}
	
	private List<Tweet> loadTweetsFromDatabase(long maxId) {
		return Tweet.getTweets(maxId);
	}
	
	
}
