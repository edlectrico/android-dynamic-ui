package es.deusto.deustotech.dynamicui.modules;

import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.CAPABILITY;
import es.deusto.deustotech.dynamicui.model.user.UserCapabilities;

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

		final Object userBrightnessValue 		= user.getCapabilityValue(CAPABILITY.BRIGHTNESS);
		final Object contextIlluminanceValue 	= context.getCapabilityValue(CAPABILITY.CONTEXT_LIGHTNING);

		if (!(userBrightnessValue.equals(ICapability.BRIGHTNESS.ONLY_HIGH)) 	//These configurations allow no adaptation
				&& !(userBrightnessValue.equals(ICapability.BRIGHTNESS.ONLY_LOW) )
				&& !(userBrightnessValue.equals(ICapability.BRIGHTNESS.ONLY_VERY_HIGH))){

			if ((userBrightnessValue.equals(ICapability.BRIGHTNESS.DEFAULT)
					|| userBrightnessValue.equals(ICapability.BRIGHTNESS.LOW)
					|| userBrightnessValue.equals(ICapability.BRIGHTNESS.HIGH))) { //If BRIGHTNESS.VERY_HIGHT -> no applicable adaptation
				//CONTEXT_ILLUMINANCE
				if (contextIlluminanceValue
						.equals(ICapability.ILLUMINANCE.SUNLIGHT)) {
					user.setCapabilityValue(CAPABILITY.BRIGHTNESS,
							ICapability.BRIGHTNESS.VERY_HIGH);
					user.setCapabilityValue(CAPABILITY.VIEW_COLOR, ICapability.COLOR.WHITE);
//					user.setCapabilityValue(CAPABILITY.USER_MAX_TEXT_SIZE, TEXT_SIZE.VERY_BIG);
				} else if (contextIlluminanceValue
						.equals(ICapability.ILLUMINANCE.DAYLIGHT)){
					user.setCapabilityValue(CAPABILITY.BRIGHTNESS,
							ICapability.BRIGHTNESS.HIGH);
					user.setCapabilityValue(CAPABILITY.VIEW_COLOR, ICapability.COLOR.RED);
//					user.setCapabilityValue(CAPABILITY.USER_MAX_TEXT_SIZE, TEXT_SIZE.BIG);
				} else if ((contextIlluminanceValue
						.equals(ICapability.ILLUMINANCE.MOONLESS_CLEAR_NIGHT)) || (contextIlluminanceValue
								.equals(ICapability.ILLUMINANCE.MOONLESS_OVERCAST_NIGHT))
								|| (contextIlluminanceValue
										.equals(ICapability.ILLUMINANCE.FULL_MOON_CLEAR_NIGHT))
										|| (contextIlluminanceValue
												.equals(ICapability.ILLUMINANCE.TWILIGHT_SKY))) {
					user.setCapabilityValue(CAPABILITY.BRIGHTNESS,
							ICapability.BRIGHTNESS.LOW);
					user.setCapabilityValue(CAPABILITY.VIEW_COLOR, ICapability.COLOR.GRAY);
//					user.setCapabilityValue(CAPABILITY.USER_MAX_TEXT_SIZE, TEXT_SIZE.DEFAULT);
				}
				
				//USER_VIEW_SIZE
				final Object userViewSizeValue 	= user.getCapabilityValue(CAPABILITY.VIEW_SIZE);
				final Object userTextSizeValue 	= user.getCapabilityValue(CAPABILITY.TEXT_SIZE);
				
				if (!userViewSizeValue.equals(ICapability.VIEW_SIZE.ONLY_VERY_BIG)){
					if (contextIlluminanceValue.equals(ICapability.ILLUMINANCE.SUNLIGHT)){ //BIGGER CONTROLS/TEXT
						if (userViewSizeValue.equals(ICapability.VIEW_SIZE.DEFAULT)){
							user.setCapabilityValue(CAPABILITY.VIEW_SIZE, ICapability.VIEW_SIZE.BIG);
						} else if (userViewSizeValue.equals(ICapability.VIEW_SIZE.BIG)){
							user.setCapabilityValue(CAPABILITY.VIEW_SIZE, ICapability.VIEW_SIZE.VERY_BIG); //TODO: Is it correct?
						}
						//USER_TEXT_SIZE
						if (userTextSizeValue.equals(ICapability.TEXT_SIZE.DEFAULT)){
							user.setCapabilityValue(CAPABILITY.TEXT_SIZE, ICapability.TEXT_SIZE.BIG);
						} else if (userTextSizeValue.equals(ICapability.TEXT_SIZE.BIG)){
							user.setCapabilityValue(CAPABILITY.TEXT_SIZE, ICapability.TEXT_SIZE.VERY_BIG); //TODO: Is it correct?
						}
					} else if ((contextIlluminanceValue
							.equals(ICapability.ILLUMINANCE.MOONLESS_CLEAR_NIGHT)) || (contextIlluminanceValue
									.equals(ICapability.ILLUMINANCE.MOONLESS_OVERCAST_NIGHT))
									|| (contextIlluminanceValue
											.equals(ICapability.ILLUMINANCE.FULL_MOON_CLEAR_NIGHT))
											|| (contextIlluminanceValue
													.equals(ICapability.ILLUMINANCE.TWILIGHT_SKY))){ //SMALLER CONTROLS/TEXT
						user.setCapabilityValue(CAPABILITY.VIEW_SIZE, ICapability.VIEW_SIZE.SMALL);
					}
				}
			}
		}
		
		final Object userInputValue 	= user.getCapabilityValue(CAPABILITY.INPUT);
		
		if (userInputValue.equals(UserCapabilities.INPUT.HAPTIC)){
			if (contextIlluminanceValue
					.equals(ICapability.ILLUMINANCE.SUNLIGHT)) {
				//TODO: Bigger and more visible controls
//				user.setCapabilityValue(CAPABILITY.USER_TEXT_SIZE, TEXT_SIZE.VERY_BIG);
//				user.setCapabilityValue(CAPABILITY.USER_VIEW_SIZE, ICapability.VIEW_SIZE.VERY_BIG);
//				user.setCapabilityValue(CAPABILITY.USER_IMAGES, IMAGES.ONLY_BIG);
//				user.setCapabilityValue(CAPABILITY.USER_VIEW_BACKGROUND_COLOR, Color.RED);
				
				/*
					caps.put(CAPABILITY.USER_CONTRAST, contrast);
					caps.put(CAPABILITY.USER_IMAGES, IMAGES.DEFAULT);
					caps.put(CAPABILITY.USER_INPUT, INPUT.HAPTIC);
					caps.put(CAPABILITY.USER_LANGUAGE, LANGUAGE.ENGLISH);
					
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
		
		final Object userVolumeValue 	= user.getCapabilityValue(CAPABILITY.VOLUME);
		final Object contextNoiseValue 	= context.getCapabilityValue(CAPABILITY.CONTEXT_NOISE);

		if (!(userVolumeValue.equals(ICapability.VOLUME.ONLY_LOW)) && !(userVolumeValue.equals(ICapability.VOLUME.ONLY_HIGHT)) && 
				!(userVolumeValue.equals(ICapability.VOLUME.ONLY_VERY_HIGHT))){
			if ((userVolumeValue.equals(ICapability.VOLUME.DEFAULT))
					|| (userVolumeValue.equals(ICapability.VOLUME.LOW))
					|| (userVolumeValue.equals(ICapability.VOLUME.HIGH))) { //If VOLUME.VERY_HIGHT -> no applicable adaptation
				if (contextNoiseValue.equals(ICapability.NOISE.NOISY)) { 
					user.setCapabilityValue(CAPABILITY.VOLUME,
							ICapability.VOLUME.VERY_HIGH);
				} else if (contextNoiseValue.equals(ICapability.NOISE.STREET)) { 
					user.setCapabilityValue(CAPABILITY.VOLUME,
							ICapability.VOLUME.HIGH);
				} else if (contextNoiseValue.equals(ICapability.NOISE.NOT_NOISY)) { 
					user.setCapabilityValue(CAPABILITY.VOLUME,
							ICapability.VOLUME.DEFAULT); //TODO: Not using VOLUME.LOW
				}
			}
		}

		return user; 
	}

}
