package com.MovieTrakt;

import java.util.ArrayList;
import java.util.List;

import com.MovieTrakt.LazyList.ImageLoader;
import com.MovieTrakt.LazyList.LazyAdapter;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MovieTraktActivity extends GDActivity {
	
	private final String apikey="a7b42c4fb5c50a85c68731b25cc3c1ed";

	private static final int REFRESH=0;
	private static final int SEARCH = 1;
	private static final int SETTINGS = 2;
	private Gallery mGallery;
	private ListView mTrendingList;
	public List<Movie> trendingList;
	private MovieTraktActivity movieTraktActivity;
	private LazyAdapter lazyAdapter;
	private ProgressBar mProgressBar;
	private ImageView mAvatar;

	public String avatarURL;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPrefs();
		if (!login){
			//			setContentView(R.layout.poster);
			Intent i =new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();

		}else
		{
			setActionBarContentView(R.layout.main);
			getActionBar().setType(ActionBar.Type.Empty);
			addActionBarItem(ActionBarItem.Type.Refresh2,REFRESH);
			addActionBarItem(ActionBarItem.Type.Search, SEARCH);
			addActionBarItem(ActionBarItem.Type.Settings, SETTINGS);

			
			getPrefs();
			movieTraktActivity=this;

			
			mAvatar = (ImageView) findViewById(R.id.avatar);
			
			
			mProgressBar =(ProgressBar) findViewById(R.id.progressBar);
			mGallery = (Gallery) findViewById(R.id.gall);
			mGallery.setAdapter(new ImageAdapter(this));


			TextView mUsername = (TextView) findViewById(R.id.user);
			mUsername.setText(username);

			Toast.makeText(getApplicationContext(), "Refreshing Trending Movies",Toast.LENGTH_SHORT).show();

			new downloadTrending().execute();
			

			mTrendingList = (ListView) findViewById(R.id.trendingList);
			mTrendingList.setClickable(true);
			mTrendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

					Intent i = new Intent(getApplicationContext(), MovieActivity.class);
					i.putExtra("Movie", trendingList.get(position));
					startActivity(i);

				}
			});

		}
	}



	//Async trendingList
	private class downloadTrending extends AsyncTask<String, Void, ArrayList<Movie>> {
		private Exception e=null;

		@Override
		protected ArrayList<Movie> doInBackground(String... params) {


			try{

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey(apikey);
				
				//TODO this is not working right
			//	avatarURL=manager.userService().profile(username).fire().getAvatar();
			
				List<Movie> mlist = manager.movieService().trending().fire();
				ArrayList<Movie> d=new ArrayList<Movie>();
				//TODO URGENTE isto � para melhorar (passar como return)
				trendingList=mlist;
				for(Movie c: mlist){
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
				//				mUser.setText(result.get(0).getTitle());
				//				mPass.setText(result.get(0).getYear());
				//				}else{
				//					
				//						goBlooey(e);
				//					}


				String[] mTitle=new String[result.size()];
				String[] mPosters=new String[result.size()];
				boolean[] mSeen = new boolean[result.size()];
				int i=0;
				for (Movie m:result){

					mPosters[i]=m.getImages().getPoster();
					if(m.getPlays()!=0)
						mSeen[i]=true;
					else
						mSeen[i]=false;

					mTitle[i]=m.getTitle()+" ("+m.getYear()+")";
					i++;
				}

				if(avatarURL!=null){
				ImageLoader imageLoader=new ImageLoader(movieTraktActivity.getApplicationContext());
				
				imageLoader.DisplayImage(avatarURL, movieTraktActivity, mAvatar);
				}
				lazyAdapter=new LazyAdapter(movieTraktActivity, mPosters,mTitle,mSeen);
				mTrendingList.setAdapter(lazyAdapter);

				//			mTrendingList.setAdapter(new ArrayAdapter<String>(movieTraktActivity,
				//					android.R.layout.simple_list_item_1, resultaux));
				mProgressBar.setVisibility(ProgressBar.GONE);
				mTrendingList.setVisibility(ListView.VISIBLE);

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

		case REFRESH:
			Toast.makeText(getApplicationContext(), "Refreshing Trending List",Toast.LENGTH_SHORT).show();
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
			mTrendingList.setVisibility(ListView.GONE);
			new downloadTrending().execute();
			break;

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

	boolean login;
	String username;
	String password;

	private void getPrefs() {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		login = prefs.getBoolean("login", false);



		username = prefs.getString("username",
				"username");
		password = prefs.getString("password",
				"password");
	}
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		login = prefs.getBoolean("rating", true);

		username = prefs.getString("username",
				"username");
		password = prefs.getString("password",
				"password");
	}	





	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private Integer[] mImageIds = {
				R.drawable.poster,
				R.drawable.poster,
				R.drawable.poster,
				R.drawable.poster,
				R.drawable.poster,
				R.drawable.poster,
				R.drawable.poster
		};

		public ImageAdapter(Context c) {
			mContext = c;
			//		        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
			//		        mGalleryItemBackground = attr.getResourceId(
			//		                R.styleable.HelloGallery_android_galleryItemBackground, 0);
			//		        attr.recycle();
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			imageView.setImageResource(mImageIds[position]);
			imageView.setLayoutParams(new Gallery.LayoutParams(100, 180));
			//		        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			//		        imageView.setBackgroundResource(mGalleryItemBackground);

			return imageView;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(1, 1, Menu.FIRST, "Collection").setIcon(R.drawable.iconlibrary);
		menu.add(1, 2, Menu.FIRST+1, "Recommendations").setIcon(R.drawable.iconrecommendations);
		menu.add(1, 3, Menu.FIRST+2, "Watchlist").setIcon(R.drawable.iconwatchlist);
		menu.add(1, 4, Menu.FIRST+3, "Watched").setIcon(R.drawable.iconhistory);
		menu.add(1, 5, Menu.FIRST+4, "Friends").setIcon(R.drawable.iconfriends);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{


		switch(item.getItemId())
		{
		case 1:
			startActivity(new Intent(this, CollectionActivity.class));
			return true;
		case 2:
			startActivity(new Intent(this, RecommendationsActivity.class));
			return true;
		case 3:
			startActivity(new Intent(this, WatchlistActivity.class));
			return true;
		case 4:
			startActivity(new Intent(this, WatchedActivity.class));
			return true;
		case 5:
			startActivity(new Intent(this, FriendsActivity.class));
			return true;

		}
		return super.onOptionsItemSelected(item);

	}
	
}