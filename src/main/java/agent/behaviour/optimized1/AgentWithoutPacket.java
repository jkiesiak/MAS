package agent.behaviour.optimized1;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.PacketRep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  This class represent the agent's behaviour when he doesn't carry a package in the environment.
 *  The implementation contains two possible moves. First, an agent has a packet in his perception,
 *  thus he moves towards the packet to pick it up.
 *  Second, an agent has not the packet in his perception, thus randomly wanders.
 */

public class AgentWithoutPacket extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));
        /**
         * Sets the behaviour for an agent, who has not a packet in his perception.
         */
        if (!agent.seePacket()){
            // AGENT WANDERS RANDOMLY SINCE THERE IS NO VISIBLE packet
            Collections.shuffle(moves);

        }
        /**
         * Sets the behaviour for an agent, who has a packet in his perception. Agent goes towards it.
         */
        if(agent.seePacket()) {
            // AGENT DOESN'T CARRY A PACKET, BUT HE SEES ONE, SO HE GOES TOWARDS IT
            PacketRep closestPacket = agent.getClosestVisiblePacket();
            int cpX = closestPacket.getX();
            int cpY = closestPacket.getY();

            int dist = Perception.manhattanDistance(cpX, cpY, agent.getX(), agent.getY());

            if(dist <= 1){
                agent.pickPacket(cpX, cpY);
                return;
            }

            moves.sort(new Comparator<Coordinate>(){
                @Override
                public int compare(Coordinate c1, Coordinate c2){
                    return ((Integer)Perception.manhattanDistance(cpX, cpY, agent.getX() + c1.getX(), agent.getY() + c1.getY()))
                            .compareTo(Perception.manhattanDistance(cpX, cpY, agent.getX() + c2.getX(), agent.getY()+ c2.getY() ));
                }
            });

        }
        // Check for viable moves
        for (var move : moves) {
            var perception = agent.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside of the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                agent.step(agent.getX() + x, agent.getY() + y); // !!!
                return;
            }
        }
//
//        // No viable moves, skip turn
//        agent.skip();

    }

    @Override
    public void communicate(AgentImp agent) {

    }
}
