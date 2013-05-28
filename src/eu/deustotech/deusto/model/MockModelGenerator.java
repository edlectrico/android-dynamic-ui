package eu.deustotech.deusto.model;

import eu.deustotech.deusto.model.context.ContextCapabilities;
import eu.deustotech.deusto.model.device.DeviceCapabilities;
import eu.deustotech.deusto.model.user.UserCapabilities;

public abstract class MockModelGenerator {

	public static UserCapabilities generateMockUser() {
		return new UserCapabilities("100", "50", "15", "10", "10");
	}

	public static ContextCapabilities generateMockContext() {
		return new ContextCapabilities("100", "20", "3", "15", "Thu-May-16",
				"Bilbao");
	}

	public static ICapability[] generateMockDevices() {
		ICapability[] mockDevices = {
				new DeviceCapabilities("Samsung Galaxy S3", 720, 1280, 0F, 0F,
						"good", "high", "standard", ""),
				new DeviceCapabilities("Samsung Galaxy Tab", 600, 1024, 4.74F, 7.48F,
						"good", "high", "standard", "")
				};

		return mockDevices;
	}

}
