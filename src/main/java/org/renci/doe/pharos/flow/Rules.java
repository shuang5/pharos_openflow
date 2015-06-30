package org.renci.doe.pharos.flow;

import org.renci.doe.pharos.flow.FlowSpace;
import org.renci.doe.pharos.flow.FlowUnit;
import org.renci.doe.pharos.flow.ComparableRange;
import com.google.common.collect.Range;

import java.util.Iterator;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Rules {
	private JSONArray ruletables;

	public Rules(){
		ruletables=new JSONArray();
		//JSONArray ruletable=new JSONArray();
		//ruletables.add(ruletable);
	}
	public Rules(Rules r){
		ruletables=r.ruletables;
	}
	public Rules(String s){
		ruletables=JSONArray.fromObject(s);
	}
	public int addRuleTable(JSONArray rt){
		ruletables.add(rt);
		return ruletables.size();
	}
	public int addRuleTable(Rule r){
		JSONArray rt=new JSONArray();
		rt.add(r.getJSONObject());
		ruletables.add(rt);
		return ruletables.size();
	}
	public void deleteRuleTable(JSONArray rt){
		ruletables.remove(rt);
	}
	public void deleteRuleTable(int i){
		ruletables.remove(i);
	}
	public void deleteLastRuleTable() throws PharosException{
		if(ruletables.size() > 0)
			ruletables.remove(ruletables.size()-1);
		else {
			throw new PharosException("Rule table is empty");
		}
	}
	public JSONArray getRuleTable(int i) throws PharosException{
		try {
			return ruletables.getJSONArray(i);
		}
		catch (JSONException je){
			throw new PharosException ("index out of boundary");
		}

	}
	/*
	public JSONArray getLastRuleTable() throws PharosException{
		try {
			if(ruletables.size()&gt;0)
				return ruletables.getJSONArray(ruletables.size()-1);
			else
				return ruletables.getJSONArray(0);
		}
		catch (JSONException je){
			throw new PharosException ("index out of boundary");
		}

	}
	*/
	public JSONArray addRuleToRuleTable(Rule r, int i) throws PharosException{
		JSONArray rt=getRuleTable(i);
		rt.add(r.getJSONObject());
		return ruletables.element(i,rt);
	}
	public JSONArray addRule(Rule r) throws PharosException{
		JSONArray rt=new JSONArray();
		if(ruletables.size()>0)
			rt=getRuleTable(ruletables.size()-1);
		rt.add(r.getJSONObject());
		return ruletables.element(ruletables.size() > 0?ruletables.size()-1:0,rt);
	}
	@SuppressWarnings("unused")
	private JSONArray setRule(Rule r, int i) throws PharosException{
		JSONArray rt=new JSONArray();
		if(ruletables.size()> 0)
			rt=getRuleTable(ruletables.size()-1);
		else
			rt.add(r.getJSONObject());
		rt.element(i,r.getJSONObject());
		return ruletables.element(ruletables.size()>0?ruletables.size()-1:0,rt);
	}
	public JSONArray deleteRule(int i) throws PharosException{
		JSONArray rt=getRuleTable(ruletables.size()-1);
		rt.remove(i);
		return ruletables.element(ruletables.size()-1,rt);

	}
	public String toString(){
		return ruletables.toString();
	}
	public String toString(int i){
		return ruletables.toString(i);
	}
	public String toString(int i, int j){
		return ruletables.toString(i,j);
	}
	@SuppressWarnings("unchecked")
	public FlowSpace evaluate (FlowSpace fs) throws PharosException{
		Iterator<JSONArray> rt_it=ruletables.iterator();
		Iterator<JSONObject> r_it;
		FlowSpace output=fs;
		while(rt_it.hasNext()){
			r_it=rt_it.next().iterator();
			while(r_it.hasNext()){
				try {
					Rule rule=new Rule(r_it.next());
					if(rule.matchRule(output)){
						output=rule.mapTo(output);
						break;
					}
				}
				catch (Exception ex){
					throw new PharosException("wrong rule format");
				}
			}
		}
		return output;
	}

	public byte[] getPacketHeader (byte[] pkt) 
	{
		byte[] returnPacket = pkt;
		FlowSpace flow = FlowSpace.getFlowspace(pkt);

		try {
			System.out.println("****** Evaluating flow space " + flow);
			flow = evaluate(flow);
		} catch (Exception e) {
			System.out.println("****** Caught an Exception "+ e +" ****");
		}

		if (flow != null)
			returnPacket = getPacketHeader(pkt, flow);

		return (returnPacket);
	}

    public static byte[] getPacketHeader (byte[] pkt, FlowSpace space)
    {
        try {
            Iterator<FlowUnit> it = space.fiterator();
            FlowUnit unit = null;

            while (it.hasNext()) {
                unit = it.next();
                pkt = getModifiedHeader (pkt, unit);
            }
        } catch  (PharosException e) {
            e.printStackTrace();
        }
        return (pkt);
    }

    public static boolean isDot1QType (byte[] pkt)
    {
        byte[] vlanEtherType = new byte[] {(byte)0x81, (byte)0x00};

        if (pkt.length >= 14) {
            if (pkt[12] == vlanEtherType[0] &&
                pkt[13] == vlanEtherType[1])
                    return (true);
        }
        return (false);
    }

    public static byte[] getModifiedHeader (byte[] pkt, FlowUnit unit)
    {
        Range<Integer> mask = unit.getMask();
        boolean isVlanEnabled = isDot1QType(pkt);
        int lowerEndpoint = unit.getMask().lowerEndpoint();
        int upperEndpoint = unit.getMask().upperEndpoint();

        if (ETHERTYPE_OFFSET.encloses(mask)) {
            if (isVlanEnabled){
                lowerEndpoint += 16;
                upperEndpoint += 16;
            } else {
                lowerEndpoint -= 16;
                upperEndpoint -= 16;
            }

        } else if (VLAN_OFFSET.encloses(mask)) {
            if (isVlanEnabled){
                lowerEndpoint +=20;
                upperEndpoint +=16;
            }
        } else if (NWPROTO_OFFSET.encloses(mask)){
            if (isVlanEnabled){
                lowerEndpoint +=88;
                upperEndpoint +=88;
            } else {
                lowerEndpoint +=56;
                upperEndpoint +=56;
            }
        } else if (IP_SRC_OFFSET.encloses(mask) ||
                   IP_DST_OFFSET.encloses(mask)) {
            if (isVlanEnabled){
                lowerEndpoint +=104;
                upperEndpoint +=104;
            } else {
                lowerEndpoint +=72;
                upperEndpoint +=72;
            }
        } else if (TP_SRC_OFFSET.encloses(mask) ||
                   TP_DST_OFFSET.encloses(mask)) {
            int ipHeaderLen = getIPHeaderLen(pkt, isVlanEnabled);
            if (isVlanEnabled) {
                if (ipHeaderLen > 0) {
                    lowerEndpoint += ipHeaderLen-56;
                    upperEndpoint += ipHeaderLen-56;
                }
            } else {
                if (ipHeaderLen > 0) {
                    lowerEndpoint += ipHeaderLen-88;
                    upperEndpoint += ipHeaderLen-88;
                }
            }
        } else {
            return (pkt);
        }
        modifyPktHeader(pkt, upperEndpoint, lowerEndpoint,
                        unit.getRange().lowerEndpoint());
        return (pkt);
    }

    public static int getIPHeaderLen (byte [] pkt, boolean isVlanEnabled)
    {
        int length = -1;
        if (isVlanEnabled && pkt.length >= 18) {
            length = (pkt[18] & 0x0F) * 4;
        } else if (!isVlanEnabled && pkt.length >= 14) {
            length = (pkt[14] & 0x0F) * 32;
        }
        return (length);
    }

    public static void modifyPktHeader (byte[] data, int highEndPoint,
                                        int lowerEndPoint, int value)
    {
        int upperByteOffset;
        int lowerByteOffset;
        int noOfBytes = (highEndPoint/8) - (lowerEndPoint/8) + 1;
        int temp, mask;
        int cleanupMask = 0;

        //System.out.println("value is:"+ Integer.toHexString(value));
        if (noOfBytes == 1) {
            upperByteOffset = ((highEndPoint/8) + 1)*8 -1;
            lowerByteOffset = (lowerEndPoint/8) * 8;
            temp = value << (upperByteOffset - highEndPoint);
            //System.out.println("Shifted by: "+ (upperByteOffset - high));
            //System.out.println("value is:"+ Integer.toHexString(temp));
            mask = (byte) 1 << (upperByteOffset - highEndPoint);
            mask = mask - 1;
            cleanupMask |= mask;
            mask = ~mask;
            temp = temp & mask;
            //System.out.println("value is:"+ Integer.toHexString(temp));
            mask = 1 << (upperByteOffset - lowerEndPoint +1);
            mask = mask - 1;
            cleanupMask |=  mask ^ 0xFF;
            mask = mask ^ 0xFF;
            temp = temp & mask;
            //System.out.println("Cleanup mask value is:"+ Integer.toHexString(cleanupMask));
            //System.out.println("value is:"+ Integer.toHexString(temp));
            //System.out.println("byte value is:"+ Integer.toHexString(data[lowerByteOffset/8]));
            data[lowerByteOffset/8] = (byte)((data[lowerByteOffset/8] & cleanupMask )| temp);
            //System.out.println("value is:"+ Integer.toHexString(data[lowerByteOffset/8]));
        } else {
            int localLow;
            upperByteOffset = ((highEndPoint/8) + 1)*8 -1;
            lowerByteOffset = (highEndPoint/8)*8;
            temp = value << (upperByteOffset - highEndPoint);
            value = value >> (highEndPoint - lowerByteOffset+1);

            //System.out.println("Shift count: "+ (upperByteOffset - lowerByteOffset));
            //System.out.println("Shifted by: "+ (upperByteOffset - high));
            //System.out.println("given value is: "+ Integer.toHexString(value));
            //System.out.println("value is:"+ Integer.toHexString(temp));

            mask = 1 << (upperByteOffset - highEndPoint);
            mask = mask - 1;
            cleanupMask |= mask;
            mask = ~mask;
            temp = temp & mask;

            //System.out.println("mask value is:"+ Integer.toHexString(mask));
            //System.out.println("temp value is:"+ Integer.toHexString(temp));
            //System.out.println("byte value is:"+ Integer.toHexString(data[lowerByteOffset/8]));

            data[lowerByteOffset/8] = (byte)((data[lowerByteOffset/8] & cleanupMask )| temp);
            //System.out.println("value is:"+ Integer.toHexString(data[lowerByteOffset/8]));
            noOfBytes--;
            upperByteOffset -= 8;
            lowerByteOffset -= 8;
            localLow = lowerByteOffset;
            while (noOfBytes > 0) {
                if (localLow < lowerEndPoint)
                    localLow= lowerEndPoint;
                temp = value;
                value = value >> (upperByteOffset - lowerByteOffset+1);
                mask = (byte) 1 << (upperByteOffset - localLow+1);
                mask = mask - 1;
                cleanupMask |=  mask ^ 0xFF;
                temp = temp & mask;
                //System.out.println("Iteration I: " + noOfBytes);
                //System.out.println("value is:"+ Integer.toHexString(temp));
                data[lowerByteOffset/8] = (byte)((data[lowerByteOffset/8] & cleanupMask )| temp);
                //System.out.println("value is:"+ Integer.toHexString(data[lowerByteOffset/8]));
                //System.out.println("given value is: "+ Integer.toHexString(value));
                noOfBytes--;
                upperByteOffset -= 8;
                lowerByteOffset -= 8;
                localLow = lowerByteOffset;
            }
        }
    }

    public static final Range <Integer> VLAN_OFFSET = Range.closed(96, 111);
    public static final Range <Integer> SRC_MAC_OFFSET = Range.closed(0, 47);
    public static final Range <Integer> DST_MAC_OFFSET = Range.closed(48, 95);
    public static final Range <Integer> ETHERTYPE_OFFSET = Range.closed(112, 127);
    public static final Range <Integer> NWPROTO_OFFSET = Range.closed(128, 135);
    public static final Range <Integer> IP_SRC_OFFSET = Range.closed(136, 167);
    public static final Range <Integer> IP_DST_OFFSET = Range.closed(168, 199);
    public static final Range <Integer> TP_SRC_OFFSET = Range.closed(200, 215);
    public static final Range <Integer> TP_DST_OFFSET = Range.closed(216, 231);
}

