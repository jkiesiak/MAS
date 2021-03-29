package environment.world.crumb;

import environment.Representation;

/**
 *  a class for representations of crumbs.
 */
public class CrumbRep extends Representation {

    /**
     *  Initializes a new CrumbRep instance
     *
     * @param  x    X-coordinate of the Crumb this representation
     *              represents
     * @param  y    Y-coordinate of the Crumb this representation
     *              represents
     * @param  number the number of crumbs in the Crumb this representation
     *                represents
     */
    protected CrumbRep(int x, int y, int number) {
        super(x, y);
        setNumber(number);
    }

    public char getTypeChar() {
        return ('.');
    }

    public int getNumber() {
        return number;
    }

    protected void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    private int number;

}
