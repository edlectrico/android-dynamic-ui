package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.R;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.BRIGHTNESS;
import es.deusto.deustotech.dynamicui.model.ICapability.TEXT_SIZE;
import es.deusto.deustotech.dynamicui.model.ICapability.VIEW_SIZE;
import es.deusto.deustotech.dynamicui.model.user.UserCapabilities;

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

	public HistoryManager(Context appContext, final ICapability user){
		super();
		
		this.context = appContext;
		this.preferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name), 0);
	}
	
	/**
	 * This method checks the SharedPreferences for a previous
	 * similar situation with the same user and UI configuration
	 * apparitions in the past, so a deeper adaptation is not
	 * needed
	 * 
	 * @param user
	 * @return if found return true, else return false
	 */
	public boolean checkConfiguration(final ICapability user) {
		// Restore preferences
		preferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name), 0);
		String json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
		
		Gson gson = new Gson();
		HashMap<ICapability, UIConfiguration> storedUI = gson.fromJson(json, HashMap.class);
		
		final BRIGHTNESS brightness = (BRIGHTNESS) user.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS);
		final VIEW_SIZE viewSize = (VIEW_SIZE) user.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE);
		final TEXT_SIZE textSize = (TEXT_SIZE) user.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE);
		final ICapability simpleUser = new UserCapabilities(brightness, null, viewSize, textSize);
		
		return (storedUI.get(simpleUser) != null);
	}

	/**
	 * This method checks if already is a configuration stored in the system
	 * @return true: if there is no configuration found
	 * false: if there is a configuration stored
	 */
	public boolean isEmpty() {
		final String json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
		
		if ((!json.equals("")) && (!json.equals("null"))){
	    	return false;
	    } else return true; //No adaptation stored
	}

	/**
	 * This method returns the last known UI configuration, which
	 * is stored by the AdaptationManager module
	 * @return a UIConfiguration which represents the last known
	 * configuration
	 */
	public UIConfiguration getLastKnownUI(final ICapability user){
		final String json = preferences.getString(context.getResources().getString(R.string.adapted_configuration), "");
		//Example: {"brightness":"VERY_HIGH","viewSize":"BIG","textSize":"BIG","viewColor":-1,"textColor":-16777216}
		Gson gson = new Gson();
//		UIConfiguration currentUI = gson.fromJson(json, UIConfiguration.class);
		HashMap<ICapability, UIConfiguration> adaptedUserConf = gson.fromJson(json, HashMap.class);
		
		return new UIConfiguration(adaptedUserConf.get(user).getViewSize(), adaptedUserConf.get(user).getTextSize(), 
				adaptedUserConf.get(user).getBrightness(), adaptedUserConf.get(user).getViewColor(), 
				adaptedUserConf.get(user).getTextColor());
	}
}
