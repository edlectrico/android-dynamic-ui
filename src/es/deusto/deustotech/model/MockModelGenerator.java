package es.deusto.deustotech.model;

import es.deusto.deustotech.model.ICapability.BRIGHTNESS;
import es.deusto.deustotech.model.ICapability.ILLUMINANCE;
import es.deusto.deustotech.model.ICapability.LOCATION;
import es.deusto.deustotech.model.ICapability.NOISE;
import es.deusto.deustotech.model.ICapability.VOLUME;
import es.deusto.deustotech.model.context.ContextCapabilities;
import es.deusto.deustotech.model.device.DeviceCapabilities;
import es.deusto.deustotech.model.user.UserCapabilities;

public abstract class MockModelGenerator {

	public static UserCapabilities generateMockUser() {
		return new UserCapabilities(BRIGHTNESS.DEFAULT, null, null, null,
				VOLUME.DEFAULT, LOCATION.STREET);
	}

	public static ContextCapabilities generateMockContext() {
		return new ContextCapabilities(ILLUMINANCE.SUNLIGHT, NOISE.VERY_NOISY, null, null,
				null);
	}

	public static ICapability[] generateMockDevices() {
		ICapability[] mockDevices = {
				new DeviceCapabilities("Samsung Galaxy S3", 720, 1280, 0F, 0F,
						"good", "high", "standard", ""),
				new DeviceCapabilities("Samsung Galaxy Tab", 600, 1024, 4.74F,
						7.48F, "good", "high", "standard", "") };

		return mockDevices;
	}

}
