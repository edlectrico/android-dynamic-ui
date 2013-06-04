package es.deusto.deustotech.modules;

import android.graphics.Color;
import es.deusto.deustotech.model.ICapability;
import es.deusto.deustotech.model.ICapability.CAPABILITY;
import es.deusto.deustotech.model.ICapability.IMAGES;
import es.deusto.deustotech.model.ICapability.TEXT_SIZE;
import es.deusto.deustotech.model.user.UserCapabilities;

public class UserCapabilitiesUpdater {

	/**
	 * This module will be the responsible for updating the user capabilities
	 * taking into account the current context parameters. Therefore, it should
	 * modify current user capabilities taking into account the new current
	 * context situation.
	 */

	/**
	 * Given a user and a context situation this method updates
	 * the user capabilities to the new current context parameters.
	 * 
	 * @param user
	 * @param context
	 * @return an updated user
	 */
	static public ICapability update(ICapability user, ICapability context) {
		// TODO: for the moment just adapt NOISE and LIGHTNING

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
		
		final Object userInputValue 	= user.getCapabilityValue(CAPABILITY.USER_INPUT);
		
		if (userInputValue.equals(UserCapabilities.INPUT.HAPTIC)){
			if (contextIlluminanceValue
					.equals(ICapability.ILLUMINANCE.SUNLIGHT)) {
				//TODO: Bigger and more visible controls
				user.setCapabilityValue(CAPABILITY.USER_MAX_TEXT_SIZE, TEXT_SIZE.VERY_BIG);
				user.setCapabilityValue(CAPABILITY.USER_MAX_VIEW_SIZE, TEXT_SIZE.VERY_BIG);
				user.setCapabilityValue(CAPABILITY.USER_IMAGES, IMAGES.ONLY_BIG);
				user.setCapabilityValue(CAPABILITY.USER_VIEW_BACKGROUND_COLOR, Color.RED);
				
				/*
				 * 	caps.put(CAPABILITY.USER_BRIGHTNESS, brightness);
					caps.put(CAPABILITY.USER_CONTRAST, contrast);
					caps.put(CAPABILITY.USER_IMAGES, IMAGES.DEFAULT);
					caps.put(CAPABILITY.USER_INPUT, INPUT.HAPTIC);
					caps.put(CAPABILITY.USER_LANGUAGE, LANGUAGE.ENGLISH);
					caps.put(CAPABILITY.USER_MAX_TEXT_SIZE, maxTextSize);
					caps.put(CAPABILITY.USER_MIN_TEXT_SIZE, minTextSize);
					caps.put(CAPABILITY.USER_OUTPUT, OUTPUT.VISUAL);
					caps.put(CAPABILITY.USER_BACKGROUND_COLOR, COLOR.DEFAULT);
					caps.put(CAPABILITY.USER_TEXT_COLOR, COLOR.DEFAULT);
					caps.put(CAPABILITY.USER_EXPERIENCE, EXPERIENCE.STANDARD);
					caps.put(CAPABILITY.USER_VOLUME, volume);
					caps.put(CAPABILITY.USER_ACTIVITY, ACTIVITIES.NONE);
					caps.put(CAPABILITY.USER_LOCATION, location); 	//TODO: configure default locations? (HOME/STREET/WORK...)
					caps.put(CAPABILITY.USER_RELATIONSHIP, RELATIONSHIP.NONE);	//TODO: Does it mean anything?
				 */
			}
		}
		
		final Object userVolumeValue 	= user.getCapabilityValue(CAPABILITY.USER_VOLUME);
		final Object contextNoiseValue 	= context.getCapabilityValue(CAPABILITY.CONTEXT_NOISE);

		if (!(userVolumeValue.equals(ICapability.VOLUME.ONLY_LOW)) && !(userVolumeValue.equals(ICapability.VOLUME.ONLY_HIGHT)) && 
				!(userVolumeValue.equals(ICapability.VOLUME.ONLY_VERY_HIGHT))){
			if ((userVolumeValue.equals(ICapability.VOLUME.DEFAULT))
					|| (userVolumeValue.equals(ICapability.VOLUME.LOW))
					|| (userVolumeValue.equals(ICapability.VOLUME.HIGH))) { //If VOLUME.VERY_HIGHT -> no applicable adaptation
				if (contextNoiseValue.equals(ICapability.NOISE.NOISY)) { 
					user.setCapabilityValue(CAPABILITY.USER_VOLUME,
							ICapability.VOLUME.VERY_HIGH);
				} else if (contextNoiseValue.equals(ICapability.NOISE.STREET)) { 
					user.setCapabilityValue(CAPABILITY.USER_VOLUME,
							ICapability.VOLUME.HIGH);
				} else if (contextNoiseValue.equals(ICapability.NOISE.NOT_NOISY)) { 
					user.setCapabilityValue(CAPABILITY.USER_VOLUME,
							ICapability.VOLUME.DEFAULT); //TODO: Not using VOLUME.LOW
				}
			}
		}

		return user; 
	}

}
