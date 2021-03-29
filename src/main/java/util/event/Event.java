/* Generated by Together */
package util.event;

/**
 *	A class for events. Every event has a type, that can be recognized by
 * 	EventManager. This makes it possible for listeners to listen to specific
 * 	eventTypes.
 */

public class Event {

    /**
     *	Constructs a new event, given the object that throws this event.
     * 	@param throwingObject: the object that throws this event
     */
    public Event(Object throwingObject) {
        thrower = throwingObject;
    }

    /**
     *	Returns the object that threw this event.
     */
    public Object getThrower() {
        return thrower;
    }

    private final Object thrower;
}
