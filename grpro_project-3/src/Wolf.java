import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wolf extends Animal {
    private int turnsWithoutHunting = 0;
    private int mealsEaten = 0;
    private boolean isBaby;
    private final WolfPack pack;
    private final Random random = new Random();

    public Wolf() {
        this(new WolfPack(), false);
    }

    public Wolf(WolfPack pack) {
        this(pack, false);
    }

    public Wolf(WolfPack pack, boolean isBaby) {
        this.pack = pack;
        this.isBaby = isBaby;
        pack.addWolf(this);
    }

    @Override
    public void act(World world) {
        if (!world.contains(this)) {
            return;
        }

        if (world.isDay()) {
            wakeUp(world);
        }

        if (!world.isOnTile(this)) {
            return; // Ulven er stadig nede i hulen.
        }

        if (fightOverRabbit(world)) {
            return; // Vinderen har allerede spist kaninen.
        }

        fight(world);

        if (!world.contains(this)) {
            return; // Ulven tabte kampen.
        }

        buildHole(world);

        if (enterDenAtNight(world)) {
            return; // Ulven var allerede tæt nok på hulen.
        }

        if (hunt(world)) {
            mealCount();
            return;
        }

        if (world.isDay()) {
            turnsWithoutHunting++;

            if (turnsWithoutHunting >= 10) {
                die(world);
                return;
            }
        }

        Hole den = pack.getDen();

        if (den == null) {
            moveTowardsPack(world);
        } else if (world.isNight()) {
            moveCloserTo(world, world.getLocation(den));
            reproduceAtDen(world);
            enterDenAtNight(world);
        } else {
            reproduceAtDen(world);
            moveRandomly(world); // Led efter mad væk fra hulen.
        }
    }

    private void buildHole(World world) {
        if (pack.getDen() != null) {
            return; // Flokken har allerede en hule.
        }

        Location location = world.getLocation(this);
        int nearbyWolves = 1; // Tæl ulven selv med.

        for (Location neighbor : world.getSurroundingTiles(location)) {
            Object animal = world.getTile(neighbor);

            if (animal instanceof Wolf wolf && wolf != this && wolf.pack == pack) {
                nearbyWolves++;
            }
        }

        if (nearbyWolves < 3) {
            return; // Mindst to andre flokmedlemmer skal være ved siden af ulven.
        }

        List<Location> availableLocations = new ArrayList<>();

        for (Location neighbor : world.getEmptySurroundingTiles(location)) {
            if (!world.containsNonBlocking(neighbor)) {
                availableLocations.add(neighbor); // Der er plads til selve hulen.
            }
        }

        if (availableLocations.isEmpty()) {
            return;
        }

        Location denLocation =
                availableLocations.get(random.nextInt(availableLocations.size()));

        Hole den = new Hole();
        world.setTile(denLocation, den);
        pack.setDen(den);
    }

    private boolean enterDenAtNight(World world) {
        Hole den = pack.getDen();

        if (!world.isNight() || den == null) {
            return false;
        }

        Location wolfLocation = world.getLocation(this);
        Location denLocation = world.getLocation(den);

        if (distance(wolfLocation, denLocation) > 1) {
            return false; // Ulven er for langt væk til at gå ned.
        }

        this.hole = den;     // Den arvede wakeUp() bruger dette felt.
        world.remove(this); // Ulven går ned i hulen og lever stadig.
        return true;
    }

    private void reproduceAtDen(World world) {
        if (isBaby) {
            return; // Kun voksne ulve kan få unger.
        }

        Location denLocation = world.getLocation(pack.getDen());
        Location wolfLocation = world.getLocation(this);

        if (distance(wolfLocation, denLocation) > 1) {
            return;
        }

        if (random.nextInt(100) >= 25) {
            return; // 25 % chance for en unge.
        }

        List<Location> emptyLocations =
                new ArrayList<>(world.getEmptySurroundingTiles(denLocation));

        if (!emptyLocations.isEmpty()) {
            Location babyLocation =
                    emptyLocations.get(random.nextInt(emptyLocations.size()));
            world.setTile(babyLocation, new Wolf(pack, true));
        }
    }

    private void moveTowardsPack(World world) {
        Location location = world.getLocation(this);
        Location nearestLocation = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (Wolf member : pack.getWolves()) {
            if (member == this || !world.contains(member) || !world.isOnTile(member)) {
                continue; // Spring ulven selv og medlemmer uden placering over.
            }

            Location memberLocation = world.getLocation(member);
            int memberDistance = distance(location, memberLocation);

            if (memberDistance < nearestDistance) {
                nearestDistance = memberDistance;
                nearestLocation = memberLocation;
            }
        }

        if (nearestLocation == null) {
            moveRandomly(world);
        } else {
            moveCloserTo(world, nearestLocation);
        }
    }

    private void moveCloserTo(World world, Location target) {
        Location location = world.getLocation(this);
        int currentDistance = distance(location, target);
        int bestDistance = currentDistance;
        List<Location> bestLocations = new ArrayList<>();

        for (Location neighbor : world.getEmptySurroundingTiles(location)) {
            int newDistance = distance(neighbor, target);

            if (newDistance < bestDistance) {
                bestDistance = newDistance;
                bestLocations.clear();
                bestLocations.add(neighbor);
            } else if (newDistance == bestDistance && newDistance < currentDistance) {
                bestLocations.add(neighbor);
            }
        }

        if (!bestLocations.isEmpty()) {
            Location nextLocation =
                    bestLocations.get(random.nextInt(bestLocations.size()));
            world.move(this, nextLocation);
        }
    }

    private int distance(Location first, Location second) {
        int xDistance = Math.abs(first.getX() - second.getX());
        int yDistance = Math.abs(first.getY() - second.getY());
        return Math.max(xDistance, yDistance); // Et diagonalt nabofelt er ét skridt væk.
    }

    private void fight(World world) {
        Location location = world.getLocation(this);

        for (Location neighbor : world.getSurroundingTiles(location)) {
            Object animal = world.getTile(neighbor);

            if (animal instanceof Wolf rival && rival.pack != pack) {
                Wolf winner = chooseWinner(world, rival);

                if (winner == this) {
                    rival.die(world);
                } else {
                    die(world);
                }

                return;
            }
        }
    }

    private boolean fightOverRabbit(World world) {
        Location location = world.getLocation(this);

        for (Location rabbitLocation : world.getSurroundingTiles(location)) {
            Object rabbit = world.getTile(rabbitLocation);

            if (!(rabbit instanceof SmallRabbit || rabbit instanceof BigRabbit)) {
                continue;
            }

            for (Location neighbor : world.getSurroundingTiles(rabbitLocation)) {
                Object animal = world.getTile(neighbor);

                if (animal instanceof Wolf rival && rival.pack != pack) {
                    Wolf winner = chooseWinner(world, rival);

                    if (winner == this) {
                        rival.die(world);
                    } else {
                        die(world);
                    }

                    world.delete(rabbit);
                    world.move(winner, rabbitLocation);
                    winner.energy++;
                    winner.mealCount();
                    return true;
                }
            }
        }

        return false;
    }

    private Wolf chooseWinner(World world, Wolf rival) {
        int myPackSize = countLivingWolves(world);
        int rivalPackSize = rival.countLivingWolves(world);

        if (myPackSize > rivalPackSize) {
            return this;
        }

        if (rivalPackSize > myPackSize) {
            return rival;
        }

        return random.nextBoolean() ? this : rival;
    }

    private int countLivingWolves(World world) {
        int count = 0;

        for (Wolf wolf : pack.getWolves()) {
            if (world.contains(wolf)) {
                count++;
            }
        }

        return count;
    }

    private void mealCount() {
        turnsWithoutHunting = 0;

        if (isBaby) {
            mealsEaten++;

            if (mealsEaten >= 2) {
                isBaby = false; // En unge bliver voksen efter to måltider.
            }
        }
    } 
    private boolean isBaby() {
        return isBaby; 
    }
}