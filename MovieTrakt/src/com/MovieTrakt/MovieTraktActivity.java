package com.MovieTrakt;



import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.services.SearchService.MoviesBuilder;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.AsyncImageView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MovieTraktActivity extends GDActivity {
	private static final int SEARCH = 0;
	private static final int SETTINGS = 1;
	private Gallery mGallery;
	private ListView mTrendingList;
	public List<Movie> trendingList;
	private MovieTraktActivity movieTraktActivity;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.main);
		addActionBarItem(ActionBarItem.Type.Search, SEARCH);
		addActionBarItem(ActionBarItem.Type.Settings, SETTINGS);
		
		setTitle("MovieTrakt");
		getPrefs();
		movieTraktActivity=this;
		

		mGallery = (Gallery) findViewById(R.id.gall);
		mGallery.setAdapter(new ImageAdapter(this));

		
		TextView mUsername = (TextView) findViewById(R.id.user);
		mUsername.setText(username);

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

	//Async trendingList
	private class downloadTrending extends AsyncTask<String, Void, ArrayList<Movie>> {
		private Exception e=null;

		@Override
		protected ArrayList<Movie> doInBackground(String... params) {


			try{

				ServiceManager manager = new ServiceManager();
				manager.setAuthentication(username, new Password().parseSHA1Password(password));
				manager.setApiKey("a7b42c4fb5c50a85c68731b25cc3c1ed");
				
				 List<Movie> mlist = manager.movieService().trending().fire();
				

				ArrayList<Movie> d=new ArrayList<Movie>();
				//TODO isto é para melhorar (passar como return)
				trendingList=mlist;
				for(Movie c: mlist){
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
			//				if(e==null){
			//				mUser.setText(result.get(0).getTitle());
			//				mPass.setText(result.get(0).getYear());
			//				}else{
			//					
			//						goBlooey(e);
			//					}

			
			String[] resultaux=new String[result.size()];
			int i=0;
			for (Movie m:result){
				resultaux[i]=m.getTitle()+" ("+m.getYear()+")";
				i++;
			}
			mTrendingList.setAdapter(new ArrayAdapter<String>(movieTraktActivity,
					android.R.layout.simple_list_item_1, resultaux));
			mTrendingList.setVisibility(ListView.VISIBLE);

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
			.setMessage("Cantina Fechada")
			.setPositiveButton("OK", null)
			.show();
			//			mEmentaDisplay.setText("");
		}

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
	public void onResume() {
		super.onResume();
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





	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private Integer[] mImageIds = {
				R.drawable.sample_1,
				R.drawable.sample_2,
				R.drawable.sample_3,
				R.drawable.sample_4,
				R.drawable.sample_5,
				R.drawable.sample_6,
				R.drawable.sample_7
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

}