package es.deusto.deustotech.components;

public class UIConfiguration {

	private int viewColor;
	private int textColor;
	private int height;
	private int width;
	private String text;

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

	public int getTextColor() {
		return textColor;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public String getText() {
		return text;
	}
}
