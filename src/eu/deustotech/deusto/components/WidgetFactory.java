package eu.deustotech.deusto.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class WidgetFactory {
	public static View createView(String widgetName, Context context, AttributeSet attrs, int defStyle, boolean styleProvided) {
		final Class<? extends View> view = WidgetRegistry.getWidget(widgetName);
		if(view == null)
			throw new IllegalArgumentException("Unregistered widget: " + widgetName + "; check " + WidgetRegistry.class.getName());
		
		View viewObj;
		try {
			if(styleProvided) {
				viewObj = view.getConstructor(Context.class, AttributeSet.class, Integer.class).newInstance(context, attrs, defStyle);
			} else {
				viewObj = view.getConstructor(Context.class, AttributeSet.class).newInstance(context, attrs);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Widget " + widgetName + " does not provide proper constructor (styleProvided = " + styleProvided + ")");
		}
		
		return viewObj;
	}
}
