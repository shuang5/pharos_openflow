package org.renci.doe.pharos.flow;

import static org.junit.Assert.*;

import org.junit.Test;
import org.renci.doe.pharos.flow.FlowSpace;
import org.renci.doe.pharos.flow.PharosException;
import org.renci.doe.pharos.flow.Rule;
import org.renci.doe.pharos.flow.Rules;

public class RulesTest {

	@Test
	public void testRules() {
		@SuppressWarnings("unused")
		Rules rules = new Rules();
	}

	@Test
	public void testRulesRules() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule();
			
			r.setCondition("1-4/10-14");
			r.setAction("11-14/16-20");
			rules.addRule(r);
			Rules r2 = new Rules(rules);
			System.out.println(r2);
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testRulesString() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule();
			
			r.setCondition("1-4/10-14");
			r.setAction("11-14/16-20");
			rules.addRule(r);
			Rule r2 = new Rule();
			r2.setCondition("10-14/10-14");
			r2.setAction("20-24/16-20");
			rules.addRule(r2);
			Rule r1 = new Rule();
			r1.setCondition("10-16/16-20");
			r1.setAction("1-7/20-24");
			rules.addRuleTable(r1);
			Rule r4 = new Rule();
			r4.setCondition("0-6/21-26");
			r4.setAction("1-7/80-85");
			rules.addRuleTable(r4);
			
			@SuppressWarnings("unused")
			Rules rr=new Rules(rules.toString());
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testAddRuleTableJSONArray() {
		testRulesString();
	}

	@Test
	public void testAddRuleTableRule() {
		try{
			Rules rules = new Rules();
			
			Rule r1 = new Rule();
			r1.setCondition("10-16/16-20");
			r1.setAction("1-7/20-24");
			assertEquals(rules.addRuleTable(r1),1);
			Rule r4 = new Rule();
			r4.setCondition("0-6/21-26");
			r4.setAction("1-7/80-85");
			int i=rules.addRuleTable(r4);
			assertEquals(i,2);
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}

	}



	@Test
	public void testDeleteRuleTableInt() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule();
			
			r.setCondition("1-4/10-14");
			r.setAction("11-14/16-20");
			rules.addRule(r);
			
			Rule r1 = new Rule("10-16/16-20","1-7/20-24");
			rules.addRuleTable(r1);
			Rule r4 = new Rule();
			r4.setCondition("0-6/21-26");
			r4.setAction("1-7/80-85");
			rules.addRuleTable(r4);
			rules.deleteRuleTable(0);
			rules.deleteRuleTable(0);
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testDeleteLastRuleTable() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule();
			
			r.setCondition("1-4/10-14");
			r.setAction("11-14/16-20");
			rules.addRule(r);
			
			Rule r1 = new Rule("10-16/16-20","1-7/20-24");
			rules.addRuleTable(r1);
			Rule r4 = new Rule();
			r4.setCondition("0-6/21-26");
			r4.setAction("1-7/80-85");
			rules.addRuleTable(r4);
			rules.deleteLastRuleTable();
			rules.deleteLastRuleTable();
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testGetRuleTable() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule();
			
			r.setCondition("1-4/10-14");
			r.setAction("11-14/16-20");
			rules.addRule(r);
			
			Rule r1 = new Rule("10-16/16-20","1-7/20-24");
			rules.addRuleTable(r1);
			Rule r4 = new Rule();
			r4.setCondition("0-6/21-26");
			r4.setAction("1-7/80-85");
			rules.addRuleTable(r4);
		
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testAddRuleToRuleTable() {
		try{
			Rules rules = new Rules();
			
			Rule r1 = new Rule();
			r1.setCondition("10-16/16-20");
			r1.setAction("1-7/20-24");
			rules.addRuleTable(r1);
			Rule r4 = new Rule();
			r4.setCondition("0-6/21-26");
			r4.setAction("1-7/80-85");
			rules.addRuleTable(r4);
			
			}
			catch(PharosException ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testAddRule() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule("1-4/10-14","11-14/16-20");	
			rules.addRule(r);
			Rule r2 = new Rule("10-14/10-14","20-24/16-20");
			rules.addRule(r2);
			Rule r1 = new Rule("10-16/16-20","1-7/20-24");
			rules.addRule(r1);
			assertEquals(rules.getRuleTable(0).size(),3);
			Rule r3 = new Rule("20-25/16-20","1-6/21-25");
			rules.addRule(r3);
			assertEquals(rules.getRuleTable(0).size(),4);
			
		
			}
			catch(Exception ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testDeleteRule() {
		try{
			Rules rules = new Rules();
			Rule r = new Rule("1-4/10-14","11-14/16-20");		
			assertEquals(rules.addRule(r).size(),1);
			Rule r2 = new Rule("10-14/10-14","20-24/16-20");
			rules.addRule(r2);
			Rule r1 = new Rule("10-16/16-20","1-7/20-24");
			rules.addRule(r1);
			Rule r3 = new Rule("20-25/16-20","1-6/21-25");
			rules.addRule(r3);
			assertEquals(rules.getRuleTable(0).size(),4);
			rules.deleteRule(0);
			assertEquals(rules.getRuleTable(0).size(),3);
			rules.deleteRule(0);
			assertEquals(rules.getRuleTable(0).size(),2);
			rules.deleteRule(0);
			rules.deleteRule(0);
			assertEquals(rules.getRuleTable(0).size(),0);
			}
			catch(Exception ex){
				fail("excetpion occured:"+ex.getMessage());
			}
	}

	@Test
	public void testEvaluate() {
		try{
		Rules rules = new Rules();
		Rule r = new Rule();
		FlowSpace fs=new FlowSpace("12-13/10-14");
		
		r.setCondition("1-4/10-14");
		r.setAction("11-14/16-20");
		rules.addRule(r);
		Rule r2 = new Rule();
		r2.setCondition("10-14/10-14");
		r2.setAction("20-24/16-20");
		rules.addRule(r2);
		Rule r1 = new Rule();
		r1.setCondition("10-16/16-20");
		r1.setAction("1-7/20-24");
		rules.addRuleTable(r1);
		Rule r3 = new Rule("20-25/16-20","1-6/21-25");
		rules.addRule(r3);
		System.out.println(rules.evaluate(fs));
		Rule r4 = new Rule();
		r4.setCondition("0-6/21-26");
		r4.setAction("1-7/80-85");
		rules.addRuleTable(r4);
		System.out.println(rules.evaluate(fs));
	
		}
		catch(Exception ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

}
