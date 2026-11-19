import itumulator.world.Location;
import itumulator.world.World;
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
    public void act(itumulator.world.World world) {
        if(!updateAge(world)) {
            return; // Afslutter metoden, hvis kaninen er død.
        }
        if (world.isDay()) {
            wakeUp(world);
        }
         if (!world.isOnTile(this)) {
            wasNight = world.isNight(); 
            return;
        }
        
        if(world.isNight() && !hasReproduced) {
            reproduce(world); // Reproducerer kun om natten og hvis den ikke allerede har reproduceret i denne cyklus
        }

        if(world.isDay()) {
            hasReproduced = false; // Reset reproduktion flag om dagen
        }

        if (world.isNight()) {
        getRabbitHole(world);
        moveTowardsRabbitHole(world);
    } else {
        moveRandomlyandEat(world);
}

    wasNight = world.isNight();

    }

    public void reproduce(World world) {
        Location location = world.getLocation(this);

        Set<Location> emptyLocations = world.getEmptySurroundingTiles(location);

        if (!emptyLocations.isEmpty()) {
            List<Location> locations = new ArrayList<>(emptyLocations);

            Random random = new Random();
            Location babyLocation = locations.get(random.nextInt(locations.size()));

            world.setTile(babyLocation, new SmallRabbit()); // Placerer baby kaninen på et tilfældigt tomt tile omkring forælderen
            
            hasReproduced = true; // Sætter flaget til true, så kaninen ikke reproducerer igen i denne cyklus
        }   

    }   
}
