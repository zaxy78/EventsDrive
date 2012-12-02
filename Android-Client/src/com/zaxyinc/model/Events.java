package com.zaxyinc.model;

import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.zaxyinc.model.Event;

public class Events {
	private ArrayList<Event> drivEvents = new ArrayList<Event>();
	
	public Events() {}
	
	public  void clearAll() {
		drivEvents.clear();
		
	}

	public  void addEvent(Map<String, JsonNode> eventEntity) {
		drivEvents.add(new Event(eventEntity));	
	}
	
	public  void addEvent(Event newEvent) {
		drivEvents.add(newEvent);	
	}

	public  int getNumEvents() {
		return drivEvents.size();
	}

	public  Event getEventByIndex(int i) {

		return drivEvents.get(i);
	}
}
