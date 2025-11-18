package socket;

/**
 * Models the server's response to a REQUEST_MOVE request in the TicTacToe game.
 * This subclass of Response provides information about the opponent's last move
 * and the current active status of the game session.
 * <p>
 * GamingResponse objects are sent from the server to the client during gameplay
 * to synchronize game state and inform the client about the opponent's actions
 * and connection status. This enables real-time gameplay updates between players.
 */
public class GamingResponse extends Response {

	/**
	 * An integer representing the last move made by the current player's opponent.
	 * The value ranges from 0-8, representing the cells of the TicTacToe board
	 * from top to bottom, left to right:
	 * <pre>
	 * 0 | 1 | 2
	 * --+---+--
	 * 3 | 4 | 5
	 * --+---+--
	 * 6 | 7 | 8
	 * </pre>
	 */
	private int move;

	/**
	 * A boolean variable indicating if the opponent is still active in the game.
	 * Returns false if the opponent has aborted the game, disconnected, or
	 * the game has otherwise been terminated prematurely.
	 */
	private boolean active;

	/**
	 * Default constructor that creates a GamingResponse with default values.
	 * Calls the superclass constructor and initializes move and active to
	 * their default values (0 and false respectively).
	 */
	public GamingResponse() {
		this(0, false);
	}

	/**
	 * Parameterized constructor that creates a GamingResponse with specific move and status.
	 * Calls the superclass constructor and initializes all attributes with provided values.
	 *
	 * @param move the integer representing the opponent's last move (0-8)
	 * @param active the boolean indicating if the opponent is still active in the game
	 */
	public GamingResponse(int move, boolean active) {
		super();
		this.move = move;
		this.active = active;
	}

	/**
	 * Returns the opponent's last move on the TicTacToe board.
	 *
	 * @return an integer from 0-8 representing the cell position of the last move
	 */
	public int getMove() {
		return move;
	}

	/**
	 * Returns the active status of the game session.
	 *
	 * @return true if the opponent is still active, false if the game has been aborted
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Sets the opponent's last move position.
	 *
	 * @param move an integer from 0-8 representing the TicTacToe cell position
	 */
	public void setMove(int move) {
		this.move = move;
	}

	/**
	 * Updates the active status of the game session.
	 *
	 * @param active true if the opponent is active, false if the game is terminated
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
}