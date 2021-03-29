package environment.world.gradient;

import environment.World;
import support.Influence;

import java.util.Collection;

public class GradientWorld extends World<Gradient> {

    /**
     * Initialize the GradientWorld
     */
    public GradientWorld() {
        super();
    }


    /**
     * Place a collection of gradients inside the Gradient World.
     *
     * @param gradients The collection of gradients.
     */
    @Override
    public void placeItems(Collection<Gradient> gradients) {
        gradients.forEach(this::placeItem);
    }

    /**
     * Place a single gradient in the Gradient World.
     *
     * @param item The gradient.
     */
    @Override
    public void placeItem(Gradient item) {
        putItem(item);
    }

    @Override
    protected void effectuate(Influence inf) {}
}
