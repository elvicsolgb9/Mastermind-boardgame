import java.math.*; // For Random number generator.

public class MasterMindBoard
{	
	/**----------------------------------------------------------------------
	// Implementation of the MasterMind Game Board // Data or Model Class
	   ---------------------------------------------------------------------*/
	private int currentRowPos;
	
	private int[] hidden_colors_holder = new int[4];
	
	private int[][] breaker_colors_holder = new int[4][10];
	private int[][] keycolors_holder = new int[4][10];
	
	protected int[] tempo_breaker_colors_holder = new int[4];
	protected int[] tempo_keycolors_holder = new int[4];
		
	static final int BLACK = 0;
	static final int WHITE = 1;
	
	static final int LOST = 0;
	static final int WIN = 1;
	
	private int guessAttempts;
	private boolean win_status;
		
	// -- Constructor Initialization --  
	MasterMindBoard()
	{
		int i, j;
		
		// Start the current row position from the bottom part of the board.
		currentRowPos = 9; 	
		guessAttempts = 0;
		
		setHiddenColors(); // Generate the hidden colors code with random color values.

		// Initialize the color holders on the board to empty contents.
		for (i = 0; i <= 3; i++) 
			for (j = 0; j <= 9; j++) { 
				breaker_colors_holder[i][j] = -1;		//(int) (Math.random() * 6)/*For test purpose only*/; 
				keycolors_holder[i][j] = -1;			//(int) (Math.random() * 2)/*For test purpose only*/; 
			}
		
		// Initialize the two arrays to be used for comparing or evaluation.
		// The value -1 means the holder-array element is empty.
		for(i = 0; i <= 3; i++) {
			tempo_breaker_colors_holder[i] =  -1;
			tempo_keycolors_holder[i] =  -1;
		}
		
		/*** For test purpose only  ***/	
		//breaker_colors_holder[0][0] = -1; 
		//breaker_colors_holder[1][0] = -1;
		//breaker_colors_holder[2][0] = -1;
		//breaker_colors_holder[3][0] = -1;
		
		/*For test purpose only*/
		//int[] testBreakerColorsArray = new int[4];
		//for (i = 0; i <= 3; i++) {
		//	testBreakerColorsArray[i] = (int) (Math.random() * 6);
		//}
		//this.setBreakerColors(testBreakerColorsArray, 9);
		
		/*For test purpose only*/
		//int[] testKeyColorsArray = new int[4];
		//for (i = 0; i <= 3; i++) {
		//	testKeyColorsArray[i] = (int) (Math.random() * 2);
		//}
		//this.setKeyColors(testKeyColorsArray, 9);
		
		/*For test purpose only*/
		//for (i=0; i<=3; i++) {
		//	this.setTempoBreakerColors(i, (int) (Math.random() * 6)/*For test purpose only*/);
		//}
		//setBreakerColors(this.tempo_breaker_colors_holder, this.currentRowPos);
		
		//this.setTempoBreakerColors(/*i*/ 1, (int) (Math.random() * 6)/*For test purpose only*/);
		//this.setTempoBreakerColors(/*i*/ 3, (int) (Math.random() * 6)/*For test purpose only*/);
		//this.setBreakerColors(this.tempo_breaker_colors_holder, this.currentRowPos);
		
		//win_status = true;
		//guessAttempts = 10;
		////////////////////////////////
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	/* This section contains the methods of this class that performs data manipulation for the game.
	****************************************************************************************************
	*/	
	public void EvaluateBoard()
	{
		/**	Check if all the temporary breaker-color holes, 
		used for comparison, were filled out; -1 means it's empty	*/
		if (tempo_breaker_colors_holder[0] == -1 || tempo_breaker_colors_holder[1] == -1 ||
			tempo_breaker_colors_holder[2] == -1 || tempo_breaker_colors_holder[3] == -1) {
			return;	/*Don't do anything yet until all colour holes were filled out*/
		} else	{		
			EvaluateTheGuessColors(); // Execute the main algorithm for comparing the guess attempt against the hidden code.
		}	
		
		DetermineRowPos(); // Move the current active row upward (which is decrease the y-axis)
		ResetBreakerColorsHolder();
	}
	
	/** Compare the positional values in the temporary tempo_breaker_colors_holder 
	& temporary tempo_keycolors_holder arrays to determine what colour keys will be 
	inserted into the key_colors holder. This is the main algorithm that calculates 
	the colour values guessed against the hidden colours code */
	public void EvaluateTheGuessColors()
	{
		int i, j;
		int index = 0;
		int[] temp_hidden_code = new int[4];

		/* Assign or copy the element values of the hidden_colors_holder array to another array(temp_hidden_code[]) 
		so as not to change the original colour value of the original hidden_colors_holder[] which must remain the same
		throughout the game & use instead the copy temp_hidden_code[] for comparison. */
		for (i = 0; i <= 3; i++) {
			temp_hidden_code[i] = hidden_colors_holder[i];
		}
		
		//initialize the temporary keycolors_holder to empty values
		for (i = 0; i <= 3; i++) {
			tempo_keycolors_holder[i] = -1; 
		}		
			
		/* This is the algorithm that inserts the BLACK color value to the temporary key colors holder 
		which will be inserted to the main keycolors_holder later */
		
		for (i = 0; i <= 3; i++) {
			if ((tempo_breaker_colors_holder[i] == temp_hidden_code[i]) &&
				(temp_hidden_code[i] != -1) && (tempo_breaker_colors_holder[i] != -1)) {
				tempo_keycolors_holder[index] = BLACK;
				index++;
				tempo_breaker_colors_holder[i] = -1;
				temp_hidden_code[i]  = -1;
			}
		}
		
		/* This is the algorithm that inserts the WHITE color value to the temporary key colors holder 
		which will be inserted to the main keycolors_holder later */
		for (i = 0; i <= 3; i++) {
			for (j = 0; j <= 3; j++) {
				if ((tempo_breaker_colors_holder[i] == temp_hidden_code[j]) && 
					(i != j) && (tempo_breaker_colors_holder[i] != temp_hidden_code[i]) && 
					((temp_hidden_code[j] != -1) && (tempo_breaker_colors_holder[i] != -1)))
			    { 
					tempo_keycolors_holder[index] = WHITE;
					index++;
					temp_hidden_code[j]  = -1;
					tempo_breaker_colors_holder[i] = -1;
				}
			}
		}
		
		// Insert the BLACK or WHITE colors into the keycolors_holder array for the scoreboard.
		// currentRowPos is a member variable  that tracks the current active row position to fill pegs.
		setKeyColors(tempo_keycolors_holder, getCurrentRowPos());	
		
		DetermineGameResult(tempo_keycolors_holder);
	}
							
	// This is called after Board Evaluation was done for a specific row,
	public void ResetBreakerColorsHolder()
	{
		for (int i = 0; i <= 3; i++)
			tempo_breaker_colors_holder[i] = -1;
	}
	
	public void DetermineGameResult(int[] key_array)
	{
		if (key_array[0] == BLACK && key_array[1] == BLACK && key_array[2] == BLACK && key_array[3] == BLACK) { 		
			SetWinStatus(true);
			// Set the active row position in such a way that no more guess attempts can be made.
			DetermineRowPos(); 
		} else {
			setGuessAttempts(); // Increment the guessAttempt value by 1, max is 10.
		}			
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	/* setter & getter methods that access the private variables of this class
	****************************************************************************************************
	*/		
	// This method is called when a peg hole was filled out by the guessing player at the click of the mouse
	public void setTempoBreakerColors(int index, int iColor)
	{
		tempo_breaker_colors_holder[index] = iColor;
	}
	
	public int[] getTempoBreakerColors()
	{
		return tempo_breaker_colors_holder;
	}
	
	public void setBreakerColors(int[] breakColorsArray, int colPos, int rowPos)
	{
		//Assign the values in the temporary breaker array to the mainboard array.
			breaker_colors_holder[colPos][rowPos] = breakColorsArray[colPos];
	}

	public int[][] getBreakerColors()
	{
		return breaker_colors_holder;
	}

	public void setKeyColors(int[] keyColorsArray, int rowPos)
	{
		//Assign the values in the temporary key array to the main key array.
		for(int i = 0; i <= 3; i++) {	
			keycolors_holder[i][rowPos] = keyColorsArray[i];	
		}
	}

	public int[][] getKeyColors()
	{
		return keycolors_holder; 
	}
	
	public void setHiddenColors()
	{
		// Insert random colors out of the available ones to hidden colors array		
		for (int i = 0; i <= 3; i++) 
			hidden_colors_holder[i] = (int) (Math.random() * 6);
	}

	public int[] getHiddenColors()
	{
		return hidden_colors_holder; 
	}
	
		public void DetermineRowPos()
	{
		if (win_status == true)
		    currentRowPos = -1; // Disable the active row so no more guess can be attempted.
	    else
	        --currentRowPos; // Just decrease the y-axis length or move the active4 row up.
	}

	public int getCurrentRowPos()
	{
		return currentRowPos;
	}
	
	public void setGuessAttempts()
	{
		guessAttempts++;
	}
	
	public int getGuessAttempts()
	{
		return guessAttempts;
	}
			
	public void SetWinStatus(boolean bstatus)
	{
		win_status = bstatus;
	}
	
	public boolean getWinStatus()
	{
		return win_status;
	}
	
	/*
	****************************************************************************************************/
}
