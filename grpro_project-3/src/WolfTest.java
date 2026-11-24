import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import itumulator.world.World;
import itumulator.world.Location;

public class WolfTest {

    @Test 
    public void WolfDieefter10TurnWithoutFood(){
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

        assertFalse(world.contains(rabbit));
        assertSame(wolf, world.getTile(rabbitLocation)); // ulven tager kaninens felt
        assertEquals(energyBeforeHunting + 1, wolf.energy); //ulven fik +1 i energi

    }

    @Test
    public void wolfEatsOnlyOneRabbitPerTurn() {
        World world = new World(2);
        Wolf wolf = new Wolf();
        SmallRabbit firstRabbit = new SmallRabbit();
        SmallRabbit secondRabbit = new SmallRabbit();

        Location firstLocation = new Location(1, 0);
        Location secondLocation = new Location(0, 1);

        world.setDay();
        world.setTile(new Location(0, 0), wolf);
        world.setTile(firstLocation, firstRabbit);
        world.setTile(secondLocation, secondRabbit);

        int energyBeforeHunting = wolf.energy;

        wolf.act(world);

        assertTrue((!world.contains(firstRabbit) && world.getTile(firstLocation) == wolf
                && world.getTile(secondLocation) == secondRabbit)||
            (!world.contains(secondRabbit) && world.getTile(secondLocation) == wolf
                && world.getTile(firstLocation) == firstRabbit)); // Én kanin er fjernet, ulven overtager dens felt, og den anden bliver stående

        assertEquals(energyBeforeHunting + 1, wolf.energy); // Ulven får én energi
    }

    @Test 
    public void WolfMoveWhenNoRabbitAround(){
        World world = new World (2);
        Wolf wolf = new Wolf();

        Location wolfLocation = new Location(0,0);
        world.setTile(wolfLocation, wolf);

        world.setDay();
        wolf.act(world);

        assertTrue(world.getTile(new Location(0, 1)) == wolf ||
            world.getTile(new Location(1, 0)) == wolf ||
            world.getTile(new Location(1, 1)) == wolf ); // Tjekker, at ulven er flyttet til et af de tre nabofelter
    }

    @Test
    public void inputFileHasTwoWolfLines() throws FileNotFoundException {
        int wolfLines = 0;

        try (Scanner scanner = new Scanner(new File("Data/t2-3a.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.split("\\s+")[0].equals("wolf")) {
                    wolfLines++; // Tæl én linje for hver flok, filen beskriver
                }
            }
        }

        assertEquals(2, wolfLines); // Filen beskriver to ulveflokke
    }

    @Test
    public void wolvesJoinThePackTheyAreGiven() {
        WolfPack firstPack = new WolfPack(); // Opretter den første flok
        WolfPack secondPack = new WolfPack(); // Opretter den anden flok

        Wolf firstWolf = new Wolf(firstPack); //  den første ulv får den første flok
        Wolf secondWolf = new Wolf(firstPack); //  den anden ulv får samme flok
        Wolf thirdWolf = new Wolf(secondPack); //  den tredje ulv får den anden flok

        assertEquals(2, firstPack.getWolves().size()); // den første flok har præcis to ulve
        assertTrue(firstPack.getWolves().contains(firstWolf)); // Den første ulv er i flokken
        assertTrue(firstPack.getWolves().contains(secondWolf)); // Den anden ulv er også i flokken

        assertEquals(1, secondPack.getWolves().size()); // Den anden flok har præcis én ulv
        assertTrue(secondPack.getWolves().contains(thirdWolf)); // Det er den tredje ulv
        assertFalse(firstPack.getWolves().contains(thirdWolf)); // Ulven fra den anden flok er ikke i den første
        assertFalse(secondPack.getWolves().contains(firstWolf)); // Ulven fra den første flok er ikke i den anden
    }

    @Test
    public void wolfMovesTowardNearestPackMember() {
        World world = new World(7);
        WolfPack pack = new WolfPack();
        Wolf wolf = new Wolf(pack);
        Wolf nearestWolf = new Wolf(pack);
        Wolf farthestWolf = new Wolf(pack);

        world.setTile(new Location(3, 3), wolf); // Ulven starter i midten
        world.setTile(new Location(5, 3), nearestWolf); // To felter til højre
        world.setTile(new Location(0, 3), farthestWolf); // Tre felter til venstre
        world.setDay();

        wolf.act(world); // Lad kun den første ulv tage én tur

        assertEquals(4, world.getLocation(wolf).getX()); // Ulven tog et skridt mod højre
    }

    @Test
    public void wolfHuntsWhileLookingForItsPack() {
        World world = new World(5);
        WolfPack pack = new WolfPack();
        Wolf wolf = new Wolf(pack);
        Wolf packMember = new Wolf(pack);
        SmallRabbit rabbit = new SmallRabbit();
        Location rabbitLocation = new Location(0, 1);

        world.setTile(new Location(0, 0), wolf); // Ulvens startfelt
        world.setTile(new Location(4, 0), packMember); // Flokmedlemmet står længere til højre
        world.setTile(rabbitLocation, rabbit); // Kaninen står lige ved siden af ulven
        world.setDay();

        wolf.act(world); // Ulven tager én tur

        assertFalse(world.contains(rabbit)); // Kaninen er blevet spist
        assertSame(wolf, world.getTile(rabbitLocation)); // Ulven overtog kaninens felt
    }

    @Test
    public void loneWolfLosesToLargerPack() {
        World world = new World(5);
        WolfPack firstPack = new WolfPack();
        WolfPack secondPack = new WolfPack();

        Wolf loneWolf = new Wolf(firstPack);
        Wolf lastPackMember = new Wolf(firstPack);
        Wolf rival = new Wolf(secondPack);
        Wolf rivalPackMember = new Wolf(secondPack);
        SmallRabbit rabbit = new SmallRabbit();

        world.setTile(new Location(0, 0), loneWolf);
        world.setTile(new Location(4, 0), lastPackMember);
        world.setTile(new Location(1, 0), rival); // Rivalen står ved siden af ulven
        world.setTile(new Location(4, 4), rivalPackMember); // Rivalens flok har to levende ulve
        world.setTile(new Location(0, 1), rabbit); // Kaninen er nabo til begge ulve
        world.setDay();

        world.delete(lastPackMember); // Ulven er nu den eneste levende i sin flok
        loneWolf.act(world); // den enlige ulv tage sin tur

        assertFalse(world.contains(loneWolf)); // den enlige ulv er fjernet
        assertTrue(world.contains(rival)); // Ulven fra den større flok overlever
        assertTrue(secondPack.getWolves().contains(rival)); // Rivalen er stadig i sin egen flok
        assertFalse(firstPack.getWolves().contains(rival)); // Rivalen er ikke blevet medlem af den anden flok
    }

    @Test
    public void wolvesFromSamePackShareDen() {
        World world = new World(3);
        WolfPack pack = new WolfPack();
        Wolf firstWolf = new Wolf(pack);
        Wolf secondWolf = new Wolf(pack);
        Hole den = new Hole();
        Location denLocation = new Location(1, 1);

        world.setTile(denLocation, den); // Placér hulen på kortet
        pack.setDen(den); // flokken har én fælles hule
        world.setTile(new Location(0, 1), firstWolf); // Første ulv står ved hulen
        world.setTile(new Location(1, 0), secondWolf); // Anden ulv står også ved hulen
        world.setNight();

        firstWolf.act(world);
        secondWolf.act(world);

        assertTrue(world.contains(firstWolf)); // Første ulv er stadig i live
        assertTrue(world.contains(secondWolf)); // Anden ulv er stadig i live
        assertFalse(world.isOnTile(firstWolf)); // Første ulv er gået ned i hulen
        assertFalse(world.isOnTile(secondWolf)); // Anden ulv er også gået ned
        assertSame(den, world.getNonBlocking(denLocation)); // Hulen ligger stadig på feltet
    }

    @Test
    public void wolvesDoNotBuildDenWhenFarApart() {
        World world = new World(10);
        WolfPack pack = new WolfPack();
        Wolf firstWolf = new Wolf(pack);
        Wolf secondWolf = new Wolf(pack);
        Wolf thirdWolf = new Wolf(pack);

        world.setTile(new Location(0, 0), firstWolf);
        world.setTile(new Location(9, 0), secondWolf); // Står langt fra den første
        world.setTile(new Location(0, 9), thirdWolf); // Står også langt fra den første
        world.setDay();

        firstWolf.act(world); 

        assertNull(pack.getDen()); // Tre ulve i samme flok er ikke nok, når de står spredt
    }

    @Test
    public void wolvesBuildDenWhenTwoPackMembersAreNearby() {
        World world = new World(5);
        WolfPack pack = new WolfPack();
        Wolf firstWolf = new Wolf(pack);
        Wolf secondWolf = new Wolf(pack);
        Wolf thirdWolf = new Wolf(pack);

        world.setTile(new Location(2, 2), firstWolf);
        world.setTile(new Location(2, 3), secondWolf); // Nabo til den første ulv
        world.setTile(new Location(3, 2), thirdWolf); // Også nabo til den første ulv
        world.setDay();

        firstWolf.act(world); 

        Hole den = pack.getDen();
        assertNotNull(den); // Flokken har fået en fælles hule
        assertTrue(world.contains(den)); // Hulen er placeret i verdenen
    }

    @Test
    public void wolfHuntsDuringDayEvenWhenItHasADen() {
        World world = new World(5);
        WolfPack pack = new WolfPack();
        Wolf wolf = new Wolf(pack);
        Hole den = new Hole();
        SmallRabbit rabbit = new SmallRabbit();
        Location rabbitLocation = new Location(0, 1);

        world.setTile(new Location(4, 0), den); // Hulen ligger et stykke væk
        pack.setDen(den);
        world.setTile(new Location(0, 0), wolf);
        world.setTile(rabbitLocation, rabbit); // Kaninen står på et nabofelt
        world.setDay();

        wolf.act(world);

        assertFalse(world.contains(rabbit)); // Ulven spiste kaninen
        assertSame(wolf, world.getTile(rabbitLocation)); // Ulven overtog kaninens felt
    }

    @Test
    public void wolfMovesTowardItsDenAtNight() {
        World world = new World(5);
        WolfPack pack = new WolfPack();
        Wolf wolf = new Wolf(pack);
        Hole den = new Hole();

        world.setTile(new Location(4, 0), den); // Hulen ligger til højre for ulven
        pack.setDen(den);
        world.setTile(new Location(0, 0), wolf);
        world.setNight();

        wolf.act(world);

        assertEquals(1, world.getLocation(wolf).getX()); // Ulven tog ét skridt mod hulen
    }

    @Test
    public void wolfEntersDenFromItsTileAtNight() {
        World world = new World(3);
        WolfPack pack = new WolfPack();
        Wolf wolf = new Wolf(pack);
        Hole den = new Hole();
        Location denLocation = new Location(1, 1);

        world.setTile(denLocation, den);
        pack.setDen(den);
        world.setTile(denLocation, wolf); // Ulven står direkte på hulens felt
        world.setNight();

        wolf.act(world);

        assertTrue(world.contains(wolf)); // Ulven findes stadig i verdenen
        assertFalse(world.isOnTile(wolf)); // Ulven er gået ned i hulen
        assertSame(den, world.getNonBlocking(denLocation)); // Hulen ligger stadig på feltet
    }

    @Test
    public void wolfFromLargerPackWinsAndEatsRabbit() {
        World world = new World(7);
        WolfPack smallPack = new WolfPack();
        WolfPack largePack = new WolfPack();

        Wolf wolf = new Wolf(smallPack);
        Wolf rival = new Wolf(largePack);
        Wolf rivalPackMember = new Wolf(largePack);
        SmallRabbit rabbit = new SmallRabbit();
        Location rabbitLocation = new Location(1, 0);

        world.setTile(new Location(0, 0), wolf);
        world.setTile(rabbitLocation, rabbit); // Kaninen er nabo til begge rivaler
        world.setTile(new Location(2, 0), rival);
        world.setTile(new Location(6, 6), rivalPackMember); // Tæller med i flokken
        world.setDay();

        int energyBeforeHunt = rival.energy;
        wolf.act(world);

        assertFalse(world.contains(wolf)); // Ulven fra den mindre flok taber
        assertFalse(world.contains(rabbit)); // Vinderen har spist kaninen
        assertSame(rival, world.getTile(rabbitLocation)); // Vinderen overtager feltet
        assertEquals(energyBeforeHunt + 1, rival.energy); // Vinderen får energien
    }

    @Test
    public void oneWolfWinsWhenPacksAreEqual() {
        World world = new World(3);
        Wolf firstWolf = new Wolf(new WolfPack()); // Én ulv i den første flok
        Wolf secondWolf = new Wolf(new WolfPack()); // Én ulv i den anden flok
        SmallRabbit rabbit = new SmallRabbit();
        Location rabbitLocation = new Location(1, 0);

        world.setTile(new Location(0, 0), firstWolf);
        world.setTile(rabbitLocation, rabbit); // Kaninen står mellem de to ulve
        world.setTile(new Location(2, 0), secondWolf);
        world.setDay();

        firstWolf.act(world); // Den første ulv starter kampen om kaninen

        boolean firstSurvived = world.contains(firstWolf);
        boolean secondSurvived = world.contains(secondWolf);

        assertNotEquals(firstSurvived, secondSurvived); // Præcis én ulv overlever
        assertFalse(world.contains(rabbit)); // Kaninen er blevet spist

        Wolf winner;

        if (firstSurvived) {
            winner = firstWolf; // Den første ulv overlevede
        } else {
            winner = secondWolf; // Den anden ulv overlevede
        }

        assertSame(winner, world.getTile(rabbitLocation)); // vinderen stå på kaninens felt. 
    }

    @Test
    public void rivalWolvesFightWithoutACarrotOrRabbit() {
        World world = new World(5);
        WolfPack smallPack = new WolfPack();
        WolfPack largePack = new WolfPack();

        Wolf loneWolf = new Wolf(smallPack);
        Wolf rival = new Wolf(largePack);
        Wolf rivalPackMember = new Wolf(largePack);

        world.setDay();
        world.setTile(new Location(0, 0), loneWolf);
        world.setTile(new Location(1, 0), rival); // Rivalen står på nabofeltet
        world.setTile(new Location(4, 4), rivalPackMember); // Rivalens flok har to levende ulve

        loneWolf.act(world);

        assertFalse(world.contains(loneWolf)); // Den enlige ulv taber
        assertTrue(world.contains(rival)); // Ulven fra den større flok overlever
    }

    @Test
    public void babyWolfBecomesAdultAfterTwoMeals() {
        World world = new World(3);
        Wolf baby = new Wolf(new WolfPack(), true);
        SmallRabbit firstRabbit = new SmallRabbit();
        SmallRabbit secondRabbit = new SmallRabbit();

        world.setDay();
        world.setTile(new Location(0, 0), baby);
        world.setTile(new Location(1, 0), firstRabbit);

        assertTrue(baby.isBaby()); // Ulven starter som unge

        baby.act(world);
        assertFalse(world.contains(firstRabbit)); // Første måltid
        assertTrue(baby.isBaby()); // Ét måltid er endnu ikke nok

        world.setTile(new Location(2, 0), secondRabbit); // Nabo til ulvens nye felt

        baby.act(world);

        assertFalse(world.contains(secondRabbit)); // Andet måltid
        assertFalse(baby.isBaby()); // Nu er ulven voksen
    }
}