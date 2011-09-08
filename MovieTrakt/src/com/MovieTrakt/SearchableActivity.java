package com.MovieTrakt;


import java.util.ArrayList;
import java.util.List;

import com.MovieTrakt.LazyList.LazyAdapter;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.services.SearchService.MoviesBuilder;


import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;

import android.app.AlertDialog;
import android.app.SearchManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SearchableActivity extends GDActivity {
	ListView searchResults;
	private ProgressBar mProgressBar;
	private SearchableActivity searchableActivitity;
	ServiceManager manager=null;

	private static final int SEARCH = 0;
	private static final int SETTINGS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO SEARCHLIST WITH COVERS
		setActionBarContentView(R.layout.search);
		addActionBarItem(ActionBarItem.Type.Search,SEARCH);
		addActionBarItem(ActionBarItem.Type.Settings,SETTINGS);
		setTitle("Search Results");


		getPrefs();

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		searchResults = (ListView) findViewById(R.id.searchResults);


		searchResults.setClickable(true);
		searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Intent i = new Intent(getApplicationContext(), MovieActivity.class);
				i.putExtra("Movie", moviesList.get(position));
				//				i.putExtra("Manager", manager);
				startActivity(i);

			}
		});





		searchableActivitity =this;
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		//	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		String query = intent.getStringExtra(SearchManager.QUERY);
		doMySearch(query);

		//	    }

	}

	private void doMySearch(String query) {
		new Searching().execute(query);
		mProgressBar.setVisibility(ProgressBar.VISIBLE);



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

	private class Searching extends AsyncTask<String, Void, ArrayList<Movie>> {
		private Exception e=null;

		@Override
		protected ArrayList<Movie> doInBackground(String... params) {


			try{

				manager = new ServiceManager();
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey("a7b42c4fb5c50a85c68731b25cc3c1ed");
				MoviesBuilder a = manager.searchService().movies(params[0].toString());
				List<Movie> b = a.fire();

				ArrayList<Movie> d=new ArrayList<Movie>();
				//TODO isto Ž para melhorar (passar como return)
				moviesList=b;
				for(Movie c: b){
					//					Movie m = manager.movieService().summary(c.getImdbId()).fire();
					d.add(c);
				}
				return d;

			}catch (Exception e){
				this.e=e;
			}
			return null;
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(ArrayList<Movie> result) {

			if(e==null){


				String[] mTitle=new String[result.size()];
				String[] mPosters=new String[result.size()];
				boolean[] mSeen = new boolean[result.size()];
				int i=0;
				for (Movie m:result){

					mPosters[i]=m.getImages().getPoster();
					if(m.getPlays()!=null && m.getPlays()!=0)
						mSeen[i]=true;
					else
						mSeen[i]=false;

					mTitle[i]=m.getTitle()+" ("+m.getYear()+")";
					i++;
				}

				LazyAdapter lazyAdapter = new LazyAdapter(searchableActivitity, mPosters,mTitle,mSeen);
				searchResults.setAdapter(lazyAdapter);


				searchResults.setVisibility(ListView.VISIBLE);

				mProgressBar.setVisibility(ProgressBar.GONE);
			}else
				goBlooey(e);
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




	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);      
		String query = intent.getStringExtra(SearchManager.QUERY);
		doMySearch(query);

	}
}