package tictactoe.model;

/**
 * An enumeration type representing the different statuses a game event can have.
 * <ul>
 *      <li>PENDING: First status. Set when a client sends a SEND_INVITATION request.</li>
 *      <li>DECLINED: Status set when a client sends a DECLINE_INVITATION request.</li>
 *      <li>ACCEPTED: Status set when a client sends an ACCEPT_INVITATION request.</li>
 *      <li>PLAYING: Status set when a client sends an ACKNOWLEDGE_RESPONSE request.</li>
 *      <li>COMPLETED: Status set when a client sends a COMPLETE_GAME request.</li>
 *      <li>ABORTED: Status set when a client sends an ABORT_GAME request.</li>
 * </ul>
 */
public enum EventStatus {
    PENDING,
    DECLINED,
    ACCEPTED,
    PLAYING,
    COMPLETED,
    ABORTED
}