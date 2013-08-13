package com.ai.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFrame;

import marvin.image.MarvinImage;

import com.ai.gui.ConstructCauldron;

import com.ai.io.FSliaison;
import com.ai.transform.Transmute;


public class ColorCauldronMain {
	//Constants
	private static final int LOW_PASS = 0;
	private static final int HIGH_PASS = 1;
	private static final int MEDIAN = 2;
	private static final int HIGH_BOOST = 3;
	
	//IO stuff
	private FSliaison liaison;
	
	//Processing stuff
	private byte arbitraryImageArray[];
	private int arbitraryMaskArray[];
	private char charMaskArray[];
	private Transmute alchemy;
	private int[][] stockMatrix;
	
	//Marvin GUI stuff
	private ConstructCauldron couldron;
	
	
	
	public ColorCauldronMain() throws IOException{
		//init shared utility objects
		liaison = new FSliaison(this);
		alchemy = new Transmute(this,8);
		
		//take action based on user args from Main in EntryPoint
		action(EntryPoint.getArgs()[0]);
		
	}
	
	public FSliaison getLiaison(){
		return liaison;
	}
	public Transmute getTransmute(){
		return alchemy;
	}
	public ConstructCauldron getCouldron(){
		return couldron;
	}
	
	public void action(String s) throws IOException{
		 if (s.equals("console")){
			 couldron = new ConstructCauldron(this,"basic",null);
			 couldron.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 }
		 else if(s.equals("cauldron")){
			 couldron = new ConstructCauldron(this,"colorcauldron",null);
			 couldron.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 }
		 else if(s.equals("grayops")){
			 couldron = new ConstructCauldron(this,"grayOps",null);
			 couldron.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 }
		 else if(s.equals("stock")){
			 alchemy.loadStockPPM("assets/P3-8-8-7.dat", 8, 8);
			 stockMatrix = alchemy.invertStockPPM(8, 8);
			 alchemy.writeStockPPM("assets/inverted_P3-8-8-7.ppm", 8, 8);
			 ///BufferedImage b = Transmute.getImageFromMatrix(stockMatrix);
			 //System.out.println("read first arg as stock correctly");
			/// couldron = new ConstructCouldron(this,"givenImage",new MarvinImage(b));
			 ///couldron.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 }
	}
	
	
	
	
	private static class MaxMemory {
	    public static void getMem(){
	        Runtime rt = Runtime.getRuntime();
	        long totalMem = rt.totalMemory();
	        long maxMem = rt.maxMemory();
	        long freeMem = rt.freeMemory();
	        double megs = 1048576.0;

	        System.out.println ("Total Memory: " + totalMem + " (" + (totalMem/megs) + " MB)");
	        System.out.println ("Max Memory:   " + maxMem + " (" + (maxMem/megs) + " MB)");
	        System.out.println ("Free Memory:  " + freeMem + " (" + (freeMem/megs) + " MB)");
	    }
	}

	
}
