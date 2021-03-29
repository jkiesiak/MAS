package environment.world.energystation;

import environment.ActiveItem;
import gui.video.Drawer;
import util.Debug;

/**
 *  A class for energyStations, items representing a station where agents can
 *  charge energy in the EnergyStationsWorld.
 */

public class EnergyStation extends ActiveItem<EnergyStationRep> {

    /**
     *  Initializes a new EnergyStation instance.
     *
     * @param  x      x-coordinate of the energyStation
     * @param  y      y-coordinate of the energyStation
     * @param  ID     the ID of the energyStation
     */
    public EnergyStation(int x, int y, int ID) {
        super(x, y, RANGE, ID);
        Debug.print(this, "EnergyStation created at " + x + " " + y);
    }


    /**
     *  Returns a representation of this energy station.
     *
     * @return    The representation of this energy station
     */
    @Override
    public EnergyStationRep getRepresentation() {
        return new EnergyStationRep(getX(), getY());
    }

    /**
     *  Draws this EnergyStation on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawEnergyStation(this);
    }


    /**
     * The range in which this energyStation has an influence, i.e. can
     * load the battery of agents. This is also the region in which the
     * energyStation eventually has to synchronize with the active items or
     * agents present.
     * The energyStation can be detected from outside this range because of the
     * gradient implementation of the energyStation. The detection range is
     * equal to the power (see gradient) of the energyStation.
     */
    public static final int RANGE = 1;
}
