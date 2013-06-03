package es.deusto.deustotech.modules;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import es.deusto.deustotech.components.UIConfiguration;
import es.deusto.deustotech.model.ICapability;

public class UIReasoner {
	
	
	public UIReasoner(Context applicationContext){
		super();
	}
	
	/**
	 * This method receives an updated user and the current device's
	 * capabilities and returns the best suitable UI configuration
	 * for this situation.
	 * 
	 * @return a new UI configuration to be displayed in the device
	 */
	public UIConfiguration getAdaptedConfiguration(ICapability updatedUser, ICapability device, HashMap<String, View> currentConfiguration) {
		//TODO: This is a mock configuration
		UIConfiguration uiConfiguration = new UIConfiguration(Color.RED, Color.GREEN, 500, 500, "TEST");
		
		return uiConfiguration;
	}
	
	
	
}
