package es.deusto.deustotech.dynamicui.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

import es.deusto.deustotech.dynamicui.R;
import es.deusto.deustotech.dynamicui.components.UIConfiguration;
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.utils.jena.ListContainsValueBuiltin;
import es.deusto.deustotech.utils.jena.ListNotContainsValueBuiltin;

public class UIReasoner {

	private ICapability user, device, context;
	
	private UIConfiguration finalConfiguration;
	
	private HashMap<String, UIConfiguration> currentUI;
	private HistoryManager historyManager;
	private Context appContext;

    public static final String NS = "http://www.deustotech.es/adaptation.owl#";
    public OntModel ontModel = null;
    public OntClass ontUserClass = null;
    public OntClass ontDeviceClass = null;
    public OntClass ontContextClass = null;
    public OntClass ontFinalConfClass = null;

    public Reasoner reasoner;
    public InfModel infModel;

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
        this.currentUI 	    = currentUI; //TODO: Use this
        this.appContext	    = appContext;
        
        BuiltinRegistry.theRegistry.register(new ListContainsValueBuiltin());
        BuiltinRegistry.theRegistry.register(new ListNotContainsValueBuiltin());
        
        Rule.Parser ruleParser = Rule.rulesParserFromReader(new BufferedReader(new InputStreamReader(appContext.getResources().openRawResource(R.raw.action_rules))));
        reasoner = new GenericRuleReasoner(Rule.parseRules(ruleParser));
        
        executeRules(generateModel());
        
        finalConfiguration = parseConfiguration();
		
		Log.d(UIReasoner.class.getSimpleName(), "Rules ended");
	}

    private Model generateModel() {
        this.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.ontModel.setNsPrefix("adaptation", NS);
        
        this.ontUserClass 	 	= ontModel.createClass(NS + "User");
        this.ontDeviceClass  	= ontModel.createClass(NS + "Device");
        this.ontContextClass 	= ontModel.createClass(NS + "Context");
        this.ontFinalConfClass 	= ontModel.createClass(NS + "FinalUIConfiguration");

        addInstancesWithJena("user", "device", "context", "final_conf");

        this.ontModel.write(System.out);

        return this.ontModel;
    }
    
    /**
     * This method adds the corresponding classes to the model
     * 
     * @param userId
     * @param deviceId
     * @param contextId
     * @param finalUIConf
     */
    private void addInstancesWithJena(final String userId, final String deviceId, final String contextId, final String finalUIConf){
    	addUserInstance(userId);
    	addDeviceInstance(deviceId);
    	addContextInstance(contextId);
    }

    private Individual addUserInstance(String id) {
        Individual individual = this.ontUserClass.createIndividual(NS + id);

        Property viewSize = this.ontModel.getProperty(NS + "VIEW_SIZE");
        Literal literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE));
        individual.setPropertyValue(viewSize, literal);
        
        Property viewColor = this.ontModel.getProperty(NS + "VIEW_COLOR");
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.VIEW_COLOR));
        individual.setPropertyValue(viewColor, literal);
        
        Property textSize = this.ontModel.getProperty(NS + "TEXT_SIZE");
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE));
        individual.setPropertyValue(textSize, literal);
        
        Property textColor = this.ontModel.getProperty(NS + "TEXT_COLOR");
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.TEXT_COLOR));
        individual.setPropertyValue(textColor, literal);

        Property input = this.ontModel.getProperty(NS + "INPUT");
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.INPUT));
        individual.setPropertyValue(input, literal);

        Property brightness = this.ontModel.getProperty(NS + "BRIGHTNESS");
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS));
        individual.setPropertyValue(brightness, literal);

        return individual;
    }
    
    private Individual addDeviceInstance(String id) {
        Individual individual = this.ontDeviceClass.createIndividual(NS + id);

        Property viewSize = this.ontModel.getProperty(NS + "VIEW_SIZE");
        Literal literal = this.ontModel.createTypedLiteral(this.device.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE));
        individual.setPropertyValue(viewSize, literal);

        Property input = this.ontModel.getProperty(NS + "INPUT");
        literal = this.ontModel.createTypedLiteral(this.device.getCapabilityValue(ICapability.CAPABILITY.INPUT));
        individual.setPropertyValue(input, literal);

        Property brightness = this.ontModel.getProperty(NS + "BRIGHTNESS");
        literal = this.ontModel.createTypedLiteral(this.device.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS));
        individual.setPropertyValue(brightness, literal);
        
        Property orientation = this.ontModel.getProperty(NS + "ORIENTATION");
        literal = this.ontModel.createTypedLiteral(this.device.getCapabilityValue(ICapability.CAPABILITY.ORIENTATION));
        individual.setPropertyValue(orientation, literal);
        
        Property acceleration = this.ontModel.getProperty(NS + "ACCELERATION");
        literal = this.ontModel.createTypedLiteral(this.device.getCapabilityValue(ICapability.CAPABILITY.ACCELERATION));
        individual.setPropertyValue(acceleration, literal);

        return individual;
    }
    
    private Individual addContextInstance(String id) {
        Individual individual = this.ontContextClass.createIndividual(NS + id);

        Property temperature = this.ontModel.getProperty(NS + "TEMPERATURE");
        Literal literal = this.ontModel.createTypedLiteral(this.context.getCapabilityValue(ICapability.CAPABILITY.TEMPERATURE));
        individual.setPropertyValue(temperature, literal);

        Property brightness = this.ontModel.getProperty(NS + "BRIGHTNESS");
        literal = this.ontModel.createTypedLiteral(this.context.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS));
        individual.setPropertyValue(brightness, literal);

        return individual;
    }
    
    private void executeRules(Model dataModel) {
        infModel = ModelFactory.createInfModel(reasoner, dataModel);
        infModel.prepare();

//        for (Statement st : infModel.listStatements().toList()){
//            Log.d("InfModel", st.toString());
//        }
    }
    
    /**
     * This method extracts the FinalUIConfiguration from the model
     * to create the corresponding Java Object
     * 
     * @return The Java Object corresponding to the same FinalUIConfiguration semantic model
     */
    private UIConfiguration parseConfiguration(){
    	final Resource resource = infModel.getResource("http://www.deustotech.es/adaptation.owl#FinalUIConfigurationInstance");
    	
    	final Statement viewSizeStmt 	= resource.getProperty(this.ontModel.getProperty(NS + "VIEW_SIZE"));
    	final Statement viewColorStmt 	= resource.getProperty(this.ontModel.getProperty(NS + "VIEW_COLOR"));
    	final Statement textSizeStmt 	= resource.getProperty(this.ontModel.getProperty(NS + "TEXT_SIZE"));
    	final Statement textColorStmt 	= resource.getProperty(this.ontModel.getProperty(NS + "TEXT_COLOR"));
    	final Statement brightnessStmt 	= resource.getProperty(this.ontModel.getProperty(NS + "BRIGHTNESS"));
    	
    	if (viewSizeStmt != null){
    		final ICapability.VIEW_SIZE viewSize 	= ICapability.VIEW_SIZE.valueOf(viewSizeStmt.getObject().toString());
    		final ICapability.TEXT_SIZE textSize 	= ICapability.TEXT_SIZE.valueOf(textSizeStmt.getObject().toString());
    		final ICapability.BRIGHTNESS brightness = ICapability.BRIGHTNESS.valueOf(brightnessStmt.getObject().toString());
    		final int viewColor = Color.parseColor(viewColorStmt.getObject().toString());
    		final int textColor = Color.parseColor(textColorStmt.getObject().toString());
    		
    		return new UIConfiguration(viewSize, textSize, brightness, viewColor, textColor);
    	}
    	
    	return new UIConfiguration();
    }
    
    public UIConfiguration getAdaptedConfiguration(){
    	return this.finalConfiguration;
    }

	/**
	 * This method takes the updated user, the current device's capabilities
	 * and the current configuration and returns the best suitable UI
	 * for this situation.
	 * 
	 * @return a new UI configuration (FinalUIConfiguration) to be displayed in the device
	 */
//	public FinalUIConfiguration getAdaptedConfiguration() {
//		//TODO: 
//		//1. Check if there is a previous configuration for this situation
//		if (checkAdaptationHistory()){
//			return historyManager.getAdaptedConfiguration();
//		} else {
//			//2. Else, generate a new one
//			//2.1 First, from a standard one
//			StandardUIManager standardUIManager 	= new StandardUIManager();
//			FinalUIConfiguration standardConfiguration 	= standardUIManager.getStandardConfiguration(user);
//			
//			if (StandardUIManager.isSufficient(standardConfiguration)){
//				return standardConfiguration;
//			} else {
//				//2.2 If it is not sufficient, a new one
//				//TODO: How do we determine if an adaptation is sufficient enough?
//				return adaptConfiguration(this.user.getAllCapabilities(), this.device.getAllCapabilities(), this.context.getAllCapabilities());
//			}
//		}
//	}
//
//	private boolean checkAdaptationHistory() {
//		return historyManager.checkConfiguration(this.user, this.currentUI);
//	}
//
//	private FinalUIConfiguration adaptConfiguration(
//			HashMap<CAPABILITY, Object> userCapabilities,
//			HashMap<CAPABILITY, Object> deviceCapabilities,
//            HashMap<CAPABILITY, Object> contextCapabilities) {
//		
//		//TODO: This is a mock configuration. The logic of this method
//		//should return the corresponding UIConfiguration object so
//		//the AdaptationModule could adapt the UI to its characteristics
//		
//		/**
//		 * 1. Get user capabilities
//		 * 2. Get current UI configuration
//		 * 3. If it is not enough, generate a new configuration taking into account the
//         * defined taxonomy of how each component from context, user and device affects
//         * to the final UI result component
//		 */
//		
//		FinalUIConfiguration finalUIConfiguration = new FinalUIConfiguration();
//
//
//        //TODO: This class should obtain the corresponding output
//        //via certain rules in order to obtain the corresponding
//        //component adaptation
//
//        /**
//        * VIEW_SIZE
//        *
//        * Affected by:
//        * -Context:     luminosity, temperature
//        * -User;        output, view_size, brightness
//        * -Device:      brightness, output, acceleration, view_size, orientation
//        *
//        * Priorities order:
//        * -user_output, device_output, user_view_size, device_view_size, context_luminosity, device_brightness.
//        * context_temperature, device_orientation, device_acceleration
//        *
//        * */
//
//        if (userCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.DEFAULT)){ //User can read text and hear audio
//            if (deviceCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.DEFAULT)){ //device can use text and audio outputs
//                if (userCapabilities.get(CAPABILITY.VIEW_SIZE).equals(deviceCapabilities.get(CAPABILITY.VIEW_SIZE))){ //the ui is updated to user preference
//                    if (brightnessComparison((ICapability.BRIGHTNESS) deviceCapabilities.get(CAPABILITY.BRIGHTNESS), (ICapability.ILLUMINANCE) contextCapabilities.get(CAPABILITY.ILLUMINANCE)) == 1){
//                        //TODO: increase brightness
//                    } else if (brightnessComparison((ICapability.BRIGHTNESS)deviceCapabilities.get(CAPABILITY.BRIGHTNESS), (ICapability.ILLUMINANCE)contextCapabilities.get(CAPABILITY.ILLUMINANCE)) == -1){
//                        //TODO: decrease brightness
//                    }
//                } else if (viewSizeComparison((ICapability.VIEW_SIZE)userCapabilities.get(CAPABILITY.VIEW_SIZE), (ICapability.VIEW_SIZE)deviceCapabilities.get(CAPABILITY.VIEW_SIZE)) == 1){ //the ui is NOT updated to user preference
//                    //TODO: increase view size
//                } else if (viewSizeComparison((ICapability.VIEW_SIZE)userCapabilities.get(CAPABILITY.VIEW_SIZE), (ICapability.VIEW_SIZE)deviceCapabilities.get(CAPABILITY.VIEW_SIZE)) == -1){
//                    //TODO: decrease view size
//                }
//            }
//        } else if (userCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.ONLY_TEXT)){
//
//        } else if (userCapabilities.get(CAPABILITY.OUTPUT).equals(ICapability.OUTPUT.ONLY_AUDIO)){
//
//        }
//
//
//
//
//		if (userCapabilities.get(CAPABILITY.VIEW_SIZE).equals(ICapability.VIEW_SIZE.BIG)){
////			if (currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
//				finalUIConfiguration.setHeight(500);
////			} else finalUIConfiguration.setHeight(200);
//			
////			if (currentUI.get(WidgetName.BUTTON).getWidth() == -2){ //wrap_content
//				finalUIConfiguration.setWidth(500);
////			} else finalUIConfiguration.setWidth(200);
//			
//		} else {
//			finalUIConfiguration.setHeight(100);
//			finalUIConfiguration.setWidth(100);
//		}
//		
//		/**
//        * TEXT_SIZE
//        *
//        * Affected by:
//        * -Context:     luminosity
//        * -User;        output, text_size
//        * -Device:      acceleration, text_size
//        *
//        * */
//
// //		if (userCapabilities.get(CAPABILITY.USER_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)){
////			if ((!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.BIG)) && 
////			(!currentUI.get(CAPABILITY.DEVICE_TEXT_SIZE).equals(ICapability.TEXT_SIZE.VERY_BIG))) {
////				//TODO: BIG
////			}
////		}
//		
//
//        /**
//        * BRIGHTNESS
//        *
//        * Affected by:
//        * -Context:     luminosity
//        * -User;        output, brightness
//        * -Device:      brightness, battery
//        *
//        * */
//
//        //http://stackoverflow.com/questions/7704961/screen-brightness-value-in-android
//        //http://stackoverflow.com/questions/3737579/changing-screen-brightness-programmatically-in-android
//
//
//		/*
//		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
//			if (!currentUI.get(CAPABILITY.DEVICE_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
//				//TODO: Higher brightness value
//			}
//		}
//		*/
//
//        finalUIConfiguration.setBrightness(ICapability.BRIGHTNESS.VERY_HIGH);
//
//
//        //TODO: Can it be changed?
//        /**
//        * CONTRAST
//        *
//        * Affected by:
//        * -Context:     luminosity
//        * -User;        output, contrast
//        * -Device:      contrast
//        *
//        * */
//
//        /**
//        * VIEW_COLOR
//        *
//        * Affected by:
//        * -Context:     luminosity
//        * -User;        output, input? text_color, view_color
//        * -Device:      brightness, text_color, view_color, output? input?
//         *
//         * rule_1.1: if user_input && user_output is HAPTIC (views available)
//         * rule_1.2: if device_input && device_output is HAPTIC
//         * //the last adaptation value will be made by context
//         * rule_1.3: if user_view_color != device_text_color (if not different, can't see the text)
//         * rule_1.4: if context_luminosity < value_x
//         *           if context_luminosity > value_y
//         *
//         * RULE_1_RESULT
//        *
//        * */
//
//        finalUIConfiguration.setViewColor(Color.GREEN);
//
//        /**
//        * TEXT_COLOR (almost same VIEW_COLOR rules)
//        *
//        * Affected by:
//        * -Context:     luminosity
//        * -User;        output, text_color, view_color
//        * -Device:      brightness, text_color, view_color,
//        *
//         * Text is not just for HAPTIC interfaces, non-haptic
//         * ones also use text.
//         * Text color must be always different from the "button"
//         * or the control one.
//         *
//        * */
//
//        finalUIConfiguration.setTextColor(Color.BLUE);
//
//
//        /**
//        * VOLUME
//        *
//        * Affected by:
//        * -Context:     noise
//        * -User;        output, volume
//        * -Device:      output, battery, volume
//        *
//         * rule_3.1: if device_output is VOLUME
//         * rule_3.2: if context_noise > device_volume
//         * rule_3.3: if user_volume < context_noise
//         * rule_3.4: if device_battery is OK
//         *
//         * RULE_3_RESULT
//        * */
//
//        //http://stackoverflow.com/questions/2539264/volume-control-in-android-application
//
//
//        /*
//		if (userCapabilities.get(CAPABILITY.USER_BRIGHTNESS).equals(ICapability.BRIGHTNESS.VERY_HIGH)){
//			if (this.currentUI.get(WidgetName.BUTTON).getHeight() == -2){ //wrap_content
//				//TODO: Bigger
//				return new UIConfiguration(Color.RED, Color.GREEN, 500, 700, "VERY BIG");
//			}
//		}
//
//		return new UIConfiguration(Color.RED, Color.GREEN, 500, 500, "TEST");
//		*/
//
////  		finalUIConfiguration.setTextColor(Color.GREEN);
////		finalUIConfiguration.setViewColor(Color.WHITE );
//		finalUIConfiguration.setText("TESTING");
//		
//		return finalUIConfiguration;
//	}


    /**
     * This method compares current device brightness status with the context brightness levels
     *
     * @param deviceBrightness
     * @param contextBrightness
     * @return a number indicating if
     *  -1: device brightness level is higher than the context one,
     *   0: if both are the same (adaptation is OK) or
     *   1: if context brightness is higher than the device configuration screen brightness
     */
    private int brightnessComparison(ICapability.BRIGHTNESS deviceBrightness, ICapability.BRIGHTNESS contextBrightness){
        if (deviceBrightness.equals(ICapability.BRIGHTNESS.LOW) || deviceBrightness.equals(ICapability.BRIGHTNESS.DEFAULT)
        || deviceBrightness.equals(ICapability.BRIGHTNESS.HIGH)){
            if (contextBrightness.equals(ICapability.BRIGHTNESS.VERY_HIGH)){
                return 1; //context value higher than device current brightness level
            } else return -1;
        } else if (((deviceBrightness.equals(ICapability.BRIGHTNESS.VERY_HIGH)) && contextBrightness.equals(ICapability.BRIGHTNESS.VERY_HIGH)) ||
                (((deviceBrightness.equals(ICapability.BRIGHTNESS.DEFAULT)) || deviceBrightness.equals(ICapability.BRIGHTNESS.LOW)
                        && contextBrightness.equals(ICapability.BRIGHTNESS.LOW)))){
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

