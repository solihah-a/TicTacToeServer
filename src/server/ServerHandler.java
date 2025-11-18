package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.Event;
import socket.GamingResponse;
import socket.Request;
import socket.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  A class that helps SocketServer Handle individual user communication. This class extends {@link Thread}
 *
 */
public class ServerHandler extends Thread {

	/**
	 * Used for printing logs of different levels
	 */
	private final Logger LOGGER;

	/**
	 * Socket connection with a client
	 */
	private final Socket socket;

	/**
	 * Input stream to get clients {@link socket.Request}
	 */
	private final DataInputStream inputStream;

	/**
	 * Output Stream to send client a {@link socket.Response}
	 */
	private final DataOutputStream outputStream;

	/**
	 * Used to serialize/deserialize objects
	 */
	private final Gson gson;

	/**
	 * Will be used to store game move
	 * A RDBMS will be used to store game move in later milestones
	 */
	public static Event event = new Event(1, null, null, null, null, -1);


	/**
	 * Username of the current client of this socket connection
	 */
	public String currentUsername;

	/**
	 * Default constructor
	 * Initializes all attributes
	 * @param socket Client's socket connection after server accepts
	 * @param username Unique username for the client
	 * @throws IOException When no valid input or output stream from socket
	 */
	public ServerHandler(Socket socket, String username) throws IOException {
		LOGGER = Logger.getLogger(ServerHandler.class.getName());

		this.socket = socket;
		this.currentUsername = username;
		this.gson = new GsonBuilder().serializeNulls().create();
		this.inputStream = new DataInputStream(socket.getInputStream());
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}

	/**
	 * Runs immediately after the thread is started
	 * The function continuously waits for a client request and sends a response
	 * Until a client disconnects
	 */
	@Override
	public void run() {
		// Keep accepting request until client disconnects are send invalid request
		while (true) {
			try {
				String serializedRequest = inputStream.readUTF(); // read/receive clients request (blocking operation)
				Request request = gson.fromJson(serializedRequest, Request.class); // deserialized the request
				LOGGER.log(Level.INFO,"Client Request: " + currentUsername + " - " + request.getType());

				Response response = handleRequest(request); // get response to client's request
				String serializedResponse = gson.toJson(response); // serialize the response
				outputStream.writeUTF(serializedResponse); // write/send the response
				outputStream.flush(); // Flush the stream, force response to go
			} catch (EOFException e) {
				LOGGER.log(Level.INFO,"Server Info: Client Disconnected: " + currentUsername + " - " + socket.getRemoteSocketAddress());
				closeSocket();
				break;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE,"Server Info: Client Connection Failed", e);
			}  catch (JsonSyntaxException e) {
				LOGGER.log(Level.SEVERE,"Server Info: Serialization Error", e);
			}
		}
	}

	/**
	 * Closes clients connection
	 */
	private void closeSocket() {
		// Close socket connection and all IO streams
		try {
			socket.close();
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,"Server Info: Unable to close socket", e);
		}
	}

	/**
	 * Handles all clients {@link Request.RequestType}
	 * @param request The request to handle
	 * @return Response to client's request
	 */
	private Response handleRequest(Request request) {
		// Decide which function to call for different types of request
		switch (request.getType()) {
			case REQUEST_MOVE:
				return handleRequestMove();
			case SEND_MOVE:
				int move = gson.fromJson(request.getData(), Integer.class);
				return handleSendMove(move);
			default: // Invalid request type
				return new Response(Response.ResponseStatus.FAILURE, "Invalid Request");
		}
	}


	/**
	 * Handle request of type {@link Request.RequestType#REQUEST_MOVE}
	 * @return a game response with move information
	 */
	private GamingResponse handleRequestMove() {
		GamingResponse response = new GamingResponse();
		response.setStatus(Response.ResponseStatus.SUCCESS);
		// check if there is a valid move made by my opponent
		if (event.getMove() != -1 && !event.getTurn().equals(currentUsername)){
			response.setMove(event.getMove());
			// Delete the move
			event.setMove(-1);
			event.setTurn(null);
		}else{
			response.setMove(-1);
		}
		return response;
	}

	/**
	 * Handle request of type {@link Request.RequestType#SEND_MOVE}
	 * @param move the move to be added to the game
	 * @return a standard response
	 */
	private Response handleSendMove(int move) {
		if(move < 0 || move > 8){ // Check for valid move
			return new Response(Response.ResponseStatus.FAILURE, "Invalid Move");
		}
		if(event.getTurn() == null || !event.getTurn().equals(currentUsername)) {
			// Save the move in the server and return a standard Response
			event.setMove(move);
			event.setTurn(currentUsername);
			return new Response(Response.ResponseStatus.SUCCESS, "Move Added");
		}else{
			return new Response(Response.ResponseStatus.FAILURE, "Not your turn to move");
		}
	}

}
