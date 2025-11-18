package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.Event;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import socket.GamingResponse;
import socket.PairingResponse;
import socket.Request;
import socket.Response;
import socket.Response.ResponseStatus;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles I/O communication between the server and a single client connection.
 * This class extends Thread to enable concurrent handling of multiple client connections.
 * <p>
 * Each ServerHandler instance manages all communication with one connected client,
 * processing various request types and maintaining the client session state. The handler
 * runs in its own thread to allow the main server to continue accepting new connections
 * while serving existing clients.
 * <p>
 * This class is responsible for processing all request types from clients, including
 * login, registration, game invitations, and gameplay moves.
 */
public class ServerHandler extends Thread {

	/**
	 * Gson class used to do serialization
	 */
	private final Gson gson;

	/**
	 * The eventId of the Event in the database that this handler is currently using for gameplay.
	 * Default value -1 indicates no current event has been set.
	 */
	private int currentEventId = -1;

	/**
	 * Stores the client connection.
	 */
	private final Socket socket;

	/**
	 * Stores the client's username.
	 */
	private String currentUsername;

	/**
	 * Input stream for receiving data from the client.
	 */
	private DataInputStream dataInputStream;

	/**
	 * Output stream for sending data to the client.
	 */
	private DataOutputStream dataOutputStream;

	/**
	 * Logger for server handler responses.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

	/**
	 * Default constructor that creates a ServerHandler instance.
	 *
	 * @param socket The socket representing the client connection.
	 */
	public ServerHandler(Socket socket) {
		this.socket = socket;
		this.gson = new GsonBuilder().serializeNulls().create();

		try {
			this.dataInputStream = new DataInputStream(socket.getInputStream());
			this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			LOGGER.error("Error can not establish input or output stream variables", e);
		}
	}

	/**
	 * Main request handler that processes client requests and returns appropriate responses.
	 *
	 * @param request the request object received from the client
	 * @return a response object based on the request type
	 */
	public Response handleRequest(Request request) {
		if (request == null) {
			LOGGER.warn("Received null request");
			return new Response(ResponseStatus.FAILURE, "Request cannot be null");
		}

		// Use switch-case to decide among the two request types
		switch (request.getType()) {
			case REGISTER:
				User user = gson.fromJson(request.getData(), User.class);
				return handleRegister(user);
			case SEND_MOVE:
				return handleSendMoveRequest(request);
			case REQUEST_MOVE:
				return handleRequestMove();
			case LOGIN:
				User loginUser = gson.fromJson(request.getData(), User.class);
				return handleLogin(loginUser);
			case UPDATE_PAIRING:
				return handleUpdatePairing();
			case SEND_INVITATION:
				String opponent = request.getData();
				return handleSendInvitation(opponent);
			case ACCEPT_INVITATION:
				return handleAcceptInvitation(Integer.parseInt(request.getData()));
			case DECLINE_INVITATION:
				return handleDeclineInvitation(Integer.parseInt(request.getData()));
			case ACKNOWLEDGE_RESPONSE:
				int eventId = Integer.parseInt(request.getData());
				return handleAcknowledgeResponse(eventId);
			case ABORT_GAME:
				return handleAbortGame();
			case COMPLETE_GAME:
				return handleCompleteGame();
			default:
				// Return failed response if neither of the two types is sent
				LOGGER.warn("Unsupported request type: {}", request.getType());
				return new Response(ResponseStatus.FAILURE, "Unsupported request type: " + request.getType());
		}
	}

	/**
	 * Handles SEND_MOVE requests by deserializing the move data from the request
	 * and delegating to the handleSendMove(move) function for processing.
	 *
	 * @param request the SEND_MOVE request containing move data in the data attribute
	 * @return a response indicating success or failure of move processing
	 */
	private Response handleSendMoveRequest(Request request) {
		try {
			// Deserialize the data attribute to get the move
			int move = Integer.parseInt(request.getData());

			// Call the separate function with just the move parameter
			return handleSendMove(move);

		} catch (NumberFormatException e) {
			// Add logging for invalid move format
			LOGGER.warn("Invalid move format: {}", request.getData());
			return new Response(ResponseStatus.FAILURE, "Invalid move format: " + request.getData());
		} catch (Exception e) {
			// Log the exception with stack trace
			LOGGER.warn("Error processing move.", e);
			return new Response(ResponseStatus.FAILURE, "Error processing move: " + e.getMessage());
		}
	}

	/**
	 * Handles SEND_MOVE requests by deserializing the move data from the request
	 * and delegating to the handleSendMove(move) function for processing.
	 *
	 * @param move the integer representing the move to be processed
	 * @return a response indicating success or failure of the move
	 */
	private Response handleSendMove(int move) {
		// Get the current username
		String currentUser = getCurrentUsername();
		try {
			// Retrieve the event from the database using currentEventId
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);
			if (event == null) {
				LOGGER.warn("No event found for eventId {}", currentEventId);
				return new Response(ResponseStatus.FAILURE, "No active game event found");
			}

			// Check to see if the last move was not made by the same user
			if (event.getTurn() != null && event.getTurn().equals(currentUser)) {
				return new Response(ResponseStatus.FAILURE, "Cannot make consecutive moves. Wait for opponent's move.");
			}

			// Set the move and turn on the event and persist to DB
			event.setMove(move);
			event.setTurn(currentUser);
			DatabaseHelper.getInstance().updateEvent(event);

			// Return a standard Response with SUCCESS status and appropriate message
			return new Response(ResponseStatus.SUCCESS, "Move " + move + " received successfully");
		} catch (SQLException e) {
			LOGGER.error("Database error while saving move", e);
			return new Response(ResponseStatus.FAILURE, "Database error: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error while saving move", e);
			return new Response(ResponseStatus.FAILURE, "Error processing move: " + e.getMessage());
		}
	}

	/**
	 * Processes the move by updating the game state and ensuring valid turn order.
	 * This method retrieves the corresponding {@link Event} from the database using
	 * the handler's {@code currentEventId}, checks to see if the opponent still wants to play or
	 * aborted, validates turn order, sets the move and the current turn on that Event, and
	 * persists the change via
	 * {@link server.DatabaseHelper#updateEvent(Event)}.
	 *
	 * @return a GamingResponse containing the opponent's move and game status
	 */
	private GamingResponse handleRequestMove() {
		try {
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);
			GamingResponse response;

			if (event == null) {
				// No event found - return move = -1
				response = new GamingResponse(-1, true);
			} else {
				// check the status of the opponent before proceeding to check for valid moves
				if (event.getStatus() == Event.EventStatus.ABORTED) {
					// opponent aborted the game
					response = new GamingResponse(-1, false);
					response.setMessage("Opponent Abort");
				} else if (event.getStatus() == Event.EventStatus.COMPLETED) {
					// opponent does not want to play
					response = new GamingResponse(-1, false);
					response.setMessage("Opponent Deny Play Again");
				} else {
					int move = event.getMove();
					String user = event.getTurn();

					// Check if there is a valid move made by the opponent, else set the move as -1
					if ((move == -1) || (user == null) || (user.equals(this.currentUsername))) {
						// No move available from opponent - create response with move = -1
						response = new GamingResponse(-1, true);
					} else {
						// Valid move available - create response with the actual move
						response = new GamingResponse(move, true);

						// Delete the move and clear turn once it is sent to the opponent and persist
						event.setMove(-1);
						event.setTurn(null);
						DatabaseHelper.getInstance().updateEvent(event);
					}
				}
			}

			// Set the response status
			response.setStatus(ResponseStatus.SUCCESS);
			return response;
		} catch (SQLException e) {
			LOGGER.error("Database error while requesting move", e);
			GamingResponse response = new GamingResponse(-1, true);
			response.setStatus(ResponseStatus.FAILURE);
			response.setMessage("Database error: " + e.getMessage());
			return response;
		} catch (Exception e) {
			LOGGER.error("Unexpected error while requesting move", e);
			GamingResponse response = new GamingResponse(-1, true);
			response.setStatus(ResponseStatus.FAILURE);
			response.setMessage("Error processing request: " + e.getMessage());
			return response;
		}
	}

	/**
	 * Handles user registration request
	 *
	 * @param user The User object containing registration details
	 * @return Response indicating success or failure of registration
	 */
	private Response handleRegister(User user) {
		try {
			// Check if username already exists in the database
			if (DatabaseHelper.getInstance().isUsernameExists(user.getUsername())) {
				return new Response(ResponseStatus.FAILURE, "Username '" + user.getUsername() + "' already exists. Please choose a different username.");
			}

			// Add the new user to the database
			DatabaseHelper.getInstance().createUser(user);

			return new Response(ResponseStatus.SUCCESS, "User '" + user.getUsername() + "' registered successfully!");
		} catch (SQLException e) {
			LOGGER.error("Database error during registration", e);
			return new Response(ResponseStatus.FAILURE, "Database error during registration: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error during registration", e);
			return new Response(ResponseStatus.FAILURE, "Error during registration: " + e.getMessage());
		}
	}

	/**
	 * Handles SEND_INVITATION requests when a user selects an opponent to invite.
	 *
	 * @param opponent the username of the player to invite
	 * @return response indicating success or failure
	 */
	private Response handleSendInvitation(String opponent) {
		// check if user is logged in
		if (currentUsername == null || currentUsername.isEmpty()) {
			return new Response(ResponseStatus.FAILURE, "user is not logged in");
		}

		try {
			// check if opponent is available to receive an invitation
			if (!DatabaseHelper.getInstance().isUserAvailable(opponent)) {
				return new Response(ResponseStatus.FAILURE, "opponent is not available");
			}

			Event event = new Event();
			event.setSender(currentUsername);
			event.setOpponent(opponent);
			event.setStatus(Event.EventStatus.PENDING);
			event.setMove(-1);

			// save event to database
			DatabaseHelper.getInstance().createEvent(event);

			return new Response(ResponseStatus.SUCCESS, "invitation sent successfully");
		} catch (Exception e) {
			LOGGER.error("error sending invitation", e);
			return new Response(ResponseStatus.FAILURE, "error message: " + e.getMessage());
		}
	}

	/**
	 * After successful login, a user can now start requesting pairing updates
	 *
	 * @return PairingResponse containing available users, invitations, responses, or a failure if not logged in
	 */
	private PairingResponse handleUpdatePairing() {
		// checks to see if the user is logged in
		if (currentUsername == null || currentUsername.isEmpty()) {
			PairingResponse response = new PairingResponse(null, null, null);
			response.setStatus(ResponseStatus.FAILURE);
			response.setMessage("user is not logged in");
			return response;
		}

		try {
			// retrieve pairing information
			List<User> availableUsers = DatabaseHelper.getInstance().getAvailableUsers(currentUsername);
			Event userInvitation = DatabaseHelper.getInstance().getUserInvitation(currentUsername);
			Event userInvitationResponse = DatabaseHelper.getInstance().getUserInvitationResponse(currentUsername);

			// create and return PairingResponse
			PairingResponse response = new PairingResponse(availableUsers, userInvitation, userInvitationResponse);
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("pairing information retrieved successfully");
			return response;

		} catch (Exception e) {
			LOGGER.error("error while retrieving pairing information", e);
			PairingResponse response = new PairingResponse(null, null, null);
			response.setStatus(ResponseStatus.FAILURE);
			response.setMessage("error while retrieving pairing information: " + e.getMessage());
			return response;
		}
	}

	/**
	 * Handles the acceptance of a pending game invitation by the opponent.
	 * This method retrieves the event associated with the given {@code eventId}
	 *
	 * @param eventId the unique identifier of the event (invitation) to be accepted
	 * @return a {@link Response} object indicating whether the acceptance was successful or failed,
	 * with an appropriate message
	 */

	public Response handleAcceptInvitation(int eventId) {
		try {
			// Retrieve the event from the database
			Event event = DatabaseHelper.getInstance().getEvent(eventId);

			// Check if the event exists
			if (event == null) {
				return new Response(ResponseStatus.FAILURE, "Event not found.");
			}

			// Check if the event is still pending
			if (event.getStatus() != Event.EventStatus.PENDING) {
				return new Response(ResponseStatus.FAILURE, "This invitation is no longer available.");
			}

			// Check if the opponent is the current user
			if (!event.getOpponent().equals(currentUsername)) {
				return new Response(ResponseStatus.FAILURE, "You are not authorized to accept this invitation.");
			}

			// Abort any other pending invitations for this user
			DatabaseHelper.getInstance().abortAllUserEvents(currentUsername);

			// Update event status to ACCEPTED
			event.setStatus(Event.EventStatus.ACCEPTED);
			DatabaseHelper.getInstance().updateEvent(event);

			// Set current event ID
			currentEventId = eventId;

			// Return success message
			return new Response(ResponseStatus.SUCCESS, "Invitation accepted successfully.");
		} catch (SQLException e) {
			LOGGER.error("Database error while accepting invitation", e);
			return new Response(ResponseStatus.FAILURE, "Database error while accepting invitation: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error while accepting invitation", e);
			return new Response(ResponseStatus.FAILURE, "Error while accepting invitation: " + e.getMessage());
		}
	}

	/**
	 * Handles DECLINE_INVITATION by validating the invitation and marking it as DECLINED.
	 *
	 * @param eventId the ID of the invitation to decline
	 * @return a Response indicating success or failure
	 */
	private Response handleDeclineInvitation(int eventId) {
		try {
			Event event = DatabaseHelper.getInstance().getEvent(eventId);

			if (event == null) {
				return new Response(ResponseStatus.FAILURE, "Event with ID " + eventId + " does not exist.");
			}

			if (event.getStatus() != Event.EventStatus.PENDING) {
				return new Response(ResponseStatus.FAILURE, "Only pending invitations can be declined.");
			}

			if (!event.getOpponent().equals(currentUsername)) {
				return new Response(ResponseStatus.FAILURE, "You are not authorized to decline this invitation.");
			}

			event.setStatus(Event.EventStatus.DECLINED);
			DatabaseHelper.getInstance().updateEvent(event);

			return new Response(ResponseStatus.SUCCESS, "Invitation declined successfully.");

		} catch (SQLException e) {
			LOGGER.error("Database error while declining invitation", e);
			return new Response(ResponseStatus.FAILURE, "Database error while declining invitation: " + e.getMessage());
		}
	}


	/**
	 * Handles user login request
	 *
	 * @param user The User object containing login credentials
	 * @return Response indicating success or failure of login
	 */
	private Response handleLogin(User user) {
		try {
			// Get the user with the corresponding username from database
			User dbUser = DatabaseHelper.getInstance().getUser(user.getUsername());

			// Validate user exists
			if (dbUser == null) {
				return new Response(ResponseStatus.FAILURE, "Username '" + user.getUsername() + "' not found. Please register first.");
			}

			// Validate password is correct
			if (!dbUser.getPassword().equals(user.getPassword())) {
				return new Response(ResponseStatus.FAILURE, "Invalid password for user '" + user.getUsername() + "'.");
			}

			// All inputs are valid - set currentUsername, set user as online, and update database
			this.currentUsername = user.getUsername();
			dbUser.setOnline(true);
			DatabaseHelper.getInstance().updateUser(dbUser);

			return new Response(ResponseStatus.SUCCESS, "User '" + user.getUsername() + "' logged in successfully!");
		} catch (SQLException e) {
			LOGGER.error("Database error during login", e);
			return new Response(ResponseStatus.FAILURE, "Database error during login: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error during login", e);
			return new Response(ResponseStatus.FAILURE, "Error during login: " + e.getMessage());
		}
	}

	/**
	 * Handles acknowledge response request after an invitation response
	 *
	 * @param eventId The eventId of the initial invitation
	 * @return Response indicating success or failure of acknowledgment
	 */
	private Response handleAcknowledgeResponse(int eventId) {
		try {
			// Use the database helper function getEvent() to retrieve the Event object
			Event event = DatabaseHelper.getInstance().getEvent(eventId);

			// Case 1: Check if the event exists, and if the sender of the event is the current username
			if (event == null) {
				return new Response(ResponseStatus.FAILURE, "Event with ID " + eventId + " not found.");
			}

			if (!event.getSender().equals(this.currentUsername)) {
				return new Response(ResponseStatus.FAILURE, "You are not the sender of this invitation.");
			}

			// Get the current status of the event
			Event.EventStatus currentStatus = event.getStatus();

			// Case 2: If the response was DECLINED, set the status to ABORTED
			if (currentStatus == Event.EventStatus.DECLINED) {
				event.setStatus(Event.EventStatus.ABORTED);
				DatabaseHelper.getInstance().updateEvent(event);
				return new Response(ResponseStatus.SUCCESS, "Game invitation declined and aborted successfully.");
			} else if (currentStatus == Event.EventStatus.ACCEPTED) {
				// Case 3: If the response was ACCEPTED

				// Set currentEventId to eventId
				this.currentEventId = eventId;

				// Abort any other pending invitation the user might have from other players
				DatabaseHelper.getInstance().abortAllUserEvents(this.currentUsername);

				// Update the event status to PLAYING
				event.setStatus(Event.EventStatus.PLAYING);
				DatabaseHelper.getInstance().updateEvent(event);

				return new Response(ResponseStatus.SUCCESS, "Game invitation accepted! Game is now starting.");
			} else {
				return new Response(ResponseStatus.FAILURE, "Invalid event status for acknowledgment: " + currentStatus);
			}

		} catch (SQLException e) {
			LOGGER.error("Database error during acknowledge response", e);
			return new Response(ResponseStatus.FAILURE, "Database error during acknowledge response: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error during acknowledge response", e);
			return new Response(ResponseStatus.FAILURE, "Error during acknowledge response: " + e.getMessage());
		}
	}

	/**
	 * Handles complete game request when players finish a game
	 *
	 * @return Response indicating success or failure of completing the game
	 */
	private Response handleCompleteGame() {
		try {
			// Use the database helper function getEvent() to retrieve the Event object
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);

			// Check if the event exists and the status is PLAYING
			if (event == null) {
				return new Response(ResponseStatus.FAILURE, "No active game event found.");
			}

			if (event.getStatus() != Event.EventStatus.PLAYING) {
				return new Response(ResponseStatus.FAILURE, "Game is not in playing status. Current status: " + event.getStatus());
			}

			// Change the status to COMPLETED
			event.setStatus(Event.EventStatus.COMPLETED);
			DatabaseHelper.getInstance().updateEvent(event);

			// Reset currentEventId to -1
			this.currentEventId = -1;

			return new Response(ResponseStatus.SUCCESS, "Game completed successfully!");

		} catch (SQLException e) {
			LOGGER.error("Database error during complete game", e);
			return new Response(ResponseStatus.FAILURE, "Database error during complete game: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error during complete game", e);
			return new Response(ResponseStatus.FAILURE, "Error during complete game: " + e.getMessage());
		}
	}

	/**
	 * Handles abort game request when a player aborts a game in progress
	 *
	 * @return Response indicating success or failure of aborting the game
	 */
	private Response handleAbortGame() {
		try {
			// Use the database helper function getEvent() to retrieve the Event object
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);

			// Check if the event exists and the status is PLAYING
			if (event == null) {
				return new Response(ResponseStatus.FAILURE, "No active game event found.");
			}

			if (event.getStatus() != Event.EventStatus.PLAYING) {
				return new Response(ResponseStatus.FAILURE, "Game is not in playing status. Current status: " + event.getStatus());
			}

			// Change the status to ABORTED
			event.setStatus(Event.EventStatus.ABORTED);
			DatabaseHelper.getInstance().updateEvent(event);

			// Reset currentEventId to -1
			this.currentEventId = -1;

			return new Response(ResponseStatus.SUCCESS, "Game aborted successfully!");

		} catch (SQLException e) {
			LOGGER.error("Database error during abort game", e);
			return new Response(ResponseStatus.FAILURE, "Database error during abort game: " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error during abort game", e);
			return new Response(ResponseStatus.FAILURE, "Error during abort game: " + e.getMessage());
		}
	}

	/**
	 * The main execution method that runs in a separate thread to handle client communication.
	 * This method processes incoming client requests, executes appropriate business logic,
	 * and sends responses back to the client.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// Read serialized request from client
				String serializedRequest = dataInputStream.readUTF();
				LOGGER.debug("Received request: {}", serializedRequest);

				// Deserialize request
				Request request = gson.fromJson(serializedRequest, Request.class);

				// Handle request and get response
				Response response = handleRequest(request);

				// Serialize and send response
				String serializedResponse = gson.toJson(response);
				dataOutputStream.writeUTF(serializedResponse);
				dataOutputStream.flush();
				LOGGER.debug("Sent response: {}", serializedResponse);

			} catch (EOFException e) {
				// Client disconnected
				LOGGER.info("Client disconnected.");
				break;
			} catch (IOException e) {
				LOGGER.error("I/O error: ", e);
				break;
			} catch (JsonSyntaxException e) {
				LOGGER.error("Invalid JSON format: ", e);
				// Optionally send error response to client
			} catch (Exception e) {
				LOGGER.error("Unexpected error: ", e);
			}
		}
		close();
	}

	/**
	 * Closes the client connection and releases all associated resources.
	 * <p>
	 * This method performs cleanup operations including setting the user's online status
	 * to offline, aborting any active user events, and closing all I/O streams and sockets.
	 * Database operations are skipped if no user is currently authenticated.
	 * <p>
	 * The method handles exceptions gracefully during cleanup to ensure all resources
	 * are properly released even if individual operations fail.
	 */
	public void close() {
		LOGGER.info("Attempting to close client connection for user: {}", currentUsername);

		// Update user offline status and abort events when user disconnects
		if (this.currentUsername != null) {
			try {
				// Get the User object corresponding to the currentUsername
				User user = DatabaseHelper.getInstance().getUser(this.currentUsername);

				if (user != null) {
					// Set the user online attribute to false
					user.setOnline(false);

					// Update the user in the database
					DatabaseHelper.getInstance().updateUser(user);

					// Abort any event that is not either COMPLETED or ABORTED
					DatabaseHelper.getInstance().abortAllUserEvents(this.currentUsername);

					LOGGER.info("User '{}' set to offline and events aborted", this.currentUsername);
				}
			} catch (SQLException e) {
				LOGGER.error("Database error while updating user offline status", e);
			} catch (Exception e) {
				LOGGER.error("Unexpected error while updating user offline status", e);
			}
		}

		// Close all streams and socket
		quietClose(this.dataInputStream);
		quietClose(this.dataOutputStream);
		quietClose(this.socket);
		LOGGER.info("Handler shutdown complete for user: {}", currentUsername);
	}

	/**
	 * Attempts to close any Closeable resource quietly.
	 * Logs a warning if an IOException occurs but does not interrupt the shutdown sequence.
	 *
	 * @param closeable the resource to close
	 */
	private void quietClose(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			LOGGER.warn("Error closing an instance of {}.", closeable.getClass().getSimpleName(), e);
		}
	}

	/**
	 * Returns the socket associated with this connection.
	 *
	 * @return the Socket object for this connection
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Returns the current username associated with this connection.
	 *
	 * @return the current username as a String
	 */
	public String getCurrentUsername() {
		return currentUsername;
	}
}