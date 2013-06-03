package es.deusto.deustotech;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import es.deusto.deustotech.components.UIConfiguration;
import es.deusto.deustotech.components.WidgetName;
import es.deusto.deustotech.model.ICapability;
import es.deusto.deustotech.model.MockModelGenerator;
import es.deusto.deustotech.modules.AdaptationEngine;
import es.deusto.deustotech.modules.UIReasoner;
import es.deusto.deustotech.modules.UserCapabilitiesUpdater;

public class Main extends Activity {

	private HashMap<String, View> viewsMap; //Current UI
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main); // Default layout

		viewsMap = new HashMap<String, View>();
		
		GridLayout layout = (GridLayout) findViewById(R.id.default_layout);
		
		View button 	= findViewById(R.id.mybutton);
		View textView 	= findViewById(R.id.mytexview);
		View editText	= findViewById(R.id.myedittext);
		
		Log.d(this.getClass().getName(), "view is: " + button.getClass().getName());
		
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
		
		//TODO: generating mock user and context to call UserCapabilitiesUpdater
		//and obtain a updatedUser
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		final ICapability user 			= MockModelGenerator.generateMockUser();
		final ICapability context 		= MockModelGenerator.generateMockContext();
		final ICapability device		= MockModelGenerator.generateMockDevices();
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		//Context and users are directly related since context affect user capabilities
		final ICapability updatedUser 	= UserCapabilitiesUpdater.update(user, context);
		
		final UIReasoner uiReasoner = new UIReasoner(getApplicationContext());
		final UIConfiguration adaptedUIConfiguration = uiReasoner.getAdaptedConfiguration(updatedUser, device, viewsMap);
		
		//Once the current UI is loaded, we call the AdaptationModule to
		//perform the corresponding changes
		AdaptationEngine adaptationModule = new AdaptationEngine(viewsMap, getApplicationContext(), adaptedUIConfiguration);
		adaptationModule.adaptConfiguration();
		
		//TODO: the following code is just to test the automatic adaptation each 1000 milliseconds 
//		new Thread(adaptationModule).start();
	}
}