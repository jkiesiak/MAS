package environment;

import environment.world.crumb.CrumbRep;
import environment.world.destination.DestinationRep;
import environment.world.energystation.EnergyStationRep;
import environment.world.flag.FlagRep;
import environment.world.gradient.GradientRep;
import environment.world.packet.PacketRep;
import environment.world.pheromone.PheromoneRep;

import java.awt.*;
import java.util.Optional;
import java.util.Vector;

/**
 *  A class of representations of positions in a Perception.
 *  A CellPerception is part of a Perception and has a vector of all
 *  Representations of items on that coordinate.
 */

public class CellPerception {

    /**
     *  Constructor.
     *
     * @param x The (absolute) x-coordinate of this CellPerception
     * @param y The (absolute) y-coordinate of this CellPerception
     */
    public CellPerception(int x, int y) {
        this.x = x;
        this.y = y;
        reps = new Vector<>();
    }

    /**
     *  Adds a Representation to this CellPerception.
     *
     * @param rep The Representation to be added.
     */
    public void addRep(Representation rep) {
        reps.add(rep);
    }

    public void clear() {
        reps.clear();
    }

    /**
     *  Returns the Representation of a given type in this CellPerception.
     *  If no such Representation is found, null is returned.
     *
     * @param clazz The class one wants the Representation of
     */
    @SuppressWarnings("unchecked")
    public <T extends Representation> T getRepOfType(Class<T> clazz) {
        for (Representation representation : reps) {
            if (clazz.isInstance(representation)) {
                return (T) representation;
            }
        }
        return null;
    }


    /**
     * Check if this cell perception has a packet in it.
     * @return True if a packet is present in this cell perception, false otherwise.
     */
    public boolean containsPacket() {
        return getRepOfType(PacketRep.class) != null;
    }


    /**
     * Check if a destination of any color is present in this cell perception.
     * @return True is a destination is present, false otherwise.
     */
    public boolean containsAnyDestination() {
        return this.getRepOfType(DestinationRep.class) != null;
    }

    /**
     * Check if a destination with the specified color is present in this cell perception.
     * @param color The color to check.
     * @return True if a destination of the given color is present, false otherwise.
     */
    public boolean containsDestination(Color color) {
        var destination = this.getRepOfType(DestinationRep.class);
        return destination != null && destination.getColor().equals(color);
    }

    /**
     * Check if this cell perception contains an energy station.
     * @return True if an energy station representation is present, false otherwise.
     */
    public boolean containsEnergyStation() {
        return this.getRepOfType(EnergyStationRep.class) != null;
    }

    /**
     * Check if this cell perception contains a gradient.
     * @return True if a gradient is present, false otherwise.
     */
    public boolean containsGradient() {
        return this.getGradientRepresentation().isPresent();
    }

    /**
     * Retrieve the Gradient in this cell perception (if present).
     * @return The Gradient representation if present, Optional.empty() otherwise.
     */
    public Optional<GradientRep> getGradientRepresentation() {
        return Optional.ofNullable(this.getRepOfType(GradientRep.class));
    }

    /**
     * Check if this cell perception contains a flag.
     * @return True if a flag is present, false otherwise.
     */
    public boolean containsFlag() {
        return this.getFlagRepresentation().isPresent();
    }

    /**
     * Check if this cell perception contains a flag.
     * @return True if a flag is present, false otherwise.
     */
    public boolean containsFlagWithColor(Color color) {
        return this.getFlagRepresentation()
            .map(f -> f.getColor() == color)
            .orElse(false);
    }


    /**
     * Retrieve the flag in this cell perception (if present).
     * @return The flag representation if present, Optional.empty() otherwise.
     */
    public Optional<FlagRep> getFlagRepresentation() {
        return Optional.ofNullable(this.getRepOfType(FlagRep.class));
    }

    /**
     * Check if this cell perception contains a pheromone.
     * @return True if a pheromone is present, false otherwise.
     */
    public boolean containsPheromone() {
        return this.getPheromoneRepresentation().isPresent();
    }

    /**
     * Retrieve the pheromone in this cell perception (if present).
     * @return The pheromone representation if present, Optional.empty() otherwise.
     */
    public Optional<PheromoneRep> getPheromoneRepresentation() {
        return Optional.ofNullable(this.getRepOfType(PheromoneRep.class));
    }


    /**
     * Check if this cell perception contains a crumb.
     * @return True if a crumb is present, false otherwise.
     */
    public boolean containsCrumb() {
        return this.getCrumbRepresentation().isPresent();
    }

    /**
     * Retrieve the crumb in this cell perception (if present).
     * @return The crumb representation if present, Optional.empty() otherwise.
     */
    public Optional<CrumbRep> getCrumbRepresentation() {
        return Optional.ofNullable(this.getRepOfType(CrumbRep.class));
    }


    /**
     * Returns the number of Representations on this CellPerception.
     */
    public int getNbReps() {
        return reps.size();
    }

    /**
     * Checks whether there are any Representations on this CellPerception.
     */
    public boolean isFree() {
        return (reps.size() == 0);
    }

    public boolean isWalkable() {
        return reps.stream().allMatch(Representation::isWalkable);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    /**
     * The x and y coordinate (wrt. the environment) of this Area .
     */
    private final int x;
    private final int y;

    /**
     * The Vector of Representations of Items on (x, y)
     */
    private final Vector<Representation> reps;

}