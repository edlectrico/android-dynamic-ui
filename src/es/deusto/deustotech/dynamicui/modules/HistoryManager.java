package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.graphics.Color;

import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;

public class HistoryManager {

	/**
	 * This class manages the previous presented UIs for
	 * a certain updated user. 
	 */
	
	public HistoryManager(){
		super();
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
		boolean found = false;
		
		//TODO: check SharedPreferences
		
		return found;
	}

	/**
	 * If an adaptation has been found, this method just
	 * returns it 
	 * 
	 * @return the adaptation stored in the SharedPreferences
	 */
	public UIConfiguration getAdaptedConfiguration() {
		
		//TODO: return the corresponding UIConfiguration
		
		return new UIConfiguration(Color.RED, Color.WHITE, 500, 500, null);
	}
}
