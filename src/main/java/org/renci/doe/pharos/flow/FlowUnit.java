package org.renci.doe.pharos.flow;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Iterator;

import com.google.common.collect.Range;


public class FlowUnit implements Comparable<FlowUnit>, Iterable<BigInteger>{
	static final int LengthOfFlow=200;
	private ComparableRange range;
	private ComparableRange mask;
	private BigInteger flowLowEnd;
	private BigInteger flowHighEnd;
	public FlowUnit(String s) throws PharosException{
		String delims="[ ]*/[ ]*";
		System.out.println("Flow Unit init String is "+s);
		String[] subTokens=s.split(delims);
		if (subTokens.length !=2) 
			throw new PharosException("wrong flowspace format -- decimal number-decimal number/decimal number-decimal number");
		range = new ComparableRange(subTokens[0]);
		mask = new ComparableRange(subTokens[1]);
		byte[] bytes=new byte[LengthOfFlow];
		flowLowEnd = new BigInteger(bytes);
		flowHighEnd = new BigInteger(bytes);
		int start = mask.getRange().lowerEndpoint();
		int stop = mask.getRange().upperEndpoint();
		int a = range.getRange().lowerEndpoint();
		int b = range.getRange().upperEndpoint();
		if(b>1<<(stop-start+1))
			throw new PharosException("wrong format -- out of range specified by the mask");
		BitSet seta=Bits.convert(a);
		BitSet setb=Bits.convert(b);
		for(int i=start,j=0;i<=stop;i++,j++){
			if (j<seta.length() && seta.get(j)){
				flowLowEnd=flowLowEnd.setBit(i);
			}
			if (j<setb.length() && setb.get(j)){
				flowHighEnd=flowHighEnd.setBit(i);
			}
		}
	}

	public FlowUnit(ComparableRange[] valnMsk) throws PharosException
	{
		int VALUE = 0;
		int MASK = 1;

		range = valnMsk[VALUE];
		mask = valnMsk[MASK];
		byte[] bytes=new byte[LengthOfFlow];
		flowLowEnd = new BigInteger(bytes);
		flowHighEnd = new BigInteger(bytes);
		int start = valnMsk[MASK].getRange().lowerEndpoint();
		int stop = valnMsk[MASK].getRange().upperEndpoint();
		int a = valnMsk[VALUE].getRange().lowerEndpoint();
		int b = valnMsk[VALUE].getRange().upperEndpoint();
		if(b>1<<(stop-start+1))
			throw new PharosException("wrong format -- out of range specified by the mask");
		BitSet seta=Bits.convert(a);
		BitSet setb=Bits.convert(b);
		for(int i=start,j=0;i<=stop;i++,j++){
			if (j<seta.length() && seta.get(j)){
				flowLowEnd=flowLowEnd.setBit(i);
			}
			if (j<setb.length() && setb.get(j)){
				flowHighEnd=flowHighEnd.setBit(i);
			}
		}
	}

	public Range<Integer> getRange(){
		return range.getRange();
	}

    public Range<Integer> getMask()
    {
        return mask.getRange();
    }

	public Iterator<BigInteger> iterator(){
		return new BigIntIterator(this);
	}

	class BigIntIterator implements Iterator<BigInteger>{
			private BigInteger val;
			private BigInteger one;
			
			public BigIntIterator(FlowUnit fu) {
				// TODO Auto-generated constructor stub
				val=flowLowEnd;
				one=BigInteger.ZERO;
				int start=mask.getRange().lowerEndpoint();
				one=one.setBit(start);
			}
			
			
			public boolean hasNext(){
				if(val==null)return false;
				if (flowLowEnd.compareTo(val)<=0 && flowHighEnd.compareTo(val)>=0)return true;
				else return false;
			}
			@Override
			public BigInteger next() {
				// TODO Auto-generated method stub
				if (flowHighEnd.compareTo(val)>0){
					BigInteger a=val;
					val=val.add(one);
					return a;
				}
				else {
					BigInteger a=val;
					val=null;
					return a;
					
				}
			}
			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}
	
	}
	public Iterator<FlowUnit> fiterator(){
		return new FlowUnitIterator(this);
	}
	class FlowUnitIterator implements Iterator<FlowUnit>{
			private int f;
			
			public FlowUnitIterator(FlowUnit fu) {
				// TODO Auto-generated constructor stub
				try{
					f=fu.getRange().lowerEndpoint();
				}
				catch (Exception ex){
					
				}
			}
			
			
			public boolean hasNext(){
				if(f<=range.getRange().upperEndpoint()) return true;
				else return false;
			}
			@Override
			public FlowUnit next() {
				// TODO Auto-generated method stub
				int a=f;
				f++;
				String s=a+"/"+mask.toString();
				System.out.println("Creating new flow unit: "+ s);
				try{
					return new FlowUnit(s);
				}
				catch (Exception ex){
					ex.printStackTrace();
					return null;
				}
					
			}
			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}
	
	}
	public BigInteger getLowend(){
		return flowLowEnd;
	}
	public BigInteger getHighend(){
		return flowHighEnd;
	}
	public boolean match(FlowUnit fu){
		System.out.println("Matching Flow units: "+ this +" vs "+ fu);
		if (flowLowEnd.compareTo(fu.getLowend())<=0 && flowHighEnd.compareTo(fu.getHighend())>=0){
			System.out.println("Match Found ");
			return true;
		}
		else return false;
	}
	public String toString2(){
		return "["+flowLowEnd+","+flowHighEnd+"]";
	}
	public String toString(){
		return range.toString()+"/"+mask.toString();
	}
	@Override
	public int compareTo(FlowUnit o) {
		// TODO Auto-generated method stub
		if (flowLowEnd.compareTo(o.getLowend())<0)
			return -1;
		else if (flowLowEnd.compareTo(o.getLowend())>0)
			return 1;
		else if (flowHighEnd.compareTo(o.getHighend())<0)
			return -1;
		else if (flowHighEnd.compareTo(o.getHighend())>0)
			return 1;
		else 
			return 0;
	}
	public boolean hasSameCardinality(FlowUnit fu){
		Iterator<FlowUnit> it1=fiterator();
		Iterator<FlowUnit> it2=fu.fiterator();
		while(it1.hasNext() || it2.hasNext()){
			if(!it1.hasNext())return false;
			else if(!it2.hasNext()) return false;
			else {
				it1.next();
				it2.next();
			}
		}
		return true;
			
	}
	public static void main(String[] args){
		String s="10-20/8-15";
		String s1="11-15/8-14";
		String s2="2-3/9-12";
		String s3="12/8-12";
		String s4="11-21/6-10";
		try {
			FlowUnit f=new FlowUnit(s);
			System.out.println(f);
			if (f.match(new FlowUnit(s1))) System.out.println(s1+" matches");
			if (f.match(new FlowUnit(s2))) System.out.println(s2+" matches");
			if (f.match(new FlowUnit(s3))) System.out.println(s3+" matches");
			Iterator<BigInteger> it=f.iterator();
			while(it.hasNext()){
				System.out.println(it.next());
			}
			Iterator<FlowUnit> it2=f.fiterator();
			while(it2.hasNext()){
				System.out.println(it2.next());
			}
			FlowUnit ff=new FlowUnit(s4);
			Iterator<FlowUnit> it3=ff.fiterator();
			while(it3.hasNext()){
				System.out.println(it3.next());
			}
			Iterator<BigInteger> it4=ff.iterator();
			while(it4.hasNext()){
				System.out.println(it4.next());
			}
			if (f.hasSameCardinality(new FlowUnit(s4))) System.out.println("s4 has the same cadinality with s");
		}
		catch (Exception ex){
			System.out.println(ex);
		}
		
	}
	
}
