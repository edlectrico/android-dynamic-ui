package es.deusto.deustotech.dynamicui.model;

import java.util.Random;

import es.deusto.deustotech.dynamicui.model.ICapability.BRIGHTNESS;
import es.deusto.deustotech.dynamicui.model.ICapability.NOISE;
import es.deusto.deustotech.dynamicui.model.ICapability.TEXT_SIZE;
import es.deusto.deustotech.dynamicui.model.ICapability.VIEW_SIZE;
import es.deusto.deustotech.dynamicui.model.ICapability.VOLUME;
import es.deusto.deustotech.dynamicui.model.context.ContextCapabilities;
import es.deusto.deustotech.dynamicui.model.device.DeviceCapabilities;
import es.deusto.deustotech.dynamicui.model.user.UserCapabilities;

public abstract class MockModelGenerator {

	public static UserCapabilities generateMockUser() {
		return new UserCapabilities(BRIGHTNESS.DEFAULT, VOLUME.DEFAULT, VIEW_SIZE.DEFAULT, TEXT_SIZE.DEFAULT);
	}

	public static ContextCapabilities generateMockContext() {
//		return new ContextCapabilities(randomContextIlluminance(), randomContextNoise());
		return new ContextCapabilities(BRIGHTNESS.HIGH, NOISE.NOT_NOISY);
	}

    //TODO: good? high?
	public static ICapability generateMockDevice() {
		return	new DeviceCapabilities(BRIGHTNESS.DEFAULT, VOLUME.DEFAULT, VIEW_SIZE.DEFAULT, TEXT_SIZE.DEFAULT);
	}
	
	private static BRIGHTNESS randomContextIlluminance() {
	    int pick = new Random().nextInt(BRIGHTNESS.values().length);
	    
	    while ((BRIGHTNESS.values()[pick].equals(BRIGHTNESS.ONLY_HIGH)) || 
	    		(BRIGHTNESS.values()[pick].equals(BRIGHTNESS.ONLY_LOW)) || 
	    		(BRIGHTNESS.values()[pick].equals(BRIGHTNESS.ONLY_VERY_HIGH))){
	    	pick = new Random().nextInt(BRIGHTNESS.values().length);
	    }
	    
	    System.out.println("Context brightness value: " + BRIGHTNESS.values()[pick]);
	    
	    return BRIGHTNESS.values()[pick];
	}
	
	private static NOISE randomContextNoise() {
	    int pick = new Random().nextInt(NOISE.values().length);
	    return NOISE.values()[pick];
	}
	
}
