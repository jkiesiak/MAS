package environment.world.energystation;

import environment.World;
import support.Influence;

import java.util.Collection;
import java.util.Objects;

/**
 *  A class for an EnergyStationWorld, being a layer of the total world that contains
 *  EnergyStations.
 */

public class EnergyStationWorld extends World<EnergyStation> {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new EnergyStationWorld instance
     */
    public EnergyStationWorld() {
        super();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     *  Gets the total amount of EnergyStations that are in this EnergyStationWorld
     *
     * @return    This EnergyStationWorld's number of EnergyStations
     */
    public int getNbEnergyStations() {
        return (int) this.items.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .count();
    }

    public String toString() {
        return "EnergyStationWorld";
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------

    /**
     *  Brings a given influence in effect in this world.
     *  This method knows the effects of certain influences and realizes them
     *  in this world.
     *
     * @param inf  the influence to bring in effect
     */
    @Override
    protected void effectuate(Influence inf) {
        //NO-OP
    }

    /**
     * Adds EnergyStations to this EnergyStationWorld.
     *
     * @param energyStations  the energyStations to place in this world
     */
    @Override
    public void placeItems(Collection<EnergyStation> energyStations) {
        energyStations.forEach(this::placeItem);
    }

    /**
     * Adds a EnergyStation to this EnergyStationWorld.
     *
     * @param energyStation  the energyStation to place in this world
     */
    @Override
    public void placeItem(EnergyStation energyStation) {
        putItem(energyStation);
        getEnvironment().addActiveItem(energyStation);
    }
}
