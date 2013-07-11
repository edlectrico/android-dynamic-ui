package es.deusto.deustotech.dynamicui.model.device;

import java.util.HashMap;

import es.deusto.deustotech.dynamicui.model.AbstractCapabilities;
import es.deusto.deustotech.dynamicui.model.ICapability;

public class DeviceCapabilities extends AbstractCapabilities {

	private static enum ORIENTATION {
		LANDSCAPE, PORTRAIT
	};

	public DeviceCapabilities() {
		super();
	}
	
	public DeviceCapabilities(BRIGHTNESS b, VOLUME v,
			VIEW_SIZE vs, TEXT_SIZE ts) {
		super();

		this.caps = new HashMap<CAPABILITY, Object>();
		
		caps.put(CAPABILITY.BRIGHTNESS, b);
		caps.put(CAPABILITY.VOLUME, v);
		caps.put(CAPABILITY.VIEW_SIZE, vs);
		caps.put(CAPABILITY.TEXT_SIZE, ts);

		//Automatic and default capabilities
		caps.put(CAPABILITY.BATTERY_LEVEL, ICapability.BATTERY_LEVEL.NORMAL);
		caps.put(CAPABILITY.ORIENTATION, ORIENTATION.PORTRAIT);
		caps.put(CAPABILITY.VOLUME, ICapability.VOLUME.DEFAULT);
		caps.put(CAPABILITY.ACCELERATION, ICapability.ACCELERATION.NONE);
		caps.put(CAPABILITY.INPUT, INPUT.DEFAULT);
	}
}
