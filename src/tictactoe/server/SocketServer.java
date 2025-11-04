package tictactoe.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
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
     * It should be empty for now and will be implemented in later milestones.
     * This method will also dispatch a new ServerHandler instance for each connection.
     */
    public void startAcceptingRequest() {
        // Implementation for the main accept loop will go here.
        System.out.println("Server is now accepting requests (currently empty).");
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
