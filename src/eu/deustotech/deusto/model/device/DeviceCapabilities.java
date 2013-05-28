package eu.deustotech.deusto.model.device;

import java.util.HashMap;

import eu.deustotech.deusto.model.AbstractCapabilities;

public class DeviceCapabilities extends AbstractCapabilities {

	private static enum ORIENTATION {
		LANDSCAPE, PORTRAIT
	};

	private static enum AVAILABLE_NETWORKS {
		WIFI, BLUETOOTH, GPRS, NO_NETWORK
	}

	public DeviceCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public DeviceCapabilities(String deviceID, int resolution_width,
			int resolution_height, float screen_width, float screen_height, String battery,
			String brightness, String volume, String acceleration) {
		super();

		this.caps = new HashMap<CAPABILITY, Object>();

		caps.put(CAPABILITY.DEVICE_RESOLUTION_WIDTH, resolution_width);
		caps.put(CAPABILITY.DEVICE_RESOLUTION_HEIGTH, resolution_height);
		caps.put(CAPABILITY.DEVICE_SCREEN_WIDTH, screen_width);
		caps.put(CAPABILITY.DEVICE_SCREEN_HEIGTH, screen_height);
		caps.put(CAPABILITY.DEVICE_BATTERY_LEVEL, battery);
		caps.put(CAPABILITY.DEVICE_ORIENTATION_MODE, ORIENTATION.PORTRAIT);
		caps.put(CAPABILITY.DEVICE_AVAILABLE_NETWORKS,
				AVAILABLE_NETWORKS.NO_NETWORK);
		caps.put(CAPABILITY.DEVICE_BRIGHTNESS, brightness);
		caps.put(CAPABILITY.DEVICE_VOLUME, volume);
		caps.put(CAPABILITY.DEVICE_ACCELERATION, acceleration);
	}

}
