import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {
    int size;
    int grassCount;
    int burrowCount;
    String rabbitName;
    int rabbitCount;


        Random random = new Random(); 
       
         try (Scanner scanner = new Scanner(new File("Data/t1-3a.txt"))) {
            size = scanner.nextInt();

            String grassRange = scanner.next();
            grassCount = getRandomCount(grassRange, random);

            String burrowRange = scanner.next();
            burrowCount = getRandomCount(burrowRange, random);  

            rabbitName = scanner.next();
            String rabbitRange = scanner.next();
            
            if(rabbitRange.contains("-")) {
                rabbitCount = getRandomCount(rabbitRange, random);
            } else {
                rabbitCount  = Integer.parseInt(rabbitRange);
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
            p.setDisplayInformation(RabbitHole.class, new DisplayInformation(Color.magenta, "hole")); 

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
          
         p.show(); 
    }


    private static int getRandomCount(String range, Random random) {
        String[] parts = range.split("-"); // deleter fx 6-8 i 6 og 8, så vi kan bruge dem som min og max
        int min = Integer.parseInt(parts[0]); // laver vi 6 og 8 om yil int frodi de var strings før
        int max = Integer.parseInt(parts[1]);
        return random.nextInt(max - min + 1) + min; // Fx 6-8: vælger 0, 1 eller 2 og lægger 6 til, så resultatet bliver 6, 7 eller 8
    }

    private static void holeDigging(World world, Location place) {
    RabbitHole hole = new RabbitHole();
    world.setTile(place, hole);
    Animal.addRabbitHole(hole);
}
}