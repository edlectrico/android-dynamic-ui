package es.deusto.deustotech.dynamicui.model.user;

import android.graphics.Color;

import java.util.HashMap;

import es.deusto.deustotech.dynamicui.model.AbstractCapabilities;

public class UserCapabilities extends AbstractCapabilities {

//	private static enum LANGUAGE {
//		ENGLISH, ESPAÑOL, EUSKERA
//	};

	//If the user is deaf he should be able to restrict the output to ONLY_VISUAL.
	//The same way, a blind user should be able to configure the output to ONLY_AUDIO
	private static enum OUTPUT {
		VISUAL, AUDIO,				//Common and adaptable configuration
		ONLY_AUDIO, ONLY_VISUAL		//Priority demands for disabled users
	};

//	private static enum EXPERIENCE {
//		EXPERT, STANDARD, NO_EXPERIENCE
//	};

	public UserCapabilities() {
		super();
		
		this.caps = new HashMap<CAPABILITY, Object>();
	}

	public UserCapabilities(BRIGHTNESS brightness, VOLUME volume, VIEW_SIZE viewSize, TEXT_SIZE textSize) {
		super();

		this.caps = new HashMap<CAPABILITY, Object>();

		caps.put(CAPABILITY.BRIGHTNESS, brightness);
		caps.put(CAPABILITY.CONTRAST, 	CONTRAST.DEFAULT);
		caps.put(CAPABILITY.INPUT, 		INPUT.HAPTIC);
		caps.put(CAPABILITY.VIEW_SIZE, 	viewSize);
		caps.put(CAPABILITY.TEXT_SIZE, 	textSize);
		caps.put(CAPABILITY.OUTPUT, 	OUTPUT.VISUAL);
		caps.put(CAPABILITY.TEXT_COLOR, Color.WHITE);
		caps.put(CAPABILITY.VIEW_COLOR, Color.DKGRAY);
		caps.put(CAPABILITY.VOLUME, 	volume);
	}
}
