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
import es.deusto.deustotech.dynamicui.components.WidgetName;
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

		this.appContext	    = appContext;
		this.historyManager = new HistoryManager(this.appContext);
		this.currentUI 	    = currentUI; //TODO: Use this

		if (!isAdaptationHistoryEmpty()){
			if (historyManager.getLastConfiguration().equals(this.currentUI.get(WidgetName.BUTTON))){
				finalConfiguration = historyManager.getLastConfiguration();

				Log.d(UIReasoner.class.getSimpleName(), "No need of executing rules");
			} else {
				BuiltinRegistry.theRegistry.register(new ListContainsValueBuiltin());
				BuiltinRegistry.theRegistry.register(new ListNotContainsValueBuiltin());

				Rule.Parser ruleParser = Rule.rulesParserFromReader(new BufferedReader(
						new InputStreamReader(appContext.getResources().openRawResource(R.raw.action_rules))));
				reasoner = new GenericRuleReasoner(Rule.parseRules(ruleParser));

				executeRules(generateModel());

				finalConfiguration = parseConfiguration();

				Log.d(UIReasoner.class.getSimpleName(), "Rules ended");
			}
		}
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
	private boolean isAdaptationHistoryEmpty() {
		return historyManager.checkConfiguration(this.user, this.currentUI);
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

