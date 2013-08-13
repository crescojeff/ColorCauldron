/*
 * This class executes a number of color-related transforms on images
 * 1. JuxtOp randomly shuffles the RGB values of each pixel in a single image resulting in an
 * interesting loss of heuristic detail, but no actual loss of color values
 * 2. ShadeStack_NoWrap adds all the color channel values of two images together
 * bounded by the maximum color value allowed by the bits-per-channel (currently hard-coded to 255)
 * such that R:155 + R:155 equals 255 rather than 310 etc.  The result using the same image is
 * an intense brightening effect which (with additional logic applied for given use-cases) could
 * be useful in enhancing image quality.  Executing the operation on two different images
 * is far more interesting as a the result is a hybrid image which exhibits elements of both
 * images (usually enough to tell heuristically that each parent image is still present in the
 * child) but is influenced at every pixel by both parents.
 * 3.ShadeStack_Wrap adds color values as above, but instead of binding them at a max value,
 * it wraps the color value around to 0 when the maximum is reached, then adds any remaining 
 * overflow to 0.  The result is an intriguing biased-inversion, where color values are altered
 * in a similar fashion to classic inversion, but carry the influence of each parent into the child.    
 * 
 * 
 * 
 * Support is also planned for some basic Steganography routines... this should be fun :)))
 */

package com.ai.transform;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

import com.ai.core.ColorCauldronMain;
import com.ai.io.FSliaison;

public class Transmute {
	private ColorCauldronMain hMain;
	private FSliaison liaison;
	private int[] stockMatrixStream;
	private int[][] stockMatrix;
	private TreeMap<Integer,int[]> imgMap = new TreeMap<Integer,int[]>(); //holds the image data as K=pixel ID (incremented top-left to bottom-right) and V={x,y,R,G,B}
	private int arrayIndexCounter;
	private static int max_color_value;
	
	public Transmute(ColorCauldronMain main, int imageBits){
		hMain = main;
		liaison = hMain.getLiaison();
		//max_color_value = (int)(Math.pow(2.0, (double)imageBits)-1);
	}
	
	//Gray-scale transforms
	
	public MarvinImage rotateCW(MarvinImage p1, int degrees){
	  if(degrees == 90 || degrees == 180 || degrees == 270){
		//MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
		int v1,v2; 
		int width = p1.getWidth();
		int height = p1.getHeight();
		int newX = 0;
		int newY = 0;
		if(degrees==90){
		  MarvinImage child = new MarvinImage(p1.getHeight(),p1.getWidth()); //gotta flip the dims for 90 and 270 rots
          for (int y = 0; y < p1.getHeight(); y++) {
            for (int x = 0; x < p1.getWidth(); x++) {
                //Get parent value at current pixel
                v1 = p1.getIntColor(x, y); 
            	//v1 = p1.getIntComponent0(x, y);
                
                newX = (height-1)-y;
                newY = x;
                //child.setIntColor(x+(width-x), y+(height-x), v1);
                child.setIntColor(newX, newY, v1);
            } 
          }
	      child.update();
	      return child;
		}
		else if(degrees==180){
			MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
			for (int y = 0; y < p1.getHeight(); y++) {
	            for (int x = 0; x < p1.getWidth(); x++) {
	                //Get parent value at current pixel
	                v1 = p1.getIntColor(x, y); 
	                
	                newX = (width-1)-x;
	                newY = (height-1)-y;
	                //child.setIntColor(x+(width-x), y+(height-x), v1);
	                child.setIntColor(newX, newY, v1);
	            } 
	          }
		      child.update();
		      return child;	
		}
		else if(degrees==270){
			MarvinImage child = new MarvinImage(p1.getHeight(),p1.getWidth()); //gotta flip the dims for 90 and 270 rots
			for (int y = 0; y < p1.getHeight(); y++) {
	            for (int x = 0; x < p1.getWidth(); x++) {
	                //Get parent value at current pixel
	                v1 = p1.getIntColor(x, y); 
	                
	                newX = y;
	                newY = (width-1)-x;
	                //child.setIntColor(x+(width-x), y+(height-x), v1);
	                child.setIntColor(newX, newY, v1);
	            } 
	          }
		      child.update();
		      return child;	
		}
		else{
			System.out.println("For now, please use 90, 180, or 270 for rotation arguments");
			return null;
		}
	  }
	  else{
		  System.out.println("For now, please use 90, 180, or 270 for rotation arguments");
		  return null;
	  }
	}
	
	public MarvinImage flip(MarvinImage p1, String axis){
		  if(axis.equals("x") || axis.equals("y")){
			MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
			int v1,v2; 
			int width = p1.getWidth();
			int height = p1.getHeight();
			int newX = 0;
			int newY = 0;
			if(axis.equals("x")){ 
				//MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
				for (int y = 0; y < p1.getHeight(); y++) {
		            for (int x = 0; x < p1.getWidth(); x++) {
		                //Get parent value at current pixel
		                v1 = p1.getIntColor(x, y); 
		                
		                newX = x;
		                newY = (height-1)-y;
		                //child.setIntColor(x+(width-x), y+(height-x), v1);
		                child.setIntColor(newX, newY, v1);
		            } 
		          }
			      child.update();
			      return child;	
			}
			else if(axis.equals("y")){ //
				//MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
				for (int y = 0; y < p1.getHeight(); y++) {
		            for (int x = 0; x < p1.getWidth(); x++) {
		                //Get parent value at current pixel
		                v1 = p1.getIntColor(x, y); 
		                
		                newX = (width-1)-x;
		                newY = y;
		                //child.setIntColor(x+(width-x), y+(height-x), v1);
		                child.setIntColor(newX, newY, v1);
		            } 
		          }
			      child.update();
			      return child;	
			}
			
			else{
				System.out.println("please choose x or y as the axis to flip over");
				return null;
			}
		  }
		  else{
			  System.out.println("please choose x or y as the axis to flip over");
			  return null;
		  }
		}
	
	public MarvinImage magnify(MarvinImage p1, String magType, int factor){
		 // if(p1.getWidth()%4 == 0 && p1.getHeight()%4 == 0){
			//MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
			int u=0;
			int v=0;
			int v1,v2,v3,v4; 
			int width = p1.getWidth();
			int height = p1.getHeight();
			int accOffsetX = 0;
			int accOffsetY = 0;
			TreeMap<Integer,Integer> interpoMap = new TreeMap<Integer,Integer>();
			int magFactorX = width*factor;//(int)Math.ceil(width/4); //for 25% zoom, we'll take the ceiling of 0.25 * <image dimension>, and add the result as the replication/interpolation range at each pixel
			int magFactorY = height*factor;//(int)Math.ceil(height/4);
			int medianR;
			int medianD;
			int medianRD;
			
			if(magType.equals("Replication")){ 
				
				MarvinImage child = new MarvinImage(magFactorX,magFactorY);//(width+magFactorX,height+magFactorY);
				for (int y = 0; y < p1.getHeight(); y++) {
				
		            for (int x = 0; x < p1.getWidth(); x++) {
		                //Get parent value at current pixel
		                v1 = p1.getIntColor(x, y); 
		               
	                	
		                /*
		                for(v=y+accOffsetY;v<(y+accOffsetY)+factor;v++){
		                	for(u=x+accOffsetX;u<(x+accOffsetX)+factor;u++){
		                		child.setIntColor(u, v, v1);
		                		accOffsetX++;
		                		
		                	}
		                	accOffsetX = 0;
		                	accOffsetY++;
		                }
		                
		                //Below is a neat effect for compartmentalizing an images pixels; the factor-x expression inserts black space in between written pixels, creating a grid partitioning effect
		                for(v=y+accOffsetY;v<(y+accOffsetY)+(factor-1);v++){
		                	for(u=x+accOffsetX;u<(x+accOffsetX)+(factor-1);u++){
		                		child.setIntColor(u, v, v1);
		                	
		                	}
		                }
		                
		                */
		    
		                for(v=y+accOffsetY;v<(y+accOffsetY)+factor;v++){
		                	for(u=x+accOffsetX;u<(x+accOffsetX)+factor;u++){
		                		child.setIntColor(u, v, v1);
		                	
		                	}
		                }
		     
		                
		               accOffsetX+=(factor-1);
		            }
		            accOffsetX = 0;
		            accOffsetY+=(factor-1);
		          }
			      child.update();
			      return child;	
			}
			else if(magType.equals("Interpolation")){ 
				MarvinImage child = new MarvinImage(magFactorX,magFactorY);//(width+magFactorX,height+magFactorY);
				//int windowWidth = factor;
				//int windowHeight = factor;
				//int windowsInImage = (width*height)/factor;
				
				for (int y = 0; y < p1.getHeight()-1; y++) {
					
		            for (int x = 0; x < p1.getWidth()-1; x++) {
		                //Get parent values at current pixel and nearest neighbors
		                v1 = p1.getIntComponent0(x, y); //current pixel
		                v2 = p1.getIntComponent0(x+1, y); //to the right
		                v3 = p1.getIntComponent0(x, y+1); //down
		                v4 = p1.getIntComponent0(x+1, y+1); //down and to the right
		                
		                //System.out.println("v1 is " + v1 + "v2 is " + v2 + "v3 is " + v3 + "v4 is " + v4);
		                //System.out.println("v1 0 is " + p1.getIntComponent0(x, y) + "v1 1 is " + p1.getIntComponent1(x, y) + "v1 2 is " + p1.getIntComponent2(x, y));
		                //Currently reading as if from 3 255 color channels, resulting in max val of 16777216 instead of 255...
		                
		                //Establish median values between v1 and v2->v4
		                medianR = medianFromVals(v1,v2);
		                medianD = medianFromVals(v1,v3);
		                medianRD = medianFromVals(v1,v4);
		                
		                //Looks like we'll have to process by window here...
		                for(v=y+accOffsetY;v<(y+accOffsetY)+factor;v++){
		                	for(u=x+accOffsetX;u<(x+accOffsetX)+factor;u++){
		                		if(v == y+accOffsetY){
		                			child.setIntColor(u, v, medianR,medianR,medianR);
		                		}
		                		else if(v > y+accOffsetY && u == x+accOffsetX){
		                			child.setIntColor(u, v, medianD,medianD,medianD);
		                		}
		                		else if(v > y+accOffsetY && u > x+accOffsetX){
		                			child.setIntColor(u, v, medianRD,medianRD,medianRD);
		                		}
		                	}
		                }
		     
		                
		               accOffsetX+=(factor-1);
		            }
		            accOffsetX = 0;
		            accOffsetY+=(factor-1);
		          }
				
				//Finally, copy the second to last column into the last column, and the second to last row into the last row
				int lastColumn = child.getWidth()-1;
				int lastRow = child.getHeight()-1;
				for(int i=0;i<child.getHeight();i++){
					child.setIntColor(lastColumn, i, child.getIntColor(lastColumn-1,i));// child.getIntComponent0(lastColumn-1, i),child.getIntComponent1(lastColumn-1, i),child.getIntComponent2(lastColumn-1, i));
					//child.setIntColor(lastColumn, i, 255,255,0);
				}
				for(int i=0;i<child.getWidth();i++){
					child.setIntColor(i, lastRow, child.getIntColor(i, lastRow-1));
				}
		  
		    /*
				for (int i=0;i<windowsInImage;i++){
				
				  for (int y = 0; y < windowHeight; y++) { //for each row y in the window
				
		              for (int x = 0; x < windowWidth; x++) { //for each col x in row y in the window
		                
		              }//end col processing in a row in a window
		            
		          }//end row processing in a window
				} //end window processing
			*/
			      child.update();
			      return child;	
			}
			else{
				System.out.println("please choose the mag type you want");
				return null;
			}
	/*
		  }
	
		  else{
			  System.out.println("please ");
			  return null;
		  }
	*/
	
		}
	
	
	public MarvinImage reduce(MarvinImage p1, String reduxType, int factor){
		  //if(axis.equals("x") || axis.equals("y")){
			//MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
			int v1; 
			int window[];
			int width = p1.getWidth();
			int height = p1.getHeight();
			int u = 0;
			int v = 0;
			int reduxFactorX = (int)Math.ceil(width/factor);//(int)Math.ceil(width/4); //for 25% zoom, we'll take the ceiling of .25 * image size, and add the result as the replication/interpolation range at each pixel
			int reduxFactorY = (int)Math.ceil(height/factor);//(int)Math.ceil(height/4);
			
			if(reduxType.equals("Simple")){ 
				MarvinImage child = new MarvinImage(reduxFactorX,reduxFactorY);
				for (int y = 0; y < p1.getHeight(); y+=factor) {
		            for (int x = 0; x < p1.getWidth(); x+=factor) {
		                //Get parent value at current pixel
		                v1 = p1.getIntColor(x, y); 
		                
		                //newX = x;
		                //newY = (height-1)-y;
		                //child.setIntColor(x+(width-x), y+(height-x), v1);
		                if(u<=reduxFactorX-1 && v<= reduxFactorY-1){
		                	child.setIntColor(u, v, v1);
		                }
		                u++;
		            }
		            u = 0;
		            v++;
		          }
			      child.update();
			      System.out.println("child width and height are " + child.getWidth() + "," + child.getHeight());
			      return child;	
			}
			else if(reduxType.equals("Average")){ 
				MarvinImage child = new MarvinImage(reduxFactorX,reduxFactorY);
				int windowArea = factor * factor;
				int windowAcc = 0;
				int indexCounter = 0;
				
				window = new int[windowArea];
				for (int y = 0; y < p1.getHeight(); y+=factor) {
		            for (int x = 0; x < p1.getWidth(); x+=factor) {
		                //Get parent values in factor*factor window
		            	indexCounter = 0;
		            	for(int z = 0;z<factor;z++){
		            		for(int i=0;i<factor;i++){
		            			if(x+i<width && y+z<height){
		            			//System.out.println("ic is " + indexCounter + " and factor is " + factor);
		            				window[indexCounter] = p1.getIntComponent0(x+i, y+z);
		            				System.out.println("" + p1.getIntComponent0(x+i, y+z));
		            				indexCounter+=1;
		            				//System.out.println("ic is " + indexCounter + " and factor is " + factor);
		            			}
		            		}
		            	}
		            	
		            	//Average the values in window[] and place in window[0]
		            	windowAcc = 0;
		            	for(int i=0;i<window.length;i++){
		            		windowAcc += window[i];
		            	}
		            	System.out.println("windowAcc is " + windowAcc + " window.length is " + window.length + " and windowAcc/window.length is" + (windowAcc/window.length));
		            	window[0] = windowAcc/window.length;
		            	
		            	/*
		                v1 = p1.getIntColor(x, y); 
		                v2 = p1.getIntColor(x+1, y);
		                v3 = p1.getIntColor(x, y+1);
		                v4 = p1.getIntColor(x+1, y+1);
		                */
		                //newX = x;
		               // newY = (height-1)-y;
		                //child.setIntColor(x+(width-x), y+(height-x), v1);
		                if(u<=reduxFactorX-1 && v<= reduxFactorY-1){
		                	child.setIntColor(u, v, window[0],window[0],window[0]);
		                }
		                u++;
		                
		            }
		            u = 0;
		            v++;
		          }
			      child.update();
			      return child;	
			}
			else if(reduxType.equals("Median")){ 
				MarvinImage child = new MarvinImage(reduxFactorX,reduxFactorY);
				/*
				//FOR DEBUG
				int test[] = {5,3,4,2,7};
				//System.out.println("test has " + test.toString());
				bubbleSort(test);
				for(int i=0;i<test.length;i++){
				System.out.println(test[i]);
				}
				*/
			
				int windowArea = factor * factor;
				int windowAcc = 0;
				int indexCounter = 0;
				
				window = new int[windowArea];
				for (int y = 0; y < p1.getHeight(); y+=factor) {
		            for (int x = 0; x < p1.getWidth(); x+=factor) {
		                //Get parent values in factor*factor window
		            	indexCounter = 0;
		            	//First we need to obtain the int[]
		            	for(int z = 0;z<factor;z++){
		            		for(int i=0;i<factor;i++){
		            			if(x+i<width && y+z<height){
		            			//System.out.println("ic is " + indexCounter + " and factor is " + factor);
		            				window[indexCounter] = p1.getIntComponent0(x+i, y+z);
		            				System.out.println("" + p1.getIntComponent0(x+i, y+z));
		            				indexCounter+=1;
		            				//System.out.println("ic is " + indexCounter + " and factor is " + factor);
		            			}
		            		}
		            	}
		            	
		            	//Then we sort it (bubble sort will do for now)
		            	bubbleSort(window);
		            	//Finally we find the median value and place it in window[0]...
		            	window[0] = medianFromArray(window);
		       
		                //...And use it for writing to the child image 
		            	if(u<=reduxFactorX-1 && v<= reduxFactorY-1){
		                	child.setIntColor(u, v, window[0],window[0],window[0]);
		                }
		                u++;
		                
		            }
		            u = 0;
		            v++;
		          }
			      child.update();
			  
			      return child;	
			}
			else{
				System.out.println("please choose the reduction type you want");
				return null;
			}
	}
	
	
	
	//Below are the Color specific transforms
	
	/*
	 * Load the stock matrix 8x8 P3-8-8-7.ppm of integer values
	 */
	public void loadStockPPM(String fileName, int imageWidth, int imageHeight) throws IOException{
		//init the shared matrix vars
		stockMatrix = new int[imageHeight][imageWidth*3];
		
		//Prepare IO stream
		liaison.createRead(fileName);
		
		//Read in the data from the stock ppm
		stockMatrixStream = liaison.readInAndReturnInts(640);
		
		//Transform stream into matrix
		arrayIndexCounter = 0;
		for(int i=0;i<imageHeight;i++){ //for each row i
			for(int x=0;x<(imageWidth*3);x++){ //for each col x in row i
				stockMatrix[i][x] = stockMatrixStream[arrayIndexCounter];
				arrayIndexCounter++;
				System.out.println("stockMatrix row " + i + " col " + x + " is " + stockMatrix[i][x]);
			}
		}
		
		
		//Close streams
		liaison.closeInStream();
	}
	
	public void writeStockPPM(String fileName, int imageWidth, int imageHeight) throws IOException{
		//init IO stream
		liaison.createWrite(fileName);
		
		//write out stockMatrixStream values
		liaison.writeOutIntsForPPM(stockMatrixStream,8,8,7);
		
		//close streams
		liaison.closeOutStream();
	}
	
	/*
	 * Invert the color values of the stock matrix 8x8 P3-8-8-7.ppm of integer values
	 */
	public int[][] invertStockPPM(int imageWidth, int imageHeight){
		arrayIndexCounter = 0;
		stockMatrixStream = new int[(imageWidth*3)*imageHeight];
		for(int i=0;i<imageHeight;i++){ //for each row i
			for(int x=0;x<(imageWidth*3);x++){ //for each col x in row i
				stockMatrix[i][x] = (7 - stockMatrix[i][x]); //7 is the MAX_COLOR_VALUE for the 3-bit image
				System.out.println("~stockMatrix row " + i + " col " + x + " is " + stockMatrix[i][x]);
				stockMatrixStream[arrayIndexCounter] = stockMatrix[i][x];
				System.out.println("stream at " + arrayIndexCounter + " is " + stockMatrixStream[arrayIndexCounter]);
				arrayIndexCounter++;
				
			}
		}
		return stockMatrix;
	}
	
	/*
	 * Perform a simple addition op, wherein all color channel values at
	 * corresponding locations in the parent image matrices will simply be 
	 * added, with upper bound at MAX_COLOR_VALUE which is derived from the
	 * bitness of the image (defaults to 8-bit [so 256 color levels], but can be changed 
	 * by the user at any time) such that any sums greater than or equal to MAX_COLOR_VALUE
	 * will be set equal to MAX_COLOR_VALUE
	 */
	public MarvinImage additionOp_NoWrap(MarvinImage p1, MarvinImage p2, int maxColors){
		MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
		max_color_value = maxColors;
	  if((p1.getHeight()*p1.getWidth()) == (p2.getHeight()*p2.getWidth())){
		int r1,g1,b1,r2,g2,b2,r3,g3,b3; 
        for (int y = 0; y < p1.getHeight(); y++) {
            for (int x = 0; x < p1.getWidth(); x++) {
                //Get parent color values
                r1 = p1.getIntComponent0(x, y); 
                g1 = p1.getIntComponent1(x, y); 
                b1 = p1.getIntComponent2(x, y); 
                r2 = p2.getIntComponent0(x, y); 
                g2 = p2.getIntComponent1(x, y); 
                b2 = p2.getIntComponent2(x, y); 
                
                //Compute and set the child color values
                r3 = applyBounds((int)(r1+r2));
                g3 = applyBounds((int)(g1+g2));
                b3 = applyBounds((int)(b1+b2));
                child.setIntColor(x,y,r3,g3,b3);                 
            } 
        }
        
	  }
	  else{
		  System.out.println("ERROR: Images are not of equal dimensions");
		  if(hMain.getCouldron() != null){
			  
			  hMain.getCouldron().setError("ERROR: Images are not of equal dimensions");
		  }
		  
		  child = MarvinImageIO.loadImage("assets/err403.bmp");
		  
	  }
	  child.update();
	  return child;
	}
	
	/*
	 * Perform an addition op with wrapping, wherein all color channel values at
	 * corresponding locations in the parent image matrices will simply be 
	 * added, with upper bound at MAX_COLOR_VALUE which is derived from the
	 * bitness of the image (defaults to 8-bit [so 256 color levels], but can be changed 
	 * by the user at any time) such that any sums greater than or equal to the
	 * MAX_COLOR_VALUE will be set equal to (currentSum-MAX_COLOR_VALUE).  So if
	 * a sum is 300, it will be set to 45 effectively 'wrapping around' 
	 */
	public MarvinImage additionOp_Wrap(MarvinImage p1, MarvinImage p2, int maxColors){
		MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
		max_color_value = maxColors;
		if((p1.getHeight()*p1.getWidth()) == (p2.getHeight()*p2.getWidth())){
			int r1,g1,b1,r2,g2,b2,r3,g3,b3; 
	        for (int y = 0; y < p1.getHeight(); y++) {
	            for (int x = 0; x < p1.getWidth(); x++) {
	                //Get parent color values
	                r1 = p1.getIntComponent0(x, y); 
	                g1 = p1.getIntComponent1(x, y); 
	                b1 = p1.getIntComponent2(x, y); 
	                r2 = p2.getIntComponent0(x, y); 
	                g2 = p2.getIntComponent1(x, y); 
	                b2 = p2.getIntComponent2(x, y); 
	                
	                //Compute and set the child color values
	                r3 = applyWrap((int)(r1+r2));
	                g3 = applyWrap((int)(g1+g2));
	                b3 = applyWrap((int)(b1+b2));
	                child.setIntColor(x,y,r3,g3,b3);                 
	            } 
	        } 
		  }
		  else{
			  System.out.println("ERROR: Images are not of equal dimensions");
			  if(hMain.getCouldron() != null){
				  hMain.getCouldron().setError("ERROR: Images are not of equal dimensions");
			  }
			  child = MarvinImageIO.loadImage("assets/err403.bmp");
		  }
		child.update();
		return child;
	}
	
	/*
	 * This operation removes all values from channels besides the given
	 * color channel (r,g, or b) resulting in a hue saturation by the 
	 * given color.
	 */
	public MarvinImage saturationOp(MarvinImage p1, char color){
		MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
		int satChannel = 0;
		int satBiasLeft = 0;
		int satBiasRight = 0;
		
		if(color == 'r'){
			satChannel = 0;
			satBiasLeft = 1;
			satBiasRight = 2;
		}
		else if(color == 'g'){
			satChannel = 1;
			satBiasLeft = 0;
			satBiasRight = 2;
		}
		else if(color == 'b'){
			satChannel = 2;
			satBiasLeft = 0;
			satBiasRight = 1;
		}
	 
		int parentPixel[] = new int[3];
		int childPixel[] = new int[3];
		
        for (int y = 0; y < p1.getHeight(); y++) {
            for (int x = 0; x < p1.getWidth(); x++) {
                //Get parent color values
                parentPixel[0] = p1.getIntComponent0(x, y); 
                parentPixel[1] = p1.getIntComponent1(x, y); 
                parentPixel[2] = p1.getIntComponent2(x, y); 
                
                
                //Compute and set the child color values
                childPixel[satChannel] = parentPixel[satChannel];
                childPixel[satBiasRight] = 0;
                childPixel[satBiasLeft] = 0;
                child.setIntColor(x,y,childPixel[0],childPixel[1],childPixel[2]);                 
            } 
        }
        
	  
	  child.update();
	  return child;
	}
	
	/*
	 * Perform a random 'juxtapose' operation, wherein the RGB values of an image
	 * are randomly shuffled for each pixel  
	 */
	public MarvinImage juxtOp_Rand(MarvinImage p1){
		//if((p1.getHeight()*p1.getWidth() == 65536)){
			MarvinImage child = new MarvinImage(p1.getWidth(),p1.getHeight());
			int r1,g1,b1,r2,g2,b2,chao; 
	        for (int y = 0; y < p1.getHeight(); y++) {
	            for (int x = 0; x < p1.getWidth(); x++) {
	                //Get parent color values at current pixel
	                r1 = p1.getIntComponent0(x, y); 
	                g1 = p1.getIntComponent1(x, y); 
	                b1 = p1.getIntComponent2(x, y); 
	                
	                
	                //Compute and set the child color values at current pixel
	                chao = (int)(10*Math.random()); //should mean chao will be 0 through 9 after this
	                switch (chao){
	                case 0: r2 = r1; g2=g1; b2=b1; break; //in case 0, RGB
	                case 1: r2 = g1; g2=b1; b2=r1; break; //in case 1, GBR
	                case 2: r2 = g1; g2=r1; b2=b1; break; //in case 2, GRB
	                case 3: r2 = b1; g2=r1; b2=g1; break; //in case 3, BRG
	                case 4: r2 = b1; g2=g1; b2=r1; break; //in case 4, BGR
	                case 5: r2 = r1; g2=b1; b2=g1; break; //in case 5, RBG
	                case 6: r2 = r1; g2=g1; b2=b1; break; //in case 6, RGB
	                case 7: r2 = g1; g2=b1; b2=r1; break; //in case 7, GBR
	                case 8: r2 = g1; g2=r1; b2=b1; break; //in case 8, GRB
	                case 9: r2 = b1; g2=g1; b2=r1; break; //in case 9, BGR
	                default: r2 = r1; g2=g1; b2=b1; System.out.println("shouldn't have reached this default..."); break; //in of default, no juxtaposition and give an error message 
	                }
	                
	                
	                child.setIntColor(x,y,r2,g2,b2);                 
	            } 
	        }
		  child.update();
		  return child;
		//}
		//else{
			//System.out.println("image in parent 1 is not 256x256; please ensure the image is 256x256 and try again");
			//return null;
		//}
		  
	}
	
	/*
	 * Perform a random 'tile' operation, wherein the pixels of an image
	 * are grouped by neighbor in a user-specified window size,
	 * and then the groups are randomly shuffled in the image.  Result should
	 * look like a sliding-piece jigsaw puzzle.  The image size % number of tiles must be 0... 
	 * Also, for now we are requiring that the image be a square
	 */
	/*TODO....
	public MarvinImage tileOp_Rand(MarvinImage p1, int numTiles){
		int imageHeight = p1.getHeight();
		int imageWidth = p1.getWidth();
		int imageSize = imageWidth*imageHeight;
		int i = 0;
		MarvinImage child = new MarvinImage(imageWidth,imageHeight);
		
		if(imageSize%numTiles==0 && imageWidth == imageHeight){
			//MarvinImage tiles[] = new MarvinImage[imageSize/numTiles];
			int pixels[][] = 
			int reds[] = new int[imageSize];
			int greens[] = new int[imageSize];
			int blues[] = new int[imageSize];
			
			for(i=0;i<numTiles;i++){
				tiles[i] = new MarvinImage(imageWidth/numTiles,imageHeight/numTiles);
			}
			//TODO: implement the tile op
			child = tiles[0];
			child.update();
			return child;
		}
		else{
			System.out.println("Please make sure the image size is evenly divisible by the desired tile count, and that the image has equal height and width");
			child = MarvinImageIO.loadImage("assets/err403.bmp");
			return null;
		}
		
		
		  
	}
	*/
	/*
	 * Check for value greater than MAX_COLOR_VALUE or less than 0.  If found,
	 * return either MAX_COLOR_VALUE or 0, depending on which bound was crossed
	 */
	public int applyBounds(int myInt){
		if (myInt > max_color_value){
			myInt = max_color_value;
		}
		else if(myInt < 0){
			myInt = 0;
		}
		return myInt;
	}
	
	/*
	 * Check for value greater than MAX_COLOR_VALUE or less than 0.  If found,
	 * return either myInt-MAX_COLOR_VALUE or myInt+MAX_COLOR_VALUE, depending on which bound was crossed
	 */
	public int applyWrap(int myInt){
		if (myInt > max_color_value){
			myInt -= max_color_value;
		}
		else if(myInt < 0){
			myInt += max_color_value;
		}
		return myInt;
	}
	
	/*
	 * Convert 1D byte[] to 2D int[][] matrix
	 */
	public int[][] convertByteStreamToIntMatrix(byte[] bytes, int width, int height){
		
		return null;
	}
	
	/*
	 * Convert 1D int[] to 2D int[][] matrix
	 */
	public int[][] convertIntStreamToIntMatrix(int[] ints, int width, int height){
		
		return null;
	}
		/*
	    * Convert back and forth between buffered image and 2D int[][] matrix
	    */
	   public static int[][] getImageMatrix(BufferedImage img){
		   int height = img.getHeight();
		   int width = img.getWidth();
		   int imageMatrix[][] = new int[height][width];
		   WritableRaster wr = img.getRaster();
		   
		   for(int y = 0; y < height; y++)
			   for(int x = 0; x < width; x++)
				   imageMatrix[y][x] = (wr.getSample(x,y,0)   +wr.getSample(x,y,1) +wr.getSample(x,y,2))/2;
		
		   return imageMatrix;
	   }
	   
	   public static BufferedImage getImageFromMatrix(int[][] matrix){
		   int[] pixel = new int[3];
		   int height = matrix.length;
		   int width = matrix[0].length;
		   BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//BufferedImage.TYPE_BYTE_GRAY);
		   WritableRaster wr = img.getRaster();
		   
		   for(int y = 0; y < height; y++)
			   for(int x = 0; x< width; x++){
				   
				   for(int i =0; i<3; i++) pixel[0] = matrix[y][x];
				   wr.setPixel(x,y,pixel);
			   }

		   return img;
	   }
	   
	   public void bubbleSort(int[] arrayToSort){
		 //Bubble sort the pixels
		    int currentMaxIndex = arrayToSort.length-1;
		    int temp;
		    int x;
		   // int sorted[] = new int[arrayToSort.length];
		    
		    while(currentMaxIndex > 0){
		    for (x=0; x<currentMaxIndex;x++){
		    	
		    	if(arrayToSort[x] > arrayToSort[x+1]){
		    		temp = arrayToSort[x];
		    		arrayToSort[x] = arrayToSort[x+1]; // .set(x, mulOpArray.get(x+1)); //  [x] = uniqueGraysArray[x+1];
		    		arrayToSort[x+1] = temp; //  .set(x+1, temp); //  [x+1] = temp;
		    	}
		    	
		    	
	    		//System.out.println("mulOpArray index " + x + " is " + mulOpArray[x] + " and sortedPixels index " + x + " is " + sortedPixels[x]);
	    	}
		    currentMaxIndex--;
		    }
		    
	   }
	   
	   public int medianFromArray(int[] m) {
		    int middle = m.length/2;  // subscript of middle element
		   // System.out.println("middle is " + middle);
		    if (m.length%2 == 1) {
		        // Odd number of elements -- return the middle one.
		        return m[middle];
		    } else {
		       // Even number -- return average of middle two
		       // Must cast the numbers to double before dividing.
		       return (m[middle-1] + m[middle]) / 2;
		    }
		}//end method median
	   
	   public int medianFromVals(int v1, int v2) {
		   //swap the values such that v2 holds higher and v1 hold lesser value
		   if(v2>=v1){
			   //nothing to do
		   }
		   else{
			   int temp = v2;
			   v2 = v1;
			   v1 = temp;
		   }
		   
		   //Now proceed to lay out an array of integers between v1 and v2
		   int m[] = new int[(v2-v1)+1];
		   m[0] = v1;
		   for(int i =1;i<v2-v1;i++){
			   m[i] = v1+i;
			  // System.out.println("array at " + i + " is " + m[i]);
		   }
		   m[m.length-1] = v2;
		   
		   /*
		   for(int i=0;i<m.length;i++){
			   System.out.println("array at " + i + " is " + m[i]);
		   }
		   */
		   
		   //Now we find the median value from the array we just created
		    int middle = m.length/2;  // subscript of middle element
		   // System.out.println("middle is " + middle);
		    if (m.length%2 == 1) {
		        // Odd number of elements -- return the middle one.
		    	//System.out.println("m[middle] is " + m[middle]);
		        return m[middle];
		    } else {
		       // Even number -- return average of middle two
		       // Must cast the numbers to double before dividing.
		    	//System.out.println("(m[middle-1] + m[middle]) / 2 is " + ((m[middle-1] + m[middle]) / 2));
		       return (m[middle-1] + m[middle]) / 2;
		    }
		}//end method median
	   
	   /*
	   public static BufferedImage getAnyImageFromMatrix(int[][] matrix, int bits){
		   int[] pixel = new int[3];
		   int height = matrix.length;
		   int width = matrix[0].length;
		   
		   BufferedImage img = new BufferedImage(new IndexColorModel(3,192,null,null,null),Raster.createWritableRaster(arg0, arg1, arg2));//BufferedImage.TYPE_BYTE_GRAY);
		   WritableRaster wr = img.getRaster();
		   
		   for(int y = 0; y < height; y++)
			   for(int x = 0; x< width; x++){
				   
				   for(int i =0; i<3; i++) pixel[0] = matrix[y][x];
				   wr.setPixel(x,y,pixel);
			   }

		   return img;
	   }
	   */
}
