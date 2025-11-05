package tictactoe.server;

import tictactoe.model.*;
import tictactoe.socket.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that helps SocketServer handle individual client communication.
 * Each instance of this class manages the I/O for a single client connection
 * and runs in a separate thread.
 */
public class ServerHandler extends Thread {

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
     */
    @Override
    public void run() {
        String serializedRequest;
        Response serverResponse;

        // Loop indefinitely to keep receiving requests
        while (true) {
            try {
                //Read the client's serialized request (JSON string)
                serializedRequest = inputStream.readUTF();

                LOGGER.log(Level.INFO, "Received request from " + currentUsername + ": " + serializedRequest);

                //Deserialize the request using Gson
                Request clientRequest = gson.fromJson(serializedRequest, Request.class);

                //Use the helper function handleRequest() to get the response
                serverResponse = handleRequest(clientRequest);

            } catch (EOFException e) {
                LOGGER.log(Level.INFO, currentUsername + " disconnected (EOF). Ending handler thread.");
                break; // Exit the infinite loop
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO Error while reading request from " + currentUsername + ". Disconnecting.", e);
                break; // Exit the infinite loop on severe error
            } catch (Exception e) {
                // Catch any other unexpected error during processing
                LOGGER.log(Level.SEVERE, "Unexpected error processing request for " + currentUsername, e);

                // Create a generic failure response
                serverResponse = new Response(ResponseStatus.FAILURE, "Internal server error.");
            }

            // Send Response Section
            try {
                //Serialize the response
                String serializedResponse = gson.toJson(serverResponse);

                LOGGER.log(Level.INFO, "Sending response to " + currentUsername + ": " + serializedResponse);

                //Write and flush the serialized response using the output stream
                outputStream.writeUTF(serializedResponse);
                outputStream.flush();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO Error while writing response to " + currentUsername + ". Disconnecting.", e);
                break; // Exit the infinite loop
            }
        }

        // Finally call close() when the loop is exited
        close();
    }

    /**
     * A function that safely closes the client's connection and associated resources (streams/socket).
     */
    public void close() {
        LOGGER.log(Level.INFO, "Closing connection and streams for user: " + currentUsername);

        //Close DataOutputStream
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing output stream for " + currentUsername, e);
        }

        //Close DataInputStream
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing input stream for " + currentUsername, e);
        }

        //lose Socket connection
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing socket connection for " + currentUsername, e);
        }

        LOGGER.log(Level.INFO, "Resources successfully closed for user: " + currentUsername);
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
     * Handles the SEND_MOVE request. Stores the move and **notifies** the opponent.
     * This method is synchronized on the shared 'event' object.
     *
     * @param move The integer move (0-8) sent by the client.
     * @return A standard Response with SUCCESS or FAILURE status.
     */
    public Response handleSendMove(Integer move) {
        synchronized (event) {
            // Check for consecutive moves (Turn check)
            if (this.currentUsername.equals(event.getTurn())) {
                String message = "It is not your turn, " + this.currentUsername + ". Wait for opponent.";
                LOGGER.log(Level.WARNING, message);
                return new Response(ResponseStatus.FAILURE, message);
            }

            // Set the move and update the turn to the current user (X or O)
            event.setMove(move);
            event.setTurn(this.currentUsername);

            LOGGER.log(Level.INFO, "Move " + move + " successfully stored by: " + this.currentUsername);

            // CRITICAL FIX: Notify the opponent's waiting thread
            event.notifyAll(); // Wakes up all threads blocked on event.wait()

            return new Response(ResponseStatus.SUCCESS, "Move stored successfully. Opponent can now retrieve it.");
        }
    }

    /**
     * Handles the REQUEST_MOVE request. **Blocks** the thread until a move is available.
     * This method is synchronized on the shared 'event' object.
     *
     * @return A GamingResponse with SUCCESS status and the move data.
     */
    public Response handleRequestMove() {
        synchronized (event) {

            // Loop and wait if the move hasn't been made OR if the move was made by this player
            while (event.getMove() == -1 || this.currentUsername.equals(event.getTurn())) {

                LOGGER.log(Level.INFO, this.currentUsername + " waiting for opponent's move...");

                try {
                    // CRITICAL FIX: Block the thread
                    // wait() releases the lock and blocks the thread until notifyAll() is called.
                    event.wait();
                    LOGGER.log(Level.INFO, this.currentUsername + " woken up, checking move...");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new Response(ResponseStatus.FAILURE, "Handler interrupted while waiting.");
                }
                // The thread wakes up and loops back to check the while condition again.
            }

            // --- Execution only reaches here once a valid opponent move is available ---

            Integer moveToSend = event.getMove();

            // Clear the move once it is sent to the waiting client
            event.setMove(-1);

            LOGGER.log(Level.INFO, "Move " + moveToSend + " retrieved and cleared by: " + this.currentUsername);

            // The client's polling request is now fulfilled with the correct move data.
            return new GamingResponse(
                    ResponseStatus.SUCCESS,
                    "New move received.",
                    moveToSend,
                    true
            );
        }
    }
}
