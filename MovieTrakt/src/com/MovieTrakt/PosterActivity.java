package com.MovieTrakt;

import com.MovieTrakt.LazyList.ImageLoaderBigger;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import greendroid.app.GDActivity;

public class PosterActivity extends GDActivity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.poster);
		
		Bundle extras = getIntent().getExtras();
		String url=extras.getString("Poster");
		
		Toast.makeText(getApplicationContext(),
				"Press Back to return",Toast.LENGTH_SHORT).show();
		
		setTitle(extras.getString("Title"));
		
		ImageLoaderBigger imageLoader=new ImageLoaderBigger(this.getApplicationContext());
		ImageView image=(ImageView) findViewById(R.id.poster);
		imageLoader.DisplayImage(url, this, image);

	}
}
