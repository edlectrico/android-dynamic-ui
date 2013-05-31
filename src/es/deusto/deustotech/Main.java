package es.deusto.deustotech;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import es.deusto.deustotech.model.ICapability;
import es.deusto.deustotech.model.MockModelGenerator;
import es.deusto.deustotech.modules.AdaptationEngine;
import es.deusto.deustotech.modules.UserCapabilitiesUpdater;

public class Main extends Activity {

	private HashMap<String, View> viewsMap;
	//Components definition in WidgetRegistry.java
	private static final String BUTTON 		= Button.class.getSimpleName();
	private static final String TEXT_VIEW 	= TextView.class.getSimpleName();
	private static final String EDIT_TEXT	= EditText.class.getSimpleName();
	
	private static String [] COMPONENT_NAMES = {BUTTON, TEXT_VIEW, EDIT_TEXT};
	
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
		
		for (int i=0; i<COMPONENT_NAMES.length; ++i){
			if (COMPONENT_NAMES[i] == BUTTON){
				viewsMap.put(COMPONENT_NAMES[i], button);
			} else if (COMPONENT_NAMES[i] == TEXT_VIEW){
				viewsMap.put(COMPONENT_NAMES[i], textView);
			} else if  (COMPONENT_NAMES[i] == EDIT_TEXT){
				viewsMap.put(COMPONENT_NAMES[i], editText);
			}
				
			layout.addView(viewsMap.get(COMPONENT_NAMES[i]));
			viewsMap.get(COMPONENT_NAMES[i]).invalidate();
		}
		
		//TODO: generating mock user and context to call UserCapabilitiesUpdater
		//and obtain a updatedUser
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		final ICapability user 			= MockModelGenerator.generateMockUser();
		final ICapability context 		= MockModelGenerator.generateMockContext();
		
		final ICapability updatedUser 	= UserCapabilitiesUpdater.update(user, context);
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		//TODO: generateUI(updatedUser);
		
		//Once the current UI is loaded, we call the AdaptationModule to
		//perform the corresponding changes
		AdaptationEngine adaptationModule = new AdaptationEngine(viewsMap, getApplicationContext());
		
		//TODO: the following code is just to test the automatic adaptation each 1000 milliseconds 
		new Thread(adaptationModule).start();
	}
	
}