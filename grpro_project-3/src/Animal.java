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
        } else {
            if (new Random().nextInt(3) == 0) { // dyr mister kun energi ca. hver 3. tur uden mad
                this.energy--;    
            } 
        }
        return false;
    }

    public boolean hunt(World world){
        Location location = world.getLocation(this);
        for(Location surroundingTile : world.getSurroundingTiles(location)){
            Object prey = world.getTile(surroundingTile);
        
            if(prey instanceof SmallRabbit || prey instanceof BigRabbit){
                world.delete(prey); // fjerne kaninen,så tile bliver ledig 
                world.move(this, surroundingTile); // flytter ulven ind på feltet
                this.energy++;
                return true; // ulven har spist, så stopper jagten
            } 
        }
        return false; // ingen kanin på nabofleterne
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
        if(rabbitHole != null) {
          return; // Kaninen har allerede et kaninhul, så den kan ikke grave et nyt.  
        } 
        

        Location location = world.getLocation(this); // vi spørge kaninen om dens placering er "this" placering
         
        if (world.containsNonBlocking(location)) {
            Object thing = world.getNonBlocking(location); // "thing" er navnet på det non-blocking object, der ligger på samme felt, fx thing kan peger på græs hvis der ligger græs der
            
             if (thing instanceof RabbitHole hole) { // her sprøge vi om obejkete er et kaninenhul, hvis det er et hul, så bruger kaninen det og hvis det er fx græs sletter koden det og graver et nyt hul
                rabbitHole = hole;
                addRabbitHole(rabbitHole);
                return;
            }
              world.delete(thing);
        }

        RabbitHole newRabbitHole = new RabbitHole(); // Opretter et nyt kaninhul hvis nu tile slet ikke havde en non-blocking objekt
        world.setTile(location, newRabbitHole); // Placerer det nye kaninhul

        rabbitHole = newRabbitHole; // Gemmer referencen til kaninhullet i kaninen, så den ved, at den har et kaninhul.
        addRabbitHole(newRabbitHole); 
    }

    public void findRabbitHole(ArrayList<RabbitHole> rabbitHoles, World world) { //World world, så metoden får adgeng til den verden kaninen befinder sig i. 
        if(rabbitHole != null) {
            return; // Kaninen har allerede et kaninhul, så den behøver ikke at finde et nyt.
        }
        Random random = new Random();

        if(!rabbitHoles.isEmpty() && random.nextBoolean()) { // hvsi listen har mindts 1 hul, afgør random, hvad kaninen skal gør og her er 50 %: del et eksisterende hul fra listen
            rabbitHole = rabbitHoles.get(random.nextInt(rabbitHoles.size()));
        } else {
            holeDigging(world); // Hvis der ikke er nogen kaninhuller, graver kaninen et nyt. hvis kaninen skal grave, sender findRabbitHole verdenen videre til holdSigging.
     } 
    } 

    public static void addRabbitHole(RabbitHole rabbitHole) { // tilføjer kun hullet hvis listen ikke allerede indeholder det
    if (!rabbitHoles.contains(rabbitHole)) {
        rabbitHoles.add(rabbitHole);
    }
    }

    public void getRabbitHole(World world) { // sikre at kaninen får tildelt et hul.
        if (rabbitHole != null) {
            return;
        }
        findRabbitHole(rabbitHoles, world);
    }
 
    public void moveTowardsRabbitHole(World world) {
        if (rabbitHole == null) {
            return;
    }
    
        Location rabbitLocation = world.getLocation(this); // vi henter kaninens koordinater
        Location holeLocation = world.getLocation(rabbitHole); // vi henter hullets koordinator

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
        if (rabbitHole == null || !world.isOnTile(this)) {
        return;
        }
        Location rabbitLocation = world.getLocation(this);
        Location holeLocation = world.getLocation(rabbitHole);

        if (rabbitLocation.getX() == holeLocation.getX() && rabbitLocation.getY() == holeLocation.getY()) { // kaninen ska stå præcis på hullets felt
        world.remove(this); // remove = væk fra kortet, men stadig i liv (delete = død) // den gå ned i hullet og fjernes midertiigt
        }
    }

    public void wakeUp(World world) { // placerer kaninen påkortet igen
        if (world.isOnTile(this) || rabbitHole == null) { 
        return; // kaninen er allerede vågen og stå på kortet ellers henter hullet placering(se næste linje)
        }
        
        Location holeLocation = world.getLocation(rabbitHole);

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

