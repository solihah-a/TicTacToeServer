package tictactoe.server;

/**
 * A class that helps SocketServer handle individual client communication.
 * Each instance of this class manages the I/O for a single client connection
 * and runs in a separate thread.
 */
public class ServerHandler extends Thread{

    /**
     * A default constructor for the ServerHandler class.
     * In a full implementation, it would typically require a Socket parameter.
     */
    public ServerHandler() {
        super();
    }

    /**
     * The override method from the Thread class. This is where the core logic
     * for receiving, processing, and responding to client requests will run.
     * It should be empty for now.
     */
    @Override
    public void run() {
        // Core client-handling logic (reading Request, sending Response) will go here.
    }

    /**
     * A function that safely closes the client's connection and associated resources (streams/socket).
     * It should be empty for now.
     */
    public void close() {
        // Implementation for closing I/O streams and the client socket will go here.
    }
}
