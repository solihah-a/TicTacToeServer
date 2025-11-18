package test;

import socket.GamingResponse;
import socket.Response;
/**
 * Test class for {@link GamingResponse}
 *
 */
public class GamingResponseTest {

    /**
     * Test constructors, getters and setters methods
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        /*
         * Tests constructors
         */
        GamingResponse response1 = new GamingResponse();

        System.out.println("GamingResponse 1: Testing Default Constructor");
        System.out.println(((response1.getStatus()==null) ? "PASSED":"FAILED") + ": status");
        System.out.println(((response1.getMessage()==null) ? "PASSED":"FAILED") + ": message");
        System.out.println(((response1.getMove()==0) ? "PASSED":"FAILED") + ": move");
        System.out.println(((!response1.isActive()) ? "PASSED":"FAILED") + ": active");

        GamingResponse response2 = new GamingResponse(Response.ResponseStatus.SUCCESS, "Game Update", 6, true);

        System.out.println("GamingResponse 2: Testing Default Constructor");
        System.out.println(((response2.getStatus()==Response.ResponseStatus.SUCCESS) ? "PASSED":"FAILED") + ": status");
        System.out.println(((response2.getMessage().equals("Game Update")) ? "PASSED":"FAILED") + ": message");
        System.out.println(((response2.getMove()==6) ? "PASSED":"FAILED") + ": move");
        System.out.println(((response2.isActive()) ? "PASSED":"FAILED") + ": active");

        /*
         * Tests all getters and setters
         */
        GamingResponse response3 = new GamingResponse();
        response3.setStatus(Response.ResponseStatus.SUCCESS);
        response3.setMessage("Game Update");
        response3.setMove(6);
        response3.setActive(true);

        System.out.println("GamingResponse 3: Testing Default Constructor");
        System.out.println(((response3.getStatus()==Response.ResponseStatus.SUCCESS) ? "PASSED":"FAILED") + ": status");
        System.out.println(((response3.getMessage().equals("Game Update")) ? "PASSED":"FAILED") + ": message");
        System.out.println(((response3.getMove()==6) ? "PASSED":"FAILED") + ": move");
        System.out.println(((response3.isActive()) ? "PASSED":"FAILED") + ": active");
    }

}