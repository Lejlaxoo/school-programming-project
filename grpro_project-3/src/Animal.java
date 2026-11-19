import itumulator.simulator.Actor; 
import itumulator.world.Location;
import itumulator.world.World;
import java.util.*;

public class Animal implements Actor {
    private int age; 
    private int energy; 
    private boolean wasNight = false;
    
    public Animal() {
        this.age = 0; 
        this.energy = 10; 
    }

    @Override 
    public void act(World world) {

    
        if (world.isDay() && wasNight) { // Hvis det er dag nu, og det var nat ved sidste tur, er en ny dag begyndt.
            
            age++; // Kaninen bliver ældre med 1 år.

            if(age >= 12) {
            world.delete(this); // Fjerner kaninen fra verdenen, hvis den er 12 år eller ældre.
            return; // Afslutter metoden, så der ikke udføres yderligere handlinger for denne tur.
            }
    
            updateEnergy(world); // Opdaterer energien baseret på den nye alder.
        
        } 

        wasNight = world.isNight(); // Opdaterer wasNight for at holde styr på, om det er nat eller dag i næste tur.
        
        Location location = world.getLocation(this);

        Set<Location> emptyLocations =
        world.getEmptySurroundingTiles(location);

        if (!emptyLocations.isEmpty()) {
            List<Location> locations = new ArrayList<>(emptyLocations);
            Random random = new Random();

            Location randomLocation = locations.get(random.nextInt(locations.size()));

            world.move(this, randomLocation); // Flytter denne kanin til det valgte tile

        }

        location = world.getLocation(this); // Opdaterer location efter flytning
        Object food = world.getNonBlocking(location); // henter den Tile hvor kaninen står, og tjekker om der er noget (nonBlocking-objekt) på den.
        

        if (food instanceof Grass) { // tjekker om food er et Grass-objekt 
            world.delete(food); // fjerner græsset, fordi kaninen spiser det
            this.energy++; // kaninen får 1 energi.
        } else {
            this.energy--; //kaninen mister 1 energi, fordi den ikke spiser noget.
        } 

        if(energy <= 0) {
            world.delete(this);
        }
    } 
    
    public void updateEnergy(World world) {
        if ( age >= 1 && age <= 5) { 
            energy = 10; 
        } else if (age >= 6 && age <= 10) {
            energy = 5; 
        } else if (age == 11) { 
            energy = 2;   
        } else { 
            energy = 0;    
            world.delete(this); 
        }  
    }
}
