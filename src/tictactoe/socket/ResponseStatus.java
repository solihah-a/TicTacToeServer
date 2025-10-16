package tictactoe.socket;

/**
 * Enumerates the possible statuses for a server response.
 * <ul>
 *      <li>SUCCESS: Indicates that the client's request was processed successfully.</li>
 *      <li>FAILURE: Indicates that an error occurred while processing the client's request.</li>
 * </ul>
 */
public enum ResponseStatus {
    SUCCESS,
    FAILURE
}