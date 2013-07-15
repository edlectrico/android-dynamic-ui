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
import es.deusto.deustotech.dynamicui.model.ICapability;

public class AdaptationManager {

	private HashMap<String, View> componentsToAdapt;
	private UIConfiguration configuration;
	private Context context;
	private SharedPreferences preferences;
	private ICapability user;

	public AdaptationManager() {
		super();
	}

	public AdaptationManager(HashMap<String, View> viewsMap,  
			UIConfiguration adaptedConfiguration, Context appContext, 
			ICapability adaptedUser) {
		super();

		this.componentsToAdapt 	= viewsMap;
		this.configuration 		= adaptedConfiguration;
		this.context 			= appContext;
		this.user 				= adaptedUser;
		
		this.preferences = this.context.getSharedPreferences(this.context.getResources().getString(R.string.preferences_name), 0);
	}
	
	/**
	 * This method takes both componentsToAdapt and adaptatedConfiguration and performs
	 * the necessary adaptations in the first HashMap.
	 * 
	 * @return an adapted configuration HashMap
	 */
	public HashMap<String, View> adaptConfiguration(){
		
		//TODO: Adapt more than Size (Color, Brightness, etc).
		componentsToAdapt.get("BUTTON").post(new Runnable() {
			@Override
			public void run() {
				if (configuration.getBrightness().equals(ICapability.BRIGHTNESS.VERY_HIGH)){
					//TODO: adapt brightness
				} else if (configuration.getBrightness().equals(ICapability.BRIGHTNESS.LOW)){
					//TODO: adapt brightness
				}
				
				if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
					componentsToAdapt.get("BUTTON").setMinimumHeight(300);
					componentsToAdapt.get("BUTTON").setMinimumWidth(500);
				} else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
					componentsToAdapt.get("BUTTON").setMinimumHeight(50);
					componentsToAdapt.get("BUTTON").setMinimumWidth(100);
				}
				componentsToAdapt.get("BUTTON").setBackgroundColor(configuration.getViewColor());
				((Button) componentsToAdapt.get("BUTTON")).setTextColor(configuration.getTextColor());
			}
		});
		
		componentsToAdapt.get("EDIT TEXT").post(new Runnable() {
			@Override
			public void run() {
				if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
					componentsToAdapt.get("EDIT TEXT").setMinimumHeight(300);
					componentsToAdapt.get("EDIT TEXT").setMinimumWidth(500);
				} else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
					componentsToAdapt.get("EDIT TEXT").setMinimumHeight(50);
					componentsToAdapt.get("EDIT TEXT").setMinimumWidth(100);
				}
			}
			
		});
		
		componentsToAdapt.get("TEXT VIEW").post(new Runnable() {
			@Override
			public void run() {
				if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
					componentsToAdapt.get("TEXT VIEW").setMinimumHeight(300);
					componentsToAdapt.get("TEXT VIEW").setMinimumWidth(500);
				} else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
					componentsToAdapt.get("TEXT VIEW").setMinimumHeight(50);
					componentsToAdapt.get("TEXT VIEW").setMinimumWidth(100);
				}
				componentsToAdapt.get("TEXT VIEW").setBackgroundColor(configuration.getViewColor());
				((TextView) componentsToAdapt.get("TEXT VIEW")).setTextColor(configuration.getTextColor());
			}
			
		});
		
		storeAdaptedConfiguration();
		
		return componentsToAdapt;
	}
	
	private void storeAdaptedConfiguration() {
		SharedPreferences.Editor editor = preferences.edit();

		HashMap<ICapability, UIConfiguration> currentSituation = new HashMap<ICapability, UIConfiguration>();
		currentSituation.put(user, configuration); //We store just the button adaptation
		
		Gson gson = new Gson();
		String json = gson.toJson(currentSituation);
		//TODO Here the problem is that every adaptation will be stored here, deleting the previous one
		editor.putString(this.context.getResources().getString(R.string.adapted_configuration), json);
		editor.commit();
		
		json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
		System.out.println(json);
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
