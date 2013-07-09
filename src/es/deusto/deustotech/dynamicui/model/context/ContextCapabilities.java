package es.deusto.deustotech.dynamicui.model.context;

import java.util.HashMap;

import es.deusto.deustotech.dynamicui.model.AbstractCapabilities;

public class ContextCapabilities extends AbstractCapabilities {

	public ContextCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public ContextCapabilities(BRIGHTNESS brightness, NOISE noise) {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
		
		caps.put(CAPABILITY.BRIGHTNESS, 	brightness);
		caps.put(CAPABILITY.NOISE, 			noise);
		caps.put(CAPABILITY.TEMPERATURE, 	20);
//		caps.put(CAPABILITY.CONTEXT_PRESSURE, 		PRESSURE.NORMAL);
//		caps.put(CAPABILITY.CONTEXT_CALENDAR, 		calendar);
	};
	
}
