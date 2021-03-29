package environment;

/**
 *  A class for active items. Active items are Items with an ID and a view.
 */
abstract public class ActiveItem<T extends Representation> extends Item<T> {

    /**
     * Constructor.
     *
     * @param x    the x-coordinate for this ActiveItem
     * @param y    the y-coordinate for this ActiveItem
     * @param view the view range for this ActiveItem
     * @param ID   the ID for this ActiveItem
     */
    protected ActiveItem(int x, int y, int view, int ID) {
        super(x, y);
        this.ID = ID;
        this.view = view;
    }

    /**
     * Gets the ID of this ActiveItem.
     *
     * @return  the ID value
     */
    public int getID() {
        return ID;
    }

    /**
     * Gets the view range of this ActiveItem
     *
     * @return  the view range
     */
    public int getView() {
        return view;
    }

    /**
     * Sets the view range for this ActiveItem
     *
     * @param  view  the new view range
     */
    public void setView(int view) {
        this.view = view;
    }

    private final int ID;
    private int view;

}