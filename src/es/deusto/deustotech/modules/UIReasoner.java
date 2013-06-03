package es.deusto.deustotech.modules;

import java.util.HashMap;

import android.graphics.Color;
import android.view.View;
import es.deusto.deustotech.components.UIConfiguration;
import es.deusto.deustotech.model.ICapability;
import es.deusto.deustotech.model.ICapability.CAPABILITY;

public class UIReasoner {

	private ICapability user, device;
	private HashMap<String, View> uiConfiguration;

	public UIReasoner(){
		super();
	}

	public UIReasoner(ICapability user, ICapability device,
			HashMap<String, View> uiConfiguration) {
		super();
		
		this.uiConfiguration 	= uiConfiguration;
		this.device 			= device;
		this.user 				= user;
	}

	/**
	 * This method receives an updated, the current device's capabilities
	 * and the current configuration and returns the best suitable UI
	 * configuration for this situation.
	 * 
	 * @return a new UI configuration to be displayed in the device
	 */
	public UIConfiguration getAdaptedConfiguration() {
		final HashMap<CAPABILITY, Object> userCapabilities 		= this.user.getAllCapabilities();
		final HashMap<CAPABILITY, Object> deviceCapabilities 	= this.device.getAllCapabilities();
		
		
		//TODO: This is a mock configuration. The logic of this method
		//should return the corresponding UIConfiguration object so
		//the AdaptationModule could adapt the UI to its characteristics
		UIConfiguration uiConfiguration = new UIConfiguration(Color.RED, Color.GREEN, 500, 500, "TEST");

		return uiConfiguration;
	}

	public ICapability getUser() {
		return user;
	}

	public ICapability getDevice() {
		return device;
	}

	public HashMap<String, View> getUiConfiguration() {
		return uiConfiguration;
	}
}