package com.ai.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/*
 * This class contains the methods necessary for communicating with the
 * local file system to read in and write out files
 */
import java.io.StreamTokenizer;

import com.ai.core.ColorCauldronMain;;

public class FSliaison {
	private ColorCauldronMain hMain; //handle to the root
	private InputStream is; //reads in bytes
	private FileOutputStream fos; //writes out bytes to a file
	private FileReader fIN; //reads in chars
	private FileWriter fOUT; //writes out chars
	private String read = ""; //char string read in via fIN OR readInBytesAndReturnString()
	private StreamTokenizer st;
	private char[] character = null; //char array to hold read in chars
	private byte[] bytes = null; //byte array to hold read in bytes
	private int[] ints = null; //int array to hold read in ints
	
	/*
	 * Constructor; no arguments taken here
	 */
	public FSliaison(ColorCauldronMain mm){
		hMain = mm;
	}
	
	public byte[] getBytes(){
		return bytes;
	}
	
	public FileReader getFIN(){
		return fIN;
	}
	
	/*
	 * Creates a FileReader object which will read from the 
	 * source file specified in the fileIn argument
	 */
	public FileReader createRead(String fileIn) throws FileNotFoundException{
		fIN = new FileReader(fileIn);
		return fIN;
	}
	
	/*
	 * Creates a new inputstream for raw bytes which reads from
	 * the fileIn argument
	 */
	public void createReadBytes(String fileIn) throws FileNotFoundException{
		is = new FileInputStream(fileIn);
	}
	
	/*
	 * Creates a FileWriter object which will write to the file specified
	 * in the fileOut argument
	 */
	public FileWriter createWrite(String fileOut) throws IOException{
		fOUT = new FileWriter(fileOut);
		return fOUT;
	}
	
	/*
	 * init the outputstream for byte writing
	 */
	public void createWriteBytes(String fileOut) throws IOException{
		fos = new FileOutputStream(fileOut);
	}
	
	/*
	 * reads in an array of characters from the file in input stream fIN
	 * the char array has length equal to the given arrayLength argument
	 * once the char array is established, it is converted to be the
	 * content of a String object, and is returned
	 */
	public String readIn(int arrayLength) throws IOException{
		character = new char[arrayLength];
		read = "";
		int clength = fIN.read(character);
		for (int i=0;i<clength;i++){
			read = read + character[i];
			//System.out.println(clength); //enable to see the number of chars read in
		}
		character = null;
		return read;
	}
	
	/*
	 * reads in an array of characters from the file in input stream fIN
	 * the char array has length equal to the given arrayLength argument
	 * once the char array is established, it is converted to be an
	 * int[], which is returned
	 */
	public int[] readInAndReturnInts(int arrayLength) throws IOException{
		character = new char[arrayLength];
		ints = new int[arrayLength];
		//read = "";
		int arrayIndexCounter = 0;
		int clength = fIN.read(character);
		System.out.println(clength);
		for (int i=0;i<clength;i++){
			//if (character[i] != 13){
				//if(i<clength-2){
					//read = read + character[i];
					//System.out.println("" + character[i]);
					//ints[i] = Integer.parseInt(read.substring(i, i+1));
				//}
				//else{
				if(character[i] >= 48 && character[i] <= 57){
					
					
					//read = read + character[i];
					
					System.out.println("in fsliaison, charcter at " + i + " is " + character[i]);
					//if(!read.substring(i).equals("")){
						ints[arrayIndexCounter] = Integer.parseInt("" + character[i]);//read.substring(i));
						System.out.println("ints at " + arrayIndexCounter + " is " + ints[arrayIndexCounter]);
						arrayIndexCounter++;
						
					//}
				}
				//}
			//}
			//else{
				//Do nothing; this is a carriage return
			//}
			//System.out.println(clength); //enable to see the number of chars read in
		}
		character = null;
		return ints;
	}
	
	/*
	 * reads in an array of characters from the file in input stream fIN
	 * the char array has length equal to the given arrayLength argument
	 * once the char array is established, it is converted to be an
	 * int[], which is returned.  this routine allows for negative values,
	 * and expects disparate values to be comma-delimited
	 */
	public char[] readInAndReturnNegInts(int arrayLength) throws IOException{
		character = new char[arrayLength];
		ints = new int[arrayLength];
		read = "";
		int clength = fIN.read(character);
		for (int i=0;i<clength;i++){
			read = read + character[i];
			///ints[i] = Integer.parseInt(read.substring(i));	
		}
		///character = null;
		///return ints;
		return character;
	}
	
	/*
	 * Read in bytes from a file and store in byte[] bytes, then return bytes
	 */
	public byte[] readInAndReturnBytes(int arrayLength) throws IOException{
		bytes = new byte[arrayLength];
		is.read(bytes);
		return bytes;
	}
	
	/*
	 * Read in bytes from a file and store in byte[] bytes
	 */
	public void readInBytes(int arrayLength) throws IOException{
		bytes = new byte[arrayLength];
		is.read(bytes);
		
	}
	
	/*
	 * Read in bytes from a file and store in byte[] bytes, then return bytes converted to a string
	 */
	public String readInBytesAndReturnString(int arrayLength) throws IOException{
		bytes = new byte[arrayLength];
		is.read(bytes);
		//int j = 0;
		for (int i = 0; i<bytes.length; i++){
			/*
			if(j==255){
				read = read + "\n";
				j = 0;
			}
			else{
				j++;
			}
			*/
			read = read + bytes[i];
			//System.out.println("read string is: " + read);
			//j++;
		}
		System.out.println("read string is: " + read);
		return read;
	}
	
	
	/*
	 * Writes out the contents of the given String to the file
	 * in output stream fOUT
	 */
	public void writeOut(String str) throws IOException{
		fOUT.write(str);
		
	}
	
	/*
	 * Writes out the contents of the given int[] to the file
	 * in output stream fOUT
	 */
	public void writeOutInts(int[] ints) throws IOException{
		for(int i=0;i<ints.length;i++){
			fOUT.write(""+ints[i]);
		}
		
	}
	
	/*
	 * Writes out the contents of the given int[] to the file
	 * in output stream fOUT, formatted for PPM
	 */
	public void writeOutIntsForPPM(int[] ints, int width, int height, int colorDepth) throws IOException{
		fOUT.write("P3\n"+width+" "+height+"\n"+colorDepth+"\n"); //write header here first
		for(int i=0;i<ints.length;i++){
			if((i+1)%3 != 0){
				fOUT.write(""+ints[i]+" ");
			}
			else if((i+1)%3 == 0 && (i+1)%24 != 0){
				fOUT.write(""+ints[i]+"  ");
			}
			else if((i+1)%24 == 0){
				fOUT.write(""+ints[i]+"\n");
			}
		}
		
	}
	
	/*
	 * write out the given byte[] to a file
	 */
	public void writeOutBytes(byte[] byteArg) throws IOException{
		
		fos.write(byteArg);
	}
	
	/*
	 * flushes all output streams
	 */
	public void flushStreams() throws IOException{
		
		if (fOUT != null){
			fOUT.flush();
		}
	}
	
	/*
	 * close streams individually
	 */
	public void closeInStream() throws IOException{
		if (fIN != null){
			fIN.close();
		}
	}
	public void closeOutStream() throws IOException{
		if (fOUT != null){
			fOUT.close();
		}
	}
	public void closeOutStreamBytes() throws IOException{
		if (fos != null){
			fos.close();
		}
	}
	public void closeInStreamBytes() throws IOException{
		if (is != null){
			is.close();
		}
	}
	
	/*
	 * closes all streams together
	 */
	public void closeStreams() throws IOException{
		if (fIN != null){
			fIN.close();
		}
		if (fOUT != null){
			fOUT.close();
		}
		if (is != null){
			is.close();
		}
		if (fos != null){
			fos.close();
		}
	}
	
	/*
	 * closes streams and severs reference to stream objects such
	 * that they will be eligible for GC
	 */
	public void closure() throws IOException{
		if (fIN != null){
		  fIN.close();
		  fIN = null;
		}
		if (fOUT != null){
		  fOUT.close();
		  fOUT = null;
		}
	}
	
	
	//Read in bytes example
	/*
	  // Returns the contents of the file in a byte array.
public static byte[] getBytesFromFile(File file) throws IOException {
    InputStream is = new FileInputStream(file);

    // Get the size of the file
    long length = file.length();

    // You cannot create an array using a long type.
    // It needs to be an int type.
    // Before converting to an int type, check
    // to ensure that file is not larger than Integer.MAX_VALUE.
    if (length > Integer.MAX_VALUE) {
        // File is too large
    }

    // Create the byte array to hold the data
    byte[] bytes = new byte[(int)length];

    // Read in the bytes
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length
           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
        offset += numRead;
    }

    // Ensure all the bytes have been read in
    if (offset < bytes.length) {
        throw new IOException("Could not completely read file "+file.getName());
    }

    // Close the input stream and return bytes
    is.close();
    return bytes;
}
	 */
	

}
