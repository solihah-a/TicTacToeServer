package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  The main class for TicTacToe Server that sets up the socket server
 *
 */
public class SocketServer {
	/**
	 * Used for printing server logs of different levels
	 */
	private final Logger LOGGER;

	/*
	The socket server's port number
	 */
	private final int PORT;

	/**
	 * ServerSocket instance
	 */
	private ServerSocket serverSocket;

	/**
	 * The main function of the application
	 * It instantiates the class, sets up the server and start accepting client's request
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			SocketServer socketServer = new SocketServer();
			socketServer.setup();
			socketServer.startAcceptingRequest();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Default constructor with default port = 5000
	 *
	 * @throws Exception when invalid port is provided
	 */
	public SocketServer() throws Exception{
		this(5000);
	}

	/**
	 * Constructor that set the {@link #PORT} attribute
	 *
	 * @param port The socket server's port number
	 * @throws Exception when invalid port is provided
	 */
	public SocketServer(int port) throws Exception{
		if(port < 0){
			throw new Exception("Port number cannot be negative");
		}
		PORT = port;
		LOGGER = Logger.getLogger(SocketServer.class.getName());
	}

	/**
	 * Sets up the socket server
	 */
	private void setup() {
		try {
			serverSocket = new ServerSocket(PORT);
			LOGGER.log(Level.INFO, "Server Initialization Succeeded"
					+ "\nServer Host Name: " + InetAddress.getLocalHost().getHostName()
					+ "\nServer IP: " + InetAddress.getLocalHost().getHostAddress()
					+ "\nServer Port Number: " + PORT);
		} catch (UnknownHostException e) {
			LOGGER.log(Level.SEVERE,"Server Error: Unable to Resolve Host", e);
			System.exit(1);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,"Server Error: Server Initialization Failed", e);
			System.exit(1);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Server Error: Unknown Exception Occurred", e);
			System.exit(1);
		}
	}

	/**
	 * Start accepting client's request
	 */
	private void startAcceptingRequest() {
		try {
			// Accept socket connection from the first player and create a new handler to handle all connections
			Socket socketPlayer1 = serverSocket.accept();
			LOGGER.log(Level.INFO,"New Socket Client Connect with IP: " + socketPlayer1.getRemoteSocketAddress());
			ServerHandler serverHandlerPlayer1 = new ServerHandler(socketPlayer1, "Player1");
			serverHandlerPlayer1.start();

			// Accept socket connection from the second player and create a new handler to handle all connections
			Socket socketPlayer2 = serverSocket.accept();
			LOGGER.log(Level.INFO,"New Socket Client Connect with IP: " + socketPlayer2.getRemoteSocketAddress());
			ServerHandler serverHandlerPlayer2 = new ServerHandler(socketPlayer2, "Player2");
			serverHandlerPlayer2.start();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,"Server Error: Client Connection Failed", e);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Server Error: Unknown Exception Occurred", e);
		}
	}

	/**
	 * Getter for PORT attribute
	 *
	 * @return PORT
	 */
	public int getPORT() {
		return PORT;
	}
}
