package environment.law;

import environment.Perception;
import environment.world.wall.WallRep;

/**
 *  A PerceptionLaw for not allowing agents to perceive Items that are
 *  situated behind a Wall Item from their point of view. They will only
 *  receive general information about these Items, such as their
 *  coordinates.
 */
public class PercLawWallObstacle implements PerceptionLaw {

    /**
     *  Initializes a new PercLawWallObstacle instance
     */
    public PercLawWallObstacle() {}

    /**
     *  Enforces this PerceptionLaw on a given Perception 'perc'.
     *  All Representations that are situated behind a WallRep relative to the
         *  perceiving agent should be generalised as 'Representations', thus removing
     *  any specific information about them.
     *
     *  Only works for the WallWorld show in the presentation of this thesis.
     *  This algorithm is purely made for this demonstration.
     *  To get a realistic effect a much (much) more complicated algorithm
     *  is needed.
     *
     * @param  perc The perception on which we will enforce this perceptionlaw
     * @return      A perception containing only unspecified 'Representations'
     *              for those representations that are situated behind a wall
     *              relative to the perceiving agent.
     */
    public Perception enforce(Perception perc) {
        Perception newPerc = new Perception(perc.getWidth(), perc.getHeight(),
                                            perc.getOffsetX(), perc.getOffsetY());
        int aX = perc.getSelfX();
        int aY = perc.getSelfY();
        for (int tX = 0; tX < perc.getWidth(); tX++) {
            for (int tY = 0; tY < perc.getHeight(); tY++) {
                boolean foundWall = false;
                int minX = Math.min(aX, tX);
                int maxX = Math.max(aX, tX);
                int minY = Math.min(aY, tY);
                int maxY = Math.max(aY, tY);
                for (int i = minX; i <= maxX && !foundWall; i++) {
                    for (int j = minY; j <= maxY && !foundWall; j++) {
                        if (perc.getCellAt(i, j).getRepOfType(WallRep.class) != null) {
                            foundWall = true;
                        }
                    }
                }
                if (foundWall) {
                    int nX = perc.getCellAt(tX, tY).getX();
                    int nY = perc.getCellAt(tX, tY).getY();
                    //newPerc.setRepAt(tX, tY, new Representation(nX, nY));
                    newPerc.getCellAt(tX, tY).clear();
                } else {
                    newPerc.setCellPerceptionAt(tX, tY, perc.getCellAt(tX, tY));
                }
            }
        }
        //newPerc.setSelf(perc.getSelf(), aX, aY);
        newPerc.setSelfX(aX);
        newPerc.setSelfY(aY);
        return newPerc;
    }

}
