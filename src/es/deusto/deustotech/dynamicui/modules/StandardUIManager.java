package es.deusto.deustotech.dynamicui.modules;

import android.graphics.Color;

import es.deusto.deustotech.dynamicui.components.FinalUIConfiguration;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;

public class StandardUIManager {

	/**
	 * This class manages the standard UI configurations
	 * for several default context situations. For example,
	 * for a context with SUNLIGH lightning a configuration
	 * with BRIGHTNESS.LEVEL.VERY_HIGH is presented
	 */

	public StandardUIManager() {
		super();
	}

	/**
	 * Given a user this method returns an standard UI configuration
	 * 
	 * @param user
	 * @return
	 */
	public FinalUIConfiguration getStandardConfiguration(ICapability user) {
		//TODO: Where is it stored?
		
		return new FinalUIConfiguration(Color.RED, Color.WHITE, 500, 500, null, ICapability.BRIGHTNESS.VERY_HIGH, 0, 0, "", 0, 0, 0);
	}

	/**
	 * This method checks every value of the user and the configuration
	 * using percentages to evaluate how it fits current situation
	 * 
	 * @param standardConfiguration
	 * @return
	 */
	public static boolean isSufficient(UIConfiguration standardConfiguration) {
		//TODO
		
		return false;
	}
}
