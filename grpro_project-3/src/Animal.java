import itumulator.simulator.Actor; 
import itumulator.world.Location;
import itumulator.world.World;
import java.util.*;

public abstract class Animal implements Actor {
    protected int age; 
    protected double energy; 
    protected boolean wasNight = false;
    protected Hole hole;
    protected int dayTurnsWithoutFood = 0;
    protected static ArrayList<Hole> holes = new ArrayList<>();
    
    public Animal() {
        this.age = 0; 
        this.energy = 10; 
        this.hole = null;
    }

    @Override 
    public abstract void act(World world);

    public boolean updateAge(World world) {
        if (world.isDay() && wasNight) { // Hvis det er dag nu, og det var nat ved sidste tur, er en ny dag begyndt.
            age++; // Kaninen bliver ældre med 1 år.
            if(age >= 12) {
            world.delete(this); // Fjerner kaninen fra verdenen, hvis den er 12 år eller ældre.
            return false; // Afslutter metoden, så der ikke udføres yderligere handlinger for denne tur.
            }
    
            updateEnergy(world); // Opdaterer energien baseret på den nye alder.
        
        }
        return true; // Returnerer true, hvilket indikerer, at kaninen stadig lever og kan fortsætte med at handle i denne tur.
    } 
        
    public void moveRandomly(World world) {
        Location location = world.getLocation(this);

        Set<Location> emptyLocations =
        world.getEmptySurroundingTiles(location);

        if (!emptyLocations.isEmpty()) {
            List<Location> locations = new ArrayList<>(emptyLocations);
            Random random = new Random();

            Location randomLocation = locations.get(random.nextInt(locations.size()));

            world.move(this, randomLocation); // Flytter denne kanin til det valgte tile

        }
    }

    public boolean eatGrass(World world){
        Location location = world.getLocation(this); // henter locationen af dyret
        Object food = world.getNonBlocking(location); // henter den Tile hvor dyr står, og tjekker om der er noget (nonBlocking-objekt) på den.
        

        if (food instanceof Grass grass) { // tjekker om food er et Grass-objekt og giver et navn til det ny variabel her food og grass peger på samme objekt
            world.delete(grass); // fjerner græsset, fordi kaninen spiser det
            this.energy++; // kaninen får 1 energi.
            return true;
        } 
        return false;
    }

    public boolean hunt(World world){
        Location location = world.getLocation(this);
        for(Location surroundingTile : world.getSurroundingTiles(location)){
            Object prey = world.getTile(surroundingTile);
        
            if (prey instanceof SmallRabbit || prey instanceof BigRabbit || (this instanceof Bear && prey instanceof Wolf)) {
                world.delete(prey);
                world.move(this, surroundingTile);
                this.energy++;
                return true;
            }
        }
        return false; // ingen kanin på nabofleterne
    }
    
public boolean huntEverything(World world) { // Bjørnens jagtmetode, hvor den kan spise alle dyr.
    Location location = world.getLocation(this);

    for (Location surroundingTile : world.getSurroundingTiles(location)) {
        Object prey = world.getTile(surroundingTile);

        if (prey instanceof Animal && prey != this) {     // Bjørnen kan spise alle dyr, der arver fra Animal.
            world.delete(prey);
            world.move(this, surroundingTile);
            this.energy += 2; // Bjørnen får energi af at spise.
            return true;
        }
    }
    return false;
}

    public void die(World world){
       world.delete(this);
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
        if(hole != null) {
          return; // Kaninen har allerede et kaninhul, så den kan ikke grave et nyt.  
        } 
        

        Location location = world.getLocation(this); // vi spørge kaninen om dens placering er "this" placering
         
        if (world.containsNonBlocking(location)) {
            Object thing = world.getNonBlocking(location); // "thing" er navnet på det non-blocking object, der ligger på samme felt, fx thing kan peger på græs hvis der ligger græs der
            
             if (thing instanceof Hole hole) { // her sprøge vi om obejkete er et kaninenhul, hvis det er et hul, så bruger kaninen det og hvis det er fx græs sletter koden det og graver et nyt hul
                this.hole = hole;
                addHole(hole);
                return;
            }
              world.delete(thing);
        }

        Hole newHole = new Hole(); // Opretter et nyt kaninhul hvis nu tile slet ikke havde en non-blocking objekt
        world.setTile(location, newHole); // Placerer det nye kaninhul

        hole = newHole; // Gemmer referencen til kaninhullet i kaninen, så den ved, at den har et kaninhul.
        addHole(newHole); 
    }

    public void findHole(ArrayList<Hole> Holes, World world) { //World world, så metoden får adgeng til den verden kaninen befinder sig i. 
        if(hole != null) {
            return; // Kaninen har allerede et kaninhul, så den behøver ikke at finde et nyt.
        }
        Random random = new Random();

        if(!Holes.isEmpty() && random.nextBoolean()) { // hvsi listen har mindts 1 hul, afgør random, hvad kaninen skal gør og her er 50 %: del et eksisterende hul fra listen
             hole = Holes.get(random.nextInt(Holes.size()));
        } else {
            holeDigging(world); // Hvis der ikke er nogen kaninhuller, graver kaninen et nyt. hvis kaninen skal grave, sender findRabbitHole verdenen videre til holdSigging.
     } 
    } 

    public static void addHole(Hole hole) { // tilføjer kun hullet hvis listen ikke allerede indeholder det
    if (!holes.contains(hole)) {
        holes.add(hole);
    }
    }

    public void getHole(World world) { // sikre at kaninen får tildelt et hul.
        if (hole != null) {
            return;
        }
        findHole(holes, world);
    }
 
    public void moveTowardHole(World world) {
        if (hole == null) {
            return;
    }
    
        Location rabbitLocation = world.getLocation(this); // vi henter kaninens koordinater
        Location holeLocation = world.getLocation(hole); // vi henter hullets koordinator

        Set<Location> locations =
            world.getEmptySurroundingTiles(rabbitLocation); // henter alle de tommer nabofleter omkring kaninen 

    for (Location surreoundingTileLocation : locations) { // for hver location i locations:
            if ((holeLocation.getX() > rabbitLocation.getX() && surreoundingTileLocation.getX() > rabbitLocation.getX()) || (holeLocation.getX() < rabbitLocation.getX() && surreoundingTileLocation.getX() < rabbitLocation.getX()) ||
            (holeLocation.getY() > rabbitLocation.getY() && surreoundingTileLocation.getY() > rabbitLocation.getY()) || (holeLocation.getY() < rabbitLocation.getY() && surreoundingTileLocation.getY() < rabbitLocation.getY())) { 
            // if spørge føre det her nabofelt kaninen i retning af hullet på mindst 1 akse? 

                world.move(this, surreoundingTileLocation);
                return; // det stopper metode, så kaninen højest flytter en gang pr. tur, og hvis ingen tomme pladser der passer, bliver den stående
            }      
        }
    }

    public void sleepIfInHole(World world) { // får kaninne til at gå ind i hullet
        if (hole == null || !world.isOnTile(this)) {
        return;
        }
        Location rabbitLocation = world.getLocation(this);
        Location holeLocation = world.getLocation(hole);

        if (rabbitLocation.getX() == holeLocation.getX() && rabbitLocation.getY() == holeLocation.getY()) { // kaninen ska stå præcis på hullets felt
        world.remove(this); // remove = væk fra kortet, men stadig i liv (delete = død) // den gå ned i hullet og fjernes midertiigt
        }
    }

    public void wakeUp(World world) { // placerer kaninen påkortet igen
        if (world.isOnTile(this) || hole == null) { 
        return; // kaninen er allerede vågen og stå på kortet ellers henter hullet placering(se næste linje)
        }
        
        Location holeLocation = world.getLocation(hole);

        if (world.isTileEmpty(holeLocation)) {
            world.setTile(holeLocation, this); // placerer kaninen dirketet oven på hullet hvis der er plads
        } else {
            Set<Location> emptyLocations = world.getEmptySurroundingTiles(holeLocation); // hvsi en kaninen stå på hullet,  finder og gemmer de ledige nabofleter
            for ( Location location : emptyLocations) {
                world.setTile(location, this); // placerer den kanin, der skal op (this), på det felt.
                break; // så kaninen bliver placeret en gang.
                // Hvis der fx er to ledige felter, sætter koden kaninen på det første felt, løkken får fat i. Hvis der 0 ledige felter, kører løkken ikke, og kaninen forbliver i hullet.
            }
        }
    }
}



