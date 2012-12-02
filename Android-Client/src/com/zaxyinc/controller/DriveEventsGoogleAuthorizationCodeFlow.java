package com.zaxyinc.controller;


import java.util.Arrays;
import java.util.List;

import android.content.Context;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

public class DriveEventsGoogleAuthorizationCodeFlow {
    
    private static GoogleAuthorizationCodeFlow instance = null;
    
	private static final String CLIENT_ID = "738348317203.apps.googleusercontent.com";
	private static final String CLIENT_S = "M_Rg8nFC3AYOf_GT-fo3umar";

	private static final List<String> SCOPE = Arrays.asList(DriveScopes.DRIVE_FILE,
			"https://www.googleapis.com/auth/userinfo.email",
			"https://www.googleapis.com/auth/userinfo.profile");
	
    public static final String REDIRECT_URL = "http://localhost";

    
    public static synchronized GoogleAuthorizationCodeFlow getInstance(Context context) {
        if (instance == null) {
            HttpTransport ht = new NetHttpTransport();
            JacksonFactory jsonF = new JacksonFactory();        
            instance = new GoogleAuthorizationCodeFlow.Builder(ht, jsonF,CLIENT_ID, CLIENT_S, SCOPE)
                    .setCredentialStore(new SharedPreferencesCredentialStore(context)).build();
            
            
        }
        return instance;
    }
}