package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.R;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.components.WidgetName;
import es.deusto.deustotech.dynamicui.model.ICapability;

public class AdaptationManager {

	private HashMap<String, View> componentsToAdapt;
	private UIConfiguration configuration;
	private Context context;
	private SharedPreferences userPreferences;
	private SharedPreferences uiPreferences;

	public AdaptationManager() {
		super();
	}

	public AdaptationManager(HashMap<String, View> viewsMap,  
			final UIConfiguration finalConfiguration, Context appContext) {
		super();

		this.componentsToAdapt 	= viewsMap;
		this.configuration 		= finalConfiguration;
		this.context 			= appContext;
		
		this.userPreferences = this.context.getSharedPreferences(this.context
				.getResources().getString(R.string.preferences_name_user), 0);
		this.uiPreferences = this.context.getSharedPreferences(this.context
				.getResources().getString(R.string.preferences_name_ui), 0);
	}
	
	/**
	 * This method takes both componentsToAdapt and adaptatedConfiguration and performs
	 * the necessary adaptations in the first HashMap.
	 * 
	 * @return an adapted configuration HashMap
	 */
	public HashMap<String, View> adaptConfiguration(final ICapability adaptedUser){
		//TODO: Adapt more than Size (Color, Brightness, etc).
		componentsToAdapt.get(WidgetName.BUTTON).post(new Runnable() {
			@Override
			public void run() {
				if (configuration != null){
					if (configuration.getBrightness().equals(ICapability.BRIGHTNESS.VERY_HIGH)){
						//TODO: adapt brightness
					} else if (configuration.getBrightness().equals(ICapability.BRIGHTNESS.LOW)){
						//TODO: adapt brightness
					}
					
					if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
						componentsToAdapt.get(WidgetName.BUTTON).setMinimumHeight(300);
						componentsToAdapt.get(WidgetName.BUTTON).setMinimumWidth(500);
					} else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
						componentsToAdapt.get(WidgetName.BUTTON).setMinimumHeight(50);
						componentsToAdapt.get(WidgetName.BUTTON).setMinimumWidth(100);
					}
					componentsToAdapt.get(WidgetName.BUTTON).setBackgroundColor(configuration.getViewColor());
					((Button) componentsToAdapt.get(WidgetName.BUTTON)).setTextColor(configuration.getTextColor());
				}
			}
		});
		
		componentsToAdapt.get(WidgetName.EDIT_TEXT).post(new Runnable() {
			@Override
			public void run() {
				if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
					componentsToAdapt.get(WidgetName.EDIT_TEXT).setMinimumHeight(300);
					componentsToAdapt.get(WidgetName.EDIT_TEXT).setMinimumWidth(500);
				} else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
					componentsToAdapt.get(WidgetName.EDIT_TEXT).setMinimumHeight(50);
					componentsToAdapt.get(WidgetName.EDIT_TEXT).setMinimumWidth(100);
				}
			}
		});
		
		componentsToAdapt.get(WidgetName.TEXT_VIEW).post(new Runnable() {
			@Override
			public void run() {
				if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
					componentsToAdapt.get(WidgetName.TEXT_VIEW).setMinimumHeight(300);
					componentsToAdapt.get(WidgetName.TEXT_VIEW).setMinimumWidth(500);
				} else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
					componentsToAdapt.get(WidgetName.TEXT_VIEW).setMinimumHeight(50);
					componentsToAdapt.get(WidgetName.TEXT_VIEW).setMinimumWidth(100);
				}
				componentsToAdapt.get(WidgetName.TEXT_VIEW).setBackgroundColor(configuration.getViewColor());
				((TextView) componentsToAdapt.get(WidgetName.TEXT_VIEW)).setTextColor(configuration.getTextColor());
			}
		});
		
		storeAdaptedConfiguration(adaptedUser);
		
		return componentsToAdapt;
	}
	
	/**
	 * This method stores the current UIConfiguration in a JSON format in
	 * the SharedPreferences
	 */
	private void storeAdaptedConfiguration(final ICapability adaptedUser) { //last known UI (HistoryManager)
		SharedPreferences.Editor userEditor = userPreferences.edit();

//		HashMap<ICapability, UIConfiguration> adaptedUserConf = new HashMap<ICapability, UIConfiguration>();
//		final BRIGHTNESS brightness = (BRIGHTNESS) adaptedUser.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS);
//		final VIEW_SIZE viewSize = (VIEW_SIZE) adaptedUser.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE);
//		final TEXT_SIZE textSize = (TEXT_SIZE) adaptedUser.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE);
//		
//		adaptedUserConf.put(new UserCapabilities(brightness, null, viewSize, textSize), configuration);
		
		Gson gson = new Gson();
		String json = gson.toJson(adaptedUser);
		//TODO Here the problem is that every adaptation will be stored here, deleting the previous one
		//Will we store more than one adaptation?
		userEditor.putString(this.context.getResources().getString(R.string.adapted_configuration_user), json);
		userEditor.commit();
		
		SharedPreferences.Editor uiEditor = uiPreferences.edit();
		json = gson.toJson(configuration);
		uiEditor.putString(this.context.getResources().getString(R.string.adapted_configuration_ui), json);
		uiEditor.commit();
		
		
		
		//Check data
//		json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
//		System.out.println(json);
	}

	
	//TODO: The following methods are just to test 
	//the automatic adaptation each 1000 milliseconds 
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/*
	private int generateRandomValue() {
		Random random = new Random();
		return random.nextInt(6);
	}
	 */

	/*
	@Override
	public void run() {
		while (true){
			adapt();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	*/
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
