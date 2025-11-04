package tictactoe.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The socket server controller class. It creates the socket server and accepts all
 * clients' connections. This class serves as the main entry point for the TicTacToe Server.
 */
public class SocketServer
{
    /**
     * Port number the socket server will listen to connections on.
     */
    private final int PORT;
    private static final int DEFAULT_PORT = 5000;
    /**
     * An object of the ServerSocket class used for creating the socket server.
     */
    private ServerSocket serverSocket;
    // Logger for logging server information and errors
    private static final Logger LOGGER = Logger.getLogger(SocketServer.class.getName());

    /**
     * A default constructor for the class. Calls the parameterized constructor
     * with a default port value of 5000.
     */
    public SocketServer() {
        this(DEFAULT_PORT);
    }

    /**
     * A constructor that sets the constant port number.
     *
     * @param port The port number for the server to listen on.
     * @throws IllegalArgumentException if an invalid port (outside 1-65535) is provided.
     */
    public SocketServer(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port);
        }
        this.PORT = port;
    }

    /**
     * A static Java main method that instantiates this class, calls setup(), and
     * starts accepting requests.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            SocketServer server = new SocketServer();
            server.setup();
            server.startAcceptingRequest();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Server failed to start due to invalid port.", e);
        }
    }

    /**
     * A method that sets up the server for connection.
     */
    public void setup() {
        try {
            // Initialize the ServerSocket
            this.serverSocket = new ServerSocket(this.PORT);

            // Log server information
            InetAddress hostAddress = InetAddress.getLocalHost();
            String logMessage = String.format("""
                    
                    --- Server Ready ---
                    Hostname: %s
                    Host Address: %s
                    Port: %d
                    --------------------""",
                    hostAddress.getHostName(),
                    hostAddress.getHostAddress(),
                    this.PORT
            );
            LOGGER.log(Level.INFO, logMessage);
        } catch (IOException e) {
            // Handle exceptions related to socket creation (e.g., port already in use)
            LOGGER.log(Level.SEVERE, "Could not set up the server socket on port " + this.PORT, e);
            // Exit the application if setup fails
            System.exit(1);
        }
    }

    /**
     * A method that sets up the server to start accepting client connections.
     * This method will also dispatch a new ServerHandler instance for each connection.
     */
    public void startAcceptingRequest() {
        LOGGER.log(Level.INFO, "Server is now listening for two client connections...");
        Socket clientSocket;
        String username;

        try {
            //Accept First Client (Player X)
            username = "Player X";
            LOGGER.log(Level.INFO, "Waiting for " + username + " connection...");

            // The accept() function is a blocking operation.
            clientSocket = serverSocket.accept();

            LOGGER.log(Level.INFO, username + " connected from: " + clientSocket.getInetAddress().getHostAddress());

            // Create and start a new thread for the first client
            ServerHandler handlerX = new ServerHandler(clientSocket, username);
            handlerX.start();


            // Accept Second Client (Player O)
            username = "Player O";
            LOGGER.log(Level.INFO, "Waiting for " + username + " connection...");

            // The server blocks here until the second client connects.
            clientSocket = serverSocket.accept();

            LOGGER.log(Level.INFO, username + " connected from: " + clientSocket.getInetAddress().getHostAddress());

            // Create and start a new thread for the second client
            ServerHandler handlerO = new ServerHandler(clientSocket, username);
            handlerO.start();

            LOGGER.log(Level.INFO, "Both players connected. Game setup is complete.");

        } catch (IOException e) {
            // Handle exceptions during accept() operation (e.g., socket closed)
            LOGGER.log(Level.SEVERE, "An error occurred while accepting client connections.", e);
        }
    }

    /**
     * A getter for the PORT attribute.
     *
     * @return The port number the socket server is listening on.
     */
    public int getPort() {
        return PORT;
    }
}
