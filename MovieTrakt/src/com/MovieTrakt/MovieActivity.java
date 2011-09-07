package com.MovieTrakt;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.entities.TvShow;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.AsyncImageView;

public class MovieActivity extends GDActivity{

	private static final int SEARCH = 0;
	private static final int SETTINGS = 1;
	public Movie m ;

	private TextView mDescription;
	private TextView mRating;
	private TextView mTag;
	private AsyncImageView mPoster;
	private TextView mReleased;
	private ImageView mTrailer;
	private ImageView mIMDB;
	private ImageView mTMDB;
	private ImageView mSeenButton;
	private ImageView mSeenTag;
	private ImageView mCollectionButton;
	private ImageView mCollectionTag;
	private ImageView mWatchButton;
	private ImageView mWatchTag;
	private ImageView mLoveButton;
	private ImageView mHateButton;
	private ImageView mLoveTag;
	private TextView mVotes;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.movie);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		addActionBarItem(ActionBarItem.Type.Settings, SETTINGS);
		
		getPrefs();
		Bundle extras = getIntent().getExtras();


		m=(Movie) extras.get("Movie");
		new DownloadMovie().execute(m.getImdbId());


		setTitle(m.getTitle()+" ("+m.getYear()+")");

		mTag = (TextView) findViewById(R.id.tag);

		mDescription = (TextView) findViewById(R.id.description);
		mRating = (TextView) findViewById(R.id.rating);
		mVotes = (TextView) findViewById(R.id.textVotes);
		mReleased = (TextView) findViewById(R.id.released);

		mTag.setText(m.getTagline());
		mDescription.setText(m.getOverview());
		mPoster = (AsyncImageView) findViewById(R.id.poster);
		mPoster.setUrl(m.getImages().getPoster());

		mTrailer = (ImageView) findViewById(R.id.trailerbutton);
		mIMDB = (ImageView) findViewById(R.id.imdbbutton);
		mTMDB = (ImageView) findViewById(R.id.tmdbbutton);
		mSeenButton = (ImageView) findViewById(R.id.seenbutton);
		mSeenTag= (ImageView) findViewById(R.id.seentag);
		mCollectionButton = (ImageView) findViewById(R.id.collectionbutton);
		mCollectionTag= (ImageView) findViewById(R.id.collectiontag);
		mWatchButton = (ImageView) findViewById(R.id.watchbutton);
		mWatchTag= (ImageView) findViewById(R.id.watchtag);

		mLoveButton = (ImageView) findViewById(R.id.imageView3);
		mHateButton = (ImageView) findViewById(R.id.imageView4);
		mLoveTag= (ImageView) findViewById(R.id.lovetag);

		mLoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO implementar comunicao com servidor

				mLoveTag.setVisibility(ImageView.VISIBLE);

			}
		});
		mHateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO implementar comunicao com servidor

				mLoveTag.setVisibility(ImageView.GONE);


			}
		});


		mWatchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO implementar comunicao com servidor
				if (mWatchTag.getVisibility()==ImageView.VISIBLE)
					mWatchTag.setVisibility(ImageView.GONE);
				else
					mWatchTag.setVisibility(ImageView.VISIBLE);

			}
		});

		mSeenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO implementar comunicao com servidor
				if (mSeenTag.getVisibility()==ImageView.VISIBLE)
					mSeenTag.setVisibility(ImageView.GONE);
				else
					mSeenTag.setVisibility(ImageView.VISIBLE);

			}
		});

		mCollectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO implementar comunicao com servidor
				if (mCollectionTag.getVisibility()==ImageView.VISIBLE)
					mCollectionTag.setVisibility(ImageView.GONE);
				else
					mCollectionTag.setVisibility(ImageView.VISIBLE);

			}
		});
		mPoster.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), PosterActivity.class);
				i.putExtra("Poster", m.getImages().getPoster());
				i.putExtra("Title", m.getTitle()+" ("+m.getYear()+")");
				startActivity(i);

			}
		});

		mTrailer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO implementar uma verificacao pra o trailer!
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(m.getTrailer())));

			}
		});

		mIMDB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/"+m.getImdbId())));

			}
		});
		mTMDB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.themoviedb.org/movie/"+m.getTmdbId())));

			}
		});
		//		
		//		mBack.setBackgroundDrawable(mBackground.getDrawable());




	}

	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {


		case SEARCH:

			this.startSearch(null, false, Bundle.EMPTY, false);
			break;
		case SETTINGS:

			startActivity(new Intent(this, SettingsActivity.class));
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);

		}
		return true;
	}

	boolean rating;
	String username;
	String password;
	String apikey;
	public List<Movie> moviesList;

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username",
				"username");
		password = prefs.getString("password",
				"password");
		apikey = prefs.getString("apikey",
				"apikey");
	}


	private class DownloadMovie extends AsyncTask<String, Void, Movie> {
		private Exception e=null;

		@Override
		protected Movie doInBackground(String... params) {


			try{

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey("a7b42c4fb5c50a85c68731b25cc3c1ed");
				
				Movie movie = manager.movieService().summary(params[0]).fire();
				return movie;


			}catch (Exception e){
				this.e=e;
			}
			return null;
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(Movie result) {
			if(e==null){
				m=result;
				
				Ratings r =result.getRatings();
				mRating.setText(r.getPercentage()+"%");
				mVotes.setText(r.getVotes()+" votes");
			}else{

				goBlooey(e);
			}

		}





	}
	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);

		if (t.getClass().equals(UnknownHostException.class)){
			builder
			.setTitle("Erro")
			.setMessage("Verifique a ligação à internet")
			.setPositiveButton("OK", null)
			.show();
			//			mEmentaDisplay.setText("");
		}else{
			builder
			.setTitle("Informação")
			.setMessage(t.toString())
			.setPositiveButton("OK", null)
			.show();
			//			mEmentaDisplay.setText("");
		}

	}


}
