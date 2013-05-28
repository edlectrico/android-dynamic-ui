package eu.deustotech.deusto.modules;

import eu.deustotech.deusto.model.ICapability;
import eu.deustotech.deusto.model.ICapability.CAPABILITY;

public class UserCapabilitiesUpdater {

	/**
	 * This module will be the responsible for updating the user
	 * capabilities taking into account the current context
	 * parameters. Therefore, it should modify current
	 * user capabilities taking into account the new current
	 * context situation. 
	 */
	
	static public ICapability update(ICapability user, ICapability context){
		//TODO: for the moment just adapt noise and lightning

		//TODO:
		//if user.CAPABILITY.USER_BRIGHTNESS == DEFAULT | LOW | HIGH and context.CAPABILITY.CONTEXT_LIGHTNING
		//then newCapability = USER_BRIGHTNESS.VERY_HIGH for avoiding sun reflection
		
		if ((user.getCapabilityValue(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.DEFAULT) || 
				user.getCapabilityValue(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.LOW) ||
				user.getCapabilityValue(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.HIGH))){
			if (context.getCapabilityValue(CAPABILITY.CONTEXT_LIGHTNING).equals(ICapability.ILLUMINANCE.SUNLIGHT)){
				user.setCapabilityValue(CAPABILITY.USER_BRIGHTNESS, ICapability.BRIGHTNESS.VERY_HIGH);
			}
		}
		
		return user;
	}
	
}
