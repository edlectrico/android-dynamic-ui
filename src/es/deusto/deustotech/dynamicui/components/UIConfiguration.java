package es.deusto.deustotech.dynamicui.components;

public class UIConfiguration {

	private String viewColor;
	private String textColor;
	private int height;
	private int width;
	
//	private int volume;
//	private int brightness;

	public UIConfiguration(){
		super();
	}

	public UIConfiguration(String viewColor, String textColor, int height, int width,
			String text) {
		super();
		this.viewColor = viewColor;
		this.textColor = textColor;
		this.height = height;
		this.width = width;
	}

	public String getViewColor() {
		return viewColor;
	}

	public void setViewColor(String viewColor) {
		this.viewColor = viewColor;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
