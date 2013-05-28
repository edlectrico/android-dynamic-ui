package eu.deustotech.deusto.model;

public interface ICapability {
	
	static enum CAPABILITY {
		//USER
		USER_BRIGHTNESS, USER_CONTRAST, USER_IMAGES, 
		USER_INPUT, USER_LANGUAGE, USER_MAX_TEXT_SIZE, 
		USER_MIN_TEXT_SIZE, USER_OUTPUT, USER_BACKGROUND_COLOR, 
		USER_TEXT_COLOR, USER_EXPERIENCE, USER_VOLUME,
		//DEVICE
		DEVICE_RESOLUTION_WIDTH, DEVICE_RESOLUTION_HEIGTH,
		DEVICE_SCREEN_WIDTH, DEVICE_SCREEN_HEIGTH,
		DEVICE_BATTERY_LEVEL, DEVICE_ORIENTATION_MODE,
		DEVICE_AVAILABLE_NETWORKS, DEVICE_BRIGHTNESS,
		DEVICE_VOLUME, DEVICE_ACCELERATION,
		//CONTEXT
		CONTEXT_LIGHT, CONTEXT_ACTIVITY, CONTEXT_NOISE,
		CONTEXT_PRESSURE, CONTEXT_TEMPERATURE,
		CONTEXT_CALENDAR, CONTEXT_LOCATION,
		CONTEXT_RELATIONSHIP, CONTEXT_TIME
	};
	
	public Object getCapabilityValue(final CAPABILITY capabilityName);
	public void setCapabilityValue(final CAPABILITY capabilityName, final Object value);
}