//EventsBoard.java - handles message board view and creates views for posts.

package com.zaxyinc.eventdrive;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.usergrid.android.client.response.ApiResponse;

import com.zaxyinc.controller.GoogleDriveController;
import com.zaxyinc.eventdrive.Messagee;
import com.zaxyinc.eventdrive.R;
import com.zaxyinc.model.Event;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Application;

public class EventsBoard extends Activity implements View.OnClickListener{

	protected static final String LOGC = "EventsBoard";
	protected final Context context = this;
	
	private ProgressDialog subscribeTaskProg = null;
	private ProgressDialog AuthDriveProg = null;
	//llEvents contains all posts, each stored in its own layout
	private LinearLayout llEvents;

	//scale layout dimensions based on screen density
	private float scale;

	//flag to indicate when message board view is active
	private boolean isActive = true;

	AsyncTask<Void, Void, Void> pollThread = null;
	AsyncTask<Void, Void, Boolean> GoogleDriveThread = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);

		//set view to show message board
		setContentView(R.layout.message_board);

		//get scale factor from device display
		scale = this.getResources().getDisplayMetrics().density;

		//grab handle to post container
		llEvents = (LinearLayout) findViewById(R.id.linear_layout_posts);

		//create buttons and listeners
		ImageButton newMessButton = (ImageButton) findViewById(R.id.new_message_button_id);
		newMessButton.setOnClickListener(this);

		ImageButton addUserToFollow = (ImageButton) findViewById(R.id.add_follow_button_id);
		addUserToFollow.setOnClickListener(this);

		ImageButton logoutButton = (ImageButton) findViewById(R.id.logout_button_id);
		logoutButton.setOnClickListener(this);


		//periodically poll for new posts in a separate thread
		pollThread = new PollForPostsTask(this.getApplication(),this).execute();

		//periodically poll for new posts in a separate thread
		TryAuthenticationDrive();
	
	}


	@Override
	protected void onResume(){

		super.onResume();
		
		if(pollThread.getStatus()==AsyncTask.Status.FINISHED){
			pollThread = new PollForPostsTask(this.getApplication(),this).execute();
		}
		

		//activity resumed set flag to true
		isActive = true;
	}


	@Override
	protected void onPause(){
		super.onPause();

		//activity leaving set flag to false
		isActive = false;
	}


	//function to draw all posts
	private void createPosts() {

		//get total number of posts from Posts.java
		int numPosts = ((Messagee) this.getApplication()).eventsController.getEvent().getNumEvents();

		//clear all posts from llEvents
		llEvents.removeAllViews();

		//create individual post layouts and add to llEvents
		Event postEvent = null;
		for(int i=numPosts-1; i>=0; i--){

			/*cell layout:  

			EventDrive cell
			  		|picture| |Name|
			  		|		| |Owner ............|
			  		|       | |FromDate ............|
			        |		| |ToDate ............|


			DEPRECATED: 
			
			  		|picture| |arrow| |Username|
			  		|		| |image| |Post ............|
			  		|       | |     | |Post ............|
			        |		| |     | |Post ............|


			 */

			//create layout for post cell
			LinearLayout llCell = new LinearLayout(this);
			llCell.setOrientation(LinearLayout.HORIZONTAL);

			//create layout to hold username and post message
			LinearLayout llUserAndPost = new LinearLayout(this);
			llUserAndPost.setOrientation(LinearLayout.VERTICAL);

			//Create image holder for user image
			ImageView postImage = new ImageView(this);
			postImage.setMaxWidth(dpToPix(70));
			postImage.setBackgroundColor(getResources().getColor(R.color.black));
			postImage.setPadding(dpToPix(1), dpToPix(1), dpToPix(1), dpToPix(1));
			postImage.setMaxHeight(dpToPix(100));
			postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			postImage.setAdjustViewBounds(true);
			postImage.setImageDrawable((getResources().getDrawable(R.drawable.eventpic)));

			/*draw arrow  //
			ImageView arrowImage = new ImageView(this);
			arrowImage.setMaxWidth(dpToPix(30));
			arrowImage.setMaxHeight(dpToPix(30));
			arrowImage.setScaleType(ImageView.ScaleType.FIT_XY);
			arrowImage.setAdjustViewBounds(true);
			arrowImage.setImageDrawable(getResources().getDrawable(R.drawable.arrow));   // */

			//Event Content Layout  
			postEvent = ((Messagee) this.getApplication()).eventsController.getEvent().getEventByIndex(i);

			//text holder for Event Name
			TextView eventNameText = new TextView(this);
			eventNameText.setPadding(0, 0, 0, dpToPix(4));
			eventNameText.setTextColor(getResources().getColor(R.color.black));
			eventNameText.setText(postEvent.name);
			eventNameText.setTypeface(null,1);
			eventNameText.setTextSize(17);

			//text holder for Creator
			TextView ownerText = new TextView(this);
			ownerText.setTextColor(getResources().getColor(R.color.post_message_gray));
			ownerText.setText("Owner: "+postEvent.createdBy);
			ownerText.setTextSize(17);

			//text holder for From date
			TextView fromText = new TextView(this);
			fromText.setTextColor(getResources().getColor(R.color.post_message_gray));
			fromText.setText("From: "+ postEvent.from.toLocaleString());
			fromText.setTextSize(17);
			
			
			//add username and post text to a linear layout
			llUserAndPost.addView(eventNameText);
			llUserAndPost.addView(ownerText);
			llUserAndPost.addView(fromText);

			//set layout properties and add rounded rectangle border
			llUserAndPost.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_white));
			llUserAndPost.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			llUserAndPost.setPadding(dpToPix(14), dpToPix(10), dpToPix(14), dpToPix(10));

			//add images and text layout to create the post layout
			llCell.addView(postImage);
			llCell.addView(llUserAndPost);
			llCell.setPadding(dpToPix(10f), dpToPix(18f), dpToPix(10f), 0);
			llCell.setOnClickListener(EventItem_Click);
			llCell.setTag(i);
			//add post layout to layout containing all posts
			llEvents.addView(llCell);

		}

	}

	//function to convert from density independent dimension to pixels
	private int dpToPix(float dps){
		return (int)(dps*scale+.5f);
	}

	public void onClick(View arg0){

		//check which button was clicked
		switch(arg0.getId()){

		
		//new message - start new message activity
		case R.id.new_message_button_id:
			//finish();
			Intent i = new Intent();
			i.setClassName("com.zaxyinc.eventdrive",
					"com.zaxyinc.eventdrive.NewMessage");
			startActivity(i);
			break;

			
		//add follow - start follow dialog
		case R.id.add_follow_button_id:

			Intent intent = new Intent(this,AddUserToFollowDialog.class);
			startActivityForResult(intent, 0);

			break;

			
		//logout - logout and start login activity
		case R.id.logout_button_id:
			
			Intent i3 = new Intent();
			i3.setClassName("com.zaxyinc.eventdrive",
					"com.zaxyinc.eventdrive.Login");
			startActivity(i3);

			break;
		}

	}

	//thread to periodically grab posts
	private class PollForPostsTask extends AsyncTask<Void, Void, Void> {

		Application app;
		EventsBoard messBoard;

		public PollForPostsTask (Application app, EventsBoard messBoard) {

			this.app = app;
			this.messBoard = messBoard;
		}

		
		//main thread function for grabbing posts
		protected Void doInBackground(Void... params) {

			//only get posts when message board is active
			while(messBoard.isActive){
				
				//draw posts
				publishProgress();
				
				//get posts from client
				((Messagee) app).eventsController.setFlagReadingPosts(true);
				((Messagee) app).eventsController.getLatestEvents();
				((Messagee) app).eventsController.setFlagReadingPosts(false);

				//wait one second
				android.os.SystemClock.sleep(5000);
			}
			return null;

		}

		//called by publishProgress() to update UI 
		protected void onProgressUpdate(Void... values){

			//create and diplay posts
			createPosts();
			if (AuthDriveProg.isShowing())
				AuthDriveProg.hide();

		};
	}

	OnClickListener EventItem_Click = new OnClickListener() {
		
		public void onClick(View llCell) {
			Log.d(LOGC, "Event click! Event index: "+llCell.getTag().toString());
			int eventIdx = (Integer)llCell.getTag();
			
			//get Event
			final Event selected = ((Messagee)getApplication()).eventsController.getEvent().getEventByIndex(eventIdx);
			final Boolean actionIsToSubscribe = selected.Subscribed() ? false: true;
			AlertDialog.Builder builder = new AlertDialog.Builder(context ,AlertDialog.THEME_HOLO_DARK );
			builder.setTitle( selected.name );
			builder.setMessage("Would you like to subscribe to Files from this event?");
            builder.setPositiveButton( actionIsToSubscribe ? "Subscribe" : "Unsubscribe" , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(LOGC, "Subscribe AlertDialog: Positive");
                    
                    //show Progress Dialog
                    subscribeTaskProg = new ProgressDialog(context);
                    subscribeTaskProg.setMessage("Please wait...");
                    subscribeTaskProg.show();

                    //Try Subscribing / Unsubscribing 
                    new SubscriptionTask(actionIsToSubscribe,selected.APIpath, getApplication()).executeOnExecutor(Executors.newSingleThreadExecutor(),null);
                    
                }
            });
            
            builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(LOGC,"Subscribe AlertDialog: Negative");
                }
            });
            builder.show();    
            }
		};
		
		private class SubscriptionTask extends AsyncTask<Void, Void, ApiResponse> { 
			Boolean subscribeFlag;
			String eventPath;
			Application app; 
			
			public SubscriptionTask(Boolean subscribeFlag, String eventPath, Application app)  {
				this.subscribeFlag = subscribeFlag;
				this.eventPath = eventPath;
				this.app = app;
			}

			@Override
			protected ApiResponse doInBackground(Void... params) {				
				if (subscribeFlag)
					//try Subscribe
					return ((Messagee) app).eventsController.attendEvent(eventPath);
				else
					//try UnSubscribe
					return ((Messagee) app).eventsController.unattendEvent(eventPath);
			}
			
			
			//check if login was successful and act on it
			@Override
			protected void onPostExecute(ApiResponse response) {
				
				
				//show error if response is empty or shows error
				if ((response == null) || "invalid_grant".equals(response.getError())) {
					subscribeTaskProg.hide();
					showSubscriptionError(subscribeFlag);
				}
				
				
				//successful login
				else{
										
					showSubscriptionSuccess(subscribeFlag);
					//hide logging in dialog
					subscribeTaskProg.hide();

				}
			}

		}
		
		//show subscription error dialog
		public void showSubscriptionError(Boolean actionFlag) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String action = actionFlag ? "Subscribe" : "Unsubscribe";
			builder.setTitle("Oops!")
					.setMessage("Unable to "+action +". Try again later.")
					.setCancelable(false)
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}

		//show subscription success dialog
		public void showSubscriptionSuccess(Boolean actionFlag) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String message = actionFlag ? "You are now Subscribed to this Evnt's Files!" : "You have Unsubscribed Successfuly from this Event";
			builder.setTitle("Done !")
					.setMessage(message)
					.setCancelable(false)
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
			
		

		private void TryAuthenticationDrive() {
			AuthDriveProg = new ProgressDialog(this);
			AuthDriveProg.setMessage("Sync your acoount...");
			AuthDriveProg.show();
			
			//((Messagee)getApplication()).driveController = new GoogleDriveController();
			//((Messagee)getApplication()).driveController.AuthenticateAppWithUser(this);
			
			
		
		}

		private class AuthenticatingDriveTask extends AsyncTask<Void, Void, Boolean> { 
			Activity activity; 
			Application app;
			
			public AuthenticatingDriveTask(Activity activity, Application app)  {
				this.activity = activity;
				this.app = app;
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				
				return null;				

			}
			
			
			//check if login was successful and act on it
			@Override
			protected void onPostExecute(Boolean b) {
				
				
			}

		}

		
		

}



