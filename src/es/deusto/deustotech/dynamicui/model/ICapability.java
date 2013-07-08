package es.deusto.deustotech.dynamicui.model;

import java.util.HashMap;

public interface ICapability {
	
	/**
	 * Initial capabilities. Since the concept is the same for User and Device's
	 * (for example, USER_BRIGTHNESS and DEVICE_BRIGHTNESS will have different values
	 * but the concept of "brightness" is the same) the CAPABILITY enum will share
	 * these concepts
	 * 
	 * USER_CAPABILITIES
	 * 
	  	USER_BRIGHTNESS, USER_CONTRAST, 
		USER_VIEW_SIZE, USER_TEXT_SIZE,
		USER_VIEW_COLOR, USER_TEXT_COLOR,
		USER_VOLUME, USER_INPUT, USER_OUTPUT,
		USER_IMAGES,
		USER_LANGUAGE, 
		USER_OUTPUT, USER_VIEW_BACKGROUND_COLOR, 
		USER_EXPERIENCE, 
		USER_LOCATION, USER_ACTIVITY, USER_RELATIONSHIP,
	 * 
	 * DEVICE_CAPABILITIES
	 * 
	 	DEVICE_BRIGHTNESS, DEVICE_CONTRAST,
		DEVICE_VIEW_SIZE, DEVICE_TEXT_SIZE,
		DEVICE_VIEW_COLOR, DEVICE_TEXT_COLOR,
		DEVICE_VOLUME, DEVICE_ACCELERATION,
		DEVICE_BATTERY_LEVEL, DEVICE_ORIENTATION,
		DEVICE_INPUT, DEVICE_OUTPUT,
		DEVICE_RESOLUTION_WIDTH, DEVICE_RESOLUTION_HEIGTH,
		DEVICE_SCREEN_WIDTH, DEVICE_SCREEN_HEIGTH,
		DEVICE_AVAILABLE_NETWORKS, 
	 * 
	 * CONTEXT_CAPABILITIES
	 * 
 		CONTEXT_LIGHTNING, CONTEXT_NOISE,
		CONTEXT_TEMPERATURE,
		CONTEXT_PRESSURE, CONTEXT_CALENDAR, CONTEXT_TIME
	 */
	
	static enum CAPABILITY {
		//USER & DEVICE
		BRIGHTNESS, CONTRAST, 
		VIEW_SIZE, TEXT_SIZE,
		VIEW_COLOR, TEXT_COLOR,
		VOLUME, INPUT, OUTPUT,
		
		//DEVICE EXCLUSIVE
		ACCELERATION, BATTERY_LEVEL, 
		ORIENTATION,
		
		//CONTEXT EXCLUSIVE
        ILLUMINANCE, NOISE,
		TEMPERATURE,
	};
	
	//User
	static enum LOCATION {
		HOME, STREET, 
		PUBLIC_BUILDING, WORK
	};
	
	//TODO: What is big? Use Trends?
	static enum BRIGHTNESS {
		DEFAULT, LOW, HIGH, VERY_HIGH, 
		ONLY_LOW, ONLY_HIGH, ONLY_VERY_HIGH
	};
	
	static enum CONTRAST {
		DEFAULT, LOW, HIGH
	};
	
	static enum VOLUME {
		DEFAULT, LOW, HIGH, VERY_HIGH,
		ONLY_LOW, ONLY_HIGHT, ONLY_VERY_HIGHT
	};
	
	static enum ACTIVITIES {
		NONE, RESTING, RUNNING
	};
	
	static enum RELATIONSHIP {
		NONE, SOCIAL, WORK
	}
	
	static enum IMAGES {
		DEFAULT, NO_IMAGES, ONLY_BIG
	};
	
	static enum TEXT_SIZE {
		SMALL, DEFAULT, BIG, VERY_BIG,
		ONLY_VERY_BIG
	}
	
	static enum VIEW_SIZE {
		SMALL, 
		DEFAULT, 
		BIG, 
		VERY_BIG,
		ONLY_VERY_BIG
	}
	
	//TODO: How do users specify that they cannot see, for example, blue?
//	static enum COLOR {
//		RED, BLUE, WHITE, GREEN,
//		BLACK, YELLOW, DEFAULT, GRAY
//	};

	//If user cannot see he/she should have an option to 
	//avoid HAPTIC -> ONLY_VOICE_CONTROL.
	static enum INPUT {
		GESTURES, HAPTIC, VOICE_CONTROL, DEFAULT,		//Common and adaptable configuration
		ONLY_VOICE_CONTROL, ONLY_HAPTIC, ONLY_GESTURES	//Priority demands for disabled users
	};

    //If user cannot see he/she should have an option to
    //avoid HAPTIC -> ONLY_VOICE_CONTROL.
    static enum OUTPUT {
        ONLY_TEXT, DEFAULT, //text and images and audio
        ONLY_AUDIO
    };

    //Context
	static enum ILLUMINANCE {
		// extracted from http://en.wikipedia.org/wiki/Lux
		MOONLESS_OVERCAST_NIGHT, 	// Moonless, overcast night sky (starlight)
//		MOONLESS_CLEAR_NIGHT, 		// Moonless clear night sky with airglow
//		FULL_MOON_CLEAR_NIGHT, 		// Full moon on a clear night
//		TWILIGHT_SKY, 				// Dark limit of civil twilight under a clear sky
//		LIVING_ROOM, 				// Family living room lights (Australia, 1998)
//		TOILET, 					// Office building hallway/toilet lighting
//		VERY_DARK_OVERCAST_DAY, 	// Very dark overcast day
//		OFFICE, 					// Office lighting
//		SUNRISE_CLEAR_DAY, 			// Sunrise or sunset on a clear day
//		OVERCAST_DAY, 				// Overcast day
//		TV_STUDIO, 					// typical TV studio lighting
		//DAYLIGHT, 					// Full daylight (not direct sun)
		SUNLIGHT, 					// Direct sunlight
//		COMPARISON_UNAVAILABLE 		// None of the above
	}
	
	static enum NOISE {
		//extracted from http://es.wikipedia.org/wiki/Decibelio
		VERY_NOISY, // x >= 110 dB
//		NOISY,		// 70 <= x < 110 dB
//		STREET,		// 50 <= x < 70 dB
		NOT_NOISY	// 0 <= x < 50 dB
	};
	
	static enum PRESSURE {
		NORMAL, HIGH, LOW
	};
	
	public Object getCapabilityValue(final CAPABILITY capabilityName);
	public void setCapabilityValue(final CAPABILITY capabilityName, final Object value);
	public HashMap<CAPABILITY, Object> getAllCapabilities();
}
