package es.deusto.deustotech.modules;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import es.deusto.deustotech.components.UIConfiguration;
import es.deusto.deustotech.model.ICapability;

public class UIReasoner {
	
	private Context appContext;
	
	public UIReasoner(Context applicationContext){
		// TODO Auto-generated constructor stub
		this.appContext = applicationContext;
	}
	
	/**
	 * This method receives an updated user and the current device's
	 * capabilities and returns the best suitable UI configuration
	 * for this situation.
	 * 
	 * @return a new UI configuration to be displayed in the device
	 */
	public HashMap<String, View> getAdaptedConfiguration(ICapability updatedUser, ICapability device, HashMap<String, View> currentConfiguration) {
		// TODO Auto-generated constructor stub
		
		return UIConfiguration.getMockConfiguration(this.appContext);
	}
	
	
	
}
