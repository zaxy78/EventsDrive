//Messagee.java - Global class for creating a single message controller for
//views to use for communications. 

package com.zaxyinc.eventdrive;


import com.zaxyinc.controller.EventsController;
import com.zaxyinc.controller.GoogleDriveController;
import com.zaxyinc.eventdrive.R;

import android.app.Application;

public class Messagee extends Application{

	public EventsController eventsController = new EventsController();
	public GoogleDriveController driveController = new GoogleDriveController();
	
}
