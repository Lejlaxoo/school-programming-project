
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import itumulator.world.Location;
import itumulator.world.World;

public class Wolf extends Animal {

    private int turnsWithoutHunting = 0;
    private final List<Wolf> pack;

    public Wolf() {
        this(new ArrayList<>());
    }

    public Wolf(List<Wolf> pack) {
        this.pack = pack;
        this.pack.add(this);
    }

    
private int countLivingWolves(World world) { // Tæller hvor mange levende ulve der er i flokken.

    int count = 0;

    for (Wolf wolf : pack) {

        if (world.contains(wolf)) {
            count++;
        }
    }

    return count;
}
    @Override
    public void act(World world) {
         fight(world);
    if (!world.contains(this)) {
        return;
    }
    if (hunt(world)) {
        turnsWithoutHunting = 0;
        return;
    }
    turnsWithoutHunting++;
    if (turnsWithoutHunting >= 10) {
        die(world);
        return;
    }

    moveTowardsPack(world);
}

    // Undersøger om ulven har levende medlemmer i sin egen flok.
    private boolean hasLivingPackMembers(World world) {
        for (Wolf wolf : pack) {
            if (wolf != this && world.contains(wolf) && world.isOnTile(wolf)) {
                return true;
            }
        }

        return false;
    }

   
    private void fight(World world) {  // Ulve fra forskellige flokke kæmper mod hinanden.
    Location location = world.getLocation(this);

    for (Location surroundingTile :
            world.getSurroundingTiles(location)) {
        Object animal = world.getTile(surroundingTile);
        if (animal instanceof Wolf otherWolf) {
            if (this.pack != otherWolf.pack) { // Undersøger om ulvene tilhører forskellige flokke.
                if (!hasLivingPackMembers(world)) { // Undersøger om denne ulv er alene.
                    die(world);
                    return;
                }
                else if (!otherWolf.hasLivingPackMembers(world)) { // Undersøger om den anden ulv er alene.
                    otherWolf.die(world);
                    return;
                }
        
                else {  // Tæller levende ulve i begge flokke.
                    int myPackSize = countLivingWolves(world);
                    int otherPackSize =
                            otherWolf.countLivingWolves(world);
    
                    if (myPackSize > otherPackSize) {  // Den største flok vinder.
                        otherWolf.die(world);
                        return;
                    } else if (myPackSize < otherPackSize) {
                        die(world);
                        return;
                    }

                    else { // Hvis flokkene er lige store, afgøres det tilfældigt.
            
                        if (new Random().nextBoolean()) {
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
    private void moveTowardsPack(World world) {

        Location location = world.getLocation(this);
        Wolf nearestWolf = null;
        int nearestDistance = 0;

        for (Wolf packMember : pack) {
            // Finder kun levende flokmedlemmer, som ikke er denne ulv.
            if (packMember != this) {
                if (world.contains(packMember) &&
                    world.isOnTile(packMember)) {

                    Location packMemberLocation =
                            world.getLocation(packMember);
                    int distance = distance(
                            location, packMemberLocation);
                    if (nearestWolf == null ||
                        distance < nearestDistance) {
                        nearestWolf = packMember;
                        nearestDistance = distance;
                    }
                }
            }
        }

        // Hvis ulven ikke har andre levende flokmedlemmer,
        // bevæger den sig tilfældigt.
        if (nearestWolf == null) {
            moveRandomly(world);
            return;
        }

        Location target = world.getLocation(nearestWolf);

        List<Location> bestLocations = new ArrayList<>();
        int bestDistance = nearestDistance;
        for (Location possibleLocation :
                world.getEmptySurroundingTiles(location)) {

            int distance = distance(possibleLocation, target);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestLocations.clear();
                bestLocations.add(possibleLocation);
            } else if (distance == bestDistance &&
                       distance < nearestDistance) {
                bestLocations.add(possibleLocation);
            }
        }

        if (!bestLocations.isEmpty()) {
            Location nextLocation = bestLocations.get(new Random().nextInt(
            bestLocations.size()));
            world.move(this, nextLocation);
        }
    }

    // Beregner afstanden mellem to positioner.
private int distance(Location first, Location second) {
    int xDistance = first.getX() - second.getX();
    int yDistance = first.getY() - second.getY();
    if (xDistance < 0) {
        xDistance = xDistance * -1;
    }
    if (yDistance < 0) {
        yDistance = yDistance * -1;
    }
    return Math.max(xDistance, yDistance); // Math.max() er en metode i Java, som finder det største tal ud af to tal.
}
}

