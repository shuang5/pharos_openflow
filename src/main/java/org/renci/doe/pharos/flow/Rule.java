package org.renci.doe.pharos.flow;

import java.math.BigInteger;
import java.util.Iterator;

import net.sf.json.JSONObject;

public class Rule {
	private JSONObject rule;
	private FlowSpace action;
	private FlowSpace condition;
	public Rule(){
		rule=new JSONObject();
		action=new FlowSpace();
		condition=new FlowSpace();
	}
	public Rule(Rule r){
		rule=r.rule;
		action=r.action;
		condition=r.condition;
	}
	public Rule(String cond, String act) throws PharosException{
		rule=new JSONObject();
		action=new FlowSpace(act);
		condition=new FlowSpace(cond);
		if(!action.hasSameCardinality(condition))
			throw new PharosException("action does not match condition, should have the same cardinality");
		rule.put("condition",cond);
		rule.put("action",act);
	}
	public Rule(JSONObject js) throws PharosException{
		rule=js;
		System.out.println("Rule condition " + js.get("condition").toString());
		System.out.println("Rule action " + js.get("action").toString());
		action=new FlowSpace(js.get("action").toString());
		condition=new FlowSpace(js.get("condition").toString());
	}
	public JSONObject getJSONObject(){
		return rule;
	}
	public void setAction(String act) throws PharosException{
		action=new FlowSpace(act);
		if (condition.getFlowSets().size()!=0 && !action.hasSameCardinality(condition))
			throw new PharosException("action does not match condition, should have the same cardinality");
		rule.put("action",act);
	}
	public void setCondition(String cond)throws PharosException{
		condition=new FlowSpace(cond);
		if (action.getFlowSets().size()!=0 && !action.hasSameCardinality(condition))
			throw new PharosException("action does not match condition, should have the same cardinality");
		rule.put("condition",cond);
	}
	public boolean matchRule(FlowSpace fs){
		System.out.println("Matching Flow space "+ fs.toString() + " against condition "+ condition.toString());
		if(condition.match(fs))return true;
		else return false;
	}
	
	public FlowSpace mapTo(FlowSpace fs) throws PharosException{
		if(!matchRule(fs)) throw new PharosException("input does not match the rule ");
		Iterator<BigInteger> it1=condition.iterator();
		Iterator<FlowUnit> it2=action.fiterator();
		FlowSpace output=new FlowSpace();
		//BigInteger tgt=it3.next();
		while(it1.hasNext()){
			BigInteger cond=it1.next();
			FlowUnit act=it2.next();
			// This is probably the worst way to do it. But ..
			Iterator<BigInteger> it3=fs.iterator();
			while (it3.hasNext()) {
				BigInteger tgt = it3.next();
				if(cond.compareTo(tgt)==0){
					System.out.println("Found a match");
					output.add(act);
					break;
				//if(it3.hasNext())tgt=it3.next();
				//else return output;
				}	
			} 
		}
		return output;
	}
	public String toString(){
		return rule.toString();
	}
	public static void main(String[] args) throws Exception {
		Rule r = new Rule("1-4/10-14^1-2/15-16","11-16/16-22");
		FlowSpace fs=new FlowSpace("2-3/10-14^1/15-16");
		System.out.println(fs);
		FlowSpace fs1=r.mapTo(fs);
		System.out.println("Output Flowspace: "+ fs1);
	}
}
