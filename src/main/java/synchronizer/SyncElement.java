package synchronizer;

import util.Debug;

/**
 *A class for members of syncsets. A syncElement represents for a synchronizer of an agent the other agents with which it is in the
 * process of synchronizing, including their state and syncTime.
 */
public class SyncElement {

    /**
     *A variable holding the name of the agent represented by this SyncElement.
     */
    private final int name;

    /**
     *A reference to the representation of the state of the agent represented by this SyncEelement.
     */
    private String state;

    /**
     *A variable for representing the synctime of the agent represented by this syncElement
     */
    private int time;

    /**
     *Initialize a new SyncElement with name n.
     *@param n
     *       The name of this new syncElement
     *@post new.getName()==n
     *@post new.getState().equals("ini")
     *@post new.getTime()==0
     */
    public SyncElement(int n) {
        name = n;
        state = "ini";
        time = 0;
    }

    /**
     *Initialize a new SyncElement with name n, in state s and time t.
     *@param n
     *       The name of this new syncElement
     *@param s
     *       The state of this new SyncElement instance
     *@param t
     *       The syncTime of this new SycnElement instance
     *@post new.getName()==n
     *@post new.getState().equals(s)
     *@post new.getTime()==t
     */
    public SyncElement(int n, String s, int t) {
        name = n;
        state = s;
        time = t;
    }

    /**
     *Return the name of this SyncElement.
     */
    public int getName() {
        return name;
    }

    /**
     *Return the state of this SycnElement.
     */
    public String getState() {
        return state;
    }

    /**
     *Return the syncTime of this SyncElement.
     */
    public int getTime() {
        return time;
    }

    /**
     *Change the state of this SyncElement into st.
     *@param st
     *       The new state of this SyncElement.
     *@post new.getState().equals(st)
     */
    public void changeState(String st) {
        state = st;
    }

    /**
     *Change the time of this SyncElement into t.
     *@param t
     *       The new syncTime for this SyncElement
     *@post new.getTime()==t
     */
    public void changeTime(int t) {
        time = t;
    }

    // For testing purposes
    public void printElem() {
        Debug.print(this, "  (" + (name + 1) + "," + state + "," + time +
                    ")");
    }

    public boolean isCommitable(int t) {
        return ( ( ( ( (getState().equals("sync")
                        | getState().equals("comS")
                        )
                      | getState().equals("comR")
                      )
                    | (getState().equals("ackS") & getTime() <= t)
                    )
                  | (getState().equals("ackR") & getTime() <= t)
                  )
                | getState().equals("add")
                );
    }

    public boolean isSyncable(int t) {
        return ( (getState().equals("sync")
                  //   |  getState().equals("comS")
                  //  )
                  | getState().equals("comR")
                  )
                | getState().equals("add")
                );
    }

    public boolean possibleBlocked(int t) {
        return ( (getState().equals("comR")
                  | getState().equals("reqS")
                  )
                | getState().equals("add")
                );
    }
}
