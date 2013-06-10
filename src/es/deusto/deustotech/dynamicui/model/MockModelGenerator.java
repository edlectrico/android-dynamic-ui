package es.deusto.deustotech.dynamicui.model;

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
		return new ContextCapabilities(ILLUMINANCE.SUNLIGHT, NOISE.STREET);
	}

	public static ICapability generateMockDevice() {
		return	new DeviceCapabilities("Samsung Galaxy S3", 720, 1280, 0F, 0F,
				"good", "high", "standard", "");
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
