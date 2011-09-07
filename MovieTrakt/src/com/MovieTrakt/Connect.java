package com.MovieTrakt;

import java.security.NoSuchAlgorithmException;

import com.jakewharton.trakt.ServiceManager;

public  class Connect {
	
	protected static final String API_KEY = "7f9fb61a46ed0d8ecc917b789154d397";

	private final ServiceManager manager = new ServiceManager();

	
	public void setUp(String username,String password, String apikey) throws NoSuchAlgorithmException {
		manager.setApiKey(apikey);
		manager.setAuthentication(username,  new Password().parseSHA1Password(password));
	}

	protected final ServiceManager getManager() {
		return manager;
	}
}
