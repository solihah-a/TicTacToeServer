package tictactoe.socket;

import java.io.Serializable;
import java.util.Objects;

public class Response implements Serializable {
    private ResponseStatus status;
    private String message;

    /**
     * A default constructor for the Response class.
     */
    public Response() {
        // Default initialization.
    }

    /**
     * A constructor that sets all attributes of this class.
     *
     * @param status The status of the server response (SUCCESS or FAILURE).
     * @param message A string message description about the status of the client-server communication.
     */
    public Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Overrides the default equals method.
     * Two Response objects are considered equal if both their status and message attributes are equal.
     *
     * @param o The object to compare with.
     * @return true if both status and message are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return status == response.status && Objects.equals(message, response.message);
    }

    /**
     * Overrides the default hashCode method.
     * Must be overridden whenever equals() is overridden to maintain the general contract.
     *
     * @return The hash code based on the status and message attributes.
     */
    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }
}
