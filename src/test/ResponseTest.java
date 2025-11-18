package test;

import socket.Response;

/**
 * Test class for {@link Response}
 *
 */
public class ResponseTest {

    /**
     * Test constructors, getters and setters methods
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        /*
         * Tests constructors
         */
        Response response1 = new Response();

        System.out.println("Response 1: Testing Default Constructor");
        System.out.println(((response1.getStatus()==null) ? "PASSED":"FAILED") + ": status");
        System.out.println(((response1.getMessage()==null) ? "PASSED":"FAILED") + ": message");

        Response response2 = new Response(Response.ResponseStatus.SUCCESS, "Invitation Sent");

        System.out.println("Response 2: Testing Default Constructor");
        System.out.println(((response2.getStatus()==Response.ResponseStatus.SUCCESS) ? "PASSED":"FAILED") + ": status");
        System.out.println(((response2.getMessage().equals("Invitation Sent")) ? "PASSED":"FAILED") + ": message");

        /*
         * Tests all getters and setters
         */
        Response response3 = new Response();
        response3.setStatus(Response.ResponseStatus.SUCCESS);
        response3.setMessage("Invitation Sent");

        System.out.println("Response 3: Testing Default Constructor");
        System.out.println(((response3.getStatus()==Response.ResponseStatus.SUCCESS) ? "PASSED":"FAILED") + ": status");
        System.out.println(((response3.getMessage().equals("Invitation Sent")) ? "PASSED":"FAILED") + ": message");
    }


}