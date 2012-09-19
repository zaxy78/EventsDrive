package com.zaxyinc.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import android.util.Log;




public class Event {
	public enum EventScope { FREE, PUBLIC, PRIVATE };
	private final String LOGC = "Event";
	public String createdBy;
	public String name;
	public String scope;
	public Date from;
	public String APIpath;
	private Boolean subscribed;
	
	public Event() {}
	
	public Event(String creator,String eventName) {
		this.createdBy = creator;
		this.name = name;
		
	}

	public Event(Map<String, JsonNode> eventEntity) {

				
		String date = eventEntity.get("date").asText();
		String hour = eventEntity.get("time").asText();
		SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yy HH:mm");  
		
		try {  
		    this.from = format.parse(date + "  " + hour);
		    Log.d(LOGC,date.toString());  
		} catch (ParseException e) {  
		    // TODO Auto-generated catch block  
			Log.e(LOGC, "Date parsing failed");  
		}
		this.subscribed=false;

		this.createdBy = eventEntity.get("created_by").asText();
		this.name = eventEntity.get("name").asText();
		this.scope =  eventEntity.get("scope").asText();
		this.APIpath = eventEntity.get("metadata").get("path").asText();
		
		/*/Won't work. Why? //
		try {
			this.scope = EventScope.valueOf(EventScope.class, eventEntity.get("scope").asText().toUpperCase() );
		} catch (Exception e) {
			Log.e(LOGC, "Unknown Scop String");
		}  //*/
		

	}

	public boolean Subscribed() {
		return subscribed;
	}
	

	public void setSubscription(boolean sub) {
		this.subscribed = sub;
	}

}
