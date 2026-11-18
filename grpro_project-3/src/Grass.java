import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Grass implements Actor, NonBlocking {

    @Override
    public void act(World world) {

        Location location = world.getLocation(this); // vi fider den Tile hvor græsset står

        Set<Location> emptyLocations =
                world.getEmptySurroundingTiles(location); // samler all Surrounding tiles som er tomme

        if (!emptyLocations.isEmpty()) { // hvis settet indeholder mindst et felt..

            List<Location> locations = new ArrayList<>(emptyLocations); // vi laver en liste så vi kan have indekser 

            Random random = new Random();

            Location randomLocation =
                    locations.get(random.nextInt(locations.size())); // vi henter en random Tile, fra de Tiles der er i listen.

            // Plant kun, hvis feltet har plads til græs.
            if (!world.containsNonBlocking(randomLocation)) {
                world.setTile(randomLocation, new Grass()); // vi sætter et nyt græs på den random Tile, som vi har hentet. men kun hvis der er plads.

            }
        }
    }
}
