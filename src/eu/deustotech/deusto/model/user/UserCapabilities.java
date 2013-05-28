package eu.deustotech.deusto.model.user;

import java.util.HashMap;

import eu.deustotech.deusto.model.AbstractCapabilities;

public class UserCapabilities extends AbstractCapabilities {

	private static enum IMAGES {
		DEFAULT, NO_IMAGES, ONLY_BIG
	};

	//TODO: If user cannot see he/she should have an option to avoid HAPTIC.
	//NO_HAPTIC, ONLY_VOICE, ONLY_GESTURES for example.
	private static enum INPUT {
		GESTURES, HAPTIC, VOICE_CONTROL,				//Common and adaptable configuration
		ONLY_VOICE_CONTROL, ONLY_HAPTIC, ONLY_GESTURES	//Priority demands for disabled users
	}; 

	private static enum LANGUAGE {
		ENGLISH, ESPAÑOL, EUSKERA
	};

	//TODO: DEFAULT means text and images. If the user is deaf
	//he should be able to restrict the output to ONLY_TEXT_AND_IMAGES.
	//The same way, a blind user should be able to configure
	//the output to ONLY_AUDIO
	private static enum OUTPUT {
		DEFAULT, AUDIO,				//Common and adaptable configuration
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
		caps.put(CAPABILITY.USER_OUTPUT, OUTPUT.DEFAULT);
		caps.put(CAPABILITY.USER_BACKGROUND_COLOR, COLOR.DEFAULT);
		caps.put(CAPABILITY.USER_TEXT_COLOR, COLOR.DEFAULT);
		caps.put(CAPABILITY.USER_EXPERIENCE, EXPERIENCE.STANDARD);
		caps.put(CAPABILITY.USER_VOLUME, volume);
	}

}
