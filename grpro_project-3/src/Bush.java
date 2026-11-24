import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World; 

public class Bush implements Actor, NonBlocking {
    private boolean isBerryBush;
    private int berries = 3;
    private int turnsSinceRegrowth = 0; // Tæller, hvor mange ture der er gået, siden bærbusken sidst har fået et nyt bær.

    public Bush (boolean isBerryBush) {   // true = bærbusk, false = almindelig busk.
        this.isBerryBush = isBerryBush; 
    } 
    public boolean eat() {
        if (isBerryBush) {
            if (berries > 0) {
                berries--;
                return true;
            }
            return false;
        }
        return true;
    }
    @Override
    public void act(World world) {  // Busken får nye bær efter 5 ture.
        if (isBerryBush && berries < 3) {
            turnsSinceRegrowth++;
            if (turnsSinceRegrowth >= 5) {
                berries++;
                turnsSinceRegrowth = 0;
            }
        }
    }
    public double getEnergyValue() {
        if (isBerryBush) {
            return 1.5;
        }
        return 0.5;
    }
     public boolean isBerryBush() {
        return isBerryBush;
    }
} 


