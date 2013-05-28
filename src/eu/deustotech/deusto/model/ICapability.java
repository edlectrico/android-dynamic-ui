package eu.deustotech.deusto.model;

public interface ICapability {
	
	static enum CAPABILITY {
		//USER
		USER_BRIGHTNESS, USER_CONTRAST, USER_IMAGES, 
		USER_INPUT, USER_LANGUAGE, USER_MAX_TEXT_SIZE, 
		USER_MIN_TEXT_SIZE, USER_OUTPUT, USER_BACKGROUND_COLOR, 
		USER_TEXT_COLOR, USER_EXPERIENCE, USER_VOLUME,
		USER_LOCATION, USER_ACTIVITY, USER_RELATIONSHIP,
		//DEVICE
		DEVICE_RESOLUTION_WIDTH, DEVICE_RESOLUTION_HEIGTH,
		DEVICE_SCREEN_WIDTH, DEVICE_SCREEN_HEIGTH,
		DEVICE_BATTERY_LEVEL, DEVICE_ORIENTATION_MODE,
		DEVICE_AVAILABLE_NETWORKS, DEVICE_BRIGHTNESS,
		DEVICE_VOLUME, DEVICE_ACCELERATION,
		//CONTEXT
		CONTEXT_LIGHTNING, CONTEXT_NOISE,
		CONTEXT_PRESSURE, CONTEXT_TEMPERATURE,
		CONTEXT_CALENDAR, CONTEXT_TIME
	};
	
	static enum ACTIVITIES {
		NONE, RESTING, RUNNING
	};
	
	static enum RELATIONSHIP {
		NONE, SOCIAL, WORK
	}
	
	static enum ILLUMINANCE {
		// extracted from http://en.wikipedia.org/wiki/Lux
		MOONLESS_OVERCAST_NIGHT, // Moonless, overcast night sky (starlight)
		MOONLESS_CLEAR_NIGHT, // Moonless clear night sky with airglow
		FULL_MOON_CLEAR_NIGHT, // Full moon on a clear night
		TWILIGHT_SKY, // Dark limit of civil twilight under a clear sky
		LIVING_ROOM, // Family living room lights (Australia, 1998)
		TOILET, // Office building hallway/toilet lighting
		VERY_DARK_OVERCAST_DAY, // Very dark overcast day
		OFFICE, // Office lighting
		SUNRISE_CLEAR_DAY, // Sunrise or sunset on a clear day
		OVERCAST_DAY, // Overcast day
		TV_STUDIO, // typical TV studio lighting
		DAYLIGHT, // Full daylight (not direct sun)
		SUNLIGHT, // Direct sunlight
		COMPARISON_UNAVAILABLE // None of the above
	}
	
	static enum LOCATION {
		HOME, STREET, PUBLIC_BUILDING,
		WORK
	};
	
	static enum BRIGHTNESS {
		DEFAULT, LOW, HIGH, VERY_HIGH
	};
	
	static enum VOLUME {
		DEFAULT, LOW, HIGHT, VERY_HIGH
	};
	
	public Object getCapabilityValue(final CAPABILITY capabilityName);
	public void setCapabilityValue(final CAPABILITY capabilityName, final Object value);
}
