package org.renci.doe.pharos.flow;

import com.google.common.collect.Range;
import org.renci.doe.pharos.flow.PharosException;

public class ComparableRange implements Comparable<ComparableRange> {
	private Range<Integer> range;

	ComparableRange(Range<Integer> r){
		range=r;
	}

	public ComparableRange (int val)
	{
		range = Range.closed(val,val);
	}

	public ComparableRange (int lowerEndPoint, int HighEndPoint)
	{
		range = Range.closed(lowerEndPoint, HighEndPoint);
	}

	public ComparableRange(String s)throws PharosException{
		s = s.trim();
		String[] ss = null;
		if (s.matches("-[0-9]+[ ]*-[ ]*-[0-9]+")) {
			ss = new String[2];
			int splitIndx = s.indexOf('-', 1);
			ss[0] = s.substring(0, splitIndx).trim();
			ss[1] = s.substring(splitIndx+1, s.length()).trim(); 
		} else if (s.matches("-[0-9]+")) {
			ss = new String[1];
			ss[0] = s; 
		} else {
			String delims="[ ]*-[ ]*";
			ss=s.split(delims);
		}
		if (ss.length==1){
			try {
				int a=Integer.parseInt(ss[0]);
				range=Range.closed(a,a);
			}
			catch (Exception ex){
				throw new PharosException("accept decimal number");
			}
		}
		else if(ss.length==2){
			try {
				int a=Integer.parseInt(ss[0]);
				int b=Integer.parseInt(ss[1]);
				if (a>b)throw new PharosException("accepted format: a-b, where a<=b");
				range=Range.closed(a,b);
			}
			catch (Exception ex){
				throw new PharosException("accept decimal number-decimal number");
			}
		}
		else {
			throw new PharosException("invalid format, shoudl be decimal number-decimal number");
		}
	}
	public Range<Integer> getRange(){
		return range;
	}
	@Override
	public int compareTo(ComparableRange o) {
		// TODO Auto-generated method stub
		if (this.range.lowerEndpoint()<o.range.lowerEndpoint()){
			return -1;
		}
		else if (this.range.lowerEndpoint()>o.range.lowerEndpoint()){
			return 1;
		}
		else if (this.range.upperEndpoint()<o.range.upperEndpoint()){
			return -1;
		}
		else if (this.range.upperEndpoint()>o.range.upperEndpoint()){
			return 1;
		}
		else return 0;
	}
	public String toString2(){
		return range.toString();
	}
	public String toString(){
		String s;
		if (range.lowerEndpoint()!=range.upperEndpoint())
			s=range.lowerEndpoint()+"-"+range.upperEndpoint();
		else 
			s=Integer.toString(range.lowerEndpoint());
		return s;
	}
	public static void main(String[] args){
		try{
			ComparableRange r=new ComparableRange("1-5");
			System.out.println(r.toString());
		}catch (PharosException ex){
			System.out.println(ex);
		}
	}
}
