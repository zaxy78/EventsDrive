package com.zaxyinc.controller;

import java.util.Arrays;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory; //
import com.google.api.services.drive.DriveScopes;


public class GoogleDriveController {
	
	
	private final String LOGC = "GoogleDriveController";
	
    //
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private String finalURL; 

	public GoogleDriveController() {
	

	}
	
	public void AuthenticateAppWithUser(Activity activity)  {
	/*	
		WebView webview = new WebView(activity);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setVisibility(View.VISIBLE);
        webview.loadUrl(finalURL);
        activity.setContentView(webview);
        
        webview.setWebViewClient( new WebViewClient() {

        	   public void onPageFinished(WebView view, String url) {
        	        Log.d(LOGC, "Url: "+url);
        	        String parse = url.replace(DriveEventsGoogleAuthorizationCodeFlow.REDIRECT_URL,"");
        	    }
        	});
         
*/        
	}
	
	
	
	

}
