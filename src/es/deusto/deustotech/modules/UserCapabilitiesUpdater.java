package es.deusto.deustotech.modules;

import es.deusto.deustotech.model.ICapability;
import es.deusto.deustotech.model.ICapability.CAPABILITY;

public class UserCapabilitiesUpdater {

	/**
	 * This module will be the responsible for updating the user capabilities
	 * taking into account the current context parameters. Therefore, it should
	 * modify current user capabilities taking into account the new current
	 * context situation.
	 */

	static public ICapability update(ICapability user, ICapability context) {
		// TODO: for the moment just adapt noise and lightning

		// TODO: Try to cover any possible case
		// if user.CAPABILITY.USER_BRIGHTNESS == DEFAULT | LOW | HIGH and
		// context.CAPABILITY.CONTEXT_LIGHTNING
		// then newCapability = USER_BRIGHTNESS.VERY_HIGH for avoiding sun
		// reflection

		final Object userBrightnessValue 		= user.getCapabilityValue(CAPABILITY.USER_BRIGHTNESS);
		final Object contextIlluminanceValue 	= context.getCapabilityValue(CAPABILITY.CONTEXT_LIGHTNING);

		if (!(userBrightnessValue.equals(ICapability.BRIGHTNESS.ONLY_HIGH)) 	//These configurations allow no adaptation
				&& !(userBrightnessValue.equals(ICapability.BRIGHTNESS.ONLY_LOW) )
				&& !(userBrightnessValue.equals(ICapability.BRIGHTNESS.ONLY_VERY_HIGH))){

			if ((userBrightnessValue.equals(ICapability.BRIGHTNESS.DEFAULT)
					|| userBrightnessValue.equals(ICapability.BRIGHTNESS.LOW)
					|| userBrightnessValue.equals(ICapability.BRIGHTNESS.HIGH))) { //If BRIGHTNESS.VERY_HIGHT -> no applicable adaptation
				if (contextIlluminanceValue
						.equals(ICapability.ILLUMINANCE.SUNLIGHT)) {
					user.setCapabilityValue(CAPABILITY.USER_BRIGHTNESS,
							ICapability.BRIGHTNESS.VERY_HIGH);
				} else if (contextIlluminanceValue
						.equals(ICapability.ILLUMINANCE.DAYLIGHT)){
					user.setCapabilityValue(CAPABILITY.USER_BRIGHTNESS,
							ICapability.BRIGHTNESS.HIGH);
				} else if ((contextIlluminanceValue
						.equals(ICapability.ILLUMINANCE.MOONLESS_CLEAR_NIGHT)) || (contextIlluminanceValue
								.equals(ICapability.ILLUMINANCE.MOONLESS_OVERCAST_NIGHT))
								|| (contextIlluminanceValue
										.equals(ICapability.ILLUMINANCE.FULL_MOON_CLEAR_NIGHT))
										|| (contextIlluminanceValue
												.equals(ICapability.ILLUMINANCE.TWILIGHT_SKY))) {
					user.setCapabilityValue(CAPABILITY.USER_BRIGHTNESS,
							ICapability.BRIGHTNESS.LOW);
				}
			}
		}
		

		final Object userVolumeValue 	= user.getCapabilityValue(CAPABILITY.USER_VOLUME);
		final Object contextNoiseValue 	= (Float) context.getCapabilityValue(CAPABILITY.CONTEXT_NOISE);

		if (!(userVolumeValue.equals(ICapability.VOLUME.ONLY_LOW)) && !(userVolumeValue.equals(ICapability.VOLUME.ONLY_HIGHT)) && 
				!(userVolumeValue.equals(ICapability.VOLUME.ONLY_VERY_HIGHT))){
			if ((userVolumeValue.equals(ICapability.VOLUME.DEFAULT))
					|| (userVolumeValue.equals(ICapability.VOLUME.LOW))
					|| (userVolumeValue.equals(ICapability.VOLUME.HIGHT))) { //If VOLUME.VERY_HIGHT -> no applicable adaptation
				if (contextNoiseValue.equals(ICapability.NOISE.NOISY)) { 
					user.setCapabilityValue(CAPABILITY.USER_VOLUME,
							ICapability.VOLUME.VERY_HIGH);
				} else if (contextNoiseValue.equals(ICapability.NOISE.STREET)) { 
					user.setCapabilityValue(CAPABILITY.USER_VOLUME,
							ICapability.VOLUME.HIGHT);
				} else if (contextNoiseValue.equals(ICapability.NOISE.NOT_NOISY)) { 
					user.setCapabilityValue(CAPABILITY.USER_VOLUME,
							ICapability.VOLUME.DEFAULT); //TODO: Not using VOLUME.LOW
				}
			}
		}

		return user; 
	}

}
