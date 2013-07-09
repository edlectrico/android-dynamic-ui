package es.deusto.deustotech.dynamicui.components;

import es.deusto.deustotech.dynamicui.model.ICapability;

public class UIConfiguration {

//	private int viewColor;
//	private int textColor;
//	private int height;
//	private int width;
	private ICapability.VIEW_SIZE viewSize;
	
//	private int volume;
//	private int brightness;

	public UIConfiguration(){
		super();
	}

	public UIConfiguration(ICapability.VIEW_SIZE vs/*int viewColor, int textColor, int height, int width*/) {
		super();
		this.viewSize = vs;
//		this.viewColor = viewColor;
//		this.textColor = textColor;
//		this.height = height;
//		this.width = width;
	}

	public ICapability.VIEW_SIZE getViewSize() {
		return viewSize;
	}

	public void setViewSize(ICapability.VIEW_SIZE viewSize) {
		this.viewSize = viewSize;
	}

}
