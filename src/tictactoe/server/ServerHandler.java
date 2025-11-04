package tictactoe.server;

import tictactoe.model.Event;
import tictactoe.model.EventStatus;
import tictactoe.socket.Request;
import tictactoe.socket.Response;
import tictactoe.socket.GamingResponse;
import tictactoe.socket.RequestType;
import tictactoe.socket.ResponseStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that helps SocketServer handle individual client communication.
 * Each instance of this class manages the I/O for a single client connection
 * and runs in a separate thread.
 */
public class ServerHandler extends Thread{

    // Logger for logging server handler actions
    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());

    /**
     * An object of the class Event. It will be used to store the game move.
     */
    public static Event event;

    /**
     * An object of the Socket class. It is used to save the socket connection.
     */
    private final Socket socket;

    /**
     * Username of the current client of this socket connection.
     */
    private final String currentUsername;

    /**
     * An object of the class DataInputStream. It will be used to get clients' requests.
     */
    private DataInputStream inputStream;

    /**
     * An object of the class DataOutputStream. It will be used to send a response to the client.
     */
    private DataOutputStream outputStream;

    /**
     * Gson object used for serialization and deserialization.
     */
    private final Gson gson;

    // Static initializer block runs only once when the class is loaded.
    static {
        event = new Event(
                0,              // eventId (Integer, set to 0/default)
                null,                  // sender (String, set to null)
                null,                  // opponent (String, set to null)
                (EventStatus) null,    // status (EventStatus, set to null)
                null,                  // turn (String, set to null)
                -1                     // move (Integer, set to -1 as required)
        );

        // Log the initialization
        Logger.getLogger(ServerHandler.class.getName())
                .log(Level.INFO, "Shared static Event object initialized with move = -1.");
    }

    /**
     * The parameterized constructor for the ServerHandler class.
     * Initializes the socket connection and the client's username.
     *
     * @param socket The connected client socket.
     * @param username A unique string to identify the user.
     */
    public ServerHandler(Socket socket, String username) {
        // Initialize attributes
        this.socket = socket;
        this.currentUsername = username;

        // Initialize Gson with Null Serialization (Completes Task 5)
        // GsonBuilder allows custom configuration, including serializing nulls.
        this.gson = new GsonBuilder()
                .serializeNulls() // Configuration to include null fields in the JSON output
                .create();

        // Initialize I/O streams (Task 4)
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize I/O streams for user: " + username, e);
        }

        // Log the new connection
        LOGGER.log(Level.INFO, "New handler created for user: " + username);
    }

    /**
     * The override method from the Thread class. This is where the core logic
     * for receiving, processing, and responding to client requests will run.
     * Implementation in progress
     */
    @Override
    public void run() {
        // Core client-handling logic (reading Request, sending Response) will go here.
        LOGGER.log(Level.INFO, "Handler thread started for user: " + currentUsername);
    }

    /**
     * A function that safely closes the client's connection and associated resources (streams/socket).
     *  Implementation in progress
     */
    public void close() {
        // Implementation for closing I/O streams and the client socket will go here.
        LOGGER.log(Level.INFO, "Connection closing logic for user: " + currentUsername + " to be implemented.");
    }

    /**
     * The main dispatcher function that takes a Request, processes it based on its
     * type, and returns an appropriate Response.
     *
     * @param request The Request object received from the client.
     * @return A Response or GamingResponse object with the operation result.
     */
    public Response handleRequest(Request request) {
        // Check if the request object is null or has no type defined
        if (request == null || request.getType() == null) {
            return new Response(ResponseStatus.FAILURE, "Invalid request received: Missing type.");
        }

        // Use a switch statement to dispatch based on RequestType
        switch (request.getType()) {
            case SEND_MOVE:
                try {
                    // Deserialize the move (an Integer) from the Request's data attribute
                    Integer move = this.gson.fromJson(request.getData(), Integer.class);
                    return handleSendMove(move);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to deserialize SEND_MOVE data.", e);
                    return new Response(ResponseStatus.FAILURE, "Error processing move data.");
                }

            case REQUEST_MOVE:
                return handleRequestMove();

            default:
                // Return a failed response if the type is not recognized
                return new Response(ResponseStatus.FAILURE, "Unknown request type: " + request.getType());
        }
    }

    /**
     * Handles the SEND_MOVE request. Stores the move and checks for turn violation.
     *
     * @param move The integer move (0-8) sent by the client.
     * @return A standard Response with SUCCESS or FAILURE status.
     */
    public Response handleSendMove(Integer move) {
        // Check for consecutive moves (Turn check)
        // If the current user made the last move, it's not their turn.
        if (this.currentUsername.equals(event.getTurn())) {
            String message = "It is not your turn, " + this.currentUsername + ". Wait for opponent.";
            LOGGER.log(Level.WARNING, message);
            return new Response(ResponseStatus.FAILURE, message);
        }

        // Set the move and turn attribute of the static variable event
        event.setMove(move);
        event.setTurn(this.currentUsername);

        // Log the successful move storage
        LOGGER.log(Level.INFO, "Move " + move + " successfully stored by: " + this.currentUsername);

        // Return a standard SUCCESS Response
        return new Response(ResponseStatus.SUCCESS, "Move stored successfully. Opponent can now retrieve it.");
    }

    /**
     * Handles the REQUEST_MOVE request. Retrieves the move and clears it for the next turn.
     *
     * @return A GamingResponse with SUCCESS status and the move data.
     */
    public Response handleRequestMove() {
        // Get the move from the static variable event
        Integer lastMove = event.getMove();
        String lastTurn = event.getTurn();

        // Check if a valid move was made by the opponent
        // A move is valid if it's NOT -1 AND it was made by the opponent.
        boolean moveAvailable = lastMove != -1 && !this.currentUsername.equals(lastTurn);

        Integer moveToSend = -1; // Default: No move available
        String message = "No new move available from opponent.";

        if (moveAvailable) {
            moveToSend = lastMove;
            message = "New move received.";

            // Delete the move once it is sent to the opponent (Prepare for next move)
            event.setMove(-1);

            // Note: The turn attribute remains set to the opponent until the current user sends their move.

            LOGGER.log(Level.INFO, "Move " + moveToSend + " retrieved and cleared by: " + this.currentUsername);
        } else {
            // Log if the user requested a move when one wasn't available
            LOGGER.log(Level.INFO, this.currentUsername + " requested move, but none was available.");
        }

        return new GamingResponse(
                ResponseStatus.SUCCESS,
                message,
                moveToSend,
                null
        );
    }
}
