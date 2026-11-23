import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int size;
        int grassCount = 0;
        int burrowCount =0;
        int rabbitCount = 0;
        List<Integer> wolfPackSizes = new ArrayList<>();

        Random random = new Random(); 
        try (Scanner scanner = new Scanner(new File("Data/t2-3a.txt"))) {
            size = scanner.nextInt();
            scanner.nextLine();

            while (scanner.hasNext()) {
                String type = scanner.next();   // fx "grass", "burrow" eller "rabbit"
                String range = scanner.next();  // fx "3-5" eller "4"

                int count;
                if (range.contains("-")) {
                    count = getRandomCount(range, random);
                } else {
                    count = Integer.parseInt(range);
                }

                if (type.equals("grass")) {
                    grassCount = count;
                } else if (type.equals("burrow")) {
                    burrowCount = count;
                } else if (type.equals("rabbit")) {
                    rabbitCount = count;
                } else if (type.equals("wolf")) {
                    wolfPackSizes.add(count);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return; 
        }

        Program p = new Program( size, 800, 75);

        World w = p.getWorld();  

        for (int i = 0; i < grassCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size); 
            Location randomLocation = new Location(x, y);

            while (w.containsNonBlocking(randomLocation)) {
                x = random.nextInt(size);
                y = random.nextInt(size);
                randomLocation = new Location(x, y);
            }

            w.setTile(randomLocation, new Grass()); // Placerer græs på et tilfældigt tile i verdenen

        }
        p.setDisplayInformation( Grass.class, new DisplayInformation(new Color(1, 142, 66), "grass"));
        p.setDisplayInformation(Hole.class, new DisplayInformation(Color.magenta, "hole")); 

        for (int i = 0; i < rabbitCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size); 

            Location randomLocation = new Location(x, y);

            while (!w.isTileEmpty(randomLocation)) {
                x = random.nextInt(size);
                y = random.nextInt(size);
                randomLocation = new Location(x, y);
            }
            SmallRabbit rabbit = new SmallRabbit();
            w.setTile(randomLocation, rabbit); 

        }
        for (int i = 0; i < burrowCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            Location randomLocation = new Location(x, y);

            while (!w.isTileEmpty(randomLocation) ||
            w.containsNonBlocking(randomLocation)) {
                x = random.nextInt(size);
                y = random.nextInt(size);
                randomLocation = new Location(x, y);
            }
            holeDigging(w, randomLocation);

        }
        p.setDisplayInformation(BigRabbit.class, new DisplayInformation(Color.WHITE, "rabbit-large")); 
        p.setDisplayInformation(SmallRabbit.class, new DisplayInformation(Color.WHITE, "rabbit-small"));

        for (int packSize : wolfPackSizes) {
        WolfPack pack = new WolfPack(); // Opretter én ny flok for denne inputlinje.
      
        for (int i = 0; i < packSize; i++) {
        int x = random.nextInt(size);
        int y = random.nextInt(size);

        Location randomLocation = new Location(x, y);

        while (!w.isTileEmpty(randomLocation) || w.containsNonBlocking(randomLocation)) { // Finder et ledigt felt.
            x = random.nextInt(size);
            y = random.nextInt(size);

            randomLocation = new Location(x, y);
        }

        Wolf wolf = new Wolf(pack); // Opretter ulven i den fælles flok.

        w.setTile(randomLocation, wolf); // Placerer ulven i verden.
    }
}
        p.setDisplayInformation( Wolf.class, new DisplayInformation(Color.BLACK, "wolf"));
        
        p.show();
        
    }

    private static int getRandomCount(String range, Random random) {
        String[] parts = range.split("-"); // deleter fx 6-8 i 6 og 8, så vi kan bruge dem som min og max
        int min = Integer.parseInt(parts[0]); // laver vi 6 og 8 om yil int frodi de var strings før
        int max = Integer.parseInt(parts[1]);
        return random.nextInt(max - min + 1) + min; // Fx 6-8: vælger 0, 1 eller 2 og lægger 6 til, så resultatet bliver 6, 7 eller 8
    }

    private static void holeDigging(World world, Location place) { // kaninen hul der allerede ligger på kortet når stimulation begynder og kan kaniner bruger det her hul til fx sove
       Hole hole = new Hole();
     world.setTile(place, hole);
        Animal.addHole(hole);
    }
}