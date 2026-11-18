
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Grass implements Actor, NonBlocking {

    @Override
    public void act(World world) {

        Location location = world.getLocation(this);

        Set<Location> emptyLocations =
                world.getEmptySurroundingTiles(location);

        if (!emptyLocations.isEmpty()) {

            List<Location> locations = new ArrayList<>(emptyLocations);

            Random random = new Random();

            Location randomLocation =
                    locations.get(random.nextInt(locations.size()));

            world.setTile(randomLocation, new Grass());
        }
    }
}
