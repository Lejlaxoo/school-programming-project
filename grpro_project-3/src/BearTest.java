import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import itumulator.world.Location;
import itumulator.world.World;

public class BearTest {

    @Test
    public void bearEatEverything() {
        checkHunt(new SmallRabbit());
        checkHunt(new BigRabbit(3, 10));
        checkHunt(new Wolf());
        checkHunt(new Bear());
    }

    private void checkHunt(Animal prey) {
        World world = new World(2);
        Bear bear = new Bear();
        Location preyLocation = new Location(1, 0);
        double energyBefore = bear.energy;

        world.setDay();
        world.setTile(new Location(0, 0), bear);
        world.setTile(preyLocation, prey); // Der er kun ét bytte

        bear.act(world);

        assertFalse(world.contains(prey)); // Byttet er fjernet
        assertSame(bear, world.getTile(preyLocation)); // Bjørnen overtager feltet
        assertEquals(energyBefore + 2.0, bear.energy);
    }

    @Test
    public void bearEatsGrass() {
        World world = new World(1);
        Bear bear = new Bear();
        Grass grass = new Grass();
        Location location = new Location(0, 0);
        double energyBefore = bear.energy;

        world.setTile(location, grass);
        world.setTile(location, bear); // Bjørnen står oven på græsset

        bear.act(world);

        assertFalse(world.contains(grass)); // Græsset er spist
        assertEquals(energyBefore + 1.0, bear.energy);
    }

    @Test
    public void bearEatsNormalBush() {
        World world = new World(1);
        Bear bear = new Bear();
        NormalBush bush = new NormalBush();
        Location location = new Location(0, 0);
        double energyBefore = bear.energy;

        world.setTile(location, bush);
        world.setTile(location, bear);

        bear.act(world);

        assertFalse(world.contains(bush)); // Den almindelige busk forsvinder
        assertEquals(energyBefore + 0.5, bear.energy);
    }

    @Test
    public void bearEatsThreeBerriesAndLeavesBushStanding() {
        World world = new World(1);
        Bear bear = new Bear();
        BerryBush bush = new BerryBush();
        Location location = new Location(0, 0);
        double energyBefore = bear.energy;

        world.setDay();
        world.setTile(location, bush);
        world.setTile(location, bear);

        bear.act(world);
        assertEquals(energyBefore + 1.5, bear.energy); // Første bær
        bear.act(world);
        assertEquals(energyBefore + 3.0, bear.energy); // Andet bær
        bear.act(world);
        assertEquals(energyBefore + 4.5, bear.energy); // Tredje bær

        bear.act(world);
        assertEquals(energyBefore + 4.5, bear.energy); // Busken er nu tom
        assertSame(bush, world.getNonBlocking(location)); // Busken står stadig
    }

    @Test
    public void berryBushRegrowsOneBerryAfterFiveTurns() {
        World world = new World(1);
        BerryBush bush = new BerryBush();
        world.setTile(new Location(0, 0), bush);

        assertTrue(bush.eat()); // Spis alle tre bær
        assertTrue(bush.eat());
        assertTrue(bush.eat());

        for (int turn = 0; turn < 4; turn++) {
            bush.act(world);
        }

        assertFalse(bush.eat()); // Stadig tom efter fire ture

        bush.act(world);
        assertTrue(bush.eat()); // Ét nyt bær efter fem ture
        assertFalse(bush.eat()); // Der kom kun ét nyt bær
    }

    @Test
    public void bearStaysWithinTerritoryOnFirstTurn() {
        World world = new World(15);
        Bear bear = new Bear();
        Location start = new Location(5, 5);

        world.setTile(start, new Grass());
        world.setTile(start, bear);
        world.setDay();
        bear.act(world); // Bjørnen gemmer startfeltet som centrum og spiser græs

        world.move(bear, new Location(10, 5)); // Sæt bjørnen ved kanten
        bear.act(world); // Første dagtur uden mad

        Location after = world.getLocation(bear);
        assertNotEquals(new Location(10, 5), after); // Bjørnen flyttede sig
        assertTrue(Math.abs(after.getX() - start.getX()) <= 5);
        assertTrue(Math.abs(after.getY() - start.getY()) <= 5);
    }

    @Test
    public void bearDiesAfter10TurnsWithoutFood() {
        World world = new World(1);
        Bear bear = new Bear();
        Location location = new Location(0, 0);
        world.setTile(location, bear);
        world.setDay();

        for (int turn = 0; turn < 9; turn++) {
            bear.act(world);
        }

        assertSame(bear, world.getTile(location)); // Lever efter ni dagture

        world.setNight();
        bear.act(world);
        assertSame(bear, world.getTile(location)); // Natteturen tæller ikke

        world.setDay();
        bear.act(world);
        assertFalse(world.contains(bear)); // Fjernes på den tiende dagtur
    }
}