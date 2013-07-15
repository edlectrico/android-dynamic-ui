package es.deusto.deustotech.dynamicui;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.components.WidgetName;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.CAPABILITY;
import es.deusto.deustotech.dynamicui.model.MockModelGenerator;
import es.deusto.deustotech.dynamicui.model.context.ContextCapabilities;
import es.deusto.deustotech.dynamicui.modules.AdaptationManager;
import es.deusto.deustotech.dynamicui.modules.HistoryManager;
import es.deusto.deustotech.dynamicui.modules.UIReasoner;
import es.deusto.deustotech.dynamicui.modules.UserCapabilitiesUpdater;

public class Main extends Activity implements android.view.View.OnClickListener{

	/**
	 * If the project will be a library we should manage the views
	 * map in a method like "addComponents" or something similar to
	 * be as transparent as possible to the developer and the main
	 * Activity. 
	 * 
	 * We also should indicate how to "draw" components in the
	 * layout file, since they are not "Button" or "TextEdit"
	 * anymore. They all are ProxyWidget now.
	 */

	private HashMap<String, View> viewsMap; //Current UI container
	private ICapability context, user, device;
	private View button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main); // Default layout

		//Button for triggering other context
		findViewById(R.id.context_force_high_button).setOnClickListener(this);
		findViewById(R.id.context_force_low_button).setOnClickListener(this);
		
		viewsMap = new HashMap<String, View>();

		GridLayout layout = (GridLayout) findViewById(R.id.default_layout); //main.xml

		button 			= findViewById(R.id.mybutton);
		View textView 	= findViewById(R.id.mytexview);
		View editText	= findViewById(R.id.myedittext);

		Log.d(this.getClass().getName(), "view is: " + button.getClass().getName());

		/*
		for (int i=0; i<WidgetName.COMPONENT_NAMES.length; ++i){
			if (WidgetName.COMPONENT_NAMES[i] == WidgetName.BUTTON){
				viewsMap.put(WidgetName.COMPONENT_NAMES[i], button);
			} else if (WidgetName.COMPONENT_NAMES[i] == WidgetName.TEXT_VIEW){
				viewsMap.put(WidgetName.COMPONENT_NAMES[i], textView);
			} else if  (WidgetName.COMPONENT_NAMES[i] == WidgetName.EDIT_TEXT){
				viewsMap.put(WidgetName.COMPONENT_NAMES[i], editText);
			}

			layout.addView(viewsMap.get(WidgetName.COMPONENT_NAMES[i]));
			viewsMap.get(WidgetName.COMPONENT_NAMES[i]).invalidate();
		}
		*/
		
		viewsMap.put(WidgetName.BUTTON, button);
		viewsMap.put(WidgetName.TEXT_VIEW, textView);
		viewsMap.put(WidgetName.EDIT_TEXT, editText);
		
		layout.addView(viewsMap.get(WidgetName.BUTTON));
		layout.addView(viewsMap.get(WidgetName.TEXT_VIEW));
		layout.addView(viewsMap.get(WidgetName.EDIT_TEXT));
		viewsMap.get(WidgetName.BUTTON).invalidate();
		viewsMap.get(WidgetName.TEXT_VIEW).invalidate();
		viewsMap.get(WidgetName.EDIT_TEXT).invalidate();
		
		//Generating mock user and context to call UserCapabilitiesUpdater
		//and obtain a updatedUser
		//+++++++++++++++++++++++++++++++++++++++++++++++++
		user 	= MockModelGenerator.generateMockUser();
		context = MockModelGenerator.generateMockContext();
		device	= MockModelGenerator.generateMockDevice();
		//+++++++++++++++++++++++++++++++++++++++++++++++++
		
		adapt(user, context);
	}
		
	private void adapt(ICapability user, ICapability context){
		Log.e(Main.class.getSimpleName(), context.getCapabilityValue(CAPABILITY.BRIGHTNESS).toString());

		//Context and users are directly related since context affect user capabilities
		//TODO: rules here?
		user = UserCapabilitiesUpdater.update(user, context);

		//TODO: For each component...
//		HashMap<String, UIConfiguration> currentUI = new HashMap<String, UIConfiguration>();
		//TODO: getCurrentUI
		//1. If getUIFromSharedPreferences == null, return new default UI
		//2. Else, currentUI = getUIFromSharedPreferences
		UIConfiguration currentUI;
		HistoryManager historyManager = new HistoryManager(getApplicationContext());
		if (historyManager.isEmpty()){
			//Default Android UI
			currentUI = new UIConfiguration(ICapability.VIEW_SIZE.DEFAULT,ICapability.TEXT_SIZE.DEFAULT,
					ICapability.BRIGHTNESS.DEFAULT, Color.DKGRAY, Color.WHITE);
		} else {
			currentUI = historyManager.getLastKnownUI();
		}
		
		/*
		final UIConfiguration defaultConf = new UIConfiguration(ICapability.VIEW_SIZE.DEFAULT,ICapability.TEXT_SIZE.DEFAULT,
				ICapability.BRIGHTNESS.DEFAULT, Color.DKGRAY, Color.WHITE);
		
		currentUI.put(WidgetName.BUTTON, defaultConf);
		currentUI.put(WidgetName.TEXT_VIEW, defaultConf);
		currentUI.put(WidgetName.EDIT_TEXT, defaultConf);
		*/

		final UIReasoner uiReasoner = new UIReasoner(user, device, context, currentUI, getApplicationContext());
		final UIConfiguration finalUIConfiguration = uiReasoner.getAdaptedConfiguration();

		//Once the current UI is loaded, we call the AdaptationModule to
		//perform the corresponding changes
		AdaptationManager adaptationModule = new AdaptationManager(viewsMap, finalUIConfiguration, getApplicationContext(), user);
		adaptationModule.adaptConfiguration();
		
		//TODO: update currentUI with the last adapted one


		//The following code is just to @test the automatic adaptation each 1000 milliseconds 
		//new Thread(adaptationModule).start();
	}

	private void toast(){
		Toast.makeText(getApplicationContext(), "Pushed!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.context_force_high_button:
			adapt(user, new ContextCapabilities(ICapability.BRIGHTNESS.HIGH, ICapability.NOISE.NOT_NOISY));
			break;
			
		case R.id.context_force_low_button:
			adapt(user, new ContextCapabilities(ICapability.BRIGHTNESS.LOW, ICapability.NOISE.NOT_NOISY));
			break;
			
		case R.id.mybutton:
			toast();
			break;
		}
	}

}