package es.deusto.deustotech.dynamicui.components;

import es.deusto.deustotech.dynamicui.model.ICapability;

public class UIConfiguration {

	private int viewColor;
	private int textColor;
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

	public int getViewColor() {
		return viewColor;
	}

	public void setViewColor(int viewColor) {
		this.viewColor = viewColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public ICapability.VIEW_SIZE getViewSize() {
		return viewSize;
	}

	public void setViewSize(ICapability.VIEW_SIZE viewSize) {
		this.viewSize = viewSize;
	}

	public ICapability.TEXT_SIZE getTextSize() {
		return textSize;
	}

	public void setTextSize(ICapability.TEXT_SIZE textSize) {
		this.textSize = textSize;
	}

	public ICapability.BRIGHTNESS getBrightness() {
		return brightness;
	}

	public void setBrightness(ICapability.BRIGHTNESS brightness) {
		this.brightness = brightness;
	}
	
	@Override
	public boolean equals(Object o){
			return ((this.brightness.equals(((UIConfiguration) o).brightness)) && (this.textColor == ((UIConfiguration) o).textColor)
					&& (this.textSize == ((UIConfiguration) o).textSize) && (this.viewColor == ((UIConfiguration) o).viewColor) 
					&& (this.viewSize == ((UIConfiguration) o).viewSize));
			
	}

}
