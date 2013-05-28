package eu.deustotech.deusto;

import java.util.HashMap;

import eu.deustotech.deusto.modules.AdaptationModule;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

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
		
		//Once the current UI is loaded, we call the AdaptationModule to
		//perform the corresponding changes
		AdaptationModule adaptationModule = new AdaptationModule(viewsMap, getApplicationContext());
		
		//TODO: the following code is just to test the automatic adaptation each 1000 milliseconds 
		new Thread(adaptationModule).start();
	}
	
}