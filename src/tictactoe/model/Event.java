package tictactoe.model;

import java.io.Serializable;

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

}
