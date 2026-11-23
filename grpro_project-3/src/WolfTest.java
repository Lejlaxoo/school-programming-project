import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import itumulator.world.World;
import itumulator.world.Location;

public class WolfTest {

    @Test 
    public void WolfDieefter6Turn(){
        World world = new World(1); // verden er tomt, kun ulven er der på Et felt //ulven prøver ta jager, men der er ikke nogen
        Wolf wolf = new Wolf();

        Location location = new Location (0, 0);
        world.setTile(location, wolf);

        for (int turn = 0; turn < 9; turn++) {
            wolf.act(world);
        }

        assertSame(wolf, world.getTile(location)); // Ulven lever efter 9 ture

        wolf.act(world); // Tur 10
        assertNull(world.getTile(location)); // Ulven er fjernet
    }

    @Test 
    public void wolfHunts (){
        World world = new World(2);
        Wolf wolf = new Wolf();
        SmallRabbit rabbit = new SmallRabbit();

        Location wolfLocation = new Location(0,0);
        Location rabbitLocation = new Location (1,0);

        world.setTile(wolfLocation, wolf);
        world.setTile(rabbitLocation, rabbit);

        int energyBeforeHunting = wolf.energy;

        wolf.act(world);

        assertSame(wolf, world.getTile(rabbitLocation)); // ulven tager kaninens felt
        assertEquals(energyBeforeHunting + 1, wolf.energy); //ulven fik +1 i energi

    }
    
    
}