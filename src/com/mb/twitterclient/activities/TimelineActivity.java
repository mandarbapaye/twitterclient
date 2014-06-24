package com.mb.twitterclient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.DetailViewActivity;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.adapters.TweetAdapter;
import com.mb.twitterclient.fragments.ComposeTweetFragment;
import com.mb.twitterclient.fragments.ComposeTweetFragment.OnTweetComposedListener;
import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.util.Constants;
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
		menu.findItem(R.id.miCompose).setVisible(Util.isNetworkConnected(this));
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
		
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tweet tweet = tweetsAdapter.getItem(position);
				Intent detailIntent = new Intent(getApplicationContext(), DetailViewActivity.class);
				detailIntent.putExtra(Constants.TWEET_PARAM, tweet);
				startActivity(detailIntent);
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
			Toast.makeText(this, "Loading from DB", Toast.LENGTH_SHORT).show();
			List<Tweet> tweetsList = loadTweetsFromDatabase(maxId);
			tweetsAdapter.addAll(tweetsList);
			tweetsAdapter.notifyDataSetChanged();
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
				tweetsAdapter.notifyDataSetChanged();
				saveToDatabase(tweetsList);
			}
			
			@Override
			public void onFailure(Throwable e, String str) {
				Log.d("error", e.getMessage());
			}
		});
	}
	
	private void loadNewerTweets() {
		
		if (tweetsAdapter.isEmpty() || !Util.isNetworkConnected(this)) {
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
						tweetsAdapter.notifyDataSetChanged();
					} else {
						for (int i = tweetsList.size() - 1; i >= 0; i--) {
							tweetsAdapter.insert(tweetsList.get(i), 0);
						}
						tweetsAdapter.notifyDataSetChanged();
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
				tweetsAdapter.notifyDataSetChanged();
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
		new DBCommitTask().execute(tweetsList.toArray(new Tweet[0]));
	}
	
	private List<Tweet> loadTweetsFromDatabase(long maxId) {
		return Tweet.getTweets(maxId);
	}
	
	
	private class DBCommitTask extends AsyncTask<Tweet, Void, Boolean> {
        @Override
	    protected Boolean doInBackground(Tweet... tweets) {
	    	boolean commitSuccessful = false;
	    	try {
	    		ActiveAndroid.beginTransaction();
	    		for (Tweet tweet : tweets) {
					tweet.getUser().save();
					tweet.save();
	    		}
	    		ActiveAndroid.setTransactionSuccessful();
	    		commitSuccessful = true;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	} finally {
	    		ActiveAndroid.endTransaction();
	    	}
	 		
	    	return commitSuccessful;
	    }
	     
	    protected void onPostExecute(Boolean result) {
//	    	String text;
//	    	if (result) {
//	    		text = "Tweets added to databse from async task.";
//	    	} else {
//	    		text = "Could not add tweets to database";
//	    	}
//	    	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	    }
	}
	
	
}
