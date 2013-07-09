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

//    private String brightness;
//    private int contrast;
//    private String textSize;
//    private String viewSize;
//    private int volume;
//    private int input;
//    private int output;
	
	private ICapability.VIEW_SIZE viewSize;

    public FinalUIConfiguration() {
    }
    
    public FinalUIConfiguration(ICapability.VIEW_SIZE vs){
    	this.viewSize = vs;
    }

//    public FinalUIConfiguration(String brightness, int contrast, String textSize, String viewSize, int volume, int input, int output) {
//        this.brightness = brightness;
//        this.contrast = contrast;
//        this.textSize = textSize;
//        this.viewSize = viewSize;
//        this.volume = volume;
//        this.input = input;
//        this.output = output;
//    }
//
//    public FinalUIConfiguration(String viewColor, String textColor, int height, int width, String text, String brightness, int contrast,
//                                String textSize, String viewSize, int volume, int input, int output) {
//        super(viewColor, textColor, height, width, text);
//        this.brightness = brightness;
//        this.contrast = contrast;
//        this.textSize = textSize;
//        this.viewSize = viewSize;
//        this.volume = volume;
//        this.input = input;
//        this.output = output;
//    }

}
