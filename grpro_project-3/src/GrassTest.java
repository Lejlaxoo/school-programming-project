import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import itumulator.world.Location;
import itumulator.world.World;

public class GrassTest {
    World world;

    @BeforeEach
    public void setUp() {
        world = new World (2);
    }

    @Test
    public void GrassSpread() {
        Grass grass = new Grass();
        Location location1 = new Location (0,0);

        world.setCurrentLocation(location1); // hvilket felt vi tage udgangspunkt i
        world.setTile(location1, grass); // vi placere grasset på kortet


        grass.act(world);

        assertEquals(location1, world.getLocation(grass)); 
        assertTrue(world.containsNonBlocking(new Location(0, 1)) || world.containsNonBlocking(new Location(1, 0))
            || world.containsNonBlocking(new Location(1, 1))); // tjekker at grasset spreder sig til en af de tome nabofleter
    }

    @Test 
    public void animalStandonGrass(){
        Grass grass = new Grass();
        Location location = new Location(0, 0);
        SmallRabbit rabbit = new SmallRabbit();

        world.setTile(location, grass);
        world.setTile(location, rabbit);

        assertSame(grass, world.getNonBlocking(location)); // der ligger græs på det felt
        assertSame(rabbit, world.getTile(location)); //kaninen står på samme felt 
        
    }
}



