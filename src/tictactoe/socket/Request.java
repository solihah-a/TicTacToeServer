package tictactoe.socket;

import java.io.Serializable;
import java.util.Objects;

/**
 * Models a client's request sent to the server. The server always expects
 * an object of this class when receiving a request from a client.
 */
public class Request implements Serializable{
    private RequestType type;
    private String data;

    /**
     * A default constructor for the Request class.
     */
    public Request() {
        // Default initialization.
    }

    /**
     * A constructor that sets all attributes of this class.
     *
     * @param type The type of client request.
     * @param data A string representation of serialized data sent by the client.
     */
    public Request(RequestType type, String data) {
        this.type = type;
        this.data = data;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * Overrides the default equals method.
     * Two Request objects are considered equal if both their type and data attributes are equal.
     *
     * @param o The object to compare with.
     * @return true if both type and data are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return type == request.type && Objects.equals(data, request.data);
    }

    /**
     * Overrides the default hashCode method.
     * Must be overridden whenever equals() is overridden to maintain the general contract.
     *
     * @return The hash code based on the type and data attributes.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, data);
    }
}

