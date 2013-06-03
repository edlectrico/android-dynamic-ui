package es.deusto.deustotech.modules;

import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class AdaptationEngine implements Runnable {

	private static final String[] COMPONENT_BACKGROUND_COLOR = { "blue", "red",
			"yellow", "green", "black", "white" };

	private HashMap<String, View> componentsToAdapt;

	public AdaptationEngine() {
		super();
	}

	public AdaptationEngine(HashMap<String, View> viewsMap, Context context) {
		super();

		this.componentsToAdapt = viewsMap;
	}

	public AdaptationEngine(HashMap<String, View> viewsMap,
			Context applicationContext, UIReasoner uiReasoner) {
		// TODO Auto-generated constructor stub
		
		super();

		this.componentsToAdapt = viewsMap;
	}

	public HashMap<String, View> adapt() {
		//Previously it should check entities status
		componentsToAdapt.get("Button").post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get("Button").setBackgroundColor(Color.parseColor(COMPONENT_BACKGROUND_COLOR[generateRandomValue()]));
				componentsToAdapt.get("Button").setMinimumHeight(generateRandomValue() * 100);
				componentsToAdapt.get("Button").setMinimumWidth(generateRandomValue() * 100);
			}
		});
		
		componentsToAdapt.get("EditText").post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get("EditText").setBackgroundColor(Color.parseColor(COMPONENT_BACKGROUND_COLOR[generateRandomValue()]));
				componentsToAdapt.get("EditText").setMinimumHeight(generateRandomValue() * 100);
				componentsToAdapt.get("EditText").setMinimumWidth(generateRandomValue() * 100);
			}
		});
		
		componentsToAdapt.get("TextView").post(new Runnable() {
			@Override
			public void run() {
				componentsToAdapt.get("TextView").setBackgroundColor(Color.parseColor(COMPONENT_BACKGROUND_COLOR[generateRandomValue()]));
				componentsToAdapt.get("TextView").setMinimumHeight(generateRandomValue() * 100);
				componentsToAdapt.get("TextView").setMinimumWidth(generateRandomValue() * 100);
			}
		});
		
		return componentsToAdapt;
	}
	
	//TODO: Remove the following methods, they're just to test 
	//the automatic adaptation each 1000 milliseconds 
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private int generateRandomValue() {
		Random random = new Random();
		return random.nextInt(6);
	}

	@Override
	public void run() {
		while (true){
			adapt();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
