package es.deusto.deustotech.dynamicui.components;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WidgetRegistry {
	private final static Map<String, Class<? extends View>> WIDGETS = new HashMap<String, Class<? extends View>>();
	
	static {
		String [] DEFAULT_WIDGETS = {
				Button.class.getSimpleName(),
				TextView.class.getSimpleName(),
				EditText.class.getSimpleName()
				//TODO add more widgets
		};
		for(String widgetName : DEFAULT_WIDGETS) {
			Class<? extends View> klass;
			try {
				klass = Class.forName("android.widget." + widgetName).asSubclass(View.class);
			} catch (ClassNotFoundException e) {
				// Or just log it (so as to support multiple android versions)
				throw new RuntimeException("Misconfigured: widget " + widgetName + " does not exist", e);
			}
			WIDGETS.put(widgetName, klass);
		}
	}
	
	public static void addWidget(String name, Class<? extends View> klass) {
		WIDGETS.put(name, klass);
	}
	
	public static Class<? extends View> getWidget(String name) {
		return WIDGETS.get(name);
	}
}
