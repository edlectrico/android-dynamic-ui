package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.graphics.Color;

import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.components.WidgetName;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.CAPABILITY;

public class UIReasoner {

	private ICapability user, device;
	private HashMap<String, UIConfiguration> currentUI;
	private HistoryManager historyManager;

	public UIReasoner(){
		super();
		
		historyManager = new HistoryManager();
	}

	public UIReasoner(ICapability user, ICapability device,
			HashMap<String, UIConfiguration> currentUI) {
		super();
		
		historyManager = new HistoryManager();
		
		this.currentUI 	= currentUI;
		this.device 	= device;
		this.user 		= user;
	}

	/**
	 * This method takes the updated user, the current device's capabilities
	 * and the current configuration and returns the best suitable UI
	 * for this situation.
	 * 
	 * @return a new UI configuration to be displayed in the device
	 */
	public UIConfiguration getAdaptedConfiguration() {
		//TODO: 
		//1. Check if there is a previous configuration for this situation
		if (checkAdaptationHistory()){
			return historyManager.getAdaptedConfiguration();
		} else {
			//2. Else, generate a new one
			//2.1 First, from a standard one
			StandardUIManager standardUIManager 	= new StandardUIManager();
			UIConfiguration standardConfiguration 	= standardUIManager.getStandardConfiguration(user);
			
			if (StandardUIManager.isSufficient(standardConfiguration)){
				return standardConfiguration;
			} else {
				//2.2 If it is not sufficient, a new one
				//TODO: How do we determine if an adaptation is sufficient enough?
				return adaptConfiguration(this.user.getAllCapabilities(), this.device.getAllCapabilities());
			}
		}
	}

	private boolean checkAdaptationHistory() {
		return historyManager.checkConfiguration(this.user, this.currentUI);
	}

	private UIConfiguration adaptConfiguration(
			HashMap<CAPABILITY, Object> userCapabilities,
			HashMap<CAPABILITY, Object> deviceCapabilities) {
		
		//TODO: This is a mock configuration. The logic of this method
		//should return the corresponding UIConfiguration object so
		//the AdaptationModule could adapt the UI to its characteristics
		
		/**
		 * 1. Get user capabilities
		 * 2. Get current UI configuration
		 * 3. If it is not enough, generate a new configuration
		 */
		
		UIConfiguration uiConfiguration = new UIConfiguration();
		
		//BRIGHTNESS
		/*
		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
			if (!currentUI.get(CAPABILITY.DEVICE_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
				//TODO: Higher brightness value
			}
		}
		*/
		
		//VIEW_SIZE
		if (userCapabilities.get(CAPABILITY.USER_VIEW_SIZE).equals(ICapability.VIEW_SIZE.BIG)){
			if (currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
				uiConfiguration.setHeight(500);
			}
			
			if (currentUI.get(WidgetName.BUTTON).getWidth() == -2){ //wrap_content
				uiConfiguration.setWidth(500);
			}
			
		}
		
		//TEXT_SIZE
//		if (userCapabilities.get(CAPABILITY.USER_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)){
//			if ((!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)) && 
//			(!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.VERY_BIG))) {
//				//TODO: BIG
//			}
//		}
		
		
		/*
		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
			if (this.currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
				//TODO: Bigger
				return new UIConfiguration(Color.RED, Color.GREEN, 500, 700, "VERY BIG");
			}
		}
		
		return new UIConfiguration(Color.RED, Color.GREEN, 500, 500, "TEST");
		*/
		
		uiConfiguration.setTextColor(Color.GREEN);
		uiConfiguration.setViewColor(Color.WHITE);
		uiConfiguration.setText("TESTING");
		
		return uiConfiguration;
	}

	public ICapability getUser() {
		return user;
	}

	public ICapability getDevice() {
		return device;
	}

	public HashMap<String, UIConfiguration> getUiConfiguration() {
		return currentUI;
	}
}