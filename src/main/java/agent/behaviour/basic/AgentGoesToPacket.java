package agent.behaviour.basic;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.PacketRep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AgentGoesToPacket extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        if (agent.seePacket()) {
            // AGENT DOESN'T CARRY A PACKET, BUT HE SEES ONE, SO HE GOES TOWARDS IT
            PacketRep closestPacket = agent.getClosestVisiblePacket();
            int cpX = closestPacket.getX();
            int cpY = closestPacket.getY();

            int dist = Perception.manhattanDistance(cpX, cpY, agent.getX(), agent.getY());

            if (dist <= 1) {
                agent.pickPacket(cpX, cpY);
                return;
            }

            moves.sort(new Comparator<Coordinate>() {
                @Override
                public int compare(Coordinate c1, Coordinate c2) {
                    return ((Integer) Perception.manhattanDistance(cpX, cpY, agent.getX() + c1.getX(), agent.getY() + c1.getY()))
                            .compareTo(Perception.manhattanDistance(cpX, cpY, agent.getX() + c2.getX(), agent.getY() + c2.getY()));
                }
            });

        }
//        else {
//            Collections.shuffle(moves);
//        }
    }

    @Override
    public void communicate(AgentImp agent) {

    }
}
