package com.zaxyinc.controller;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;

public class SharedPreferencesCredentialStore implements CredentialStore {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String EXPIRATION_TOKEN = "expiration_token";

	private SharedPreferences prefs;

	public SharedPreferencesCredentialStore (Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

    public void delete(String arg0, Credential arg1) throws IOException {
        Editor editor = prefs.edit();
        editor.remove(ACCESS_TOKEN);
        editor.remove(REFRESH_TOKEN);
        editor.commit();
    }

    public boolean load(String arg0, Credential cred) throws IOException {
        String accessToken = prefs.getString(ACCESS_TOKEN, null);
        String refreshToken = prefs.getString(REFRESH_TOKEN, null);
        
        cred.setAccessToken(accessToken);
        cred.setRefreshToken(refreshToken);
        
        if (accessToken == null)
            return false;
        else
            return true;
    }


    public void store(String arg0, Credential cred) throws IOException {
        Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN, cred.getAccessToken());
        editor.putString(REFRESH_TOKEN, cred.getRefreshToken());
        editor.putLong(EXPIRATION_TOKEN, cred.getExpirationTimeMilliseconds());
        editor.commit();
    }
}