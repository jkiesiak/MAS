package agent.behaviour.wander;

import agent.AgentImp;
import agent.behaviour.LTDBehaviour;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wander extends LTDBehaviour {
    @Override
    public void act(AgentImp agent) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        // Check for viable moves
        for (var move : moves) {
            var perception = agent.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside of the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                agent.step(agent.getX() + x, agent.getY() + y);
                return;
            }
        }

        // No viable moves, skip turn
        agent.skip();
    }

    @Override
    public void communicate(AgentImp agent) {
        // No communication
    }
}
