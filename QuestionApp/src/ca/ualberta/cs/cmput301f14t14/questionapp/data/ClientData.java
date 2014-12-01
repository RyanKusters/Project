package ca.ualberta.cs.cmput301f14t14.questionapp.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * This class handles data that only exists on the client, and is
 * never transferred to the web service.
 * 
 * The back-end for this data store is Android SharedPreferences.
 */
public class ClientData {

	private static final String PREF_SET = "cs.ualberta.cs.cmput301f14t14.questionapp.prefs";
	private static final String VAL_USERNAME = "username";
	private static final String VAL_FAVORITES_LIST = "favqlist";
	private static final String VAL_READ_LATER_LIST = "readlaterlist";
	private static final String VAL_UPVOTED_LIST = "upvotelist";
	
	private SharedPreferences prefs;

	public ClientData(Context context) {
		this.prefs = context.getSharedPreferences(PREF_SET, Context.MODE_PRIVATE);
	}

	/**
	 * Get username record
	 * @return
	 */
	public String getUsername() {
		return this.prefs.getString(VAL_USERNAME, null);
	}

	/**
	 * Set username record
	 * @param username
	 */
	public void setUsername(String username) {
		username = username.trim();
		if (username.length() == 0) {
			throw new IllegalArgumentException("New username may not be blank.");
		}
		Editor e = prefs.edit();
		e.putString(VAL_USERNAME, username);
		e.apply();
	}

	public void saveFavoriteQuestions(List<UUID> list){
		saveItems(list, VAL_FAVORITES_LIST);
	}
	
	public List<UUID> getFavoriteQuestions() {
		return getItems(VAL_FAVORITES_LIST);
	}
	
	public List<UUID> getReadLaterQuestions() {
		return getItems(VAL_READ_LATER_LIST);
	}
	
	public void markQuestionReadLater(UUID u) {
		List<UUID> appendlist = getItems(VAL_READ_LATER_LIST);
		appendlist.add(u);
		saveItems(appendlist, VAL_READ_LATER_LIST);
	}
	
	public void unmarkQuestionReadLater(UUID u){
		List<UUID> list = getItems(VAL_READ_LATER_LIST);
		list.remove(u);
		saveItems(list,VAL_READ_LATER_LIST);
	}
	public boolean isQuestionReadLater(UUID id) {
		/** Call this to see if your desired question is
		 *  marked as read later. After that, the user will
		 *  call the datamanager to get the question itself
		 */  
		List<UUID> rllist = getItems(VAL_READ_LATER_LIST);
		if (rllist.contains(id)){
			return true; 
		} else {
			return false;
		}


	}
	
	public void markItemUpvoted(UUID id) {
		List<UUID> upvoteList = getItems(VAL_UPVOTED_LIST);
		upvoteList.add(id);
		saveItems(upvoteList, VAL_UPVOTED_LIST);
	}
	
	public boolean isItemUpvoted(UUID id) {
		List<UUID> list = getItems(VAL_UPVOTED_LIST);
		return (list.contains(id)) ? true : false;
	}
	
	/**
	 * Get a list of UUIDs from a named list
	 * @param name Name of list
	 * @return List of UUIDs
	 */
	public List<UUID> getItems(String name) {
		// Get set of data from SharedPreferences
		Set<String> set = prefs.getStringSet(name, null);

		// Set may be null on first run of app
		if (set == null) {
			return new ArrayList<UUID>();
		}

		// Need to build up a new set of UUIDs before converting to List
		Set<UUID> intermediateSet = new HashSet<UUID>();
		for (String s: set) {
			if (s == null) continue;
			intermediateSet.add(UUID.fromString(s));
		}

		// Construct the final list
		ArrayList<UUID> returnlist = new ArrayList<UUID>();
		returnlist.addAll(intermediateSet);
		return returnlist;
	}

	/**
	 * Save a list of UUIDs to a named list
	 * @param list List of UUIDs
	 * @param name Name of list
	 */
	public void saveItems(List<UUID> list, String name) {
		Editor e = prefs.edit();
		Set<String> set = new HashSet<String>();

		for (UUID i: list) {
			set.add(i.toString());
		}
		e.putStringSet(name, set);
		e.commit();
	}

	/**
	 * Clear all data in this store
	 */
	public void clear() {
		Editor e = prefs.edit();
		e.clear();
		e.apply();
	}

}
