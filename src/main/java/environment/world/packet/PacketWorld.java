package environment.world.packet;

import environment.Coordinate;
import environment.World;
import environment.world.agent.Agent;
import environment.world.destination.DestinationWorld;
import support.*;
import util.Debug;
import util.MyColor;
import util.event.AgentActionEvent;
import util.event.EventManager;

import java.util.Collection;

/**
 *  A class for a PacketWorld, being a layer of the total world, that contains Packets.
 */
public class PacketWorld extends World<Packet> {

    /**
     *  Initializes a new PacketWorld instance
     */
    public PacketWorld() {
        super();
    }

    /**
     *  Gets the total amount of packets that are in this AgentWorld
     *
     * @return    This AgentWorld's nbPackets
     */
    public int getNbPackets() {
        return nbPackets;
    }

    /**
     *  Gets the agent with the given 'ID'
     *
     * @param  ID   the number we gave to the agent we are looking for
     * @return      The agent with the given ID in this AgentWorld.
     *              Or, if there is no agent with that ID, we return null
     */
    public Agent getAgent(int ID) {
        return getEnvironment().getAgentWorld().getAgent(ID);
    }

    public String toString() {
        return "PacketWorld";
    }


    /**
     *  Make a given Agent in this AgentWorld put the packet it is carrying at the given
     *  coordinates
     *
     * @param  tx       x coordinate
     * @param  ty       y coordinate
     * @param  agent    The agent that is putting a packet
     * @pre    The agent must be carrying a packet
     *          agent.carry() != null
     * @post   The packet 'agent' was carrying is positioned on coordinate tx,ty
     * @post   The ID of the agent no longer carries a packet
     * @post   The putting down of the packet is shown on the gui
     */
    protected synchronized void putPacket(int tx, int ty, int agent) {
        Packet packet = getAgent(agent).carry();
        Packet oldTo = getItem(tx, ty);
        AgentActionEvent event = new AgentActionEvent(this);

        if (getEnvironment().getWorld(DestinationWorld.class).getItem(tx, ty) != null) {
            setNbPackets(getNbPackets() - 1); // there's one packet less in the world
            event.setAction(AgentActionEvent.DELIVER);
        } else {
            packet.moveTo(tx, ty); // make the packet consume these coordinates
            putItem(tx, ty, packet); // place it
            event.setAction(AgentActionEvent.PUT);
        }
        getAgent(agent).consume(null); //not holding a packet anymore

        event.setOldTo(oldTo);
        event.setTo(tx, ty);
        event.setPacket(getAgent(agent).carry());
        event.setAgent(getAgent(agent));
        EventManager.getInstance().throwEvent(event);

    }

    /**
     *  Make a given Agent in this AgentWorld pick the packet at the given coordinates
     *
     * @param  fx   x coordinate of the packet that is to be picked up
     * @param  fy   y coordinate of the packet that is to be picked up
     * @param  agent    The ID of the agent that is picking up a packet
     * @post   The position that held the picked up packet is free
     * @post   The agent holds the packet that was positioned on coordinate
     *         fx,fy
     * @post   The packet that is picked up is informed of it's new coordinates,
     *         being the agent's coordinates
     * @post   The picking up of the packet is shown on the gui
     */
    protected synchronized void pickPacket(int fx, int fy, int agent) {
        Packet from = getItem(fx, fy);
        getAgent(agent).consume(from);
        free(fx, fy);
        from.moveTo(getAgent(agent).getX(), getAgent(agent).getY());

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PICK);
        event.setFrom(fx, fy);
        event.setPacket(from);
        event.setAgent(getAgent(agent));
        EventManager.getInstance().throwEvent(event);
    }

    protected synchronized void passPacket(int tx, int ty, int agentID) {
        Agent srcAgent = getAgent(agentID);
        Agent dstAgent = getEnvironment().getAgentWorld().getItem(tx, ty);
        if (srcAgent == null) Debug.alert(this, "Source agent of passing is null!");
        if (dstAgent == null) Debug.alert(this, "Destination agent of passing is null!");
        Packet packet = srcAgent.carry();
        dstAgent.consume(packet);
        srcAgent.consume(null);
        packet.moveTo(tx, ty);
    }

    private void setNbPackets(int nbPackets) {
        this.nbPackets = nbPackets;
    }

    /**
     *  Brings a given influence in effect in this world.
     *  This method knows the effects of certain influences and realizes them
     *  in this world.
     *
     * @param inf  the influence to bring in effect
     */
    @Override
    protected void effectuate(Influence inf) {
        if (inf instanceof InfPickPacket) {
            pickPacket(inf.getX(), inf.getY(), inf.getID());
        } else if (inf instanceof InfPutPacket) {
            putPacket(inf.getX(), inf.getY(), inf.getID());
        } else if (inf instanceof InfPassPacket) {
            passPacket(inf.getX(), inf.getY(), inf.getID());
        }
    }

    /**
     * Adds a number of Packets randomly to this PacketWorld.
     *
     * @param nbPacketKinds the number of different packet colors
     * @param nbPacketsPerKind the number of packets to place per color
     */
    public void createWorld(int nbPacketKinds, int nbPacketsPerKind) {
        for (int i = 0; i < nbPacketKinds; i++) {
            for (int j = 0; j < nbPacketsPerKind; j++) {
                boolean ok = false;
                while (!ok) {
                    Coordinate c = getRandomCoordinate(getEnvironment().getWidth(), getEnvironment().getHeight());

                    if (getEnvironment().isFreePos(c.getX(), c.getY())) {
                        placeItem(new Packet(c.getX(), c.getY(), MyColor.getColor(i)));
                        ok = true;
                    }
                }
            }
        }
        setNbPackets(nbPacketKinds * nbPacketsPerKind);
    }

    /**
     * Adds Packets to this PacketWorld.
     *
     * @param  packets  the array of Packets that should be placed
     */
    @Override
    public void placeItems(Collection<Packet> packets) {
        packets.forEach(this::placeItem);
    }

    /**
     * Adds a Packet to this PacketWorld.
     *
     * @param packet the packet to place in this world
     */
    @Override
    public void placeItem(Packet packet) {
        try {
            setNbPackets(getNbPackets() + 1);
            putItem(packet);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place an Packet in PacketWorld.");
        }
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    private int nbPackets;
}