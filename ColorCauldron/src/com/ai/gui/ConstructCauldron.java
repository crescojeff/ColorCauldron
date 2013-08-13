package com.ai.gui;

import com.ai.core.ColorCauldronMain;


import java.awt.BorderLayout; 
import java.awt.Container; 
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton; 
import javax.swing.JComboBox;
import javax.swing.JFrame; 
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalBorders;

import marvin.gui.*;
import marvin.image.MarvinImage; 
import marvin.io.MarvinImageIO;
import marvin.image.MarvinImageMask; 
import marvin.plugin.MarvinImagePlugin; 
import marvin.util.MarvinPluginLoader;

public class ConstructCauldron extends JFrame implements ActionListener, DropTargetListener{
	private ColorCauldronMain hMain;
	private int saveCount = 0;
	private GridBagLayout gridbag; 
    private GridBagConstraints c;
    private Insets insets = new Insets(0,0,0,0);
    
	private int window_width;
	private int window_height;
	private DropTarget dt1;
	private DropTarget dt2;
	private JPanel		panelBottom; 
    
    
    private JButton             buttonGray,  
                                buttonEdgeDetector,  
                                buttonInvert,  
                                buttonReset,
                                buttonJuxt,  
                                buttonAddNW,  
                                buttonAddW,
                                buttonTile,
                                buttonFlip,
                                buttonMag,
                                buttonRedux,
                                buttonSave,
    							buttonRotate;
    private JTextArea debugTA;
    private JComboBox hueChoicesCB, rotChoicesCB, flipChoicesCB, magChoicesCB, magFactorsCB, reduxChoicesCB, reduxFactorsCB;
    private JLabel parent1L,parent2L,childL;
    
    private MarvinImagePanel	imagePanelParent1; 
    private MarvinImagePanel	imagePanelParent2;
    private MarvinImagePanel	imagePanelChild;
    private MarvinImage		image,  
    						backupImage;
    private MarvinImagePlugin     imagePlugin;
	
	public ConstructCauldron(ColorCauldronMain main, String UI, MarvinImage mi){
		super("Welcome to the Color Cauldron!");
		hMain = main;
		if(UI.equals("givenImage")){
			buildUIGivenImage(mi);
		}
		else if(UI.equals("colorcauldron")){
			System.out.println("building UI cauldron");
			buildUICauldron();
		}
		else if(UI.equals("grayOps")){
			buildUIGrayOps();
		}
		else{ //(UI.equals("basic")){
			buildUIBasic();
		}
		
		
	}
	
	public void setError(String s){
		if(debugTA != null){
			//System.out.println("got here");
			debugTA.setText(s);  //For some reason text doesn't get updated from this method... not sure why yet
			
		}
	}
	
	public void buildUIBasic(){
		// Create Graphical Interface 
		window_width = 1000;
		window_height = 1000;
		
		//Define Window specific parameters
		setSize(window_width,window_height);
		
		//Define Layout
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
		
		//Create Content Container
        Container l_c = getContentPane(); 
		l_c.setLayout(gridbag);
        
        buttonGray = new JButton("Gray");
        buttonGray.addActionListener(this); 
        buttonEdgeDetector = new JButton("EdgeDetector");
        buttonEdgeDetector.addActionListener(this); 
        buttonInvert = new JButton("Invert");
        buttonInvert.addActionListener(this); 
        buttonReset = new JButton("Reset");
        buttonReset.addActionListener(this); 
        debugTA = new JTextArea();
        parent1L = new JLabel();
        parent2L = new JLabel();
        childL = new JLabel();
         
        panelBottom = new JPanel(); 
        panelBottom.add(buttonGray); 
        panelBottom.add(buttonEdgeDetector); 
        panelBottom.add(buttonInvert); 
        panelBottom.add(buttonReset); 
        panelBottom.setLocation(150, 800);
        
        
        // ImagePanel 
        imagePanelParent1 = new MarvinImagePanel();
        imagePanelParent2 = new MarvinImagePanel();
        imagePanelChild = new MarvinImagePanel();
        
        
        
        // Load image
    	//image = new MarvinImage(new BufferedImage())//MarvinImageIO.loadImage("assets/butterfly.ppm");
        image = MarvinImageIO.loadImage("assets/color/caffeinated_owl_256.jpg");
        backupImage = image.clone();
        imagePanelParent1.setBorder(MetalBorders.getDesktopIconBorder());
        imagePanelParent1.setLocation(0,0);
        imagePanelParent1.setImage(image); 
        imagePanelParent2.setBorder(MetalBorders.getDesktopIconBorder());
        imagePanelParent2.setLocation(300,0);
        imagePanelParent2.setImage(image); 
        imagePanelChild.setBorder(MetalBorders.getDesktopIconBorder());
        imagePanelChild.setLocation(100,300);
        imagePanelChild.setImage(image); 
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 4;
        
        l_c.add(panelBottom,c);
        
        
        
        
        parent1L.setText("PARENT 1");
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 256;
        c.ipady = 0;
       // insets.set(0, 0, 10, 256);
        //c.insets = insets;
        l_c.add(parent1L,c);
        parent2L.setText("PARENT 2");
        c.gridx = 2;
        c.gridy = 0;
        c.ipadx = 256;
        c.ipady = 0;
        //insets.set(0, 0, 10, 0);
       // c.insets = insets;
        l_c.add(parent2L,c);
        imagePanelParent1.setBorder(MetalBorders.getDesktopIconBorder());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 0, 10, 256);
        //c.insets = insets;
        l_c.add(imagePanelParent1,c);
        //imagePanelParent1.setImage(image); 
        imagePanelParent2.setBorder(MetalBorders.getDesktopIconBorder());
        //imagePanelParent2.setSize(256, 256);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 0, 10, 0);
        //c.insets = insets;
        l_c.add(imagePanelParent2,c);
        //imagePanelParent2.setImage(image); 
        childL.setText("CHILD");
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 256;
        c.ipady = 0;
        //insets.set(10, 256, 0, 0);
        //c.insets = insets;
        l_c.add(childL,c);
        imagePanelChild.setBorder(MetalBorders.getDesktopIconBorder());
       // imagePanelChild.setSize(256, 256);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(10, 256, 0, 0);
        //c.insets = insets;
       // c.gridwidth = 2;
        l_c.add(imagePanelChild,c);
        //imagePanelChild.setImage(image); 
        
        
        //Create a drop target on imagePanel
        dt1 = new DropTarget(imagePanelParent1,this);
        dt2 = new DropTarget(imagePanelParent2,this);
         
       
        setVisible(true);   
        imagePanelParent1.setLocation(0,0);
       // System.out.println("image panel 1 location is " + imagePanelParent1.getLocation().x + "," + imagePanelParent1.getLocation().y);
        //render();
	}
	
	public void buildUICauldron(){
		char[] hueChars = {'r','g','b'};
		
		//Define Layout
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
		
		// Create Graphical Interface 
		window_width = 1100;
		window_height = 1000;
		
		//Define Window specific parameters
		setSize(window_width,window_height);
		
		//Create Content Container
        Container l_c = getContentPane(); 
        l_c.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
		
		//Define widgets
        buttonJuxt = new JButton("JuxtOp");
        buttonJuxt.addActionListener(this); 
        buttonAddNW = new JButton("ShadeStack_NoWrap");
        buttonAddNW.addActionListener(this);
        buttonAddW = new JButton("ShadeStack_Wrap");
        buttonAddW.addActionListener(this); 
        buttonTile = new JButton("Hue_Saturation");
        buttonTile.addActionListener(this); 
        buttonInvert = new JButton("Invert");
        buttonInvert.addActionListener(this); 
        buttonReset = new JButton("Reset");
        buttonReset.addActionListener(this); 
        debugTA = new JTextArea();
        debugTA.setEditable(false);
        parent1L = new JLabel();
        parent2L = new JLabel();
        childL = new JLabel();
        hueChoicesCB = new JComboBox();
        for(int i=0;i<hueChars.length;i++){
        	hueChoicesCB.addItem(hueChars[i]);
        }
         
        panelBottom = new JPanel(); 
        panelBottom.add(buttonJuxt); 
        panelBottom.add(buttonAddNW);
        panelBottom.add(buttonAddW);
        panelBottom.add(buttonTile);
        panelBottom.add(hueChoicesCB);
        panelBottom.add(buttonInvert); 
        panelBottom.add(buttonReset); 
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 4;
        
        l_c.add(panelBottom,c);
        
        
        // ImagePanel 
        imagePanelParent1 = new MarvinImagePanel();
        imagePanelParent2 = new MarvinImagePanel();
        imagePanelChild = new MarvinImagePanel();
        
       
        
        // Load image panels
        parent1L.setText("PARENT 1");
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 256;
        c.ipady = 0;
       // insets.set(0, 0, 10, 256);
        //c.insets = insets;
        l_c.add(parent1L,c);
        parent2L.setText("PARENT 2");
        c.gridx = 2;
        c.gridy = 0;
        c.ipadx = 256;
        c.ipady = 0;
        //insets.set(0, 0, 10, 0);
       // c.insets = insets;
        l_c.add(parent2L,c);
        imagePanelParent1.setBorder(MetalBorders.getDesktopIconBorder());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 300, 0, 0);
        //c.insets = insets;
        l_c.add(imagePanelParent1,c);
        //imagePanelParent1.setImage(image); 
        imagePanelParent2.setBorder(MetalBorders.getDesktopIconBorder());
        //imagePanelParent2.setSize(256, 256);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 0, 0, 0);
       // c.insets = insets;
        l_c.add(imagePanelParent2,c);
        //imagePanelParent2.setImage(image); 
        childL.setText("CHILD");
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 50;
        c.ipady = 0;
       // insets.set(0,0, 0, 0);
        //c.insets = insets;
        l_c.add(childL,c);
        
        debugTA.setText("DEBUG AREA");
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 200;
        c.ipady = 0;
        insets.set(0, 55, 0, 0);
        c.insets = insets;
        l_c.add(debugTA,c);
        insets.set(0, 0, 0, 0); //reset the insets for any components following this one
        c.insets = insets; //reset the insets for any components following this one
        
        imagePanelChild.setBorder(MetalBorders.getDesktopIconBorder());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 0, 0, 100);
        //c.insets = insets;
       // c.gridwidth = 2;
        l_c.add(imagePanelChild,c);
   
        //Create a drop target on imagePanel
        dt1 = new DropTarget(imagePanelParent1,this);
        dt2 = new DropTarget(imagePanelParent2,this);
         
       
        setVisible(true);   
       
       // System.out.println("image panel 1 location is " + imagePanelParent1.getLocation().x + "," + imagePanelParent1.getLocation().y);
        //render();
	}
	
	public void buildUIGrayOps(){
		
		//Define Layout
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
		
		// Create Graphical Interface 
		window_width = 1100;
		window_height = 1000;
		
		//Define Window specific parameters
		setSize(window_width,window_height);
		
		//Create Content Container
        Container l_c = getContentPane(); 
        l_c.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
		
		//Define widgets
        buttonRotate = new JButton("Rotate");
        buttonRotate.addActionListener(this); 
        rotChoicesCB = new JComboBox();
        flipChoicesCB = new JComboBox();
        magChoicesCB = new JComboBox();
        magFactorsCB = new JComboBox();
        reduxChoicesCB = new JComboBox();
        reduxFactorsCB = new JComboBox();
        buttonFlip = new JButton("Flip");
        buttonFlip.addActionListener(this);
        buttonMag = new JButton("Magnify");
        buttonMag.addActionListener(this);  
        buttonRedux = new JButton("Reduce");
        buttonRedux.addActionListener(this); 
        buttonSave = new JButton("Save");
        buttonSave.addActionListener(this);
        for(int i=0;i<3;i++){
        	switch(i){
        	case 0: rotChoicesCB.addItem("90");flipChoicesCB.addItem("x axis");magChoicesCB.addItem("Replication");magFactorsCB.addItem("2");reduxFactorsCB.addItem("2");reduxChoicesCB.addItem("Simple");break;
        	case 1: rotChoicesCB.addItem("180");flipChoicesCB.addItem("y axis");magChoicesCB.addItem("Interpolation");magFactorsCB.addItem("3");reduxFactorsCB.addItem("3");reduxChoicesCB.addItem("Average");break;
        	case 2: rotChoicesCB.addItem("270");reduxChoicesCB.addItem("Median");magFactorsCB.addItem("4");reduxFactorsCB.addItem("4");break;
        	}
        	
        }
        
        debugTA = new JTextArea();
        debugTA.setEditable(false);
        parent1L = new JLabel();
        parent2L = new JLabel();
        childL = new JLabel();
        
         
        panelBottom = new JPanel(); 
        panelBottom.add(buttonRotate);
        panelBottom.add(rotChoicesCB);
        panelBottom.add(buttonFlip);
        panelBottom.add(flipChoicesCB);
        panelBottom.add(buttonMag);
        panelBottom.add(magChoicesCB);
        panelBottom.add(magFactorsCB);
        panelBottom.add(buttonRedux);
        panelBottom.add(reduxChoicesCB);
        panelBottom.add(reduxFactorsCB);
        panelBottom.add(buttonSave);
   
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 4;
        
        l_c.add(panelBottom,c);
        
        
        // ImagePanel 
        imagePanelParent1 = new MarvinImagePanel();
        imagePanelParent2 = new MarvinImagePanel();
        imagePanelChild = new MarvinImagePanel();
        
       
        
        // Load image panels
        parent1L.setText("PARENT 1");
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 256;
        c.ipady = 0;
       // insets.set(0, 0, 10, 256);
        //c.insets = insets;
        l_c.add(parent1L,c);
        parent2L.setText("PARENT 2");
        c.gridx = 2;
        c.gridy = 0;
        c.ipadx = 256;
        c.ipady = 0;
        //insets.set(0, 0, 10, 0);
       // c.insets = insets;
        l_c.add(parent2L,c);
        imagePanelParent1.setBorder(MetalBorders.getDesktopIconBorder());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 300, 0, 0);
        //c.insets = insets;
        l_c.add(imagePanelParent1,c);
        //imagePanelParent1.setImage(image); 
        imagePanelParent2.setBorder(MetalBorders.getDesktopIconBorder());
        //imagePanelParent2.setSize(256, 256);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 0, 0, 0);
       // c.insets = insets;
        l_c.add(imagePanelParent2,c);
        //imagePanelParent2.setImage(image); 
        childL.setText("CHILD");
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 50;
        c.ipady = 0;
       // insets.set(0,0, 0, 0);
        //c.insets = insets;
        l_c.add(childL,c);
        
        debugTA.setText("DEBUG AREA");
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 200;
        c.ipady = 0;
        insets.set(0, 55, 0, 0);
        c.insets = insets;
        l_c.add(debugTA,c);
        insets.set(0, 0, 0, 0); //reset the insets for any components following this one
        c.insets = insets; //reset the insets for any components following this one
        
        imagePanelChild.setBorder(MetalBorders.getDesktopIconBorder());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 256;
        c.ipady = 256;
        //insets.set(0, 0, 0, 100);
        //c.insets = insets;
       // c.gridwidth = 2;
        l_c.add(imagePanelChild,c);
   
        //Create a drop target on imagePanel
        dt1 = new DropTarget(imagePanelParent1,this);
        dt2 = new DropTarget(imagePanelParent2,this);
         
       
        setVisible(true);   
       
       // System.out.println("image panel 1 location is " + imagePanelParent1.getLocation().x + "," + imagePanelParent1.getLocation().y);
        //render();
	}
	
	public void buildUIGivenImage(MarvinImage m){
		// Create Graphical Interface 
		window_width = 1000;
		window_height = 1000;
		
		//Define Window specific parameters
		setSize(window_width,window_height);
		
        buttonGray = new JButton("Gray");
        buttonGray.addActionListener(this); 
        buttonEdgeDetector = new JButton("EdgeDetector");
        buttonEdgeDetector.addActionListener(this); 
        buttonInvert = new JButton("Invert");
        buttonInvert.addActionListener(this); 
        buttonReset = new JButton("Reset");
        buttonReset.addActionListener(this); 
         
        panelBottom = new JPanel(); 
        panelBottom.add(buttonGray); 
        panelBottom.add(buttonEdgeDetector); 
        panelBottom.add(buttonInvert); 
        panelBottom.add(buttonReset); 
        panelBottom.setLocation(150, 800);
        
        
        // ImagePanel 
        imagePanelParent1 = new MarvinImagePanel();
        imagePanelParent2 = new MarvinImagePanel();
        imagePanelChild = new MarvinImagePanel();
        
        
        // Load image
    	//image = new MarvinImage(new BufferedImage())//MarvinImageIO.loadImage("assets/butterfly.ppm");
        image = m;//MarvinImageIO.loadImage("assets/caffeinated_owl_256.jpg");
        image.resize(256, 256);
        backupImage = image.clone();
        imagePanelParent1.setBorder(MetalBorders.getDesktopIconBorder());
        imagePanelParent1.setLocation(0,0);
        imagePanelParent1.setImage(image); 
        imagePanelParent2.setBorder(MetalBorders.getDesktopIconBorder());
        imagePanelParent2.setLocation(300,0);
        imagePanelParent2.setImage(image); 
        imagePanelChild.setBorder(MetalBorders.getDesktopIconBorder());
        imagePanelChild.setLocation(100,300);
        imagePanelChild.setImage(image); 
        
        //Define Layout
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        
     
        
        //Create Content Container
        Container l_c = getContentPane(); 
       // l_c.setLayout(new BorderLayout()); 
        l_c.setLayout(gridbag); 
        l_c.add(panelBottom, c); 
        l_c.add(imagePanelParent1, c);
        l_c.add(imagePanelParent2, c);
        l_c.add(imagePanelChild, c);
       // l_c.validate();
        
        
        //Create a drop target on imagePanel
        dt1 = new DropTarget(imagePanelParent1,this);
        dt2 = new DropTarget(imagePanelParent2,this);
         
       
        setVisible(true);   
        imagePanelParent1.setLocation(0,0);
        //System.out.println("image panel 1 location is " + imagePanelParent1.getLocation().x + "," + imagePanelParent1.getLocation().y);
        //render();
	}
	
	public void render(){
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
	  if (image != null){
		image = backupImage.clone();
		
		if(event.getSource() == buttonGray){ 
			
			imagePlugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.grayScale.jar");
            imagePlugin.process(image, image, null, MarvinImageMask.NULL_MASK, false);
            image.update();
            imagePanelChild.setImage(image);
            debugTA.setText("Graying...");
		} 
        else if(event.getSource() == buttonEdgeDetector){
        	
        	 imagePlugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.edge.edgeDetector.jar");
             imagePlugin.process(image, image, null, MarvinImageMask.NULL_MASK, false);
             image.update();
             imagePanelChild.setImage(image);
             debugTA.setText("Detecting edges...");
        } 
        else if(event.getSource() == buttonRotate){
        	System.out.println("Hmm, let's rotate!");
        	if(imagePanelParent1.getImage() != null){
        	
        		 imagePanelChild.setImage(hMain.getTransmute().rotateCW(imagePanelParent1.getImage(), (Integer.parseInt((String)rotChoicesCB.getSelectedItem()))));
        		 
        		 debugTA.setText("rotating...");// + i);
        		 
        	
        	}
        	else{
        		debugTA.setText("Please place an image in Parent 1 panel");
        	}
        }
        else if(event.getSource() == buttonFlip){
        	System.out.println("Hmm, let's flip!");
        	if(imagePanelParent1.getImage() != null){
        	
        		 imagePanelChild.setImage(hMain.getTransmute().flip(imagePanelParent1.getImage(), ((String)flipChoicesCB.getSelectedItem()).substring(0, 1)));
        		 
        		 debugTA.setText("flipping out...");// + i);
        		 
        	
        	}
        	else{
        		debugTA.setText("Please place an image in Parent 1 panel");
        	}
        }
        else if(event.getSource() == buttonMag){
        	System.out.println("Hmm, let's magnify!");
        	if(imagePanelParent1.getImage() != null){
        	
        		 imagePanelChild.setImage(hMain.getTransmute().magnify(imagePanelParent1.getImage(), ((String)magChoicesCB.getSelectedItem()),Integer.parseInt(((String)magFactorsCB.getSelectedItem()))));
        		 
        		 debugTA.setText("magnifying...");// + i);
        		 
        	
        	}
        	else{
        		debugTA.setText("Please place an image in Parent 1 panel");
        	}
        }
        else if(event.getSource() == buttonRedux){
        	System.out.println("Hmm, let's reduce!");
        	if(imagePanelParent1.getImage() != null){
        	
        		 imagePanelChild.setImage(hMain.getTransmute().reduce(imagePanelParent1.getImage(), ((String)reduxChoicesCB.getSelectedItem()),Integer.parseInt(((String)reduxFactorsCB.getSelectedItem()))));
        		 
        		 debugTA.setText("reductions are a crime against god-given gifts, ladies!...");// + i);
        		 
        	
        	}
        	else{
        		debugTA.setText("Please place an image in Parent 1 panel");
        	}
        }
        else if(event.getSource() == buttonJuxt){
        	System.out.println("Hmm, let's juxtapose!");
        	if(imagePanelParent1.getImage() != null){
        	
        		 imagePanelChild.setImage(hMain.getTransmute().juxtOp_Rand(imagePanelParent1.getImage()));
        		 
        		 debugTA.setText("Juxtaposing...");// + i);
        		 
        	
        	}
        	else{
        		debugTA.setText("Please place an image in Parent 1 panel");
        	}
        }
        else if(event.getSource() == buttonAddNW){
        	System.out.println("Hmm, let's addNW!");
        	if(imagePanelParent1.getImage() != null && imagePanelParent2.getImage() != null){
        		
        		imagePanelChild.setImage(hMain.getTransmute().additionOp_NoWrap(imagePanelParent1.getImage(), imagePanelParent2.getImage(), 255));
        		debugTA.setText("Adding NW...");
        	}
        	else{
        		debugTA.setText("This op requires that an image be loaded in Parent 1 and Parent 2");
        	}
        } 
        else if(event.getSource() == buttonAddW){
        	System.out.println("Hmm, let's addW!");
        	if(imagePanelParent1.getImage() != null && imagePanelParent2.getImage() != null){
        		imagePanelChild.setImage(hMain.getTransmute().additionOp_Wrap(imagePanelParent1.getImage(), imagePanelParent2.getImage(), 255));
        		debugTA.setText("Adding W...");
        	}
        	else{
        		debugTA.setText("This op requires that an image be loaded in Parent 1 and Parent 2");
        	}
        } 
        else if(event.getSource() == buttonTile){
        	System.out.println("Hmm, let's saturate!");
        	if(imagePanelParent1.getImage() != null){
        		imagePanelChild.setImage(hMain.getTransmute().saturationOp(imagePanelParent1.getImage(), (Character)hueChoicesCB.getSelectedItem()));
        		////LATER: imagePanelChild.setImage(hMain.getTransmute().tileOp_Rand(imagePanelParent1.getImage(), 4));
        		debugTA.setText("Saturating...");
        	}
        	else{
        		debugTA.setText("This op requires that an image be loaded in Parent 1");
        	}
        } 
        else if(event.getSource() == buttonInvert){
        	
        	imagePlugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.invert.jar");
            imagePlugin.process(image, image, null, MarvinImageMask.NULL_MASK, false);
            image.update();
            imagePanelChild.setImage(image);
            debugTA.setText("inverting...");
        }
        else if(event.getSource() == buttonSave){
        	MarvinImageIO.saveImage(imagePanelChild.getImage(), "results/" + debugTA.getText() + "result" + saveCount + ".jpg");
        	saveCount++;
            debugTA.setText("Saving to <project folder location>/results...");
       } 
        else if(event.getSource() == buttonReset){
        	image = MarvinImageIO.loadImage("assets/color/caffeinated_owl_256.jpg");
            backupImage = image.clone();  
            
            imagePanelParent1.setImage(image); 
            
            imagePanelParent2.setImage(image); 
            
            imagePanelChild.setImage(image); 
            
            debugTA.setText("resetting...");
           
            System.gc();
        }
		//image.update();
        //imagePanelChild.setImage(image); 
	  }//end if image != null
	  else{
		  System.out.println("The image is null; please provide an image");
		  debugTA.setText("The image is null; please provide an image");
	  }
	}

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent evt) {
		List result = new ArrayList();
        int action = evt.getDropAction();
        evt.acceptDrop(action);
        try {
            Transferable data = evt.getTransferable();
            DataFlavor flavors[] = data.getTransferDataFlavors();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> list = (List<File>) data.getTransferData(
                    DataFlavor.javaFileListFlavor);
                String absPath = list.get(0).getAbsolutePath();
                image = MarvinImageIO.loadImage(absPath);
               // System.out.println("images color model: " + image.getType());
                backupImage = image.clone(); 
                if(((DropTarget) evt.getSource()).getComponent() == imagePanelParent1){
                	
                	imagePanelParent1.setImage(image); 
                }
                else if(((DropTarget)evt.getSource()).getComponent() == imagePanelParent2){
                	
                	imagePanelParent2.setImage(image); 
                }
                System.out.println("evt.getSource is " + evt.getSource().toString());
                System.out.println("the file format is " + absPath.substring(absPath.length()-3));
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            evt.dropComplete(true);
            repaint();
        }
        
        
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
