package environment;

import environment.world.agent.AgentWorld;
import environment.world.crumb.CrumbWorld;
import environment.world.destination.DestinationWorld;
import environment.world.energystation.EnergyStationWorld;
import environment.world.flag.FlagWorld;
import environment.world.gradient.Gradient;
import environment.world.packet.PacketWorld;
import environment.world.pheromone.Pheromone;

/**
 * A class that keeps track of which (kind of) Items can stand together on one
 * area and which cannot.
 */
public class CollisionMatrix {

    public static boolean AgentCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean AreaValueCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w instanceof EnergyStationWorld) &&
                    !(w instanceof PacketWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean CrumbCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean DestinationCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean EnergyStationCanStandOn(Environment env, int x, int y) {
        return DestinationCanStandOn(env, x, y);
    }

    public static boolean FlagCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean GradientCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w instanceof PacketWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean PacketCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof DestinationWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean PheromoneCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w instanceof PacketWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }


    public static boolean WallCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null) {
                return false;
            }
        }
        return true;
    }
}