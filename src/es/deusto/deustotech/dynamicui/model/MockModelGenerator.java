package es.deusto.deustotech.dynamicui.model;

import java.util.Random;

import android.util.Log;

import es.deusto.deustotech.dynamicui.model.ICapability.BRIGHTNESS;
import es.deusto.deustotech.dynamicui.model.ICapability.ILLUMINANCE;
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
		Log.e(MockModelGenerator.class.getSimpleName(), randomContextIlluminance().toString());
		Log.e(MockModelGenerator.class.getSimpleName(), randomContextNoise().toString());
		return new ContextCapabilities(randomContextIlluminance(), randomContextNoise());
	}

    //TODO: good? high?
	public static ICapability generateMockDevice() {
		return	new DeviceCapabilities("good", "high", "standard", "");
	}
	
	private static ILLUMINANCE randomContextIlluminance() {
	    int pick = new Random().nextInt(ILLUMINANCE.values().length);
	    return ILLUMINANCE.values()[pick];
	}
	
	private static NOISE randomContextNoise() {
	    int pick = new Random().nextInt(NOISE.values().length);
	    return NOISE.values()[pick];
	}
	/*
	public static ICapability[] generateMockDevices() {
		ICapability[] mockDevices = {
				new DeviceCapabilities("Samsung Galaxy S3", 720, 1280, 0F, 0F,
						"good", "high", "standard", ""),
				new DeviceCapabilities("Samsung Galaxy Tab", 600, 1024, 4.74F,
						7.48F, "good", "high", "standard", "") };

		return mockDevices;
	}
	 */
	
}
