package com.mb.twitterclient.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.twitterclient.R;
import com.mb.twitterclient.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetAdapter extends ArrayAdapter<Tweet> {

	public TweetAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);

		View v;
		if (convertView != null) {
			v = convertView;
		} else {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.tweet_list_item, parent, false);
		}
		
		ImageView ivProfileImg = (ImageView) v.findViewById(R.id.ivProfileImg);
		TextView tvName = (TextView) v.findViewById(R.id.tvName);
		TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		TextView tvTweet = (TextView) v.findViewById(R.id.tvTweet);
		TextView tvTimeAgo = (TextView) v.findViewById(R.id.tvTimeAgo);

		ivProfileImg.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImg);
		tvTweet.setText(tweet.getBody());
		tvName.setText(tweet.getUser().getName());
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvTimeAgo.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

		return v;
	}

	
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		long msFor7Days = 604800000; 
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			long agoMs = System.currentTimeMillis() - dateMillis;
			if (agoMs > (msFor7Days - 1)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
				return dateFormat.format(new Date(dateMillis));
			} else {
				String relativeDateFull = DateUtils.getRelativeTimeSpanString(dateMillis,
										 System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
				Scanner scanner = new Scanner(relativeDateFull);
				scanner.useDelimiter(" ");
				
				StringBuilder relativeDateShort = new StringBuilder();				
				relativeDateShort.append(scanner.next());
				relativeDateShort.append(scanner.next().charAt(0));
				return relativeDateShort.toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	
}
