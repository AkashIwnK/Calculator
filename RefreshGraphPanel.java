/* Name: Alex White */


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class RefreshGraphPanel extends JPanel implements MouseListener 
{
	double[]           xValues;
    double[]           yValues;
    double[]		   yScale;
    String[]           xString;   
    String 			   expression;
    double xPixelsToValueConversionFactor;
    double yPixelsToValueConversionFactor;
    double yMax;
    double xMin;
    
    JFrame displayXYpairWindow = new JFrame();
	JPanel xyPanel = new JPanel();
	JTextField xTextField = new JTextField();
	JTextField yTextField = new JTextField();
    
	public RefreshGraphPanel(
			String			   expression,
			double[]           xValues,
            double[]           yValues, 
            double[] 		   yScale) throws IllegalArgumentException
	{
		this.xValues = xValues;
		this.yValues = yValues;
		this.yScale  = yScale;
		this.expression = expression;
		xString = new String[xValues.length];
		for (int i = 0; i < xString.length; i++)
			{
		      xString[i] = String.valueOf(xValues[i]);
		    }

		xyPanel.setLayout(new GridLayout(2, 1));
		xyPanel.add(xTextField);
		xyPanel.add(yTextField);
		displayXYpairWindow.getContentPane().add(xyPanel, "Center");
		displayXYpairWindow.setSize(200, 75);
		addMouseListener(this);
		
	    this.addMouseListener(this); // called on the JPanel class in THIS object!
	}
	
	@Override
	public void paint(Graphics g) // overrides paint() in JPanel
	    {
		int i = 0;
		int windowWidth  = getWidth();  // get the panel's   
	    int windowHeight = getHeight(); // *CURRENT* size!
	    // Now use the instance variables saved in the constructor and current window size to draw the graph.
	    int space = 35;
	    int xAxisX1 = space;
	    int xAxisX2 = windowWidth - space;
	    int xAxisY = windowHeight - space;	  
	    int xAxisSpace = (windowWidth - 2 * space)/(xValues.length - 1);
	    int xAxisStart = space;
	    int[] xPoints = new int[11];
	    double xMax = getMaxY(xValues);
	    xMin = getMinY(xValues);
	    double xRange = xMax - xMin;
	    xPixelsToValueConversionFactor = xRange/(xAxisX2 - xAxisX1);
	    
	    //Draw x-axis
	    g.setColor(Color.BLACK);
	    g.setFont(new Font("Times Roman", 1, 15));
	    g.drawLine(xAxisX1, xAxisY, xAxisX2, xAxisY);
	    g.drawString("X", xAxisX2 + 5, xAxisY);
	    for (i = 0; i < xValues.length; i++)
	    	{
		    g.drawString("|", xAxisStart, xAxisY + 5);
		    g.drawString(xString[i], xAxisStart - 10, xAxisY + 20);
		    xPoints[i] = xAxisStart;		//Gets x-value coordinates
		    xAxisStart += xAxisSpace;
	    	}
	    
	    int yAxisY1 = windowHeight - space;
	    int yAxisY2 = space;
	    int yAxisX = space;	 
	    
	    //Get y-scale values
	    String[] yScaleValues = new String[yScale.length];
	    for (i = 0; i < yScale.length; i++)
		{
	      yScaleValues[i] = String.valueOf(yScale[i]);
	    }
	    
	    
	    int yAxisSpace = (windowHeight - 2 * space)/(yScaleValues.length - 1);
	    int[] yPoints = new int[yValues.length];
	    int yAxisLength;
	    int yAxisStart = 0;
	    int yAxisEnd = 0;
	    double[] yDoubles = new double[yScaleValues.length];
	    
	 

	    
	    //Draw y-axis
	    g.setColor(Color.BLACK);
	    g.setFont(new Font("Times Roman", 1, 15));
	    g.drawLine(yAxisX, yAxisY1, yAxisX, yAxisY2);
	    g.drawString("Y", yAxisY2 + 5, yAxisX);


	    for (i = 0; i < yScaleValues.length; i++)
	    	{
		      g.drawString("__", yAxisX - 7, yAxisY1);
		      g.drawString(yScaleValues[i], yAxisX - 34, yAxisY1);
		      if(i == 0) yAxisStart = yAxisY1;
		      if(i == yScaleValues.length - 1) yAxisEnd = yAxisY1;
		      yAxisY1 -= yAxisSpace;
		 	}
	    yAxisLength = yAxisStart - yAxisEnd;
	    
	    for(i = 0; i < yScaleValues.length; i++)
    	{
    		yDoubles[i] = Double.parseDouble(yScaleValues[i]);
    	}

	    double yMin = getMinY(yDoubles);
	    yMax = getMaxY(yDoubles);
	    double yRange = yMax - yMin;
	    yPixelsToValueConversionFactor = yRange/((windowHeight - space) - space);
	    
	    //Get y-value coordinates
	    for(i = 0; i < yValues.length; i++)
	    {
	      double yPercent = 1 - ((yValues[i] - yMin) / yRange);
	      int yPointSpacing = (int) (yAxisLength * yPercent);
	      yPoints[i] = yPointSpacing;
	    } 
	   
	    //Draw points and lines using coordinates from xPoints[] and yPoints[]
	    g.setColor(Color.red);
	    for (i = 0; i < yValues.length; i++)
		    {
		      g.drawOval(xPoints[i] - 2, yAxisEnd + yPoints[i] - 2, 5, 5);
		      if (i > 0) 
		      	  {
			      g.drawLine(xPoints[(i - 1)], yAxisEnd + yPoints[(i - 1)], xPoints[i], yAxisEnd + yPoints[i]);
			      }
		    }
		    
	    }

	public void mouseClicked(MouseEvent arg0) 
	{

	}

	public void mouseEntered(MouseEvent arg0)
	{

	}

	public void mouseExited(MouseEvent arg0) 
	{

	}

	//"gc" doesn't work (null pointer) and can't get return value for "calculate" (see GraphingCalculator for method)
    public void mousePressed(MouseEvent me) // show tiny x,y values window
	    {
	    // xTextField and yTextField are in the mini displayXYpairWindow
	    int xInPixels = me.getX();
	    int yInPixels = me.getY();
	    System.out.println("yPixels " + yInPixels);
	    System.out.println("yCoversion " + yPixelsToValueConversionFactor);
	    double xValue = ((xInPixels - 35) * xPixelsToValueConversionFactor) + xMin;
	    String xValueString = String.valueOf(xValue);
	    xTextField.setText("X = " + xValueString);
	    
	    double yValue = yMax - ((yInPixels - 35) * yPixelsToValueConversionFactor);
	    String yValueString = String.valueOf(yValue);
	    yTextField.setText("Y = " + yValueString);

	    // show mini x,y display window
	    displayXYpairWindow.setLocation(me.getX(), me.getY());
	    displayXYpairWindow.setVisible(true); 
	    }

    public void mouseReleased(MouseEvent me) // hide tiny window
	    {
	    // "erase" mini x,y display window	
	    displayXYpairWindow.setVisible(false);
	    }
	
		
	public double getMinY(double[] yStuff) {
		if(yStuff == null)
			return 0.0; 
		
		double min = yStuff[0];
		int index = 0;
		int num = yStuff.length;
		while(index != num) {
			if(yStuff[index] < min)
				min = yStuff[index];
			index++;
		}
		return (min);
	}
	
	public double getMaxY(double[] yStuff) {
		if(yStuff == null)
			return 0.0; 
		
		double max = yStuff[0];
		int index = 0;
		int num = yStuff.length;
		while(index != num) {
			if(yStuff[index] > max)
				max = yStuff[index];
			index++;
		}
		return (max);
	}

}

