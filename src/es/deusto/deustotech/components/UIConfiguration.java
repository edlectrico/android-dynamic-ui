package es.deusto.deustotech.components;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class UIConfiguration {

	public static HashMap<String, View> getMockConfiguration(Context context){
		
		HashMap<String, View> componentsToAdapt = new HashMap<String, View>();
		
		Button button = new Button(context);
		button.setBackgroundColor(Color.RED);
		button.setHeight(300);
		button.setWidth(400);
		button.setText("TESTING");
		button.setTextColor(Color.WHITE);

		Log.e(UIConfiguration.class.getSimpleName(), "Height: " + button.getHeight());
		Log.e(UIConfiguration.class.getSimpleName(), "Width: " + button.getWidth());
		
		componentsToAdapt.put("Button", button);
		
		return componentsToAdapt;
	}
}
