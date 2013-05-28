package eu.deustotech.deusto.model.context;

import java.util.HashMap;

import eu.deustotech.deusto.model.AbstractCapabilities;

public class ContextCapabilities extends AbstractCapabilities {

	public ContextCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public ContextCapabilities(String light, String noise, String pressure,
			String temperature, String calendar, String location) {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
		
		caps.put(CAPABILITY.CONTEXT_LIGHTNING, light);
		caps.put(CAPABILITY.CONTEXT_NOISE, noise);
		caps.put(CAPABILITY.CONTEXT_PRESSURE, pressure);
		caps.put(CAPABILITY.CONTEXT_TEMPERATURE, temperature);
		caps.put(CAPABILITY.CONTEXT_CALENDAR, calendar);
	};
	
}
