package test;

import model.User;

/**
 * Test class for {@link User}
 *
 */
public class UserTest {

    /**
     * Test constructors, getters, setters and equals methods
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        /*
         * Tests constructors
         */
        User user1 = new User();

        System.out.println("Event 1: Testing Default Constructor");
        System.out.println(((user1.getUsername()==null) ? "PASSED":"FAILED") + ": username");
        System.out.println(((user1.getPassword()==null) ? "PASSED":"FAILED") + ": password");
        System.out.println(((user1.getDisplayName()==null) ? "PASSED":"FAILED") + ": displayName");
        System.out.println(((!user1.isOnline()) ? "PASSED":"FAILED") + ": online");

        User user2 = new User("bob", "12345", "Bob Jake", true);

        System.out.println("Event 2: Testing Parameterized Constructor");
        System.out.println(((user2.getUsername().equals("bob")) ? "PASSED":"FAILED") + ": username");
        System.out.println(((user2.getPassword().equals("12345")) ? "PASSED":"FAILED") + ": password");
        System.out.println(((user2.getDisplayName().equals("Bob Jake")) ? "PASSED":"FAILED") + ": displayName");
        System.out.println(((user2.isOnline()) ? "PASSED":"FAILED") + ": online");

        /*
         * Tests all getters and setters
         */
        User user3 = new User();
        user3.setUsername("bob");
        user3.setPassword("12345");
        user3.setDisplayName("Bob Jake");
        user3.setOnline(true);

        System.out.println("Event 3: Testing Getters and Setters");
        System.out.println(((user3.getUsername().equals("bob")) ? "PASSED":"FAILED") + ": username");
        System.out.println(((user3.getPassword().equals("12345")) ? "PASSED":"FAILED") + ": password");
        System.out.println(((user3.getDisplayName().equals("Bob Jake")) ? "PASSED":"FAILED") + ": displayName");
        System.out.println(((user3.isOnline()) ? "PASSED":"FAILED") + ": online");

        /*
         * Tests equals() function
         */
        User user4 = new User("bob", "12345", "Bob Jake", true);
        User user5 = new User("alan", "secure", "Alan Brown", false);
        User user6 = new User("bob", "12345", "Bob Jake", true);
        Object user7 = new Object();

        System.out.println("User 4, 5, 6, 7: Testing equals() method");
        System.out.println((user4.equals(user6) ? "PASSED":"FAILED") + ": User 4 equals() User 6");
        System.out.println((!user4.equals(user5) ? "PASSED":"FAILED") + ": User 4 not equals() User 5");
        System.out.println((!user5.equals(user6) ? "PASSED":"FAILED") + ": User 5 not equals() User 6");
        System.out.println((!user4.equals(user7) ? "PASSED":"FAILED") + ": User 4 not equals() User 7");
    }
}