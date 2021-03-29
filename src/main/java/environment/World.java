package environment;

import support.Influence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 *  A general world "concept".
 */

public abstract class World<T extends Item<?>> {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new World instance
     */
    public World() {
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     *  Measures the distance between two coordinates in the world
     *
     * @param  x1   First position's X
     * @param  y1   First position's Y
     * @param  x2   Second position's X
     * @param  y2   Second position's Y
     * @return  The distance between these coordinates
     */
    public static int distance(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     *  Returns a random coordinate in this world.
     *
     * @param maxX The width of this world
     * @param maxY The height of this world
     * @return The random coordinate
     */
    protected static Coordinate getRandomCoordinate(int maxX, int maxY) {
        int x = rnd.nextInt(maxX);
        int y = rnd.nextInt(maxY);
        return new Coordinate(x, y);
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    /**
     *  Places a given Item at the given coordinates
     *
     * @param	x    x coordinate
     * @param	y    y coordinate
     * @param	item  the Item to place in the world
     * @post	items[x][y] = item
     */
    protected void putItem(int x, int y, T item) {
        items.get(x).set(y, item);
    }

    /**
     *  Places a given Item at its coordinates
     *
     * @param item the Item to place in the world
     * @post  items[item.getX()][item.getY()] = item
     */
    protected void putItem(T item) {
        this.putItem(item.getX(), item.getY(), item);
    }

    /**
     * Method must be overridden to cast Items to the right subclass of Item.
     */
    public abstract void placeItems(Collection<T> items);

    /**
     * Method must be overridden to cast Items to the right subclass of Item.
     */
    public abstract void placeItem(T item);

    /**
     *   Effectuates a given Influence in this World
     */
    abstract protected void effectuate(Influence inf);

    /**
     *   Initializes this world.
     */
    public void initialize(int width, int height, Environment env) {
        setEnvironment(env);
        items = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            List<T> row = new ArrayList<>();
            for (int j = 0; j < height; j++) {
                row.add(null);
            }
            items.add(row);
        }
    }

    /**
     *  Make the given coordinates free.
     *
     * @param  x    x coordinate
     * @param  y    y coordinate
     * @post        getItem(x, y) == null
     */
    public void free(int x, int y) {
        this.putItem(x, y, null);
    }

    public String toString() {
        return "World";
    }



    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    /**
     *  Returns all Items in this world
     *
     * @return    A 2d list containing all Items in this world
     */
    public List<List<T>> getItems() {
        return items;
    }


    /**
     * Return the items in this world in newly created lists.
     * Note: does not copy the items themselves.
     *
     * @return A 2d list containing all Items in this world
     */
    public List<List<T>> getItemsCopied() {
        var result = new ArrayList<List<T>>();

        for (var l : items) {
            result.add(new ArrayList<>(l));
        }

        return result;
    }


    /**
     *  Returns the Item at the given coordinate in this world
     *
     * @param  x    x coordinate
     * @param  y    y coordinate
     * @return      items[x][y]
     */
    public T getItem(int x, int y) {
        return items.get(x).get(y);
    }

    /**
     *  Gets the environment this World is part of
     *
     * @return    The environment value
     */
    public Environment getEnvironment() {
        return this.env;
    }

    /**
     *  Sets the environment attribute of the World object
     *
     * @param  environ  The new environment value
     */
    protected void setEnvironment(Environment environ) {
        this.env = environ;
    }


    /**
     * Check if a given coordinate pair is within the bounds of this world
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return True if the coordinates are valid, false otherwise
     */
    protected boolean inBounds(int x, int y) {
        if (items.isEmpty()) {
            return false;
        } else if (items.get(0).isEmpty()) {
            return false;
        }
        return x >= 0 && x < items.size() &&
                y >= 0 && y < items.get(0).size();
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    protected List<List<T>> items;
    private Environment env;
    private static final Random rnd = new Random();
}
