import itumulator.world.World;
import itumulator.world.Location;

public class Bear extends Animal { 

private static final int TERRITORY_RADIUS = 5;  // Bjørnens territorium har en radius på 5 felter.
private Location territoryCenter = null; // Midten af bjørnens territorium.
private int turnsWithoutHunting = 0;

        
    private void setTerritoryCenter(World world) { // Gemmer bjørnens startposition.
        if (territoryCenter == null) {
            territoryCenter = world.getLocation(this);
        }
        }

    @Override
    public void act(World world) { 
        setTerritoryCenter(world); // Sørger for at gemme startpositionen.

        if (huntEverything(world) || eatBush(world) || eatGrass(world)) {
            turnsWithoutHunting = 0; // Bjørnen har spist, så tælleren starter forfra
            return;
        }

        if (world.isDay()) {
            turnsWithoutHunting++;

            if (turnsWithoutHunting >= 10) {
            die(world);
            return;
        }
        }

        
            if (turnsWithoutHunting >= 3) { // Hvis bjørnen har været sulten i mindst 3 dagture, må den bevæge sig frit for at lede efter mad.
                 moveRandomly(world);
            } else {
                 moveWithinTerritory(world);
            }

    } 
    private void moveWithinTerritory(World world) {  // Flytter bjørnen tilfældigt inden for dens territorium.
        Location currentLocation = world.getLocation(this); 

            for ( Location location : world.getEmptySurroundingTiles(currentLocation)) { 
                int xDistance = location.getX() - territoryCenter.getX();
                int yDistance = location.getY() - territoryCenter.getY();

                    if (xDistance < 0) {
                     xDistance = -xDistance;
                    }

                    if (yDistance < 0) {
                     yDistance = -yDistance;
                    } 
                   
                    if (xDistance <= TERRITORY_RADIUS && yDistance <= TERRITORY_RADIUS) {  // Kontrollerer, om feltet er inden for territoriets radius.
                     world.move(this, location);
                     return;
                    }
            } 
    } 
    private boolean eatBush(World world) {
        Location location = world.getLocation(this);
        Object food = world.getNonBlocking(location);

         if (food instanceof Bush bush) {
             if (bush.eat()) {
             energy += bush.getEnergyValue();
 
             if (!bush.isBerryBush()) { // Normalbusken forsvinder efter spisning.
             world.delete(bush);
            }
            return true;
        }
    }
    return false;
}
} 

