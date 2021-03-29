package environment.world.agent;

import environment.Representation;
import environment.world.packet.PacketRep;

/**
 *  A class for representations of Agents.
 */
public class AgentRep extends Representation {

    private int ID;
    private String name;
    private PacketRep carry;

    /**
     *  Initializes a new AgentRep object
     *
     * @param  x      X-coordinate of the agent this representation represents
     * @param  y      Y-coordinate of the agent this representation represents
     * @param  aID    the ID of the agent this representation represents
     * @param  aName  the name of the agent this representation represents
     */
    protected AgentRep(int x, int y, int aID, String aName) {
        super(x, y);
        setID(aID);
        setName(aName);
    }

    /**
     *  Gets the ID of the Agent this AgentRep represents
     *
     * @return    This AgentRep's ID
     */
    public int getID() {
        return ID;
    }

    /**
     *  Gets the name of the Agent this AgentRep represents
     *
     * @return    This AgentRep's name
     */
    public String getName() {
        return name;
    }

    /**
     *  Gets the representation of the Packet that the Agent this AgentRep represents is carrying
     *
     * @return    This AgentRep's carry
     */
    public PacketRep getCarry() {
        return carry;
    }

    /**
         *  Returns 'true' if the agent this AgentRep represents is carrying a Packet
     *
     * @return true if getCarry() != null && getCarry() instanceof PacketRep
     */
    public boolean carriesPacket() {
        return getCarry() != null;
    }

    /**
     *  Sets the ID of this AgentRep
     *
     * @param  aID  The new ID value
     */
    protected void setID(int aID) {
        ID = aID;
    }

    /**
     *  Sets the name of this AgentRep
     *
     * @param  aName  The new name value
     */
    protected void setName(String aName) {
        name = aName;
    }

    /**
     *  Sets the Packet this AgentRep is carrying
     *
     * @param  aCarry  The new carry value
     */
    protected void setCarry(PacketRep aCarry) {
        carry = aCarry;
    }

    public char getTypeChar() {
        return ('A');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

}
