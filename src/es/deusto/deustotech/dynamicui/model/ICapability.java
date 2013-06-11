package es.deusto.deustotech.dynamicui.model;

import java.util.HashMap;

public interface ICapability {
	
	static enum CAPABILITY {
		//USER
		USER_BRIGHTNESS, USER_CONTRAST, USER_IMAGES,
		USER_VIEW_SIZE, USER_TEXT_SIZE,
		USER_INPUT, USER_LANGUAGE, 
//		USER_MAX_TEXT_SIZE, 
//		USER_MIN_TEXT_SIZE, 
		USER_OUTPUT, USER_VIEW_BACKGROUND_COLOR, 
		USER_TEXT_COLOR, USER_EXPERIENCE, USER_VOLUME,
		USER_LOCATION, USER_ACTIVITY, USER_RELATIONSHIP,
		//DEVICE
		DEVICE_RESOLUTION_WIDTH, DEVICE_RESOLUTION_HEIGTH,
		DEVICE_SCREEN_WIDTH, DEVICE_SCREEN_HEIGTH,
		DEVICE_BATTERY_LEVEL, DEVICE_ORIENTATION_MODE,
		DEVICE_AVAILABLE_NETWORKS, DEVICE_BRIGHTNESS,
		DEVICE_VOLUME, DEVICE_ACCELERATION,
		DEVICE_VIEW_SIZE, DEVICE_TEXT_SIZE,
		//CONTEXT
		CONTEXT_LIGHTNING, CONTEXT_NOISE,
		CONTEXT_PRESSURE, CONTEXT_TEMPERATURE,
		CONTEXT_CALENDAR, CONTEXT_TIME
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
	static enum COLOR {
		RED, BLUE, WHITE, GREEN, 
		BLACK, YELLOW, DEFAULT, GRAY
	};

	//If user cannot see he/she should have an option to 
	//avoid HAPTIC -> ONLY_VOICE_CONTROL.
	static enum INPUT {
		GESTURES, HAPTIC, VOICE_CONTROL,				//Common and adaptable configuration
		ONLY_VOICE_CONTROL, ONLY_HAPTIC, ONLY_GESTURES	//Priority demands for disabled users
	}; 
	
	//Context
	static enum ILLUMINANCE {
		// extracted from http://en.wikipedia.org/wiki/Lux
		MOONLESS_OVERCAST_NIGHT, 	// Moonless, overcast night sky (starlight)
		MOONLESS_CLEAR_NIGHT, 		// Moonless clear night sky with airglow
		FULL_MOON_CLEAR_NIGHT, 		// Full moon on a clear night
		TWILIGHT_SKY, 				// Dark limit of civil twilight under a clear sky
//		LIVING_ROOM, 				// Family living room lights (Australia, 1998)
//		TOILET, 					// Office building hallway/toilet lighting
//		VERY_DARK_OVERCAST_DAY, 	// Very dark overcast day
//		OFFICE, 					// Office lighting
//		SUNRISE_CLEAR_DAY, 			// Sunrise or sunset on a clear day
//		OVERCAST_DAY, 				// Overcast day
//		TV_STUDIO, 					// typical TV studio lighting
		DAYLIGHT, 					// Full daylight (not direct sun)
		SUNLIGHT, 					// Direct sunlight
//		COMPARISON_UNAVAILABLE 		// None of the above
	}
	
	static enum NOISE {
		//extracted from http://es.wikipedia.org/wiki/Decibelio
		VERY_NOISY, // x >= 110 dB
		NOISY,		// 70 <= x < 110 dB
		STREET,		// 50 <= x < 70 dB
		NOT_NOISY	// 0 <= x < 50 dB 
	};
	
	static enum PRESSURE {
		NORMAL, HIGH, LOW
	};
	
	public Object getCapabilityValue(final CAPABILITY capabilityName);
	public void setCapabilityValue(final CAPABILITY capabilityName, final Object value);
	public HashMap<CAPABILITY, Object> getAllCapabilities();
}
