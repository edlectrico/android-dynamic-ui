package es.deusto.deustotech.dynamicui.components;

import es.deusto.deustotech.dynamicui.model.ICapability;

public class UIConfiguration {

	private int viewColor;
	private int textColor;
//	private int height;
//	private int width;
	private ICapability.VIEW_SIZE viewSize;
	private ICapability.TEXT_SIZE textSize;
	
//	private int volume;
	private ICapability.BRIGHTNESS brightness;

	public UIConfiguration(){
		super();
	}

	public UIConfiguration(ICapability.VIEW_SIZE vs, ICapability.TEXT_SIZE ts,ICapability.BRIGHTNESS b, int vc, int tc) {
		super();
		this.viewSize 	= vs;
		this.textSize 	= ts;
		this.brightness = b;
		this.viewColor 	= vc;
		this.textColor 	= tc;
	}

	public ICapability.VIEW_SIZE getViewSize() {
		return viewSize;
	}

	public void setViewSize(ICapability.VIEW_SIZE viewSize) {
		this.viewSize = viewSize;
	}

}
