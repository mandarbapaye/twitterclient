package com.mb.twitterclient.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mb.twitterclient.R;

public class ComposeTweetFragment extends DialogFragment {
	
	EditText etNewTweet;
	TextView tvCharCount;
	
	OnTweetComposedListener listener;
	
	public static ComposeTweetFragment newInstance() {
		ComposeTweetFragment composeTweetFragment = new ComposeTweetFragment();
		return composeTweetFragment;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.new_tweet_dialog, null);
		
		etNewTweet = (EditText) v.findViewById(R.id.etNewTweet);
		tvCharCount = (TextView) v.findViewById(R.id.tvCharCount);
		
		etNewTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				tvCharCount.setText(etNewTweet.getText().toString().length() + "/140");
			}
		});
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setView(v);
		alertDialogBuilder.setTitle(R.string.compose_tweet_label);
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              	   listener.onTweetComposed(etNewTweet.getText().toString());
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        return alertDialogBuilder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnTweetComposedListener) {
			listener = (OnTweetComposedListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement ComposeTweetFragment.OnTweetComposedListener");
		}
	}
	
	public interface OnTweetComposedListener {
		public void onTweetComposed(String tweetText);
	}

}
