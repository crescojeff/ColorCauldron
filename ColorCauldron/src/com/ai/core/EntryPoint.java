package com.ai.core;

import java.io.IOException;

public class EntryPoint {
	
	private static ColorCauldronMain ccmain;
	protected static int imageSizeX;
	protected static int imageSizeY;
	protected static String[] myArgs; 
	/**
	* @param args
	 * @throws IOException 
	*/
	public static void main(String[] args) throws IOException {
	//Process args
	myArgs = args;
	
	//Init root object
	ccmain = new ColorCauldronMain();
	}
	
	public static String[] getArgs(){
		return myArgs;
	}

	


}
