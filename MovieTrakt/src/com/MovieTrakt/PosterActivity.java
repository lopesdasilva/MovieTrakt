package com.MovieTrakt;

import android.os.Bundle;
import android.widget.Toast;
import greendroid.app.GDActivity;
import greendroid.widget.AsyncImageView;

public class PosterActivity extends GDActivity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.poster);
		
		Bundle extras = getIntent().getExtras();
		String url=extras.getString("Poster");
		
		Toast.makeText(getApplicationContext(),
				"Press Back to return",Toast.LENGTH_SHORT).show();
		
		setTitle(extras.getString("Title"));
		AsyncImageView poster = (AsyncImageView) findViewById(R.id.poster);
		poster.setUrl(url);

	}
}
