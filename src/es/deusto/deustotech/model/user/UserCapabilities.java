package es.deusto.deustotech.model.user;

import java.util.HashMap;

import es.deusto.deustotech.model.AbstractCapabilities;

public class UserCapabilities extends AbstractCapabilities {

	private static enum LANGUAGE {
		ENGLISH, ESPAÑOL, EUSKERA
	};

	//If the user is deaf he should be able to restrict the output to ONLY_VISUAL.
	//The same way, a blind user should be able to configure the output to ONLY_AUDIO
	private static enum OUTPUT {
		VISUAL, AUDIO,				//Common and adaptable configuration
		ONLY_AUDIO, ONLY_VISUAL		//Priority demands for disabled users
	};

	private static enum EXPERIENCE {
		EXPERT, STANDARD, NO_EXPERIENCE
	};

	public UserCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public UserCapabilities(BRIGHTNESS brightness, VOLUME volume) {
		super();

		this.caps = new HashMap<CAPABILITY, Object>();

		caps.put(CAPABILITY.USER_BRIGHTNESS, 			brightness);
		caps.put(CAPABILITY.USER_CONTRAST, 				CONTRAST.DEFAULT);
		caps.put(CAPABILITY.USER_IMAGES, 				IMAGES.DEFAULT);
		caps.put(CAPABILITY.USER_INPUT, 				INPUT.HAPTIC);
		caps.put(CAPABILITY.USER_LANGUAGE, 				LANGUAGE.ENGLISH);
		caps.put(CAPABILITY.USER_MAX_TEXT_SIZE, 		TEXT_SIZE.DEFAULT);
		caps.put(CAPABILITY.USER_MIN_TEXT_SIZE, 		TEXT_SIZE.DEFAULT);
		caps.put(CAPABILITY.USER_MAX_VIEW_SIZE, 		VIEW_SIZE.DEFAULT);
		caps.put(CAPABILITY.USER_OUTPUT, 				OUTPUT.VISUAL);
		caps.put(CAPABILITY.USER_VIEW_BACKGROUND_COLOR, COLOR.DEFAULT);
		caps.put(CAPABILITY.USER_TEXT_COLOR, 			COLOR.DEFAULT);
		caps.put(CAPABILITY.USER_EXPERIENCE, 			EXPERIENCE.STANDARD);
		caps.put(CAPABILITY.USER_VOLUME, 				volume);
		caps.put(CAPABILITY.USER_ACTIVITY, 				ACTIVITIES.NONE);
		caps.put(CAPABILITY.USER_LOCATION, 				LOCATION.HOME); 			//TODO: configure default locations? (HOME/STREET/WORK...)
		caps.put(CAPABILITY.USER_RELATIONSHIP, 			RELATIONSHIP.NONE);	//TODO: Does it mean anything?
	}

}
