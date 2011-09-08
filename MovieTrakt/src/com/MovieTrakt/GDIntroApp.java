package com.MovieTrakt;

import java.util.ArrayList;
import java.util.List;

import com.jakewharton.trakt.entities.Movie;

import greendroid.app.GDApplication;

public class GDIntroApp extends GDApplication {
	
	
    @Override
    public Class<MovieTraktActivity> getHomeActivityClass() {
        return MovieTraktActivity.class;
    	
    }
    
	private ArrayList<String> libraryIMDBs;
	private List<Movie> library;
	public ArrayList<String> getLibraryIMDBs() {
		return libraryIMDBs;
	}
	public void setLibraryIMDBs(ArrayList<String> libraryIMDBs) {
		this.libraryIMDBs = libraryIMDBs;
	}
	public List<Movie> getLibrary() {
		return library;
	}
	public void setLibrary(List<Movie> library) {
		this.library = library;
	}

	
}
