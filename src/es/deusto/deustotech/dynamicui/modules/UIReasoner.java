package es.deusto.deustotech.dynamicui.modules;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

import es.deusto.deustotech.dynamicui.components.FinalUIConfiguration;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.ICapability.CAPABILITY;

public class UIReasoner {

	private ICapability user, device, context;
	private HashMap<String, UIConfiguration> currentUI;
	private HistoryManager historyManager;
	private Context appContext;

    public static final String NS = "http://www.deustotech.es/prueba.owl#";
    public OntModel ontModel = null;
    public OntClass ontClass = null;

    Reasoner reasoner;
    InfModel infModel;

//    private static final float MAX_BRIGHTNESS = 1.0F;

	public UIReasoner(){
		super();
	}

	public UIReasoner(ICapability user, ICapability device, ICapability context,
			HashMap<String, UIConfiguration> currentUI, Context appContext) {
		super();
		
        this.user 		= user;
        this.device 	= device;
		this.context    = context;

		this.historyManager = new HistoryManager(this.appContext);
        this.currentUI 	    = currentUI;
        this.appContext	    = appContext;

        generateModel();

        reasoner = new GenericRuleReasoner(Rule.parseRules(loadRules()));
	}

    private Model generateModel() {
        this.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.ontModel.setNsPrefix("prueba", NS);

        this.ontClass = ontModel.createClass(NS +"User");

        addInstanceWithJena("edu");

        this.ontModel.write(System.out);

        return this.ontModel;
    }

    public Individual addInstanceWithJena(String id) {
        Individual individual = this.ontClass.createIndividual(NS + id);

        Property viewSize = this.ontModel.getProperty(NS + "VIEW_SIZE");
        Literal literal = this.ontModel.createTypedLiteral("DEFAULT");
        individual.setPropertyValue(viewSize, literal);

        Property output = this.ontModel.getProperty(NS + "OUTPUT");
        literal = this.ontModel.createTypedLiteral("DEFAULT");
        individual.setPropertyValue(output, literal);

        Property brightness = this.ontModel.getProperty(NS + "BRIGHTNESS");
        literal = this.ontModel.createTypedLiteral("DEFAULT");
        individual.setPropertyValue(brightness, literal);

        return individual;
    }

    private String loadRules() {
        String rules = "";

        String adaptViewSize =  "[adaptViewSize: " +
//                "print(\"0\") " +
                "(?u http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://www.deustotech.es/prueba.owl#User) " +
                "(?u http://www.deustotech.es/prueba.owl#view_size ?vs) " +
                "equal(?vs, \"DEFAULT\") " +

                " -> " +

                "(?u http://edu.ontology.es#fakeproperty \"RULE_EXECUTED\")] ";

        rules = adaptViewSize + "";

        return rules;
    }

    public void executeRules(Model dataModel) {
        infModel = ModelFactory.createInfModel(reasoner, dataModel);
        infModel.prepare();

        for (Statement st : infModel.listStatements().toList())
        {
            Log.d("InfModel", st.toString());
        }
    }

	/**
	 * This method takes the updated user, the current device's capabilities
	 * and the current configuration and returns the best suitable UI
	 * for this situation.
	 * 
	 * @return a new UI configuration (FinalUIConfiguration) to be displayed in the device
	 */
	public FinalUIConfiguration getAdaptedConfiguration() {
		//TODO: 
		//1. Check if there is a previous configuration for this situation
		if (checkAdaptationHistory()){
			return historyManager.getAdaptedConfiguration();
		} else {
			//2. Else, generate a new one
			//2.1 First, from a standard one
			StandardUIManager standardUIManager 	= new StandardUIManager();
			FinalUIConfiguration standardConfiguration 	= standardUIManager.getStandardConfiguration(user);
			
			if (StandardUIManager.isSufficient(standardConfiguration)){
				return standardConfiguration;
			} else {
				//2.2 If it is not sufficient, a new one
				//TODO: How do we determine if an adaptation is sufficient enough?
				return adaptConfiguration(this.user.getAllCapabilities(), this.device.getAllCapabilities(), this.context.getAllCapabilities());
			}
		}
	}

	private boolean checkAdaptationHistory() {
		return historyManager.checkConfiguration(this.user, this.currentUI);
	}

	private FinalUIConfiguration adaptConfiguration(
			HashMap<CAPABILITY, Object> userCapabilities,
			HashMap<CAPABILITY, Object> deviceCapabilities,
            HashMap<CAPABILITY, Object> contextCapabilities) {
		
		//TODO: This is a mock configuration. The logic of this method
		//should return the corresponding UIConfiguration object so
		//the AdaptationModule could adapt the UI to its characteristics
		
		/**
		 * 1. Get user capabilities
		 * 2. Get current UI configuration
		 * 3. If it is not enough, generate a new configuration taking into account the
         * defined taxonomy of how each component from context, user and device affects
         * to the final UI result component
		 */
		
		FinalUIConfiguration finalUIConfiguration = new FinalUIConfiguration();


        //TODO: This class should obtain the corresponding output
        //via certain rules in order to obtain the corresponding
        //component adaptation

        /**
        * VIEW_SIZE
        *
        * Affected by:
        * -Context:     luminosity, temperature
        * -User;        output, view_size, brightness
        * -Device:      brightness, output, acceleration, view_size, orientation
        *
        * Priorities order:
        * -user_output, device_output, user_view_size, device_view_size, context_luminosity, device_brightness.
        * context_temperature, device_orientation, device_acceleration
        *
        * */

        if (userCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.DEFAULT)){ //User can read text and hear audio
            if (deviceCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.DEFAULT)){ //device can use text and audio outputs
                if (userCapabilities.get(CAPABILITY.VIEW_SIZE).equals(deviceCapabilities.get(CAPABILITY.VIEW_SIZE))){ //the ui is updated to user preference
                    if (brightnessComparison((ICapability.BRIGHTNESS) deviceCapabilities.get(CAPABILITY.BRIGHTNESS), (ICapability.ILLUMINANCE) contextCapabilities.get(CAPABILITY.ILLUMINANCE)) == 1){
                        //TODO: increase brightness
                    } else if (brightnessComparison((ICapability.BRIGHTNESS)deviceCapabilities.get(CAPABILITY.BRIGHTNESS), (ICapability.ILLUMINANCE)contextCapabilities.get(CAPABILITY.ILLUMINANCE)) == -1){
                        //TODO: decrease brightness
                    }
                } else if (viewSizeComparison((ICapability.VIEW_SIZE)userCapabilities.get(CAPABILITY.VIEW_SIZE), (ICapability.VIEW_SIZE)deviceCapabilities.get(CAPABILITY.VIEW_SIZE)) == 1){ //the ui is NOT updated to user preference
                    //TODO: increase view size
                } else if (viewSizeComparison((ICapability.VIEW_SIZE)userCapabilities.get(CAPABILITY.VIEW_SIZE), (ICapability.VIEW_SIZE)deviceCapabilities.get(CAPABILITY.VIEW_SIZE)) == -1){
                    //TODO: decrease view size
                }
            }
        } else if (userCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.ONLY_TEXT)){

        } else if (userCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.ONLY_AUDIO)){

        }




		if (userCapabilities.get(CAPABILITY.VIEW_SIZE).equals(ICapability.VIEW_SIZE.BIG)){
//			if (currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
				finalUIConfiguration.setHeight(500);
//			} else finalUIConfiguration.setHeight(200);
			
//			if (currentUI.get(WidgetName.BUTTON).getWidth() == -2){ //wrap_content
				finalUIConfiguration.setWidth(500);
//			} else finalUIConfiguration.setWidth(200);
			
		} else {
			finalUIConfiguration.setHeight(100);
			finalUIConfiguration.setWidth(100);
		}
		
		/**
        * TEXT_SIZE
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, text_size
        * -Device:      acceleration, text_size
        *
        * */

 //		if (userCapabilities.get(CAPABILITY.USER_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)){
//			if ((!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)) && 
//			(!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.VERY_BIG))) {
//				//TODO: BIG
//			}
//		}
		

        /**
        * BRIGHTNESS
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, brightness
        * -Device:      brightness, battery
        *
        * */

        //http://stackoverflow.com/questions/7704961/screen-brightness-value-in-android
        //http://stackoverflow.com/questions/3737579/changing-screen-brightness-programmatically-in-android


		/*
		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
			if (!currentUI.get(CAPABILITY.DEVICE_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
				//TODO: Higher brightness value
			}
		}
		*/

        finalUIConfiguration.setBrightness(ICapability.BRIGHTNESS.VERY_HIGH);


        //TODO: Can it be changed?
        /**
        * CONTRAST
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, contrast
        * -Device:      contrast
        *
        * */

        /**
        * VIEW_COLOR
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, input? text_color, view_color
        * -Device:      brightness, text_color, view_color, output? input?
         *
         * rule_1.1: if user_input && user_output is HAPTIC (views available)
         * rule_1.2: if device_input && device_output is HAPTIC
         * //the last adaptation value will be made by context
         * rule_1.3: if user_view_color != device_text_color (if not different, can't see the text)
         * rule_1.4: if context_luminosity < value_x
         *           if context_luminosity > value_y
         *
         * RULE_1_RESULT
        *
        * */

        finalUIConfiguration.setViewColor(Color.GREEN);

        /**
        * TEXT_COLOR (almost same VIEW_COLOR rules)
        *
        * Affected by:
        * -Context:     luminosity
        * -User;        output, text_color, view_color
        * -Device:      brightness, text_color, view_color,
        *
         * Text is not just for HAPTIC interfaces, non-haptic
         * ones also use text.
         * Text color must be always different from the "button"
         * or the control one.
         *
        * */

        finalUIConfiguration.setTextColor(Color.BLUE);


        /**
        * VOLUME
        *
        * Affected by:
        * -Context:     noise
        * -User;        output, volume
        * -Device:      output, battery, volume
        *
         * rule_3.1: if device_output is VOLUME
         * rule_3.2: if context_noise > device_volume
         * rule_3.3: if user_volume < context_noise
         * rule_3.4: if device_battery is OK
         *
         * RULE_3_RESULT
        * */

        //http://stackoverflow.com/questions/2539264/volume-control-in-android-application


        /*
		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
			if (this.currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
				//TODO: Bigger
				return new UIConfiguration(Color.RED, Color.GREEN, 500, 700, "VERY BIG");
			}
		}

		return new UIConfiguration(Color.RED, Color.GREEN, 500, 500, "TEST");
		*/

//  		finalUIConfiguration.setTextColor(Color.GREEN);
//		finalUIConfiguration.setViewColor(Color.WHITE );
		finalUIConfiguration.setText("TESTING");
		
		return finalUIConfiguration;
	}


    /**
     * This method compares current device brightness status with the context brightness levels
     *
     * @param deviceBrightness
     * @param contextBrightness
     * @return a number indicting if
     *  -1: device brightness level is higher than the context one,
     *   0: if both are the same (adaptation is ok) or
     *   1: if context brightness is higher than the device configuration screen brightness
     */
    private int brightnessComparison(ICapability.BRIGHTNESS deviceBrightness, ICapability.ILLUMINANCE contextBrightness){
        if (deviceBrightness.equals(ICapability.BRIGHTNESS.LOW) || deviceBrightness.equals(ICapability.BRIGHTNESS.DEFAULT)
        || deviceBrightness.equals(ICapability.BRIGHTNESS.HIGH)){
            if (contextBrightness.equals(ICapability.ILLUMINANCE.SUNLIGHT)){
                return 1; //context value higher than device current brightness level
            } else return -1;
        } else if (((deviceBrightness.equals(ICapability.BRIGHTNESS.VERY_HIGH)) && contextBrightness.equals(ICapability.ILLUMINANCE.SUNLIGHT)) ||
                (((deviceBrightness.equals(ICapability.BRIGHTNESS.DEFAULT)) || deviceBrightness.equals(ICapability.BRIGHTNESS.LOW)
                        && contextBrightness.equals(ICapability.ILLUMINANCE.MOONLESS_OVERCAST_NIGHT)))){
                return 0;
            } else return -1;
        }

    /**
     *This method compares current device view size status with the user's
     * @param userViewSize
     * @param deviceViewSize
     * @return a number indicting if
     *  -1: user view size is higher than the device's,
     *   0: if both are the same (adaptation is ok) or
     *   1: if device view size is higher than the user's
     */
    private int viewSizeComparison(ICapability.VIEW_SIZE userViewSize, ICapability.VIEW_SIZE deviceViewSize){
        if (userViewSize.equals(deviceViewSize)){
            return 0;
        } else if (userViewSize.compareTo(deviceViewSize) == -1){
            return -1;
        } else return 1;
        //TODO: compare enum cardinal order, if User > Device -> return -1; else return 1;
    }


    public ICapability getUser() {
		return user;
	}

	public ICapability getDevice() {
		return device;
	}

	public HashMap<String, UIConfiguration> getUiConfiguration() {
		return currentUI;
	}
}

