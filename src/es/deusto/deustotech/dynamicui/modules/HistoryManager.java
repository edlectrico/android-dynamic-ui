package es.deusto.deustotech.dynamicui.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.R;
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
	 * @param currentUI
	 * @return if found return true, else return false
	 */
	public boolean checkConfiguration(UIConfiguration currentUI) {
		// Restore preferences
		preferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name), 0);
		String json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
		
		Gson gson = new Gson();
		UIConfiguration storedUI = gson.fromJson(json, UIConfiguration.class);
		
		return storedUI.equals(currentUI);
	}

	public boolean isEmpty() {
		preferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name), 0);
	    String json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
	    
	    if (json != ""){ //No adaptation stored
	    	return false;
	    } else return true;
	    
	    //TODO
//	    HashMap<ICapability, UIConfiguration> previousSituation = gson.fromJson(json, HashMap.class);
//	    final UIConfiguration uiConfiguration = gson.fromJson(json, UIConfiguration.class);
	}

	/**
	 * If an adaptation has been found, this method just
	 * returns it 
	 * 
	 * @return the adaptation stored in the SharedPreferences
	 */
	public UIConfiguration getLastConfiguration() {
		
		//TODO: return the corresponding UIConfiguration
		return new UIConfiguration(ICapability.VIEW_SIZE.VERY_BIG,ICapability.TEXT_SIZE.DEFAULT,
				ICapability.BRIGHTNESS.DEFAULT, Color.DKGRAY, Color.WHITE);
	}
	
	public UIConfiguration getCurrentUI(){
		
		preferences = context.getSharedPreferences(context.getResources().getString(R.string.preferences_name), 0);
		String json = preferences.getString(context.getResources().getString(R.string.adapted_configuration), "");
		//{"brightness":"VERY_HIGH","viewSize":"BIG","textSize":"BIG","viewColor":-1,"textColor":-16777216}
		Gson gson = new Gson();
		UIConfiguration currentUI = gson.fromJson(json, UIConfiguration.class);
		
		return new UIConfiguration(currentUI.getViewSize(), currentUI.getTextSize(), currentUI.getBrightness(), 
				currentUI.getViewColor(), currentUI.getTextColor());
	}
}
