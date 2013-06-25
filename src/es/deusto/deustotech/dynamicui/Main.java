package es.deusto.deustotech.dynamicui;

import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import es.deusto.deustotech.dynamicui.components.FinalUIConfiguration;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.components.WidgetName;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.CAPABILITY;
import es.deusto.deustotech.dynamicui.model.MockModelGenerator;
import es.deusto.deustotech.dynamicui.modules.AdaptationManager;
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
	private SharedPreferences preferences;
	private ICapability context, user, device;
	private View button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main); // Default layout

		//Button for triggering other context
		findViewById(R.id.context_trigger_button).setOnClickListener(this);
		
		preferences = getSharedPreferences(
				getResources().getString(R.string.preferences_name), 0);

		viewsMap = new HashMap<String, View>();

		GridLayout layout = (GridLayout) findViewById(R.id.default_layout); //main.xml

		button 	= findViewById(R.id.mybutton);
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
		//+++++++++++++++++++++++++++++++++++++++++++++++++
		user 	= MockModelGenerator.generateMockUser();
		context = MockModelGenerator.generateMockContext();
		device	= MockModelGenerator.generateMockDevice();
		//+++++++++++++++++++++++++++++++++++++++++++++++++
		
		adapt(user, context);
	}
		
	private void adapt(ICapability user, ICapability context){
		Log.e(Main.class.getSimpleName(), context.getCapabilityValue(CAPABILITY.CONTEXT_LIGHTNING).toString());
		
		//Context and users are directly related since context affect user capabilities
		user = UserCapabilitiesUpdater.update(user, context);

		//TODO: For each component...
		HashMap<String, UIConfiguration> currentUI = new HashMap<String, UIConfiguration>();

		currentUI.put(WidgetName.BUTTON, new UIConfiguration(0, 0, 
				button.getLayoutParams().height, button.getLayoutParams().width, null));


		final UIReasoner uiReasoner     = new UIReasoner(user, device, currentUI, getApplicationContext());
		final FinalUIConfiguration finalUIConfiguration = uiReasoner.getAdaptedConfiguration();

		//TODO: Store updated user and adapted configuration
		storeCurrentSituation(user, finalUIConfiguration);

		//Once the current UI is loaded, we call the AdaptationModule to
		//perform the corresponding changes
		AdaptationManager adaptationModule = new AdaptationManager(viewsMap, finalUIConfiguration, getApplicationContext(), user);
		adaptationModule.adaptConfiguration();

		//The following code is just to @test the automatic adaptation each 1000 milliseconds 
		//new Thread(adaptationModule).start();
	}

	//TODO This probably should be outside the Main activity. 
	private void storeCurrentSituation(ICapability user, UIConfiguration configuration) {
		SharedPreferences.Editor editor = preferences.edit();
		
		HashMap<ICapability, UIConfiguration> currentSituation = new HashMap<ICapability, UIConfiguration>();
		currentSituation.put(user, configuration);
		
		Gson gson = new Gson();
		String json = gson.toJson(currentSituation);
		editor.putString(getResources().getString(R.string.current_situation), json);
		editor.commit();
	}
	
	private void toast(){
		Toast.makeText(getApplicationContext(), "Pushed!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.context_trigger_button:
			adapt(user, MockModelGenerator.generateMockContext());
			break;
			
		case R.id.mybutton:
			toast();
			break;
		}
	}

}