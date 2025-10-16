package tictactoe.socket;

import java.io.Serializable;
import java.util.Objects;

/**
 * Server response to a REQUEST_MOVE request. This class is a subclass of Response.
 * It carries the opponent's last move and the active status of the game.
 */
public class GamingResponse extends Response implements Serializable {
    private Integer move;
    private Boolean active;

    /**
     * A default constructor for the GamingResponse class.
     * Calls the default constructor of the superclass (Response).
     */
    public GamingResponse() {
        super();
    }

    /**
     * A constructor that sets all attributes of this class, including those inherited from Response.
     * Calls the constructor of the superclass.
     *
     * @param status The status of the server response (SUCCESS or FAILURE).
     * @param message A string message description about the status of the client-server communication.
     * @param move An integer representing the last move made by the opponent (0-8).
     * @param active A boolean variable to indicate if the opponent is still active in the game.
     */
    public GamingResponse(ResponseStatus status, String message, Integer move, Boolean active) {
        // Call the superclass constructor to set status and message
        super(status, message);
        this.move = move;
        this.active = active;
    }

    /**
     * Overrides the default equals method.
     * Two GamingResponse objects are considered equal if they are equal to the superclass
     * and their specific attributes (move and active) are also equal.
     *
     * @param o The object to compare with.
     * @return true if both objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Check superclass equality
        GamingResponse that = (GamingResponse) o;
        return Objects.equals(move, that.move) && Objects.equals(active, that.active);
    }

    /**
     * Overrides the default hashCode method.
     * Must be overridden whenever equals() is overridden.
     *
     * @return The hash code based on the superclass, move, and active attributes.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), move, active);
    }
}
