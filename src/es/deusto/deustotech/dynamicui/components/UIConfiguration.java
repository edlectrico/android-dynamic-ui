package es.deusto.deustotech.dynamicui.components;

public class UIConfiguration {

	private int viewColor;
	private int textColor;
	private int height;
	private int width;
	private String text;
	
//	private int volume;
//	private int brightness;

	public UIConfiguration(){
		super();
	}

	public UIConfiguration(int viewColor, int textColor, int height, int width,
			String text) {
		super();
		this.viewColor = viewColor;
		this.textColor = textColor;
		this.height = height;
		this.width = width;
		this.text = text;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
