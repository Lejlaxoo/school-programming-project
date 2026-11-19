import itumulator.wolrd.World;
import itumulator.world.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BigRabbit extends Animal {

    private boolean hasReproduced = false; // Flag to track if the rabbit has reproduced in the current cycle

    public BigRabbit(int age, int energy) {
        this.age = age;
        this.energy = energy;
    }

    @Override
    public void act(World world) {
        if(!updateAge(world)) {
            return; // Afslutter metoden, hvis kaninen er død.
        }

        public void reproduce(World world) {
        Location location = world.getLocation(this);

        Set<Location> emptyLocations = world.getEmptySurroundingTiles(location);

        if (!emptyLocations.isEmpty()) {
            List<Location> locations = new ArrayList<>(emptyLocations);

            Random random = new Random();
            Location babyLocation = locations.get(random.nextInt(locations.size()));

            world.setTile(babyLocation, new SmallRabbit()); // Placerer baby kaninen på et tilfældigt tomt tile omkring forælderen
        }   

    }
        
        wasNight = world.isNight(); // Opdaterer wasNight for at holde styr på, om det er nat eller dag i næste tur.

        moveRandomlyandEat(world);
    }

}