package org.renci.doe.pharos.flow;

public class PharosException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = null;
	public PharosException(){
		super();
	}
	public PharosException(String msg){
		super(msg);
		message=msg;
	}
	public String toString(){
		return message;
	}
	public String getMessage(){
		return message;
	}
}
