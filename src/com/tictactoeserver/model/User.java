package com.tictactoeserver.model;

import java.io.Serializable;

/**
 *  Models a user who will play the TicTacToe game.
 *  Objects of this class are used for registration, login, and player identification.
 *  This class maps directly to the 'User' database table.
 */
public class User implements Serializable {

    // Attributes required for the User
    private String username;
    private String password;
    private String displayName;
    private Boolean online;  // e.g., false: Offline, true: Online

    /**
     * A default constructor for the User class.
     */
    public User() {
        // Initializes all fields to their default values (null for objects, false for Boolean)
    }

    /**
     * A constructor that sets all attributes of this class.
     *
     * @param username The username for the user.
     * @param password The user's password.
     * @param displayName The user's display name.
     * @param online Boolean indicating the user's online status.
     */
    public User(String username, String password, String displayName, Boolean online) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.online = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    /**
     * Overrides the default equals method.
     * Two User objects are considered equal if their usernames are equal,
     * as the username is the unique identifier.
     *
     * @param o The object to compare with.
     * @return true if the usernames are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        // Use the username as the unique attribute for comparison
        return username != null ? username.equals(user.username) : user.username == null;
    }

    /**
     * Overrides the default hashCode method.
     * Must be overridden whenever equals() is overridden to maintain the general contract.
     *
     * @return The hash code based on the unique username attribute.
     */
    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
