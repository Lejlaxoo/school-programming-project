import itumulator.simulator.Actor; 
import itumulator.world.Location;
import itumulator.world.World;
import java.util.*;

public abstract class Animal implements Actor {
    protected int age; 
    protected int energy; 
    protected boolean wasNight = false;
    protected RabbitHole rabbitHole;
    
    public Animal() {
        this.age = 0; 
        this.energy = 10; 
        this.rabbitHole = null;
    }

    @Override 
    public abstract void act(World world);

    public boolean updateAge(World world) {
        if (world.isDay() && wasNight) { // Hvis det er dag nu, og det var nat ved sidste tur, er en ny dag begyndt.
            
            age++; // Kaninen bliver ældre med 1 år.
            System.out.println("Kaninen er nu " + age + " år gammel."); // Udskriver kaninens alder til konsollen.
           
            if(age >= 12) {
            world.delete(this); // Fjerner kaninen fra verdenen, hvis den er 12 år eller ældre.
            return false; // Afslutter metoden, så der ikke udføres yderligere handlinger for denne tur.
            }
    
            updateEnergy(world); // Opdaterer energien baseret på den nye alder.
        
        }
        return true; // Returnerer true, hvilket indikerer, at kaninen stadig lever og kan fortsætte med at handle i denne tur.
    } 
        
    public void moveRandomlyandEat(World world) {
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

    public void holeDigging(World world){
        if(rabbitHole != null) {
          return; // Kaninen har allerede et kaninhul, så den kan ikke grave et nyt.
        }

        Location location = world.getLocation(this);

        
        RabbitHole newRabbitHole = new RabbitHole(); // Opretter et nyt kaninhul
        world.setTile(location, newRabbitHole); // Placerer det nye kaninhul

        rabbitHole = newRabbitHole; // Gemmer referencen til kaninhullet i kaninen, så den ved, at den har et kaninhul.
    }

    public void findRabbitHole(ArrayList<RabbitHole> rabbitHoles) {
        if(rabbitHole != null) {
            return; // Kaninen har allerede et kaninhul, så den behøver ikke at finde et nyt.
        }
        if(!rabbitHoles.isEmpty()) {
            Random random = new Random();
            rabbitHole = rabbitHoles.get(random.nextInt(rabbitHoles.size())); // Vælger et tilfældigt kaninhul fra listen
        }
    }
}