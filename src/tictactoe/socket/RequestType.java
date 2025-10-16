package tictactoe.socket;

/**
 * Enumerates the types of requests that can be sent from the client to the server.
 * <ul>
 *      <li>REGISTER: Sent when a user wants to register in the game for the first time.</li>
 *      <li>LOGIN: Sent when a user wants to log into the game.</li>
 *      <li>UPDATE_PAIRING: Sent periodically to the server to ask for all pairing updates
 *          (i.e., available players, new game invitation, invitation response).</li>
 *      <li>SEND_INVITATION: Sent when a player selects an opponent to play a game.</li>
 *      <li>ACCEPT_INVITATION: Sent when a player receives a game invitation and decides to accept it.</li>
 *      <li>DECLINE_INVITATION: Sent when a player receives a game invitation and decides to decline it.</li>
 *      <li>ACKNOWLEDGE_RESPONSE: Sent after a client receives a game response to their invitation
 *          and has acknowledgment.</li>
 *      <li>REQUEST_MOVE: Sent periodically during gameplay to ask for the opponent's move.</li>
 *      <li>SEND_MOVE: Sent during the gameplay to transmit a game move.</li>
 *      <li>ABORT_GAME: Sent when a user wants to abort an ongoing game.</li>
 *      <li>COMPLETE_GAME: Sent when a user receives a final move to indicate a game is over.</li>
 * </ul>
 */
public enum RequestType
{
    REGISTER,
    LOGIN,
    UPDATE_PAIRING,
    SEND_INVITATION,
    ACCEPT_INVITATION,
    DECLINE_INVITATION,
    ACKNOWLEDGE_RESPONSE,
    REQUEST_MOVE,
    SEND_MOVE,
    ABORT_GAME,
    COMPLETE_GAME
}
