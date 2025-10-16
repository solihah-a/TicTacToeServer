package tictactoe.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Models a game lifecycle between two users, from invitation to completion or abortion.
 * Event objects are used to store and track game state.
 * This class maps directly to the 'Event' database table.
 */
public class Event implements Serializable{

    private Integer eventId;
    private String sender;
    private String opponent;
    private EventStatus status;
    private String turn;
    private Integer move;

    /**
     * A default constructor for the Event class.
     */
    public Event() {
        // Default initialization. eventId is typically set by the database later.
    }

    /**
     * A constructor that sets all attributes of this class.
     *
     * @param eventId The globally unique ID for the event.
     * @param sender The username of the user who sends the game invitation.
     * @param opponent The username of the user the game invitation was sent to.
     * @param status The current status of the game event.
     * @param turn The username of the player who made the last move.
     * @param move An integer storing the last move of the game.
     */
    public Event(Integer eventId, String sender, String opponent, EventStatus status, String turn, Integer move) {
        this.eventId = eventId;
        this.sender = sender;
        this.opponent = opponent;
        this.status = status;
        this.turn = turn;
        this.move = move;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public Integer getMove() {
        return move;
    }

    public void setMove(Integer move) {
        this.move = move;
    }

    /**
     * Overrides the default equals method.
     * Two Event objects are considered equal if their eventId's are equal,
     * as the eventId is the unique identifier.
     *
     * @param o The object to compare with.
     * @return true if the eventIds are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        // Use Objects.equals for safe comparison of the Integer eventId
        return Objects.equals(eventId, event.eventId);
    }

    /**
     * Overrides the default hashCode method.
     * Must be overridden whenever equals() is overridden to maintain the general contract.
     *
     * @return The hash code based on the unique eventId attribute.
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
