import java.awt.*;
import java.math.*;

/***
This is the class that will assess the position of the mouse pointer if it's within the bounds of the board area.
It will also determine where the position of the pointer is at the moment its methods are called 
(isWithinBounds & setColoumn()) respectively. The methods receive a Point object that represent the coordinates
of the last pointer position it will be passed to by a mouse event.
*/
public class BoundRect
{
    private int x = 0;
    private int y = 0;
    private int width;
    private int height;
   
    private int colPos;
    private boolean boundStatus = false;
    
    BoundRect()
    {
        //this.x = 0;
        //this.y = 0;
        //this.width = 0;
        //this.height = 0;
    }
        
    BoundRect(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean isWithinBounds(Point point)
    {      		
		int leftBound = this.x;
		int rightBound = this.x + this.width;		
		int upperBound = this.y;
		int lowerBound = this.y + this.height;				
						
        // Ignore mouse click if above or below 
        // & beyond left or beyound right bounds 
        // of proper board coordinates
		if ((point.x < leftBound) || (point.x > rightBound)) 
		    return false;	    	
		else if ((point.y < upperBound) || (point.y > lowerBound)) 
		    return false;
		else  
		    return true;		                                        
    }
    
    /*  Perform some calculations to determine which coloumn 
        the mouse pointer was on when it's released to determine
        which coloumn in the temporary breaker's array the color value should be placed. 
    */
    public void setColoumn(Point point, int rowPos)
    {               
        int startX = 200;
        int startY = 80;
            
	    int colSpan = startX + 40;
	    int rowSpan = (rowPos*40 + startY);
		    
	    //Translate logical coordinate 
	    //into Mastermind board array units.    
	    if ((point.x > startX) && (point.x < colSpan) 
		        && (point.y > rowSpan) && (point.y < rowSpan + 40))
        {			
		    colPos = 0;					
	    }	 
	    else if ((point.x > colSpan) && (point.x < colSpan + 40) 
		        && (point.y > rowSpan) && (point.y < rowSpan + 40))
	    {
		    colPos = 1;
	    } 		    
	    else if ((point.x > colSpan + 40) && (point.x < colSpan + 40*2) 
		        && (point.y > rowSpan) && (point.y < rowSpan + 40))
        {
		    colPos = 2;
	    }		    
	    else if ((point.x > colSpan + 40*2) && (point.x < colSpan + 40*3) 
		        && (point.y > rowSpan) && (point.y < rowSpan + 40))
        {
		    colPos = 3;
	    } 
    }
    
    public void setBoundStatus(BoundRect bRect, boolean bStatus) {
        bRect.boundStatus =  bStatus; 
        bRect.x = 0;
        bRect.y = 0;
        bRect.width = 0;
        bRect.height = 0;     
    }
    
    public boolean getBoundStatus()
    {
        return boundStatus;
    }
        
    public int getColoumn()
    {
        return colPos;
    }

	public int getBoundRectX()
	{
	    return this.x;
	}
	
	public int getBoundRectY()
	{
	    return this.y;
	}
	
	public int getBoundRectWidth()
	{
	    return this.width;
	}
    
    public int getBoundRectHeight()
	{
	    return this.height;
	}
}
