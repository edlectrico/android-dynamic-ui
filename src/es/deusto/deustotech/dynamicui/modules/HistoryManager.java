package es.deusto.deustotech.dynamicui.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.R;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.user.UserCapabilities;

public class HistoryManager {

	/**
	 * This class manages the previous presented UIs for
	 * a certain updated user. 
	 */
	
	private SharedPreferences userPreferences;
	private SharedPreferences uiPreferences;
	private Context context;
	
	public HistoryManager(){
		super();
	}

	public HistoryManager(Context appContext, final ICapability user){
		super();
		
		this.context = appContext;
		this.userPreferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name_user), 0);
		this.uiPreferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name_ui), 0);
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
		userPreferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name_user), 0);
		String json = userPreferences.getString(this.context.getResources().getString(R.string.adapted_configuration_user), "");
		
		Gson gson = new Gson();
		ICapability storedUser = gson.fromJson(json, UserCapabilities.class);
		
//		final BRIGHTNESS brightness = (BRIGHTNESS) user.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS);
//		final VIEW_SIZE viewSize = (VIEW_SIZE) user.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE);
//		final TEXT_SIZE textSize = (TEXT_SIZE) user.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE);
//		final ICapability simpleUser = new UserCapabilities(brightness, null, viewSize, textSize);
		
		//TODO: This always returns false...
		return ((String.valueOf(storedUser.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS)).
				equalsIgnoreCase(String.valueOf(user.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS))))
				&& (String.valueOf(storedUser.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE)).
						equalsIgnoreCase(String.valueOf(user.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE))))
				&& (String.valueOf(storedUser.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE)).
						equalsIgnoreCase(String.valueOf(user.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE))))); 
	}

	/**
	 * This method checks if already is a configuration stored in the system
	 * @return true: if there is no configuration found
	 * false: if there is a configuration stored
	 */
	public boolean isEmpty() {
		final String json = userPreferences.getString(this.context.getResources().getString(R.string.adapted_configuration_user), "");
		
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
		final String json = uiPreferences.getString(context.getResources().getString(R.string.adapted_configuration_ui), "");
		//Example: {"brightness":"VERY_HIGH","viewSize":"BIG","textSize":"BIG","viewColor":-1,"textColor":-16777216}
		Gson gson = new Gson();
//		UIConfiguration currentUI = gson.fromJson(json, UIConfiguration.class);
		UIConfiguration adaptedUserConf = gson.fromJson(json, UIConfiguration.class);
		
		return adaptedUserConf;
	}
}
