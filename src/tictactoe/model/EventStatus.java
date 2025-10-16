package tictactoe.model;

/**
 * An enumeration type representing the different statuses a game event can have.
 */
public enum EventStatus {
    /** First status. Set when a client sends a SEND_INVITATION request. */
    PENDING,
    /** Status set when a client sends a DECLINE_INVITATION request. */
    DECLINED,
    /** Status set when a client sends an ACCEPT_INVITATION request. */
    ACCEPTED,
    /** Status set when a client sends an ACKNOWLEDGE_RESPONSE request. */
    PLAYING,
    /** Status set when a client sends a COMPLETE_GAME request. */
    COMPLETED,
    /** Status set when a client sends an ABORT_GAME request. */
    ABORTED
}