package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.R;
import es.deusto.deustotech.dynamicui.components.FinalUIConfiguration;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;

public class HistoryManager {

	/**
	 * This class manages the previous presented UIs for
	 * a certain updated user. 
	 */
	
	private SharedPreferences preferences;
	private Context context;
	
	public HistoryManager(){
		super();
	}

	public HistoryManager(Context appContext){
		super();
		
		this.context = appContext;
	}

	
	/**
	 * This method checks the SharedPreferences for a previous
	 * similar situation with the same user and UI configuration
	 * apparitions in the past, so a deeper adaptation is not
	 * needed
	 * 
	 * @param user
	 * @param currentUI
	 * @return if found return true, else return false
	 */
	public boolean checkConfiguration(ICapability user,
			HashMap<String, UIConfiguration> currentUI) {
		//TODO: check SharedPreferences
		// Restore preferences
		preferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.adapted_configuration), 0);
		
		return compareUsers();
	}

	private boolean compareUsers() {
		Gson gson 	= new Gson();
	    String json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
	    
	    if (json == ""){ //No adaptation stored
	    	return false;
	    }
	    
	    //TODO
	    HashMap<ICapability, UIConfiguration> previousSituation = gson.fromJson(json, HashMap.class);
	    
		return true;
	}

	/**
	 * If an adaptation has been found, this method just
	 * returns it 
	 * 
	 * @return the adaptation stored in the SharedPreferences
	 */
	public FinalUIConfiguration getAdaptedConfiguration() {
		
		//TODO: return the corresponding UIConfiguration
		
		return new FinalUIConfiguration(Color.RED, Color.WHITE, 500, 500, null, ICapability.BRIGHTNESS.VERY_HIGH, 0, 0, 0, 0, 0, 0);
	}
}
