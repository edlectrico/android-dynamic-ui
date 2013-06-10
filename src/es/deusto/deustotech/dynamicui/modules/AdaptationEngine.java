package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.view.View;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.components.WidgetName;

public class AdaptationEngine {

//	private static final String[] COMPONENT_BACKGROUND_COLOR = { "blue", "red",
//			"yellow", "green", "black", "white" };

	private HashMap<String, View> componentsToAdapt;
	private UIConfiguration configuration;

	public AdaptationEngine() {
		super();
	}

	public AdaptationEngine(HashMap<String, View> viewsMap) {
		super();

		this.componentsToAdapt = viewsMap;
	}
	
	public AdaptationEngine(HashMap<String, View> viewsMap,  
			UIConfiguration adaptedConfiguration) {
		super();

		this.componentsToAdapt 	= viewsMap;
		this.configuration 		= adaptedConfiguration;
	}
	
	/**
	 * This method takes both componentsToAdapt and adaptatedConfiguration and performs
	 * the necessary adaptations in the first HashMap.
	 * 
	 * @return an adapted configuration HashMap
	 */
	public HashMap<String, View> adaptConfiguration(){
		componentsToAdapt.get(WidgetName.BUTTON).post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get(WidgetName.BUTTON).setBackgroundColor(configuration.getViewColor());
				componentsToAdapt.get(WidgetName.BUTTON).setMinimumHeight(configuration.getHeight());
				componentsToAdapt.get(WidgetName.BUTTON).setMinimumWidth(configuration.getWidth());
			}
		});
		
		return componentsToAdapt;
	}

	//Not used
	/*
	public HashMap<String, View> adapt() {
		//Previously it should check entities status
		componentsToAdapt.get(WidgetName.BUTTON).post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get(WidgetName.BUTTON).setBackgroundColor(Color.parseColor(COMPONENT_BACKGROUND_COLOR[generateRandomValue()]));
				componentsToAdapt.get(WidgetName.BUTTON).setMinimumHeight(generateRandomValue() * 100);
				componentsToAdapt.get(WidgetName.BUTTON).setMinimumWidth(generateRandomValue() * 100);
			}
		});
		
		componentsToAdapt.get(WidgetName.EDIT_TEXT).post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get(WidgetName.EDIT_TEXT).setBackgroundColor(Color.parseColor(COMPONENT_BACKGROUND_COLOR[generateRandomValue()]));
				componentsToAdapt.get(WidgetName.EDIT_TEXT).setMinimumHeight(generateRandomValue() * 100);
				componentsToAdapt.get(WidgetName.EDIT_TEXT).setMinimumWidth(generateRandomValue() * 100);
			}
		});
		
		componentsToAdapt.get(WidgetName.TEXT_VIEW).post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get(WidgetName.TEXT_VIEW).setBackgroundColor(Color.parseColor(COMPONENT_BACKGROUND_COLOR[generateRandomValue()]));
				componentsToAdapt.get(WidgetName.TEXT_VIEW).setMinimumHeight(generateRandomValue() * 100);
				componentsToAdapt.get(WidgetName.TEXT_VIEW).setMinimumWidth(generateRandomValue() * 100);
			}
		});
		
		return componentsToAdapt;
	}
	*/
	
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
