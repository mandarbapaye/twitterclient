package com.mb.twitterclient.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="Users")
public class User extends Model implements Serializable {
	
	@Column(name="Name")
	private String name;
	
//	@Column(name="UserId", unique=true, onUniqueConflict=Column.ConflictAction.REPLACE)
	@Column(name="UserId")
	private long userId;
	
	@Column(name="ScreenName")
	private String screenName;
	
	@Column(name="ProfileImageUrl")
	private String profileImageUrl;
	
	public User() {
		super();
	}
	
	public String getName() {
		return name;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public String getScreenName() {
		return screenName;
	}
	
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public static User fromJSON(JSONObject json) {
		User user = new User();
		try {
			user.name = json.getString("name");
			user.userId = json.getLong("id");
			user.screenName = json.getString("screen_name");
			user.profileImageUrl = json.getString("profile_image_url");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}
	
}
