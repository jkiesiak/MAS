package environment;

import util.Debug;

import java.util.Vector;

/**
 *  A class for a clock that can be increased, queried and listened to.
 */
public class Clock {

    private int time;
    private final Vector<ClockListener> listeners;

    /**
     *  Initializes a new Clock instance
     */
    public Clock() {
        time = 0;
        listeners = new Vector<>();
    }

    /**
     *  Increase the clock by one and notify all listeners
     *
     *  @post   this Clock's time is increased by one
     *  @post   all listeners registered at this Clock are notified
     *          by means of a new ClockEvent.
     */
    protected void incrClock() {
        time++;
        notifyListeners(new ClockEvent());
    }

    /**
     *  Gets the time of this Clock
     *
     * @return    This Clock's time
     */
    protected int getTime() {
        return time;
    }

    /**
     *  Adds a given ClockListener to this Clock's listeners.
     *
     * @param  cListener  The ClockListener to be added
     * @post    this Clock's 'listeners' is extended with 'cListener'
     */
    public void addListener(ClockListener cListener) {
        synchronized (this) {
            listeners.addElement(cListener);
        }
    }

    /**
     *  Remove a given ClockListener from this Clock's listeners.
     *
     * @param   cListener    The ClockListener to be removed
     * @post    'cListener' is removed from this Clock's 'listeners'
     */
    public void removeListener(ClockListener cListener) {
        synchronized (this) {
            listeners.removeElement(cListener);
        }
    }

    /**
     * Removes a given ClockListener from this Clock's listeners in a delayed fashion
     *  (spawning in different thread to ensure proper synchronization).
     * @param cListener The ClockListener to remove.
     */
    public void removeListenerDelayed(ClockListener cListener) {
        new Thread(() -> this.removeListener(cListener));
    }

    /**
     *  Notify all listeners registered at this Clock by means of a given
     *  ClockEvent.
     *
     * @param  event    The ClockEvent by which to notify all listeners
     * @post    All listeners received the ClockEvent
     */
    private void notifyListeners(ClockEvent event) {
        synchronized (this) {
            listeners.forEach(l -> l.onClockEvent(event));
        }
        Debug.print(this, "All ClockListeners have been notified.");
    }
}
