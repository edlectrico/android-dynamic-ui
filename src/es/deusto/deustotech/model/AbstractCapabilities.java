package es.deusto.deustotech.model;

import java.util.HashMap;

public abstract class AbstractCapabilities implements ICapability {

	public HashMap<CAPABILITY, Object> caps;
	
	@Override
	public Object getCapabilityValue(CAPABILITY capabilityName) {
		return this.caps.get(capabilityName);
	}
	
	@Override
	public void setCapabilityValue(CAPABILITY capabilityName, Object value) {
		this.caps.put(capabilityName, value);
	}

}
