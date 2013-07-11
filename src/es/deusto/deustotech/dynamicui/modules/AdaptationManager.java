package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.R;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.components.WidgetName;
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
		componentsToAdapt.get(WidgetName.BUTTON).post(new Runnable() {
			@Override
			public void run() {
//				componentsToAdapt.get(WidgetName.BUTTON).setBackgroundColor(configuration.getViewColor());
//                ((Button)componentsToAdapt.get(WidgetName.BUTTON)).setTextColor(configuration.getTextColor());
                if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.BIG)){
                	componentsToAdapt.get(WidgetName.BUTTON).setMinimumHeight(300);
                	componentsToAdapt.get(WidgetName.BUTTON).setMinimumWidth(500);
                } else if (configuration.getViewSize().equals(ICapability.VIEW_SIZE.SMALL)){
                	componentsToAdapt.get(WidgetName.BUTTON).setMinimumHeight(50);
                	componentsToAdapt.get(WidgetName.BUTTON).setMinimumWidth(100);
                }
			}
		});
		
		storeAdaptedConfiguration();
		
		return componentsToAdapt;
	}
	
	private void storeAdaptedConfiguration() {
		SharedPreferences.Editor editor = preferences.edit();

		//TODO: for each component
//		final UIConfiguration configuration = new UIConfiguration(componentsToAdapt.get(WidgetName.BUTTON).getSolidColor(),
//                ((Button) componentsToAdapt.get(WidgetName.BUTTON)).getCurrentTextColor(),
//				componentsToAdapt.get(WidgetName.BUTTON).getHeight(),
//				componentsToAdapt.get(WidgetName.BUTTON).getWidth());
		final UIConfiguration configuration = new UIConfiguration(ICapability.VIEW_SIZE.DEFAULT,ICapability.TEXT_SIZE.DEFAULT,
				ICapability.BRIGHTNESS.DEFAULT, Color.DKGRAY, Color.WHITE);
		
		
//		HashMap<ICapability, HashMap<String, View>> currentSituation = new HashMap<ICapability, HashMap<String, View>>();
		HashMap<ICapability, UIConfiguration> currentSituation = new HashMap<ICapability, UIConfiguration>();
		currentSituation.put(user, configuration);
		
		Gson gson = new Gson();
		String json = gson.toJson(currentSituation);
		//TODO Here the problem is that every adaptation will be stored here, deleting the previous one
		editor.putString(this.context.getResources().getString(R.string.adapted_configuration), json);
		editor.commit();
		
//		json = preferences.getString(this.context.getResources().getString(R.string.adapted_configuration), "");
//		System.out.println(json);
	}

	
	//TODO: Remove the following methods, they're just to test 
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
