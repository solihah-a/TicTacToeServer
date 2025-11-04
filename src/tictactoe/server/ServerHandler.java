package tictactoe.server;

import tictactoe.model.Event;
import com.google.gson.Gson;

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
    private Gson gson;


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

        // Log the new connection
        LOGGER.log(Level.INFO, "New handler created for user: " + username);

        // Initialize I/O streams and Gson here (Implementation in a later task)
        this.gson = new Gson();
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize I/O streams for user: " + username, e);
        }
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

    // Placeholder for helper methods required in later tasks:

    // public Response handleRequest(Request request) { ... }
    // public Response handleRequestMove() { ... }
    // public Response handleSendMove(String move) { ... }
}
