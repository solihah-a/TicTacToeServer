package test;

import socket.Request;

/**
 * Test class for {@link Request}
 *
 */
public class RequestTest {

    /**
     * Test constructors, getters and setters methods
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        /*
         * Tests constructors
         */
        Request request1 = new Request();

        System.out.println("Request 1: Testing Default Constructor");
        System.out.println(((request1.getType()==null) ? "PASSED":"FAILED") + ": type");
        System.out.println(((request1.getData()==null) ? "PASSED":"FAILED") + ": data");

        Request request2 = new Request(Request.RequestType.SEND_INVITATION, "bob");

        System.out.println("Request 2: Testing Parameterized Constructor");
        System.out.println(((request2.getType()==Request.RequestType.SEND_INVITATION) ? "PASSED":"FAILED") + ": type");
        System.out.println(((request2.getData().equals("bob")) ? "PASSED":"FAILED") + ": data");

        /*
         * Tests all getters and setters
         */
        Request request3 = new Request();
        request3.setType(Request.RequestType.SEND_INVITATION);
        request3.setData("bob");

        System.out.println("Request 3: Testing Getters and Setters");
        System.out.println(((request3.getType()==Request.RequestType.SEND_INVITATION) ? "PASSED":"FAILED") + ": type");
        System.out.println(((request3.getData().equals("bob")) ? "PASSED":"FAILED") + ": data");

    }

}