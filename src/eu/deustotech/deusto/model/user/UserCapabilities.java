package eu.deustotech.deusto.model.user;

import java.util.HashMap;

import eu.deustotech.deusto.model.AbstractCapabilities;

public class UserCapabilities extends AbstractCapabilities {

	private static enum IMAGES {
		DEFAULT, NO_IMAGES, ONLY_BIG
	};

	//If user cannot see he/she should have an option to 
	//avoid HAPTIC -> ONLY_VOICE_CONTROL.
	private static enum INPUT {
		GESTURES, HAPTIC, VOICE_CONTROL,				//Common and adaptable configuration
		ONLY_VOICE_CONTROL, ONLY_HAPTIC, ONLY_GESTURES	//Priority demands for disabled users
	}; 

	private static enum LANGUAGE {
		ENGLISH, ESPAÑOL, EUSKERA
	};

	//If the user is deaf he should be able to restrict the output to ONLY_VISUAL.
	//The same way, a blind user should be able to configure the output to ONLY_AUDIO
	private static enum OUTPUT {
		VISUAL, AUDIO,				//Common and adaptable configuration
		ONLY_AUDIO, ONLY_VISUAL		//Priority demands for disabled users
	};

	//TODO: How do users specify that they cannot see, for example, blue?
	private static enum COLOR {
		RED, BLUE, WHITE, GREEN, BLACK, YELLOW, DEFAULT
	};

	private static enum EXPERIENCE {
		EXPERT, STANDARD, NO_EXPERIENCE
	};

	public UserCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public UserCapabilities(String brightness, String contrast,
			String maxTextSize, String minTextSize, String volume) {
		super();

		this.caps = new HashMap<CAPABILITY, Object>();

		caps.put(CAPABILITY.USER_BRIGHTNESS, brightness);
		caps.put(CAPABILITY.USER_CONTRAST, contrast);
		caps.put(CAPABILITY.USER_IMAGES, IMAGES.DEFAULT);
		caps.put(CAPABILITY.USER_INPUT, INPUT.HAPTIC);
		caps.put(CAPABILITY.USER_LANGUAGE, LANGUAGE.ENGLISH);
		caps.put(CAPABILITY.USER_MAX_TEXT_SIZE, maxTextSize);
		caps.put(CAPABILITY.USER_MIN_TEXT_SIZE, minTextSize);
		caps.put(CAPABILITY.USER_OUTPUT, OUTPUT.VISUAL);
		caps.put(CAPABILITY.USER_BACKGROUND_COLOR, COLOR.DEFAULT);
		caps.put(CAPABILITY.USER_TEXT_COLOR, COLOR.DEFAULT);
		caps.put(CAPABILITY.USER_EXPERIENCE, EXPERIENCE.STANDARD);
		caps.put(CAPABILITY.USER_VOLUME, volume);
		caps.put(CAPABILITY.USER_ACTIVITY, ACTIVITIES.NONE);
		caps.put(CAPABILITY.USER_LOCATION, new String("Home")); 	//TODO: configure default locations? (HOME/STREET/WORK...)
		caps.put(CAPABILITY.USER_RELATIONSHIP, RELATIONSHIP.NONE);	//TODO: Does it mean anything?
	}

}
