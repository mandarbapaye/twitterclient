package com.mb.twitterclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailViewActivity extends Activity {
	
	ImageView ivDetailImage;
	TextView tvDetailName;
	TextView tvDetailScreenName;
	TextView tvDetailTweetText;
	TextView tvDetailCreatedAt;
	TextView tvDetailRetweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
		tvDetailName = (TextView) findViewById(R.id.tvDetailName);
		tvDetailScreenName = (TextView) findViewById(R.id.tvDetailScreenName);
		tvDetailTweetText = (TextView) findViewById(R.id.tvDetailTweetText);
		tvDetailCreatedAt = (TextView) findViewById(R.id.tvDetailCreatedAt);
		tvDetailRetweets = (TextView) findViewById(R.id.tvDetailRetweets);
		
		Tweet tweet = (Tweet) getIntent().getExtras().get(Constants.TWEET_PARAM);
		if (tweet != null) {
			showTweetDetails(tweet);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
	    	case android.R.id.home:
	    		NavUtils.navigateUpFromSameTask(this);
	    		return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	private void showTweetDetails(Tweet tweet) {
		ivDetailImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivDetailImage);

		tvDetailName.setText(tweet.getUser().getName());
		tvDetailScreenName.setText("@" + tweet.getUser().getScreenName());
		tvDetailTweetText.setText(tweet.getBody());
		tvDetailCreatedAt.setText(getFormattedTS(tweet.getCreatedAt()));
		StringBuilder builder = new StringBuilder();
		builder.append(tweet.getRetweetCount());
		builder.append(" RETWEETS, ");
		builder.append(tweet.getFavCount());
		builder.append(" FAVOURITES");
		tvDetailRetweets.setText(builder.toString());
	}
	
	public String getFormattedTS(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMM yy", Locale.ENGLISH);
			return dateFormat.format(new Date(dateMillis));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "";
	}

}
