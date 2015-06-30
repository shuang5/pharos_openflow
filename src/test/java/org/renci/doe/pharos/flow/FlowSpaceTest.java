package org.renci.doe.pharos.flow;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Iterator;

import org.junit.Test;
import org.renci.doe.pharos.flow.FlowSpace;
import org.renci.doe.pharos.flow.FlowUnit;
import org.renci.doe.pharos.flow.PharosException;

public class FlowSpaceTest {
	
	@SuppressWarnings("unused")
	@Test
	public void testFlowSpace() {
		FlowSpace fs3= new FlowSpace();
	}

	@Test
	public void testFlowSpaceString() {
		try {
			@SuppressWarnings("unused")
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4^2-4/5-7^10-15/15-20");
		}
		catch (PharosException ex){
			fail("constructor failed");
		}
	}

	@Test
	public void testAdd() {
		try {
			String s="1-10/16-20";
			FlowUnit fu= new FlowUnit(s);
			FlowSpace fs=new FlowSpace("10-14/0-7");
			FlowUnit fu1= new FlowUnit("5-8/8-12");
			fs.add(fu);
			assertEquals(fs.getFlowSets().size(),2);
			fs.add(fu1);
			assertEquals(fs.getFlowSets().size(),3);
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

	@Test
	public void testRemove() {
		try {
			String s="1-10/16-20";
			FlowUnit fu= new FlowUnit(s);
			FlowSpace fs=new FlowSpace("10-14/0-7");
			FlowUnit fu1= new FlowUnit("5-8/8-12");
			fs.add(fu);
			//System.out.println("fs="+fs);
			fs.add(fu1);
			//System.out.println("fs2="+fs);
			fs.remove(fu);
			//System.out.println("fs3="+fs);
			//System.out.println("size()="+fs.getFlowSets().size());
			assertEquals(fs.getFlowSets().size(),2);
			fs.remove(new FlowUnit("11-23/1-8"));
			//System.out.println("size()="+fs.getFlowSets().size());
			assertEquals(fs.getFlowSets().size(),2);
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

	@Test
	public void testIterator() {
		try {
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4^2-4/5-7^10-15/15-20");
			System.out.println(fs3.toString2());
			FlowSpace fs= new FlowSpace("1-3/8-15^48/16-23^100-200/32-47");
			System.out.println(fs);
			FlowSpace fs1= new FlowSpace("1-2/8-15^48/16-23^110-180/32-47");
			if (fs.match(fs1)) System.out.println(fs1+" matches");
			FlowSpace fs2= new FlowSpace("1-3/8-15^48/16-21^100-200/32-47");
			if (fs.match(fs2)) System.out.println(fs2+" matches");
			
			Iterator<BigInteger> it=fs3.iterator();
			while(it.hasNext()){
				BigInteger a=it.next();
				System.out.println(a);
			}
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	
	}

	@Test
	public void testFiterator() {
		try {
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4^2-4/5-7^10-15/15-20");
			System.out.println(fs3.toString2());
			FlowSpace fs= new FlowSpace("1-3/8-15^48/16-23^100-200/32-47");
			System.out.println(fs);
			FlowSpace fs1= new FlowSpace("1-2/8-15^48/16-23^110-180/32-47");
			if (fs.match(fs1)) System.out.println(fs1+" matches");
			FlowSpace fs2= new FlowSpace("1-3/8-15^48/16-21^100-200/32-47");
			if (fs.match(fs2)) System.out.println(fs2+" matches");
			
			Iterator<FlowUnit> it2=fs3.fiterator();
			while(it2.hasNext()){
				FlowUnit a=it2.next();
				System.out.println(a.toString2());
			}
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

	@Test
	public void testToString() {
		try {
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4^2-4/5-7^10-15/15-20");
			System.out.println(fs3);
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

	@Test
	public void testToString2() {
		try {
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4^2-4/5-7^10-15/15-20");
			System.out.println(fs3.toString2());
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}	
	}

	@Test
	public void testGetFlowSets() {
		testRemove();
	}

	@Test
	public void testMatch() {
		try {
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4^2-4/5-7^10-15/15-20");
			FlowSpace fs= new FlowSpace("1-3/8-15^48/16-23^100-200/32-47");
			FlowSpace fs1= new FlowSpace("1-2/8-15^48/16-23^110-180/32-47");
			FlowSpace fs2= new FlowSpace("1-3/8-15^48/16-21^100-200/32-47");
			assertTrue(fs.match(fs1));
			assertTrue(fs.match(fs2));
			assertTrue(!fs.match(fs3));
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

	@Test
	public void testHasSameCardinality() {
		try {	
			FlowSpace fs= new FlowSpace("1-3/8-15^8-9/16-23");
			FlowSpace fs1= new FlowSpace("1-2/8-15^48/16-23^10-20/32-47");
			FlowSpace fs2= new FlowSpace("1-3/1-7^48/16-21^20-29/32-47");
			FlowSpace fs3= new FlowSpace("1-2/0-2^1-3/2-4");
			FlowSpace fs4= new FlowSpace("1-5/0-3");
			FlowSpace fs5= new FlowSpace("1-2/0-2^1-3/2-4^10-11/5-8");
			assertTrue(fs.hasSameCardinality(fs3));
			assertTrue(fs1.hasSameCardinality(fs2));
			assertTrue(fs3.hasSameCardinality(fs4));
			assertTrue(!fs3.hasSameCardinality(fs5));
		}
		catch (PharosException ex){
			fail("excetpion occured:"+ex.getMessage());
		}
	}

}
