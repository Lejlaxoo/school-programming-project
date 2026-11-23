import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import itumulator.world.World;
import itumulator.world.Location;

public class WolfTest { 

    @Test 
    public void WolfDieefter6Turn(){
        World world = new World(1); // verden er tomt, kun ulven er der på Et felt //ulven prøver ta jager, men der er ikke nogen
        Wolf wolf = new Wolf();

        Location location = new Location (0, 0);
        world.setTile(location, wolf);

        wolf.act(world); // første tur uden mad
        wolf.act(world); // anden tur uden mad
        wolf.act(world);
        wolf.act(world);
        wolf.act(world);

        assertSame(wolf, world.getTile(location)); // ulven lever stadig 

        wolf.act(world);
        assertNull(world.getTile(location));
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

    @Test
    public void wolfMovesCloserToItsPack() {
        World world = new World(5);
        List<Wolf> pack = new ArrayList<>();
        Wolf wolf = new Wolf(pack);
        Wolf packMember = new Wolf(pack);
        Location wolfStart = new Location(0, 0);
        Location packMemberLocation = new Location(4, 4);

        world.setTile(wolfStart, wolf);
        world.setTile(packMemberLocation, packMember);

        wolf.act(world);

        Location newLocation = world.getLocation(wolf);
        int newDistance = Math.max(
            Math.abs(newLocation.getX() - packMemberLocation.getX()),
            Math.abs(newLocation.getY() - packMemberLocation.getY())
        );
        assertTrue(newDistance < 4);
    }
}
