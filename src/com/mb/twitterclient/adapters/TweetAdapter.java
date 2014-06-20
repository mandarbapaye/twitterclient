package com.mb.twitterclient.adapters;

import java.util.List;

import android.content.Context;
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

		ivProfileImg.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImg);
		tvTweet.setText(tweet.getBody());
		tvName.setText(tweet.getUser().getName());
		tvScreenName.setText(tweet.getUser().getScreenName());

		return v;
	}


	
	

}
