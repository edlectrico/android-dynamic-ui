package es.deusto.deustotech.dynamicui.components;

import es.deusto.deustotech.dynamicui.model.ICapability;

/**
 * Created by edlectrico on 6/25/13.
 *
 * This class will store the final configuration to be
 * executed in the device according to the defined
 * taxonomy.
 *
 */
public class FinalUIConfiguration extends UIConfiguration{

    private ICapability.BRIGHTNESS brightness;
    private int contrast;
    private int textSize;
    private String viewSize;
    private int volume;
    private int input;
    private int output;

    public FinalUIConfiguration() {
    }

    public FinalUIConfiguration(ICapability.BRIGHTNESS brightness, int contrast, int textSize, String viewSize, int volume, int input, int output) {
        this.brightness = brightness;
        this.contrast = contrast;
        this.textSize = textSize;
        this.viewSize = viewSize;
        this.volume = volume;
        this.input = input;
        this.output = output;
    }

    public FinalUIConfiguration(int viewColor, int textColor, int height, int width, String text, ICapability.BRIGHTNESS brightness, int contrast,
                                int textSize, String viewSize, int volume, int input, int output) {
        super(viewColor, textColor, height, width, text);
        this.brightness = brightness;
        this.contrast = contrast;
        this.textSize = textSize;
        this.viewSize = viewSize;
        this.volume = volume;
        this.input = input;
        this.output = output;
    }

    public ICapability.BRIGHTNESS getBrightness() {
        return brightness;
    }

    public void setBrightness(ICapability.BRIGHTNESS brightness) {
        this.brightness = brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public String getViewSize() {
        return viewSize;
    }

    public void setViewSize(String viewSize) {
        this.viewSize = viewSize;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }
}
