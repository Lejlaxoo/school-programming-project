
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import itumulator.world.Location;
import itumulator.world.World;

public class Wolf extends Animal {
    private int turnsWithoutHunting = 0;
    private int mealsEaten = 0;
    private boolean isBaby;
    private final WolfPack pack; // Ulven tilhører en bestemt flok.
    private final Random random = new Random();

   public Wolf() {
    this(new WolfPack(), false);
}


public Wolf(WolfPack pack) { // Opretter en ulv i en eksisterende flok.
    this(pack, false);
}


public Wolf(WolfPack pack, boolean isBaby) { // Opretter en ulv og bestemmer, om den er en baby.
    this.pack = pack;
    this.isBaby = isBaby;
    this.pack.addWolf(this);
}

   
    private int countLivingWolves(World world) {  // Tæller hvor mange levende ulve der er i flokken.

        int count = 0;

        for (Wolf wolf : pack.getWolves()) {
            if (world.contains(wolf)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void act(World world) {

        // Først undersøger ulven, om den skal kæmpe.
        fight(world);

        if (!world.contains(this)) {
            return;
        }

        // Flokken bygger en hule, når den er stor nok.
        buildHole(world);

       if (hunt(world)) {
         turnsWithoutHunting = 0;
             if (isBaby) { // Babyulven tæller, hvor mange gange den har spist.
                  mealsEaten++;

              if (mealsEaten >= 2) { // Babyulven bliver voksen efter 2 måltider.
                   isBaby = false;
        }
    }

    return;
}

        turnsWithoutHunting++;

        // Ulven dør efter 10 ture uden jagt.
        if (turnsWithoutHunting >= 10) {
            die(world);
            return;
        }

        // Hvis flokken har en hule, søger ulven mod den.
        if (pack.getDen() != null) {
            moveTowardsDen(world);
            reproduceAtDen(world);
        } else {
            // Ellers søger ulven mod de andre medlemmer af flokken.
            moveTowardsPack(world);
        }
    }

    // Bygger én fælles hule, når flokken har mindst 3 levende ulve.
    private void buildHole(World world) {
        int packSize = countLivingWolves(world);
        if (packSize >= 3 && pack.getDen() == null) {

            Location location = world.getLocation(this);

            // Finder ledige felter omkring ulven.
            List<Location> emptyLocations = new ArrayList<>(world.getEmptySurroundingTiles(location));
                            
                   

            if (!emptyLocations.isEmpty()) {

                // Vælger et tilfældigt ledigt felt.
                Location holeLocation = emptyLocations.get(random.nextInt(emptyLocations.size()));
                Hole hole = new Hole(); 
                world.setTile(holeLocation, hole);
                Animal.addHole(hole);
                pack.setDen(hole); // Gemmer hulen som flokkens fælles hule.
            }
        }
    }

    // Får ulven til at bevæge sig mod flokkens fælles hule.
    private void moveTowardsDen(World world) {

        Hole den = pack.getDen();

        if (den == null) {
            return;
        }

        Location wolfLocation = world.getLocation(this);
        Location denLocation = world.getLocation(den);

        int currentDistance = distance(wolfLocation, denLocation);

        List<Location> bestLocations = new ArrayList<>();
        int bestDistance = currentDistance;

        // Undersøger alle ledige nabofelter.
        for (Location possibleLocation : world.getEmptySurroundingTiles(wolfLocation)) {
            int newDistance = distance(possibleLocation, denLocation);

            if (newDistance < bestDistance) {
                bestDistance = newDistance;
                bestLocations.clear();
                bestLocations.add(possibleLocation);

            } else if (newDistance == bestDistance  && newDistance < currentDistance) {
                bestLocations.add(possibleLocation);
            }
        }

        // Flytter ulven til et felt, der er tættere på hulen.
        if (!bestLocations.isEmpty()) {

            Location nextLocation = bestLocations.get(
                    random.nextInt(bestLocations.size())
            );

            world.move(this, nextLocation);
        }
    }

    // Ulvene formerer sig ved deres fælles hule.
    private void reproduceAtDen(World world) {

        Hole den = pack.getDen();
        if (den == null) {
            return;
        }

        Location wolfLocation = world.getLocation(this);
        Location denLocation = world.getLocation(den);

        
        if (distance(wolfLocation, denLocation) > 1 ) { // Ulven skal være ved hulen eller på et nabofelt.
            return;
        }

        if (random.nextInt(100) >= 25) {  // Der er 25 % chance for formering. Hvis tallet er 25 eller højere, stopper metoden, og ulven formerer sig ikke.
            return;
        }

    
        List<Location> emptyLocations =  new ArrayList<>( world.getEmptySurroundingTiles(denLocation)); // Finder ledige felter omkring hulen.

        if (emptyLocations.isEmpty()) {
            return;
        }

        // Vælger et tilfældigt felt til ungen.
        Location babyLocation = emptyLocations.get(random.nextInt(emptyLocations.size()));

       Wolf baby = new Wolf(pack, true);// Opretter en unge i den samme flok som forældrene. 

        // Placerer ungen i verden.
        world.setTile(babyLocation, baby);
    }

    // Undersøger, om ulven har andre levende medlemmer i sin flok.
    private boolean hasLivingPackMembers(World world) {

        for (Wolf wolf : pack.getWolves()) {

            if (wolf != this
                    && world.contains(wolf)
                    && world.isOnTile(wolf)) {

                return true;
            }
        }

        return false;
    }

    // Håndterer kamp mellem ulve fra forskellige flokke.
    private void fight(World world) {

        Location location = world.getLocation(this);

        for (Location surroundingTile :
                world.getSurroundingTiles(location)) {

            Object animal = world.getTile(surroundingTile);

            if (animal instanceof Wolf otherWolf) {

                // Ulve fra samme flok kæmper ikke.
                if (this.pack != otherWolf.pack) {

                    if (!hasLivingPackMembers(world)) {
                        die(world);
                        return;
                    }

                    else if (!otherWolf.hasLivingPackMembers(world)) {
                        otherWolf.die(world);
                        return;
                    }

                    else {

                        int myPackSize = countLivingWolves(world);
                        int otherPackSize =
                                otherWolf.countLivingWolves(world);

                        // Den største flok vinder.
                        if (myPackSize > otherPackSize) {
                            otherWolf.die(world);
                            return;
                        }

                        else if (myPackSize < otherPackSize) {
                            die(world);
                            return;
                        }

                        // Ved lige store flokke afgøres kampen tilfældigt.
                        else {
                            if (random.nextBoolean()) {
                                otherWolf.die(world);
                                return;
                            } else {
                                die(world);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void moveTowardsPack(World world) { // Får ulven til at bevæge sig tættere på sin egen flok.
        Location location = world.getLocation(this);
        Wolf nearestWolf = null;
        int nearestDistance = 0;
        
        for (Wolf packMember : pack.getWolves()) { // Finder den nærmeste levende ulv i samme flok.
            if (packMember != this) {
                if (world.contains(packMember) && world.isOnTile(packMember)) {
                    Location memberLocation = world.getLocation(packMember);

                    int memberDistance = distance(location, memberLocation);
                    if (nearestWolf == null || memberDistance < nearestDistance) {
                        nearestWolf = packMember;
                        nearestDistance = memberDistance;
                    }
                }
            }
        }

        if (nearestWolf == null) {
            moveRandomly(world);
            return;
        }

        Location target = world.getLocation(nearestWolf);

        List<Location> bestLocations = new ArrayList<>();
        int bestDistance = nearestDistance;

        for (Location possibleLocation : world.getEmptySurroundingTiles(location)) {
            int newDistance = distance(possibleLocation, target);

            if (newDistance < bestDistance) {
                bestDistance = newDistance;
                bestLocations.clear();
                bestLocations.add(possibleLocation);

            } else if (newDistance == bestDistance && newDistance < nearestDistance) {
                bestLocations.add(possibleLocation);
            }
        }

        if (!bestLocations.isEmpty()) {

            Location nextLocation = bestLocations.get(random.nextInt(bestLocations.size()));

            world.move(this, nextLocation);
        }
    }
    private int distance(Location first, Location second) { // Beregner afstanden mellem to felter.
        int xDistance = first.getX() - second.getX();
        int yDistance = first.getY() - second.getY();

        if (xDistance < 0) {
        xDistance = xDistance * -1;
        }

        if (yDistance < 0) {
            yDistance = yDistance * -1;
        }

        return Math.max(xDistance, yDistance);
    }
}
