import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import itumulator.world.Location;
import itumulator.world.World;

public class RabbitTest {
    World world;

    @BeforeEach
    public void setUp() {
        world = new World (3);
    }

    @Test
    public void RabbitRemvoed() {
        BigRabbit bigrabbit = new BigRabbit(11, 2);
        Location location = new Location(0, 0);

        world.setTile(location, bigrabbit);

        bigrabbit.wasNight = true; // at der var nat sidste tur
        world.setDay(); // her bliver alderen fra 11->12

        bigrabbit.act(world); 

        assertNull(world.getTile(location));
    }

    @Test 
    public void RabbitEatGrass(){
        BigRabbit rabbit = new BigRabbit(3, 5);
        Location location = new Location(0, 0);

        world.setDay();
        world.setTile(location, rabbit);

        world.setTile(new Location(0 , 1), new Grass());
        world.setTile(new Location(1 , 1), new Grass());
        world.setTile(new Location(1 , 0), new Grass());

        rabbit.act(world);

        Location newLocation = world.getLocation(rabbit);
        assertFalse(world.containsNonBlocking(newLocation));
        assertEquals(6, rabbit.energy);
    }

    @Test 
    public void RabbitRemoved0Energy(){
        World world = new World(1); // her er kortet størrelse 1,så kaninen har ingen felter at flytte sig til, og vi sikre at den er fjernet
        BigRabbit rabbit = new BigRabbit(3, 0);
        Location location = new Location(0, 0);

        world.setDay();
        world.setTile(location, rabbit);

        rabbit.act(world);

        assertNull(world.getTile(location));
    }

    @Test 
    public void RabbitReproduceatNight(){
        BigRabbit rabbit = new BigRabbit(3, 10);
        Location location = new Location(0, 0);

        world.setTile(location, rabbit);
        rabbit.reproduce(world);

        assertTrue(
            world.getTile(new Location(0, 1)) instanceof SmallRabbit ||
            world.getTile(new Location(1, 0)) instanceof SmallRabbit
            || world.getTile(new Location(1,1)) instanceof SmallRabbit
        ); //tjekker om der står en småkanin på en af storkanins nabofelter

    }

    @Test 
    public void RabbitDigHole(){
        SmallRabbit smallRabbit = new SmallRabbit();
        Location location = new Location(0, 0);

        world.setTile(location, smallRabbit);

        smallRabbit.holeDigging(world);

        assertTrue(world.getNonBlocking(location) instanceof RabbitHole); //tjkker om kaninen har gravet et hul
        assertSame(world.getNonBlocking(location), smallRabbit.rabbitHole); //tjekker om kaninen er knyttet til hullet

    }

    @Test 
    public void twoRabbitShareHole(){
        SmallRabbit first = new SmallRabbit();
        SmallRabbit second = new SmallRabbit();

        Location holeLocation = new Location(0, 0); //felt, hvso hullet skal graves

        world.setTile(holeLocation, first); // sæt den første kanin på feltet
        first.holeDigging(world); // den første kanine graver et hule 
        world.move(first, new Location(1, 0)); // flyt kanine, så hullet er nu ledigt 
        
       world.setTile(holeLocation, second); 
       second.holeDigging(world); // den anden kanine, bruger hullet, der allerede er der

       assertNotNull(first.rabbitHole); // tjekker, atder faktisk et hul
       assertSame(first.rabbitHole, second.rabbitHole); // tjekt, at begge kaniner bruger det samme hul
    }

    @Test 
    public void RabbitMovesToHoleAtNight(){
        SmallRabbit rabbit = new SmallRabbit();
        RabbitHole rabbitHole = new RabbitHole();
        Location location = new Location (0,0); // kaninen startfelt
        Location holeLocation = new Location(2, 0); // hulet tile

        world.setTile(location, rabbit); //sæt knainen på kortet

        world.setTile(holeLocation, rabbitHole); // sæt hullet på kortet
        
        rabbit.rabbitHole = rabbitHole; // give kanine det hul, den skal til
        world.setNight(); // gør det til nat

        rabbit.act(world); //kanine går en tur
        Location rabbitNextLocation = world.getLocation(rabbit); // find kaninens ny felt
        assertEquals(1, rabbitNextLocation.getX()); // tjekke at dens x-koordinar nu er 1

// kaninen starter ved x= 0 og hullet x=2, efter 1 skridt mod hullet står kaninen ved (1,0) eller(1,1)
    
    }

    @Test 
    public void rabbitStandonHole(){
        RabbitHole rabbitHole = new RabbitHole();
        Location location = new Location(0, 0);
        SmallRabbit rabbit = new SmallRabbit();

        world.setTile(location, rabbitHole);
        world.setTile(location, rabbit);

        assertSame(rabbitHole, world.getNonBlocking(location)); // der ligger hul på det felt
        assertSame(rabbit, world.getTile(location)); //kaninen står på samme felt 
        
    }


}