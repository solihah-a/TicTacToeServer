package test;

import model.Event;

/**
 * Test class for {@link Event}
 *
 */
public class EventTest {

    /**
     * Test constructors, getters, setters and equals methods
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        /*
         * Tests constructors
         */
        Event event1 = new Event();

        System.out.println("Event 1: Testing Default Constructor");
        System.out.println(((event1.getEventId()==0) ? "PASSED":"FAILED") + ": eventId");
        System.out.println(((event1.getSender()==null) ? "PASSED":"FAILED") + ": sender");
        System.out.println(((event1.getOpponent()==null) ? "PASSED":"FAILED") + ": opponent");
        System.out.println(((event1.getStatus()==null) ? "PASSED":"FAILED") + ": status");
        System.out.println(((event1.getTurn()==null) ? "PASSED":"FAILED") + ": turn");
        System.out.println(((event1.getMove()==0) ? "PASSED":"FAILED") + ": move");

        Event event2 = new Event(10, "bob", "smith", Event.EventStatus.PLAYING, "bob", 4);

        System.out.println("Event 2: Testing Parameterized Constructor");
        System.out.println(((event2.getEventId()==10) ? "PASSED":"FAILED") + ": eventId");
        System.out.println(((event2.getSender().equals("bob")) ? "PASSED":"FAILED") + ": sender");
        System.out.println(((event2.getOpponent().equals("smith")) ? "PASSED":"FAILED") + ": opponent");
        System.out.println(((event2.getStatus() == Event.EventStatus.PLAYING) ? "PASSED":"FAILED") + ": status");
        System.out.println(((event2.getTurn().equals("bob")) ? "PASSED":"FAILED") + ": turn");
        System.out.println(((event2.getMove()==4) ? "PASSED":"FAILED") + ": move");

        /*
         * Tests all getters and setters
         */
        Event even3 = new Event();
        even3.setEventId(10);
        even3.setSender("bob");
        even3.setOpponent("smith");
        even3.setStatus(Event.EventStatus.PLAYING);
        even3.setTurn("bob");
        even3.setMove(4);

        System.out.println("Event 3: Testing Getters and Setters");
        System.out.println(((even3.getEventId()==10) ? "PASSED":"FAILED") + ": eventId");
        System.out.println(((even3.getSender().equals("bob")) ? "PASSED":"FAILED") + ": sender");
        System.out.println(((even3.getOpponent().equals("smith")) ? "PASSED":"FAILED") + ": opponent");
        System.out.println(((even3.getStatus() == Event.EventStatus.PLAYING) ? "PASSED":"FAILED") + ": status");
        System.out.println(((even3.getTurn().equals("bob")) ? "PASSED":"FAILED") + ": turn");
        System.out.println(((even3.getMove()==4) ? "PASSED":"FAILED") + ": move");

        /*
         * Tests equals() function
         */
        Event event4 = new Event(10, "bob", "smith", Event.EventStatus.PLAYING, "bob", 4);
        Event event5 = new Event(43, "alan", "lucy", Event.EventStatus.PENDING, null, -1);
        Event event6 = new Event(10, "bob", "smith", Event.EventStatus.PLAYING, "bob", 4);
        Object event7 = new Object();

        System.out.println("Event 4, 5, 6, 7: Testing equals() method");
        System.out.println((event4.equals(event6) ? "PASSED":"FAILED") + ": Event 4 equals() Event 6");
        System.out.println((!event4.equals(event5) ? "PASSED":"FAILED") + ": Event 4 not equals() Event 5");
        System.out.println((!event5.equals(event6) ? "PASSED":"FAILED") + ": Event 5 not equals() Event 6");
        System.out.println((!event4.equals(event7) ? "PASSED":"FAILED") + ": Event 4 not equals() Event 7");
    }

}