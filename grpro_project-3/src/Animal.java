import itumulator.simulator.Actor; 
import itumulator.world.Location;
import itumulator.world.World;
import java.util.*;

public abstract class Animal implements Actor {
    protected int age; 
    protected int energy; 
    protected boolean wasNight = false;
    protected RabbitHole rabbitHole;
    protected static ArrayList<RabbitHole> rabbitHoles = new ArrayList<>();
    
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
             System.out.println("Kanin døde af alderdom");
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
            if (new Random().nextInt(3) == 0) { // kaninen mister kun energi ca. hver 3. tur uden mad
         this.energy--;
}
            
        } 

        if(energy <= 0) {
            System.out.println("Kanin døde af sult");
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
         
        if (world.containsNonBlocking(location)) {
            Object thing = world.getNonBlocking(location);
            
             if (thing instanceof RabbitHole) { 
                rabbitHole = (RabbitHole) thing;
                addRabbitHole(rabbitHole);
                return;
            }
              world.delete(thing);
        }

        RabbitHole newRabbitHole = new RabbitHole(); // Opretter et nyt kaninhul
        world.setTile(location, newRabbitHole); // Placerer det nye kaninhul

        rabbitHole = newRabbitHole; // Gemmer referencen til kaninhullet i kaninen, så den ved, at den har et kaninhul.
         addRabbitHole(newRabbitHole); 
    }

    public void findRabbitHole(ArrayList<RabbitHole> rabbitHoles, World world) {
        if(rabbitHole != null) {
            return; // Kaninen har allerede et kaninhul, så den behøver ikke at finde et nyt.
        }
        Random random = new Random();
        if(!rabbitHoles.isEmpty() && random.nextBoolean()) { // 50 %: del et eksisterende hul
            rabbitHole = rabbitHoles.get(random.nextInt(rabbitHoles.size()));
        } else {
            holeDigging(world); // Hvis der ikke er nogen kaninhuller, graver kaninen et nyt.
     } 
    } 
    public static void addRabbitHole(RabbitHole rabbitHole) {
    if (!rabbitHoles.contains(rabbitHole)) {
        rabbitHoles.add(rabbitHole);
    }
}

public void getRabbitHole(World world) {
    if (rabbitHole != null) {
        return;
    }
    findRabbitHole(rabbitHoles, world);
}
 
public void moveTowardsRabbitHole(World world) {
    if (rabbitHole == null) {
        return;
    }
    Location rabbitLocation = world.getLocation(this);
    Location holeLocation = world.getLocation(rabbitHole);

    Set<Location> locations =
            world.getEmptySurroundingTiles(rabbitLocation);

    for (Location location : locations) {
        if ((holeLocation.getX() > rabbitLocation.getX() && location.getX() > rabbitLocation.getX()) || (holeLocation.getX() < rabbitLocation.getX() && location.getX() < rabbitLocation.getX()) ||
            (holeLocation.getY() > rabbitLocation.getY() && location.getY() > rabbitLocation.getY()) || (holeLocation.getY() < rabbitLocation.getY() && location.getY() < rabbitLocation.getY())) { 

                 world.move(this, location);
            return;
    }   
}
}
public void sleepIfInHole(World world) {
    if (rabbitHole == null || !world.isOnTile(this)) {
        return;
    }
    Location rabbitLocation = world.getLocation(this);
    Location holeLocation = world.getLocation(rabbitHole);

    if (rabbitLocation.getX() == holeLocation.getX() && rabbitLocation.getY() == holeLocation.getY()) {
        world.remove(this); // remove = væk fra kortet, men stadig i live (delete = død)
    }
}
public void wakeUp(World world) {
    if (world.isOnTile(this) || rabbitHole == null) {
        return; // kaninen er allerede vågen
    }
    Location holeLocation = world.getLocation(rabbitHole);

    if (world.isTileEmpty(holeLocation)) {
        world.setTile(holeLocation, this); // titter frem på hullet
    } else {
        Set<Location> emptyLocations = world.getEmptySurroundingTiles(holeLocation);
        if (!emptyLocations.isEmpty()) {
            world.setTile(emptyLocations.iterator().next(), this); // en anden står på hullet → kom op ved siden af
        }
    }
    }
}





    
