package hw2;
import api.ThrowType;
import static api.ThrowType.*;
/**
 * This class models a standard game of darts, keeping track of the scores,
 * whose turn it is, and how many darts the current player has remaining.
 * The number of starting points and the number of darts used in 
 * a player's turn are configurable.
 * @author timqn
 */
public class DartGame
{  
	private int playerTurn;
	private int startingPoints;
	private int playerZeroScore;
	private int playerOneScore;
	private int numMaxDarts;
	private boolean playerZeroDoubledIn;
	private boolean playerOneDoubledIn;
	private int dartsRemaining;
	private int roundScore = 0;
	
	/**
	 * @param startingPlayer Int to pick which player starts out throwing (0 or 1)
	 * Sets both players scores to 301 and numMaxDarts to 3, then starts the round
	 */
	public DartGame(int startingPlayer) 
	{
		playerTurn = startingPlayer;
		startingPoints = 301;
		playerZeroScore = startingPoints;
		playerOneScore = startingPoints;
		numMaxDarts = 3;
		dartsRemaining = numMaxDarts;
	//constructs a DartGame with given starting player, initial points 301, and three darts for each player's turn.
	}
	
	/**
	 * @param startingPlayer Int to pick which player starts out throwing (0 or 1)
	 * @param startingPoints Int maximum score that the players start counting down from (301 or 501 in normal matches)
	 * @param numDarts Int number of darts in each round of throwing
	 * sets both players scores to the starting point value and constructs the match with the parameters
	 */
	public DartGame(int startingPlayer, int startingPoints, int numDarts) 
	{
		playerTurn = startingPlayer;
		this.startingPoints = startingPoints;
		this.numMaxDarts = numDarts;
		dartsRemaining = numMaxDarts;
		playerZeroScore = startingPoints;
		playerOneScore = startingPoints;
	}
	
	/**
	 * @param type Input of a ThrowType that classifies as MISS, SINGLE, DOUBLE, TRIPLE, OUTER_BULLSEYE, and INNER_BULLSEYE
	 * @param number Int classifies what slice the numbers
	 */
	public void throwDart(ThrowType type, int number) 
	{
		int throwScore = calcPoints(type, number);
		if(getDartCount() > 0 && !isOver()) 
		{
			roundScore = roundScore + throwScore;
			//Check if it is player0's turn
			if(getCurrentPlayer() == 0) 
			{
				//If not a bust
				if(validScore(0, type))
				{
					useDart();
				}
				//Test for the double in cases
				if((type == DOUBLE || type == INNER_BULLSEYE))
				{
					setDoubleIn(0);
				}
				
				//Checks if player 1 is doubled in
				if(getDoubleIn(0) && validScore(0, type))
				{
					playerZeroScore =  playerZeroScore - throwScore;
				}
				
				//If case of a "bust"
				if(!validScore(0, type)) {
					//Cycle player turn to next player
					playerTurn = (playerTurn+1)%2;
					//Give player maxDarts
					reloadDart();
					//Resets player Score to scoer before round
					playerZeroScore = playerZeroScore + roundScore;
					//Resets roundScore
					roundScore = 0;
				}
			}
			
			
			//Check if it is player1's turn
			else if(getCurrentPlayer() == 1) 
			{
				//If not a bust
				if(validScore(1, type))
				{
					useDart();
				}
				
				//Test for the double in cases
				if((type == DOUBLE || type==INNER_BULLSEYE))
				{
					setDoubleIn(1);
				}
				
				//Checks if player 1 is doubled in
				if(getDoubleIn(1))
				{
					playerOneScore =  playerOneScore - throwScore;
				}
				
				//If case of a "bust"
				if(!validScore(1, type)) {
					//Cycle player turn to next player
					playerTurn = (playerTurn+1)%2;
					//Give player maxDarts
					reloadDart();
					//Resets player Score to scoer before round
					playerOneScore = playerOneScore + roundScore;
					//Resets roundScore
					roundScore = 0;
				}
			}
			
		}
		//If there are no darts left and the game is not over
		if (getDartCount() <= 0 && !this.isOver()) {
			
				//Cycle player turn to next player
				playerTurn = (playerTurn+1) %  2;
				//Teset the score for the round
				roundScore = 0;
				//Give new player maxDarts
				reloadDart();
		}
}

	
	/**
	 * Calculates the number of points for a given throw of a dart.
	 * @param type type is a ThrowType object that is defined in the ThrowType.java file
	 * @param number corresponds to which slice of the board the dart hits, only counts in cases of SINGLE DOUBLE TRIPLE, as the amount to multiply by.
	 * @return score caluclated from MISS=0, SINGLE=1*number, DOUBLE=2*number, TRIPLE=3*number, and OUTER_BULLSEYE = 25 and INNER_BULLSEYE = 50
	 */
	public static int calcPoints(ThrowType type, int number)
	{
		
		switch(type) 
			{
			case MISS:
				return 0;
			case SINGLE:
				return number;
			case DOUBLE:
				return number*2;
			case TRIPLE:
				return number*3;
			case OUTER_BULLSEYE:
				return 25;
			case INNER_BULLSEYE:
				return 50;
			 }
		//default case should never call
		return 0;
	}
	
	/**
	 * @param which is an int that chooses player to set the double in
	 * 
	 * Doubling in allows players throws to start counting from total
	 * before double in score does not count, this modifies double in for chosen player to be true
	 * never needs to be set to false
	 * 
	 */
	private void setDoubleIn(int which)
	{
		if(which==0) 
		{
			playerZeroDoubledIn=true;
		}
		if(which==1)
		{
			playerOneDoubledIn=true;
		}
	}
	
	
	/**
	 * @param which Int value that chooses player to check status of
	 * @return Boolean that returns whether specified player is doubled in
	 */
	private boolean getDoubleIn(int which)
	{
		switch(which) 
		{
		case 0: 
			return playerZeroDoubledIn;
		case 1:
			return playerOneDoubledIn;
		//Default case accounts for any non 0 or 1 inputs
		default:
			return false;
		}
	}
	
	
	/**
	 * @param which Specifies player for which to check the score
	 * @param type Specifies the ThrowType of the previous throw
	 * @return Boolean that is true if the score is valid and false if it is a "bust"
	 */
	private boolean validScore(int which, ThrowType type)
	{
		if((getScore(which)) > 1) 
			{
				return true;
			}
		else if (getScore(which)==0 && (type==DOUBLE||type==INNER_BULLSEYE))
			{
				return true;
			}
			return false;
	}
	
	
  /**
   * Returns the player whose turn it is.  (When the game is over,
   * this method always returns the winning player.)
   * @return
   *   current player (0 or 1)
   */
  public int getCurrentPlayer()
  {
    return playerTurn;
  }
  
  
  /**
   * Returns the score of the indicated player (0 or 1).  If
   * the argument is any value other than 0 or 1, the method returns
   * -1.
   * @param which
   *   indicator for which player (0 or 1)
   * @return
   *   score for the indicated player, or -1 if the argument is invalid
   */
  public int getScore(int which)
  {
	  if(which==0)
	  {
		  return playerZeroScore;
	  }
	  if(which==1)
	  {
		  return playerOneScore;
	  }
	  else
		  return 0;
  }
  
  
  /**
   * 
   * @return
   * boolean true if game is over otherwise false
   */
public boolean isOver()
  {
	  
	  if(getScore(0)==0||getScore(1)==0) 
	  {
		  return true;
	  }
	  else
		  return false;
	  
  }


  /**
 * @return Returns int value of the player who won (0 or 1)
 */
public int whoWon()
  {
	  
	  if (this.isOver()){
		  
		  if(getScore(0)==0)
			  
			  return 0;
		  
		  if (getScore(1)==0)
			  return 1;
	  }
		  return -1;
	  
  }
  

  /**
   * Returns the number of darts left in the current player's turn.
   * @return
   *   the number of darts left in the current player's turn
   */
  public int getDartCount()
  {
    return dartsRemaining;
  }
  
  
  /**
 * Resets dartRemaining counts to maxDarts 
 */
private void reloadDart() 
  {
	dartsRemaining =  numMaxDarts; 
  }
  

  /**
 * Subracts one from dartsRemaining 
 */
private void useDart()
  {
	  dartsRemaining--;
  }


  /**
   * Returns a string representation of the current game state.
   */
  public String toString()
  {
    String result = "Player 0: " + getScore(0) +
                    "  Player 1: " + getScore(1) +
                    "  Current: Player " + getCurrentPlayer() +
                    "  Darts: " + getDartCount();
    return result;
  }
  
}