package com.MovieTrakt;

import java.util.Date;
import java.util.List;


import com.MovieTrakt.LazyList.ImageLoader;
import com.MovieTrakt.LazyList.ImageLoaderBigger;
import com.MovieTrakt.LazyList.LazyAdapterShouts;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.entities.Shout;

import com.jakewharton.trakt.enumerations.Rating;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;


public class MovieActivity extends GDActivity implements OnDrawerOpenListener, OnDrawerCloseListener{

	private static final int SEARCH = 0;
	private static final int SETTINGS = 1;
	public Movie m ;

	String apikey="a7b42c4fb5c50a85c68731b25cc3c1ed";

	private TextView mDescription;
	private TextView mRating;
	private TextView mTag;
	private ImageView mPoster;
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
	private SlidingDrawer mSlidingDrawer;
	private ScrollView mContentScroll;
	private LazyAdapterShouts lazyAdapter;
	ListView mShoutList;
	private ProgressBar mProgressBarShout;
	private ImageView mShoutButton;
	private ServiceManager managerGlobal;
	private EditText mShoutText;
	private ImageView mAvatar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.movie);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		addActionBarItem(ActionBarItem.Type.Settings, SETTINGS);

		getPrefs();
		Bundle extras = getIntent().getExtras();
		movieActivity=this;

		m=(Movie) extras.get("Movie");
		new DownloadMovie().execute(m.getImdbId());

		//		GDIntroApp appsate =(GDIntroApp) getApplicationContext();


		setTitle(m.getTitle()+" ("+m.getYear()+")");

		mTag = (TextView) findViewById(R.id.tag);
		mAvatar = (ImageView) findViewById(R.id.avatar);
		mDescription = (TextView) findViewById(R.id.description);
		mRating = (TextView) findViewById(R.id.rating);
		mVotes = (TextView) findViewById(R.id.textVotes);
//		mReleased = (TextView) findViewById(R.id.released);

		mTag.setText(m.getTagline());
		mDescription.setText(m.getOverview());
		
		ImageLoaderBigger imageLoader=new ImageLoaderBigger(this.getApplicationContext());
		mPoster = (ImageView) findViewById(R.id.poster);
		imageLoader.DisplayImage(m.getImages().getPoster(), this, mPoster);

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
		mContentScroll = (ScrollView) findViewById(R.id.scrollView1);
		mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);

		mProgressBarShout = (ProgressBar) findViewById(R.id.progressBarShout);
		mShoutList = (ListView) findViewById(R.id.shoutList);

		mSlidingDrawer.setOnDrawerOpenListener(this);
		mSlidingDrawer.setOnDrawerCloseListener(this);

		mShoutButton =(ImageView) findViewById(R.id.shoutButton);
		mShoutText = (EditText) findViewById(R.id.shoutText);
		mShoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mShoutText.getText().toString().equals("")){
					managerGlobal.shoutService().movie(m.getImdbId()).shout(mShoutText.getText().toString()).fire();
					mShoutText.setText("");
					//Update shoutlist
					mShoutList.setVisibility(ProgressBar.GONE);
					mProgressBarShout.setVisibility(ProgressBar.VISIBLE);
					new DownloadShouts().execute();
				}
			}
		});

		mLoveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new Marking().execute(MarkingTypes.LOVE);

			}
		});
		mHateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				new Marking().execute(MarkingTypes.HATE);

			}
		});


		mWatchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Marking().execute(MarkingTypes.WATCHLIST);

			}
		});

		mSeenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			new Marking().execute(MarkingTypes.SEEN);
			}
		});

		mCollectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Marking().execute(MarkingTypes.COLLECTION);
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
				if (!m.getTrailer().equals(""))
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(m.getTrailer())));
				else
					Toast.makeText(getApplicationContext(), "Trailer not available", Toast.LENGTH_SHORT).show();

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

	public List<Movie> moviesList;
	public MovieActivity movieActivity;

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		rating = prefs.getBoolean("rating", true);

		username = prefs.getString("username",
				"username");
		password = prefs.getString("password",
				"password");
	}


	private class DownloadMovie extends AsyncTask<String, Void, Movie> {
		private Exception e=null;


		@Override
		protected Movie doInBackground(String... params) {


			try{


				ServiceManager manager = new ServiceManager();
				//TODO VERIFICAR ISTO
				managerGlobal=manager;
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);





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
				if(result.getWatched())
					mSeenTag.setVisibility(ImageView.VISIBLE);
				if(result.getInCollection())
					mCollectionTag.setVisibility(ImageView.VISIBLE);
				if( result.getInWatchlist())
					mWatchTag.setVisibility(ImageView.VISIBLE);

				if(result.getRating()!=null && result.getRating().equals(Rating.Love))
					mLoveTag.setVisibility(ImageView.VISIBLE);

				if(!result.getTrailer().equals(""))
				mTrailer.setVisibility(ImageView.VISIBLE);
				
				
				if (mTag.getText().toString().equals(""))
					mTag.setText(result.getTagline());
				if(mDescription.getText().toString().equals(""))
					mDescription.setText(result.getOverview());


			}else{

				goBlooey(e);
			}

		}





	}
	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);

		builder
		.setTitle("Connection Error")
		.setMessage("Movie Trakt can not connect with trakt service")
		.setPositiveButton("OK", null)
		.show();

	}

	@Override
	public void onDrawerOpened() {
		mContentScroll.setVisibility(ScrollView.GONE);
		new DownloadShouts().execute();



	}

	@Override
	public void onDrawerClosed() {
		mContentScroll.setVisibility(ScrollView.VISIBLE);

	}


	private class DownloadShouts extends AsyncTask<String, Void, List<Shout>> {
		private Exception e=null;
		private String avatarURL;


		@Override
		protected List<Shout> doInBackground(String... params) {


			try{


				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
//				avatarURL=manager.userService().profile(username).fire().getAvatar();
				List<Shout> shouts = manager.movieService().shouts(m.getImdbId()).fire();

				return shouts;


			}catch (Exception e){
				this.e=e;
			}
			return null;
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(List<Shout> result) {
			if(e==null){

				String[] Users = new String[result.size()];
				String[] UsersAvatar = new String[result.size()];
				String[] Shouts = new String[result.size()];
				String[] ShoutsDate = new String[result.size()];
				int i=0;
				for (Shout s:result){

					Users[i]=s.getUser().getUsername();
					UsersAvatar[i]=s.getUser().getAvatar();
					Shouts[i]=s.getShout();
					ShoutsDate[i]=s.getInserted().toString();


					i++;

				}

				if(avatarURL!=null){
					ImageLoader imageLoader=new ImageLoader(movieActivity.getApplicationContext());
					
					imageLoader.DisplayImage(avatarURL, movieActivity, mAvatar);
					}
				lazyAdapter=new LazyAdapterShouts(movieActivity, Users,UsersAvatar,Shouts, ShoutsDate);

				mShoutList.setAdapter(lazyAdapter);

				mProgressBarShout.setVisibility(ProgressBar.GONE);
				mShoutList.setVisibility(ListView.VISIBLE);
			}else{

				goBlooey(e);
			}

		}





	}


	//Make changes in movie
	private class Marking extends AsyncTask<MarkingTypes, Void, MarkingTypes> {
		private Exception e=null;


		@Override
		protected MarkingTypes doInBackground(MarkingTypes... params) {


			try{
				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				
				switch (params[0]){
				case HATE:
					managerGlobal.rateService().movie(m.getImdbId()).rating(Rating.Hate).fire();
				break;
				case LOVE:
					manager.rateService().movie(m.getImdbId()).rating(Rating.Love).fire();
					break;
				case WATCHLIST:
					if (mWatchTag.getVisibility()==ImageView.VISIBLE)
						manager.movieService().unwatchlist().movie(m.getImdbId()).fire();
					else
						manager.movieService().watchlist().movie(m.getImdbId()).fire();		
					break;
				case SEEN:
					if (mSeenTag.getVisibility()==ImageView.VISIBLE)
						manager.movieService().unseen().movie(m.getImdbId()).fire();
					else
						manager.movieService().seen().movie(m.getImdbId(), m.getPlays()+1, new Date()).fire();				
					break;
				case COLLECTION:
					//TODO implementar comunicao com servidor
					if (mCollectionTag.getVisibility()==ImageView.VISIBLE)
						managerGlobal.movieService().unlibrary().movie(m.getImdbId()).fire();
						else
						managerGlobal.movieService().library().movie(m.getImdbId(), 1, new Date()).fire();
					break;
				}
				return params[0];


			}catch (Exception e){
				this.e=e;
			}
			return null;
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(MarkingTypes param) {
			if(e==null){

				switch (param){
				case HATE:
					mLoveTag.setVisibility(ImageView.GONE);
				break;
				case LOVE:
					mLoveTag.setVisibility(ImageView.VISIBLE);
					break;
				case WATCHLIST:
					if (mWatchTag.getVisibility()==ImageView.VISIBLE)
						mWatchTag.setVisibility(ImageView.GONE);
					else
						mWatchTag.setVisibility(ImageView.VISIBLE);
					
					break;
				case SEEN:
					if (mSeenTag.getVisibility()==ImageView.VISIBLE)
						mSeenTag.setVisibility(ImageView.GONE);
					else
						mSeenTag.setVisibility(ImageView.VISIBLE);
					
					break;
				case COLLECTION:
					//TODO implementar comunicao com servidor
					if (mCollectionTag.getVisibility()==ImageView.VISIBLE)
						mCollectionTag.setVisibility(ImageView.GONE);
					else
						mCollectionTag.setVisibility(ImageView.VISIBLE);
					break;
				}
			}else{

				goBlooey(e);
			}

		}





	}


}
