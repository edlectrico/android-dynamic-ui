package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;

import es.deusto.deustotech.dynamicui.components.FinalUIConfiguration;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.CAPABILITY;

public class UIReasoner {

	private ICapability user, device;
	private HashMap<String, UIConfiguration> currentUI;
	private HistoryManager historyManager;
	private Context context;

    private static final float MAX_BRIGHTNESS = 1.0F;

	public UIReasoner(){
		super();
	}

	public UIReasoner(ICapability user, ICapability device,
			HashMap<String, UIConfiguration> currentUI, Context appContext) {
		super();
		
		this.currentUI 	= currentUI;
		this.device 	= device;
		this.user 		= user;
		this.context 	= appContext;
		
		historyManager = new HistoryManager(this.context);
	}

	/**
	 * This method takes the updated user, the current device's capabilities
	 * and the current configuration and returns the best suitable UI
	 * for this situation.
	 * 
	 * @return a new UI configuration (FinalUIConfiguration) to be displayed in the device
	 */
	public FinalUIConfiguration getAdaptedConfiguration() {
		//TODO: 
		//1. Check if there is a previous configuration for this situation
		if (checkAdaptationHistory()){
			return historyManager.getAdaptedConfiguration();
		} else {
			//2. Else, generate a new one
			//2.1 First, from a standard one
			StandardUIManager standardUIManager 	= new StandardUIManager();
			FinalUIConfiguration standardConfiguration 	= standardUIManager.getStandardConfiguration(user);
			
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

	private FinalUIConfiguration adaptConfiguration(
			HashMap<CAPABILITY, Object> userCapabilities,
			HashMap<CAPABILITY, Object> deviceCapabilities) {
		
		//TODO: This is a mock configuration. The logic of this method
		//should return the corresponding UIConfiguration object so
		//the AdaptationModule could adapt the UI to its characteristics
		
		/**
		 * 1. Get user capabilities
		 * 2. Get current UI configuration
		 * 3. If it is not enough, generate a new configuration taking into account the
         * defined taxonomy of how each component from context, user and device affects
         * to the final UI result component
		 */
		
		FinalUIConfiguration finalUIConfiguration = new FinalUIConfiguration();


        //TODO: This class should obtain the corresponding output
        //via certain rules in order to obtain the corresponding
        //component adaptation

        /**
        * VIEW_SIZE
        *
        * Affected by:
        * -Context:     luminosity, temperature
        * -User;        output, view_size,
        * -Device:      brightness, output, acceleration, view_size, orientation
        *
        * */

		if (userCapabilities.get(CAPABILITY.VIEW_SIZE).equals(ICapability.VIEW_SIZE.BIG)){
//			if (currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
				finalUIConfiguration.setHeight(500);
//			} else finalUIConfiguration.setHeight(200);
			
//			if (currentUI.get(WidgetName.BUTTON).getWidth() == -2){ //wrap_content
				finalUIConfiguration.setWidth(500);
//			} else finalUIConfiguration.setWidth(200);
			
		} else {
			finalUIConfiguration.setHeight(100);
			finalUIConfiguration.setWidth(100);
		}
		
		/**
        * TEXT_SIZE
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, text_size
        * -Device:      acceleration, text_size
        *
        * */

 //		if (userCapabilities.get(CAPABILITY.USER_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)){
//			if ((!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)) && 
//			(!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.VERY_BIG))) {
//				//TODO: BIG
//			}
//		}
		

        /**
        * BRIGHTNESS
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, brightness
        * -Device:      brightness, battery
        *
        * */

        //http://stackoverflow.com/questions/7704961/screen-brightness-value-in-android
        //http://stackoverflow.com/questions/3737579/changing-screen-brightness-programmatically-in-android


		/*
		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
			if (!currentUI.get(CAPABILITY.DEVICE_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
				//TODO: Higher brightness value
			}
		}
		*/

        finalUIConfiguration.setBrightness(ICapability.BRIGHTNESS.VERY_HIGH);


        //TODO: Can it be changed?
        /**
        * CONTRAST
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, contrast
        * -Device:      contrast
        *
        * */

        /**
        * VIEW_COLOR
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, input? text_color, view_color
        * -Device:      brightness, text_color, view_color, output? input?
         *
         * rule_1.1: if user_input && user_output is HAPTIC (views available)
         * rule_1.2: if device_input && device_output is HAPTIC
         * //the last adaptation value will be made by context
         * rule_1.3: if user_view_color != device_text_color (if not different, can't see the text)
         * rule_1.4: if context_luminosity < value_x
         *           if context_luminosity > value_y
         *
         * RULE_1_RESULT
        *
        * */

        finalUIConfiguration.setViewColor(Color.GREEN);

        /**
        * TEXT_COLOR (almost same VIEW_COLOR rules)
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, text_color, view_color
        * -Device:      brightness, text_color, view_color,
        *
         * Text is not just for HAPTIC interfaces, non-haptic
         * ones also use text.
         * Text color must be always different from the "button"
         * or the control one.
         *
        * */

        finalUIConfiguration.setTextColor(Color.BLUE);


        /**
        * VOLUME
        *
        * Affected by:
        * -Context:     noise
        * -User;        output, volume
        * -Device:      output, battery, volume
        *
         * rule_3.1: 
         * rule_3.2:
         * rule_3.3:
         * rule_3.4:
         *
         * RULE_3_RESULT
        * */

        //http://stackoverflow.com/questions/2539264/volume-control-in-android-application


        /*
		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
			if (this.currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
				//TODO: Bigger
				return new UIConfiguration(Color.RED, Color.GREEN, 500, 700, "VERY BIG");
			}
		}

		return new UIConfiguration(Color.RED, Color.GREEN, 500, 500, "TEST");
		*/

//  		finalUIConfiguration.setTextColor(Color.GREEN);
//		finalUIConfiguration.setViewColor(Color.WHITE );
		finalUIConfiguration.setText("TESTING");
		
		return finalUIConfiguration;
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