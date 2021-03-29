package util.event;

import environment.Item;
import environment.world.agent.Agent;
import environment.world.packet.Packet;

public class AgentActionEvent extends Event {
    public AgentActionEvent(Object thrower) {
        super(thrower);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

    public void setTo(int x, int y) {
        setToX(x);
        setToY(y);
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public void setFrom(int x, int y) {
        setFromX(x);
        setFromY(y);
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Item<?> getOldTo() {
        return oldTo;
    }

    public void setOldTo(Item<?> oldTo) {
        this.oldTo = oldTo;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        this.value = val;
    }

    private Agent agent;
    private Packet packet;
    private int toX;
    private int toY;
    private int fromY;
    private int fromX;
    private int action;
    private Item<?> oldTo;
    private int value;

    public final static int PICK = 1;
    public final static int PUT = 2;
    public final static int DELIVER = 3;
    public final static int STEP = 4;
    public final static int SKIP = 5;
    public final static int PUTFLAG = 6;
    public final static int PUTPHEROMONE = 7;
    public final static int REMOVEPHEROMONE = 8;
    public final static int PUTCRUMB = 9;
    public final static int PICKCRUMB = 10;
    public final static int PUTAREAVALUE = 11;
    public final static int LOADENERGY = 12;
    public final static int IDLE_ENERGY = 13; // Idle action in terms of energy consumption
}
