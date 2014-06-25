Simple Twitter Client
=====================

This is an android application that allows the user to connect to Twitter and view her timeline, post new tweets and view tweets details. Tweets are also saved to the local database for offline mode.

Time Spent: 20 hours spent in total

Completed User Stories:


* [x] Required: User can sign in to Twitter using OAuth login
* [x] Required: User can view the tweets from their home timeline
* [x] Required: User should be able to see the username, name, body and timestamp for each tweet
* [x] Required: User should be displayed the relative timestamp for a tweet "8m", "7h"
* [x] Required: User can view more tweets as they scroll with infinite pagination
* [x] Optional: Links in tweets are clickable and will launch the web browser (see autolink)
* [x] Required: User can compose a new tweet
* [x] Required: User can click a “Compose” icon in the Action Bar on the top right
* [x] Required: User can then enter a new tweet and post this to twitter
* [x] Required: User is taken back to home timeline with new tweet visible in timeline
* [x] Optional: User can see a counter with total number of characters left for tweet

* [x] Advanced: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
* [x] Advanced: User can open the twitter app offline and see last loaded tweets
* [x] Advanced: Tweets are persisted into sqlite and can be displayed from the local DB
* [x] Advanced: User can tap a tweet to display a "detailed" view of that tweet
* [x] Advanced: Improve the user interface and theme the app to feel "twitter branded"
* [x] Advanced: Compose activity is replaced with a modal overlay

Notes:

This time I spent more time on the UI side to try out drawables (selects/items/shapes etc) and also played with Action bar styling.
DB commits of tweets are carried out using ActiveAndroid in a separate AsyncTask.

Walkthrough of all user stories:

![Video Walkthrough](anim_twittering2_demo.gif)