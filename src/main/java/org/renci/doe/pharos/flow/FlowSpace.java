package org.renci.doe.pharos.flow;

import java.util.SortedSet;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.math.BigInteger;

public class FlowSpace implements Iterable<BigInteger> {
    private SortedSet<FlowUnit> flowsets;
    public FlowSpace(){
        flowsets=new TreeSet<FlowUnit>();
    }

    public FlowSpace (List<FlowUnit> list)
    {
        this();
        for (FlowUnit unit: list)
            flowsets.add(unit);		
    }

    public FlowSpace(String s) throws PharosException{
        flowsets=new TreeSet<FlowUnit>();
        String delims="[ ]*\\^[ ]*";
        String[] tokens = s.split(delims);
        for (int i=0;i<tokens.length;i++){
            FlowUnit fu=new FlowUnit(tokens[i]);
            System.out.println(fu);
            flowsets.add(fu);
        }
    }
    public boolean add(FlowUnit fu){
        return flowsets.add(fu);
    }
    public boolean remove(FlowUnit fu){
        return flowsets.remove(fu);
    }
    public Iterator<BigInteger> iterator(){
        return new BigIntIterator(flowsets);
    }
    class BigIntIterator implements Iterator<BigInteger>{
        private Iterator<FlowUnit> it;
        private Iterator<BigInteger> i;

        public BigIntIterator(SortedSet<FlowUnit> flowsets) {
            // TODO Auto-generated constructor stub
            it=flowsets.iterator();
            i=it.next().iterator();
        }	

        public boolean hasNext(){
            if(i.hasNext())return true;
            else if(it.hasNext())return true;
            else return false;
        }
        @Override
            public BigInteger next() {
                // TODO Auto-generated method stub
                if(i.hasNext()) return i.next();
                else if(it.hasNext()){
                    i=it.next().iterator();
                    return i.next();
                }

                else return null;
            }
        @Override
            public void remove() {
                // TODO Auto-generated method stub

            }

    }
    public Iterator<FlowUnit> fiterator() throws PharosException{
        return new FlowUnitIterator(flowsets);
    }
    class FlowUnitIterator implements Iterator<FlowUnit>{
        private Iterator<FlowUnit> it;
        private Iterator<FlowUnit> i;

        public FlowUnitIterator(SortedSet<FlowUnit> flowsets) throws PharosException {
            // TODO Auto-generated constructor stub
            it=flowsets.iterator();
            if (it.hasNext())i=it.next().fiterator();
            else throw new PharosException("empty flowsets");

        }	

        public boolean hasNext(){
            if(i.hasNext())return true;
            else if(it.hasNext())return true;
            else return false;
        }
        @Override
            public FlowUnit next() {
                // TODO Auto-generated method stub
                if(i.hasNext()) return i.next();
                else if(it.hasNext()){
                    i=it.next().fiterator();
                    return i.next();
                }

                else return null;
            }
        @Override
            public void remove() {
                // TODO Auto-generated method stub

            }

    }
    public String toString(){
        //return flowsets.toString();
    	String s=new String();
		try{
			Iterator<FlowUnit> it=flowsets.iterator();
			boolean first=true;
			while(it.hasNext()){
				if(first){
					first=false;
					s=s+it.next().toString();
				}
				else
					s=s+"^"+it.next().toString();
			}
			return s;
		}
		catch (Exception ex){
			return s;
		}
    }
    public String toString2(){
        String s=new String("[");
        try{
            Iterator<FlowUnit> it=fiterator();
            while(it.hasNext()){
                s=s+" ["+it.next().toString2()+"]";
            }
            return s+" ]";
        }
        catch (Exception ex){
            return s+" ]";
        }

    }
    public SortedSet<FlowUnit> getFlowSets(){
        return flowsets;
    }
    public boolean match(FlowSpace fs){
		
        Iterator<FlowUnit> it1=flowsets.iterator();
		boolean matched = true;
        while (it1.hasNext() && matched){
			Iterator<FlowUnit> it2=fs.getFlowSets().iterator();
            //System.out.println(it1.next() + " ");
			boolean matchFound = false;
			FlowUnit unit = it1.next();
			
			while (it2.hasNext() && !matchFound) {
				matchFound = unit.match(it2.next());
        	}
			matched = matchFound;
		}
        return matched;
    }
    public boolean hasSameCardinality(FlowSpace fs){
        Iterator<BigInteger> it1=iterator();
        Iterator<BigInteger> it2=fs.iterator();
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

    public static FlowSpace getFlowspace (byte[] pkt)
    {

        int networkProtocol = 0;
        int transportOffset = 34;
        short transportSource = 0;
        short transportDestination = 0;

        List<FlowUnit> list  = new ArrayList<FlowUnit>();
        ByteBuffer buffer = ByteBuffer.wrap(pkt);

        if (buffer.limit() >= 14) {
            buffer.position(12);
            short etherType = buffer.getShort(); 
            try {
                if (etherType == (short) 0x8100) {
                    short vlan  = (short) (buffer.getShort() & 0xfff);
                    list.add(new FlowUnit(new ComparableRange[] {
                                new ComparableRange((int)vlan), VLAN_OFFSET}));
                    //match.append(Short.toString(vlan))
                    //        .append(VLAN_OFFSET)
                    //        .append(OR); 
                    etherType = buffer.getShort();
                }

                if (etherType == (short) 0x0800) {
                    // ipv4
                    // check packet length
                    int scratch = buffer.get();
                    scratch = (short) (0xf & scratch);
                    transportOffset = (buffer.position() - 1)
                        + (scratch * 4);

                    buffer.get();
                    buffer.position(buffer.position() + 7);
                    networkProtocol = buffer.get();
                    list.add(new FlowUnit(new ComparableRange[] {
                                new ComparableRange(networkProtocol), NPROT_OFFSET}));
                    //match.append(Integer.toString(networkProtocol))
                    //    .append(NPROT_OFFSET + OR);

                    // nw src
                    buffer.position(buffer.position() + 2);
                    int networkSource = buffer.getInt();
                    list.add(new FlowUnit(new ComparableRange[] {
                                new ComparableRange(networkSource), IP_SRC_OFFSET}));
                    //match.append(IPv4.fromIPv4Address(networkSource))
                    //    .append(IP_SRC_OFFSET + OR);
                    // nw dst
                    int networkDestination = buffer.getInt();
                    list.add(new FlowUnit(new ComparableRange[] {
                                new ComparableRange(networkDestination), IP_DST_OFFSET}));
                    //match.append(IPv4.fromIPv4Address(networkDestination))
                    //    .append(IP_DST_OFFSET + OR);
                    buffer.position(transportOffset);
                } else {
                    ComparableRange defaultR = new ComparableRange(0);
                    list.add(new FlowUnit(new ComparableRange[] {
                                defaultR, NPROT_OFFSET}));
                    list.add(new FlowUnit(new ComparableRange[] {
                                defaultR, IP_SRC_OFFSET}));
                    list.add(new FlowUnit(new ComparableRange[] {
                                defaultR, IP_DST_OFFSET}));
                    //match.append(Integer.toString(0))
                    //    .append(NPROT_OFFSET + OR);
                    //    .append(IPv4.fromIPv4Address(0x0))
                    //    .append(IP_SRC_OFFSET + OR)
                    //    .append(IPv4.fromIPv4Address(0x0))
                    //    .append(IP_DST_OFFSET + OR);
                }

                switch (networkProtocol) {
                    case 0x01:
                        // icmp
                        // type
                        transportSource = f(buffer.get());    
                        transportDestination = f(buffer.get());
                        break;
                    case 0x06:
                        // tcp
                        // tcp src
                        transportSource = buffer.getShort();
                        // tcp dest
                        transportDestination = buffer.getShort();
                        break;
                    case 0x11:
                        // udp
                        // udp src
                        transportSource = buffer.getShort();
                        // udp dest
                        transportDestination = buffer.getShort();
                        break;
                }
                list.add(new FlowUnit(new ComparableRange[] {
                            new ComparableRange(transportSource), TP_SRC_OFFSET}));
                list.add(new FlowUnit(new ComparableRange[] {
                            new ComparableRange(transportDestination), TP_DST_OFFSET}));
                //match.append(Short.toString(transportSource))
                //.append(TP_SRC_OFFSET + OR)
                //.append(Short.toString(transportDestination))
                //.append(TP_DST_OFFSET);
                return (new FlowSpace(list));
            } catch (PharosException e) {
                return (null); 
            }
        } else {
            return (null);
        }
    }

    public static short f (byte i)
    {
        return (short) ((short)i & 0xff);
    }

    private static final ComparableRange VLAN_OFFSET = new ComparableRange(96, 111);
    private static final ComparableRange NPROT_OFFSET = new ComparableRange(128, 135);
    private static final ComparableRange IP_SRC_OFFSET = new ComparableRange(136, 167);
    private static final ComparableRange IP_DST_OFFSET = new ComparableRange(168, 199);
    private static final ComparableRange TP_SRC_OFFSET = new ComparableRange(200, 215);
    private static final ComparableRange TP_DST_OFFSET = new ComparableRange(216, 231);
    //private static final String OR = " ^ ";
}
