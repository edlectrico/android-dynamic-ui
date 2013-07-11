package es.deusto.deustotech.utils.jena;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class ListNotContainsValueBuiltin extends BaseBuiltin{
	
	@Override
	public String getName() {
		return "listNotContainsValue";
	}
	
	public boolean bodyCall(Node[] args, int length, RuleContext context) {
		Object element = getArg(0, args, context).getLiteralValue();
		
		for (int i = 1 ; i < length; i++) {
			Node n = getArg(i, args, context);
			
			if (n.getLiteralValue().toString().equals(element.toString())){
				return false;
			}
		}

		return true;
	}
}
