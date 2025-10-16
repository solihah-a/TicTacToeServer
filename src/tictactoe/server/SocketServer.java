package tictactoe.server;

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
     */
    public SocketServer(int port) {
        this.PORT = port;
    }

    /**
     * A static Java main method that instantiates this class, calls setup(), and
     * starts accepting requests.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Instantiate the server (uses default port 5000)
        SocketServer server = new SocketServer();

        System.out.println("Starting TicTacToe Server on port " + server.getPort() + "...");

        // Setup the server
        server.setup();

        // Start listening for connections
        server.startAcceptingRequest();
    }

    /**
     * A method that sets up the server for connection.
     * It should be empty for now and will be implemented in later milestones.
     */
    public void setup() {
        // Implementation for setting up ServerSocket will go here.
        System.out.println("Server setup complete (currently empty).");
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
