import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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
    public void rabbitDiesAfterThreeDayTurnsWithoutFood() {
        World world = new World(1); // Ét felt uden græs, så kaninen bliver samme sted
        BigRabbit rabbit = new BigRabbit(3, 10);
        Location location = new Location(0, 0);

        world.setDay();
        world.setTile(location, rabbit);

        rabbit.act(world);
        assertSame(rabbit, world.getTile(location)); // Lever efter første dagtur

        rabbit.act(world);
        assertSame(rabbit, world.getTile(location)); // Lever efter anden dagtur

        rabbit.act(world);
        assertNull(world.getTile(location)); // Fjernes på tredje dagtur
    }

    @Test 
    public void RabbitRemoved0Energy(){
        World world = new World(1);
        BigRabbit rabbit = new BigRabbit(3, 0);
        Location location = new Location(0, 0);

        world.setDay();
        world.setTile(location, rabbit);

        rabbit.act(world);

        assertNull(world.getTile(location));

    }

    @Test
    public void rabbitEnergyDropsToFiveAtSixYears() {
        World world = new World(1);
        Location location = new Location(0, 0);
        BigRabbit rabbit = new BigRabbit(5, 10);
        rabbit.wasNight = true;

        world.setTile(location, rabbit);
        world.setDay();

        rabbit.act(world);

        assertEquals(6, rabbit.age);
        assertEquals(5, rabbit.energy);
    }

    @Test
    public void rabbitEnergyDropsToTwoAtElevenYears() {
        World world = new World(1);
        Location location = new Location(0, 0);
        BigRabbit rabbit = new BigRabbit(10, 5);
        rabbit.wasNight = true;

        world.setTile(location, rabbit);
        world.setDay();

        rabbit.act(world);

        assertEquals(11, rabbit.age);
        assertEquals(2, rabbit.energy);
    }

    @Test
    public void smallRabbitBecomesAdultAtThree() {
        World world = new World(1);
        Location location = new Location(0, 0);
        SmallRabbit rabbit = new SmallRabbit();
        rabbit.age = 2;
        rabbit.wasNight = true;

        world.setTile(location, rabbit);
        world.setDay();

        rabbit.act(world);

        Object animal = world.getTile(location);
        assertTrue(animal instanceof BigRabbit);
        assertEquals(3, ((BigRabbit) animal).age);
    }

    @Test 
    public void RabbitReproduceatNight(){
        BigRabbit rabbit = new BigRabbit(3, 10);
        Location location = new Location(0, 0);
        Hole hole = new Hole();
        world.setTile(new Location(2, 2), hole); // Placér hullet på 3 × 3-kortet
        rabbit.hole = hole;

        world.setTile(location, rabbit);
        world.setNight();
        rabbit.act(world);

        assertTrue(
            world.getTile(new Location(0, 1)) instanceof SmallRabbit ||
            world.getTile(new Location(1, 0)) instanceof SmallRabbit
            || world.getTile(new Location(1,1)) instanceof SmallRabbit
        ); //tjekker om der står en småkanin på en af storkanins nabofelter
    }

    @Test
    public void rabbitDoesNotReproduceWhenAllSurroundingTilesAreOccupied() {
        World world = new World(2);
        BigRabbit rabbit = new BigRabbit(3, 10);
        Hole hole = new Hole();

        Location start = new Location(0, 0);
        Location first = new Location(0, 1);
        Location second = new Location(1, 0);
        Location third = new Location(1, 1);

        SmallRabbit firstRabbit = new SmallRabbit();
        SmallRabbit secondRabbit = new SmallRabbit();
        SmallRabbit thirdRabbit = new SmallRabbit();

        world.setTile(start, hole);
        world.setTile(start, rabbit);
        rabbit.hole = hole; // Den voksne kanin har allerede et hul

        world.setTile(first, firstRabbit);
        world.setTile(second, secondRabbit);
        world.setTile(third, thirdRabbit);
        world.setNight();

        rabbit.act(world);

        assertSame(firstRabbit, world.getTile(first));
        assertSame(secondRabbit, world.getTile(second));
        assertSame(thirdRabbit, world.getTile(third));
    }

    @Test
    public void rabbitDoesNotReproduceDuringDay() {
        World world = new World(2);
        BigRabbit rabbit = new BigRabbit(3, 10);
        Location start = new Location(0, 0);

        world.setTile(start, rabbit);
        world.setDay(); //  det bliver til dag
        rabbit.act(world); //  kaninen tage sin normale dagtur

        assertFalse(world.getTile(start) instanceof SmallRabbit);
        assertFalse(world.getTile(new Location(0, 1)) instanceof SmallRabbit);
        assertFalse(world.getTile(new Location(1, 0)) instanceof SmallRabbit);
        assertFalse(world.getTile(new Location(1, 1)) instanceof SmallRabbit);
    }

    @Test 
    public void RabbitDigHole(){
        SmallRabbit smallRabbit = new SmallRabbit();
        Location location = new Location(0, 0);

        world.setTile(location, smallRabbit);

        smallRabbit.holeDigging(world);

        assertTrue(world.getNonBlocking(location) instanceof Hole); //tjkker om kaninen har gravet et hul
        assertSame(world.getNonBlocking(location), smallRabbit.hole); //tjekker om kaninen er knyttet til hullet

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

        assertNotNull(first.hole); // tjekker, atder faktisk et hul
        assertSame(first.hole, second.hole); // tjekt, at begge kaniner bruger det samme hul
    }

    @Test
    public void RabbitGoesinHoleatNight() {
        SmallRabbit rabbit = new SmallRabbit();
        Hole hole = new Hole();
        Location location = new Location(0, 0);

        world.setTile(location, hole);
        world.setTile(location, rabbit);
        rabbit.hole = hole; // Giv kaninen det hul, den står på
        world.setNight();

        rabbit.act(world);

        assertFalse(world.isOnTile(rabbit)); // Kaninen er gået ned i hullet
        assertSame(hole, world.getNonBlocking(location)); // Hullet ligger stadig på feltet
    }

    @Test 
    public void RabbitMovesToHoleAtNight(){
        SmallRabbit rabbit = new SmallRabbit();
        Hole rabbitHole = new Hole();
        Location location = new Location (0,0); // kaninen startfelt
        Location holeLocation = new Location(2, 0); // hulet tile

        world.setTile(location, rabbit); //sæt knainen på kortet

        world.setTile(holeLocation, rabbitHole); // sæt hullet på kortet

        rabbit.hole = rabbitHole; // give kanine det hul, den skal til
        world.setNight(); // gør det til nat

        rabbit.act(world); //kanine går en tur
        Location rabbitNextLocation = world.getLocation(rabbit); // find kaninens ny felt
        assertEquals(1, rabbitNextLocation.getX()); // tjekke at dens x-koordinar nu er 1

        // kaninen starter ved x= 0 og hullet x=2, efter 1 skridt mod hullet står kaninen ved (1,0) eller(1,1)

    }

    @Test 
    public void rabbitStandonHole(){
        Hole rabbitHole = new Hole();
        Location location = new Location(0, 0);
        SmallRabbit rabbit = new SmallRabbit();

        world.setTile(location, rabbitHole);
        world.setTile(location, rabbit);

        assertSame(rabbitHole, world.getNonBlocking(location)); // der ligger hul på det felt
        assertSame(rabbit, world.getTile(location)); //kaninen står på samme felt 

    }

    public void rabbitKeepsItsExistingHole() {
        SmallRabbit rabbit = new SmallRabbit();
        Hole hole = new Hole();
        Location holeLocation = new Location(0, 0);
        Location rabbitLocation = new Location(1, 0);

        world.setTile(holeLocation, hole); // Placér hullet på kortet
        world.setTile(rabbitLocation, rabbit); // Placér kaninen på et andet felt
        rabbit.hole = hole; // kaninen knyttet til det eksisterende hul

        rabbit.holeDigging(world); // kaninen forsøge at grave et hul

        assertSame(hole, rabbit.hole); // Kaninen beholder sit oprindelige hul
        assertFalse(world.containsNonBlocking(rabbitLocation)); // Der er ikke gravet et nyt hul på kaninens felt
    }

    public void rabbitWakesUpOnHoleTile() {
        SmallRabbit rabbit = new SmallRabbit();
        Hole hole = new Hole();
        Location location = new Location(0, 0);

        world.setTile(location, hole); // Placér hullet
        world.setTile(location, rabbit); // Placér kaninen oven på hullet
        rabbit.hole = hole; 
        world.remove(rabbit); 

        world.setDay(); 

        rabbit.wakeUp(world); // kaninen kommer op af hullet

        assertSame(rabbit, world.getTile(location)); // Kaninen kommer op på hullets eget felt
    }

    @Test
    public void rabbitWakesUpOnNeighborWhenHoleTileIsOccupied() {
        SmallRabbit rabbit = new SmallRabbit();
        SmallRabbit otherRabbit = new SmallRabbit();
        Hole hole = new Hole();
        Location location = new Location(0, 0);

        world.setTile(location, hole); 
        world.setTile(location, rabbit); 
        rabbit.hole = hole; 
        world.remove(rabbit); //  den første kanin er nede i hullet, fjernes fra feltet
        world.setTile(location, otherRabbit); //  den anden kanin blokere indgangen

        world.setDay(); 

        rabbit.wakeUp(world); // den første kanin forsøger at komme op

        assertSame(otherRabbit, world.getTile(location)); // Den anden kanin står stadig ved indgangen
        assertTrue(world.isOnTile(rabbit)); // Den første kanin er kommet op på kortet
        assertTrue(world.getSurroundingTiles(location).contains(world.getLocation(rabbit))); // Den er kommet op på et nabofelt til hullet
    }
}