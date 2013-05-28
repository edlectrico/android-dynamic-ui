package es.deusto.deustotech.model.context;

import java.util.HashMap;

import es.deusto.deustotech.model.AbstractCapabilities;

public class ContextCapabilities extends AbstractCapabilities {

	public ContextCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public ContextCapabilities(ILLUMINANCE illuminance, String noise, String pressure,
			String temperature, String calendar) {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
		
		caps.put(CAPABILITY.CONTEXT_LIGHTNING, illuminance);
		caps.put(CAPABILITY.CONTEXT_NOISE, noise);
		caps.put(CAPABILITY.CONTEXT_PRESSURE, pressure);
		caps.put(CAPABILITY.CONTEXT_TEMPERATURE, temperature);
		caps.put(CAPABILITY.CONTEXT_CALENDAR, calendar);
	};
	
}
