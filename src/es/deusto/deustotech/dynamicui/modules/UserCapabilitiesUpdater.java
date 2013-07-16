package es.deusto.deustotech.dynamicui.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;

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
import es.deusto.deustotech.dynamicui.model.ICapability;
import es.deusto.deustotech.dynamicui.model.user.UserCapabilities;
import es.deusto.deustotech.utils.jena.ListContainsValueBuiltin;
import es.deusto.deustotech.utils.jena.ListNotContainsValueBuiltin;

public class UserCapabilitiesUpdater {

	/**
	 * This module will be the responsible for updating the user capabilities
	 * taking into account the current context parameters. Therefore, it should
	 * modify current user capabilities taking into account the new current
	 * context situation.
	 */

	public static final String NS = "http://www.deustotech.es/adaptation.owl#";
    public OntModel ontModel = null;
    public OntClass ontUserClass = null;
    public OntClass ontContextClass = null;
    public OntClass ontAdaptedUserClass = null;

    public Reasoner reasoner;
    public InfModel infModel;
    
    public ICapability user;
    public ICapability context;
	
	public UserCapabilitiesUpdater(ICapability user, ICapability context, Context appContext) {
		super();
		
		this.user = user;
		this.context = context;
		
		BuiltinRegistry.theRegistry.register(new ListContainsValueBuiltin());
		BuiltinRegistry.theRegistry.register(new ListNotContainsValueBuiltin());
		
		Rule.Parser ruleParser = Rule.rulesParserFromReader(new BufferedReader(
				new InputStreamReader(appContext.getResources().openRawResource(R.raw.user_rules))));
		reasoner = new GenericRuleReasoner(Rule.parseRules(ruleParser));
		
		executeRules(generateModel());
	}

	private Model generateModel() {
        this.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.ontModel.setNsPrefix("adaptation", NS);
        
        this.ontUserClass 	 	= ontModel.createClass(NS + "User");
        this.ontContextClass 	= ontModel.createClass(NS + "Context");
        this.ontAdaptedUserClass 	= ontModel.createClass(NS + "UpdatedUser");

        addInstancesWithJena("user", "context", "updated_user");

        this.ontModel.write(System.out);

        return this.ontModel;
    }
	
	private void addInstancesWithJena(final String userId, final String contextId, final String adaptedUser){
    	addUserInstance(userId);
    	addContextInstance(contextId);
    }
	
	private Individual addUserInstance(String id) {
        Individual individual = this.ontUserClass.createIndividual(NS + id);

        Property viewSize = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.VIEW_SIZE));
        Literal literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.VIEW_SIZE));
        individual.setPropertyValue(viewSize, literal);
        
        Property viewColor = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.VIEW_COLOR));
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.VIEW_COLOR));
        individual.setPropertyValue(viewColor, literal);
        
        Property textSize = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.TEXT_SIZE));
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.TEXT_SIZE));
        individual.setPropertyValue(textSize, literal);
        
        Property textColor = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.TEXT_COLOR));
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.TEXT_COLOR));
        individual.setPropertyValue(textColor, literal);

        Property input = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.INPUT));
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.INPUT));
        individual.setPropertyValue(input, literal);

        Property brightness = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.BRIGHTNESS));
        literal = this.ontModel.createTypedLiteral(this.user.getCapabilityValue(ICapability.CAPABILITY.BRIGHTNESS));
        individual.setPropertyValue(brightness, literal);

        return individual;
    }
	
	private Individual addContextInstance(String id) {
        Individual individual = this.ontContextClass.createIndividual(NS + id);

        Property temperature = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.TEMPERATURE));
        Literal literal = this.ontModel.createTypedLiteral(this.context.getCapabilityValue(ICapability.CAPABILITY.TEMPERATURE));
        individual.setPropertyValue(temperature, literal);

        Property brightness = this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.BRIGHTNESS));
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
    
    private ICapability parseUser(){
    	final Resource resource = infModel.getResource("http://www.deustotech.es/adaptation.owl#UpdatedUserInstance");
    	
    	final Statement viewSizeStmt 	= resource.getProperty(this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.VIEW_SIZE)));
//    	final Statement viewColorStmt 	= resource.getProperty(this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.VIEW_COLOR)));
    	final Statement textSizeStmt 	= resource.getProperty(this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.TEXT_SIZE)));
//    	final Statement textColorStmt 	= resource.getProperty(this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.TEXT_COLOR)));
    	final Statement brightnessStmt 	= resource.getProperty(this.ontModel.getProperty(NS + String.valueOf(ICapability.CAPABILITY.BRIGHTNESS)));
    	
    	if (viewSizeStmt != null){
    		final ICapability.VIEW_SIZE viewSize 	= ICapability.VIEW_SIZE.valueOf(viewSizeStmt.getObject().toString());
    		ICapability.TEXT_SIZE textSize; 	
    		//TODO: Fix this
    		if (textSizeStmt != null){
    			textSize = ICapability.TEXT_SIZE.valueOf(textSizeStmt.getObject().toString());
    		} else {
    			textSize = ICapability.TEXT_SIZE.valueOf("DEFAULT");
    		}
    		final ICapability.BRIGHTNESS brightness = ICapability.BRIGHTNESS.valueOf(brightnessStmt.getObject().toString());
//    		final int viewColor = Color.parseColor(viewColorStmt.getObject().toString());
//    		final int textColor = Color.parseColor(textColorStmt.getObject().toString());
    		
    		return new UserCapabilities(brightness, null, viewSize, textSize);
    	}
    	
    	return null;
    }

	public ICapability update() {
		return parseUser();
	}

}
