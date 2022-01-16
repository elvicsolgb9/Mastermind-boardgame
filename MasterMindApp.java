import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class MasterMindApp extends JFrame
{
	static int activeColor;	
				
	public MasterMindApp()
	{
		activeColor = 0;
		
		//For test purpose only.
		// Create a GameboardDisplayer instance for moving a message
		GameboardDisplayer gBoard = new GameboardDisplayer("|| Mastermind Game Board Program || ");
		
		// Place the Mastermind game board panel on the frame
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(gBoard);
	}
	
	/* Main method */
	public static void main(String[] args)
	{
		MasterMindApp frame = new MasterMindApp();								
		frame.setTitle("MasterMindApp");
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(480, 720);
		frame.setVisible(true);	
	}
		
	// Inner class: GameBoardDisplayer
	/**----------------------------------------------------------------------
	// Implementation of the Game Board Displayer // Viewer Class
	// This is the class that draws the color pegs on the board through
	// the overriden paintComponent() method.
	---------------------------------------------------------------------*/ 
	static class GameboardDisplayer extends JPanel
	{		
		/**********************************************/
		/*** Declare & create a MasterMindBoard object 
		and use its getter methods to assign its
		private member variable values to locally 
		created ones for local use in this class.  
		***/
		MasterMindBoard gameBoard = new MasterMindBoard();
		int[][] breakerColors = gameBoard.getBreakerColors();
		int[] tempobreaker = gameBoard.getTempoBreakerColors();
				
		Color fill, outline, textcolor;		// The various colors we use.
		Font font;							// The font we use for text.	
		FontMetrics metrics;
		
		static final Color[] m_crColors = {  
		new Color( 255,    0,    0),    // Red
		new Color( 214,  170,    0),    // Brown
		new Color( 255,  255,    0),    // Yellow
		new Color(   0,  255,    0),    // Green
		new Color(   0,    0,  255),    // Blue
		new Color( 255,   17,  255),    // Magenta
		};

		static final Color[] keyColors = {
		new Color(  0,   0,   0),	//Black
		new Color(255, 255, 255), 	//White 
		};
		
		private String message = "";
		Color color;
		/** These are the coordinates of the the previous mouse position */
		protected int last_x = 20, last_y = 20;
		protected boolean clickStarted = false;
		protected boolean withinDraggable = false;
						
		/** Construct a panel to draw objects on it (Inner class constructor for this panel)*/
		GameboardDisplayer(String s)
		{			
			message = s;
			
			// Initialization values for graphics resources
			// Initialize colors for the general use of this panel display. 
			fill = new Color(200, 200, 200);	// Same as Color.gray
			outline = new Color(0, 0, 255);		// Same as Color.blue
			textcolor = new Color(255, 0, 0);	// Same as Color.red
		
			// Create a font for use in the paintComponent() method. Get its metrics too.
			font = new Font("sansserif", Font.BOLD, 14);
			metrics = this.getFontMetrics(font);	
						
			//////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////
			//***				Mouse Event Handling Section				*/
			// Register mouse event handlers defined as an inner class      //
			//////////////////////////////////////////////////////////////////			
			// Register a mouse motion event handler defined as an inner class
			// By subclassing MouseMotionAdapter rather than implementing
			// MouseMotionListener, we only override the method we're interested
			// in and inherit default (empty) implementations of the other methods.
			
			// Mouse pressed.
			addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) { 
					requestFocus();              // Take keyboard focus					
					last_x = e.getX();
					last_y = e.getY();
					
					Point xy = new Point();
					xy.x = last_x;
					xy.y = last_y;
					
					// Declare & create a bounding rectangle that represents the selectable colors.
					// Mouse coordinates will be assessed if it's within the boundary & will determine 
					// the coloumn position corresponding to the coordinate of the selectable colors.
					BoundRect bRect = new BoundRect(100, 560, 240, 40);
					if(bRect.isWithinBounds(xy)) {
					    clickStarted = true;
					    withinDraggable = true;
						setValueForTheSelectedColor(xy);
					}
					 
					Graphics g = getGraphics();					   
					DrawSelectedColor(g, last_x, last_y, activeColor, clickStarted, withinDraggable);	
					repaint(); 
		    	}
			});
			
			// Mouse released.
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) { 			                                                              
                    last_x = e.getX();
					last_y = e.getY();
					clickStarted = false;
					withinDraggable = false;
															     			        
			        gameBoard.EvaluateBoard();                  
					repaint();
		    	}
			});
			
		    // Mouse dragged.
		    addMouseMotionListener(new MouseMotionAdapter() {
				/** Handle mouse dragged event */
				public void mouseDragged(MouseEvent e) {
					// Get the new location and repaint screen
					last_x = e.getX();
					last_y = e.getY();
						
					Point xy = new Point();
					xy.x = last_x;
					xy.y = last_y;
					
					int currentRow = gameBoard.getCurrentRowPos();
					// Declare & create a bounding rectangle that represents the gameboard area.
					// Mouse coordinates will be assessed if it's within the boundary & will determine 
					// the coloumn position corresponding to the coordinate of the gameboard's color peg hole.					
					BoundRect bRect = new BoundRect(20, 5, 400, 635);
													
					if(currentRow < -1) {
					      bRect.setBoundStatus(bRect, false);				        
					} 
					
					else if(bRect.isWithinBounds(xy) || bRect.getBoundStatus() != false) {
					    //***   BoundRect object bRect has the same logic as setColorOnTempoBreaker()
					    //      that uses mouse pointer coordinates to synchronize the assigment of values
					    //      to tempobreaker's & the breakerColors arrays.                           **/		
					    bRect.setColoumn(xy, currentRow);
					    int colPosition = bRect.getColoumn();
					    setColorOnTempoBreaker(xy, currentRow);					    
					    gameBoard.setBreakerColors(tempobreaker, colPosition, currentRow); 
					} 
					
					Graphics g = getGraphics();					   
					DrawSelectedColor(g, last_x, last_y, activeColor, clickStarted, withinDraggable);
					
					// For testing purpose only //
					int activeColoumn = bRect.getColoumn();
					message = "||activeColoumn:" + activeColoumn;
					message += "||" + "currentRow:" + currentRow;
					message += "  last_x: " + last_x + "  last_y: " + last_y;			
					///////////////////////////
										
					repaint();
				}
			});
			
			// Add a keyboard event handler to clear the screen on key 'C'
			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
		    		
				}
			});		
		}
		/**				End Section of Event Handling 			              **/
		////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
				
		/**********************************************************************/
		
		/*  Perform some calculations to determine which coloumn 
            the mouse pointer was on while it's being dragged to determine
            which coloumn in the temporary breaker's array the color value should be placed. 
        */ 
		public void setColorOnTempoBreaker(Point point, int rowPos)
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
		        gameBoard.setTempoBreakerColors(0, activeColor);
		        return;					
	        }	 
	        else if ((point.x > colSpan) && (point.x < colSpan + 40) 
		            && (point.y > rowSpan) && (point.y < rowSpan + 40))
	        {
		        gameBoard.setTempoBreakerColors(1, activeColor);
		        return;
	        } 		    
	        else if ((point.x > colSpan + 40) && (point.x < colSpan + 40*2) 
		            && (point.y > rowSpan) && (point.y < rowSpan + 40))
            {
		        gameBoard.setTempoBreakerColors(2, activeColor);
		        return;
	        }		    
	        else if ((point.x > colSpan + 40*2) && (point.x < colSpan + 40*3) 
		            && (point.y > rowSpan) && (point.y < rowSpan + 40))
            {
		        gameBoard.setTempoBreakerColors(3, activeColor);
		        return;
	        } 
        }
        
        public void setValueForTheSelectedColor(Point point)
        {
        	int startX = 100;
			int startY = 560;
                
	        int colSpan = (startX + 40);
	        int rowSpan = (startY + 40); 
	        	        
	       	//Translate logical coordinate 
	        //into Mastermind board array units.    
	        if ((point.x > startX) && (point.x < colSpan) 
		            && (point.y > startY) && (point.y < rowSpan))
            {			
		        activeColor = 0;
		        return;					
	        }	 
	        else if ((point.x > startX) && (point.x < colSpan + 40*1)
		            && (point.y > startY) && (point.y < rowSpan))
	        {
		        activeColor = 1;
		        return;
	        } 		    
	        else if ((point.x > startX) && (point.x < colSpan + 40*2)
		            && (point.y > startY) && (point.y < rowSpan))
            {
		        activeColor = 2;
		        return;
	        }		    
	        else if ((point.x > startX) && (point.x < colSpan + 40*3)
		            && (point.y > startY) && (point.y < rowSpan))
            {
		        activeColor = 3;
		        return;
	        } 
	        
	        else if ((point.x > startX) && (point.x < colSpan + 40*4)
		            && (point.y > startY) && (point.y < rowSpan))
		    {
		    	activeColor = 4;
		    	return;
		    }
		    
		    else if ((point.x > startX) && (point.x < colSpan + 40*5)
		           && (point.y > startY) && (point.y < rowSpan))
		    {
		    	
		    	activeColor = 5;
		    	return;
		    }	        		 	        		        
        }
        
		/**********************************************************************/ 
	    
	    public void DrawBoundaries(Graphics g, BoundRect rect, int numOfBoxes)
	    {	        
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setColor(new Color(0, 0, 0));
	        
	        int x = rect.getBoundRectX();
	        int y = rect.getBoundRectY();
	        int width = rect.getBoundRectWidth();
	        int height = rect.getBoundRectHeight();
	        
	        int rectCornerClips = 20;
	        
	        for(int i = 0; i <= numOfBoxes; i++) {
                g2d.drawRoundRect(x, y, width*i, height, rectCornerClips, rectCornerClips); 
	        }
	    }
	        
		public void DrawGameBoardArea(Graphics g)
		{	
			// Specify the font we'll be using throughout	
			g.setFont(font);
					
			// Draw and fill a rounded rectangle
			g.setColor(fill);
			g.fillRoundRect(60, 35, 320, 480, 20, 20);
			g.setColor(outline);
			g.drawRoundRect(60, 35, 320, 480, 20, 20);
		}
		
		public void DrawSelectedColor(Graphics g, int x,  int y, int selectedColor, boolean clickStarted, boolean withinDraggable)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(m_crColors[selectedColor]);
								
			int wxh = 40;
			if(clickStarted && withinDraggable)
				g2d.fillOval((x - (wxh/2)), (y - (wxh/2)), wxh, wxh);
			else
				return;
		}
		
		public void DrawSelectableColors(Graphics g, int x, int y, int width, int height, int iPegColor)
		{
			int StartX = 100;
			int StartY = 520;
					
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setColor(m_crColors[iPegColor]);
			g2d.fillOval((StartX + x), (StartY + y), width, height);
		}
		
		public void DrawColorPegs(Graphics g, int x, int y, int width, int height, int iPegColor)
		{
			
			int StartX = 200;
			int StartY = 80;
						
			Graphics2D g2d = (Graphics2D) g;
						
			/***	Draw colored circles contained in the breaker colors array
			representing the color pegs filled in the breaker holes		***/ 
			g2d.setColor(m_crColors[iPegColor]);
			GradientPaint gradient = new GradientPaint(70, 70, m_crColors[iPegColor], 150, 150, m_crColors[iPegColor]);
			g2d.fillOval((StartX + x), (StartY + y), width, height);
				
		}
		
		public void DrawKeyColorPegs(Graphics g, int iKeyColor, int x, int y, int width, int height)
		{
			int StartX = 75;
			int StartY = 90;
			
			Graphics2D g2d = (Graphics2D) g;
			
			/***	Draw colored circles contained in the key colors array
			representing the color pegs filled in the key holes		***/ 
			g2d.setColor(keyColors[iKeyColor]);
			GradientPaint gradient = new GradientPaint(70, 70, keyColors[iKeyColor], 150, 150, keyColors[iKeyColor]);
			g2d.fillOval((StartX + x), (StartY + y), width, height);	
		}
		
		public void DisplayGameResults(Graphics g, int status)
		{
			int i;
			int wxh = 40; // Set the width & height of the color circles.
			int[] hiddenColors = gameBoard.getHiddenColors();
			
			switch(status) {
			case 0:
				g.setColor(new Color(128, 128, 0));
				g.drawString("Sorry, you didn't get it right. Try again !!!", 60, 540);
				//m_bttnNew.EnableWindow(TRUE);
				//m_bttnQuit.EnableWindow(TRUE);

				for(i = 0; i <= 3; i++)  
					DrawHiddenColors(g, i*40, 1, wxh, wxh, hiddenColors[i]);
					g.setColor(new Color(128, 128, 0));
					g.drawString("Correct Colors: ", 70,  65);
					gameBoard.DetermineRowPos();
					break;
			case 1:
				g.setColor(new Color(128, 128, 0));
				g.drawString("Well done, you got it right. Try again !!!", 60, 540);
				//m_bttnNew.EnableWindow(TRUE);
				//m_bttnQuit.EnableWindow(TRUE);
				break;
			}
			
		}
		
		public void DrawCurrentRow(Graphics g, int x, int y, int width, int height)
		{			
			int StartX = 200;
			int StartY = 80;
			
			Graphics2D g2d = (Graphics2D) g;
			
			// Draw empty circles to represent the current active row position.
			if (gameBoard.getCurrentRowPos() >= 0 && gameBoard.getCurrentRowPos() != -1) {
				g2d.setColor(new Color(0, 0, 0));
				g2d.drawOval((StartX + x), (StartY + y), width, height);
			} else return;
		}
		
		public void DrawHiddenColors(Graphics g, int x, int y, int width, int height, int iColor)
		{			
			int StartX = 200;
			int StartY = 40;
			
			Graphics2D g2d = (Graphics2D) g;
			
			// Draw the hidden colors contained in the hidden colors array if guess isn't successful.
			g2d.setColor(m_crColors[iColor]);
			g2d.fillOval((StartX + x), (StartY + y), width, height);
		}
		
		public void DrawTestGraphics(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
		
			int red = (int) (Math.random() * 256);
			int green = (int) (Math.random() * 256);
			int blue = (int) (Math.random() * 256);
			Color startColor = new Color(red, green, blue);

			red = (int) (Math.random() * 256);
			green = (int) (Math.random() * 256);
			blue = (int) (Math.random() * 256);
			Color endColor = new Color(red, green, blue);

			GradientPaint gradient = new GradientPaint(70, 70, startColor, 150, 150, endColor);
			g2d.setPaint(gradient);
			
			int x = 15, y = 35, wxh = 40;
			g2d.fillOval(x, y, wxh, wxh);		
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// This method is called whenever the component needs to be drawn or redrawn.
		//
		public void paintComponent(Graphics g) {
			// This is necessary to ensure that the viewing area is cleared before a new drawing is displayed 
			super.paintComponent(g); 
						
			int i, j;
			int wxh = 40; // Set the width & height of the color circles.
            
            DrawGameBoardArea(g); 
                                              
            BoundRect selColorsRect = new BoundRect(100, 560, 40, 40);       
			DrawBoundaries(g, selColorsRect, 6);  
			
			// Draw the colors available for selection 
			for (i = 0; i <= 5; i++) {
				DrawSelectableColors(g, i*40, 40, wxh, wxh, i);
			}
			
			for(i=0; i<=3; i++) { 
				DrawCurrentRow(g, i*40, gameBoard.getCurrentRowPos()*40, wxh, wxh);
			}
			
			// Draw the color values stored in the board's color holder array 
			// represented by breaker_colors_holder array in MasterMindBoard class.		
			int[][] breakerColors = gameBoard.getBreakerColors();
					
			for(i=0; i<=3; i++) { 
				for(j=0; j<=9; j++) {
					switch(breakerColors[i][j]) {
					case 0:
						DrawColorPegs(g, (i*40), (j*40), wxh, wxh, 0);
						break;
					case 1:
						DrawColorPegs(g, (i*40), (j*40), wxh, wxh, 1);
						break;
					case 2:
						DrawColorPegs(g, (i*40), (j*40), wxh, wxh, 2); 
						break;
					case 3:
						DrawColorPegs(g, (i*40), (j*40), wxh, wxh, 3);
						break;
					case 4:
						DrawColorPegs(g, (i*40), (j*40), wxh, wxh, 4);
						break;
					case 5:
						DrawColorPegs(g, (i*40), (j*40), wxh, wxh, 5);
						break;
					}
				}
			}
			
			// Draw the color values stored in the board's key holder array 
			// represented by keycolors_holder array in MasterMindBoard class.	
			int[][] keyColors = gameBoard.getKeyColors();
			for (i = 0; i <= 3; i++) { 
				for (j = 0; j <= 9; j++) {
					switch (keyColors[i][j]) {
					case 0:
						DrawKeyColorPegs(g, 0, (i*30), (j*40), 20, 20);
						break;
					case 1:
						DrawKeyColorPegs(g, 1, (i*30), (j*40), 20, 20);
						break;
					}
				}
			}
			
			{

				if (gameBoard.getWinStatus() == true) { 
					DisplayGameResults(g, MasterMindBoard.WIN);
				}

				else if (gameBoard.getGuessAttempts() == 10)
					DisplayGameResults(g, MasterMindBoard.LOST);
			}
			
			g.drawString(message, 20, 20);
			
			DrawSelectedColor(g, last_x, last_y, activeColor, clickStarted, withinDraggable);
			
			/*******************************************************************				
				if (gameBoard.getGuessAttempts() == 0) {
					m_bttnNew.EnableWindow(FALSE);
					m_bttnQuit.EnableWindow(FALSE);
				}
			******************************************************************/							
		}
		
		///////////////////////////////////////////////////////////////////////*/
		// *********************************************************************

		// Utility method to center two lines of text in an area.
		// Relies on the FontMetrics obtained in the init() method.
		protected void centerText(String s1, String s2, Graphics g, Color c, int x, int y, int w, int h) {
			int height = metrics.getHeight();		// How tall is the font?
			int ascent = metrics.getAscent();		// Where is the font baseline?
			
			int width1 = 0, width2 = 0; 
			int x0 = 0, x1 = 0; 
			int y0 = 0, y1 = 0;
			
			width1 = metrics.stringWidth(s1);		// How wide are the strings?
			
			if (s2 != null) 
				width2 = metrics.stringWidth(s2);
			x0 = x + (w - width1)/2;			// Center the strings horizontally
			x1 = x + (w - width2)/2;
			
			if (s2 == null)
				y0 = y + (h - height)/2 + ascent;
			else {
				y0 = y + (h - (int)(height * 2.2))/2 + ascent;
				y1 = y0 + (int)(height * 1.2);

			}
			
			g.setColor(c); 	// Set the color of the string
			g.drawString(s1, x0, y0);	// Draw the string	
			if (s2 != null)
				g.drawString(s2, x1, y1);
		}
		
		///////////////////////////////////////////////////////////////////////*/
		// *********************************************************************	
	}
}
