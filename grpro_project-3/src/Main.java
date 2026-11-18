import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {
         System.out.println("GRASS: " +  Main.class.getResource("/images/grass.png"));

        System.out.println("RABBIT: " + Main.class.getResource("/images/rabbit-small.png"));

        int size;
        int grassCount; 
        String grass;
        String rabbitName; 
        int rabbitCount;

        Random random = new Random(); 
       
         try (Scanner scanner = new Scanner(new File("Data/t1-2cde.txt"))) {
            size = scanner.nextInt();
            grass = scanner.next();     
            String grassRange = scanner.next();
            grassCount = getRandomCount(grassRange, random);

            rabbitName = scanner.next();
            String rabbitRange = scanner.next();
            rabbitCount = getRandomCount(rabbitRange, random);

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

            while (!w.isTileEmpty(randomLocation)) {
                x = random.nextInt(size);
                y = random.nextInt(size);
                randomLocation = new Location(x, y);
            }
            
            w.setTile(randomLocation, new Grass()); 

        }

            p.setDisplayInformation( Grass.class, new DisplayInformation(new Color(1, 142, 66), "grass"));
          
          for (int i = 0; i < rabbitCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size); 
            
            Location randomLocation = new Location(x, y);

                while (!w.isTileEmpty(randomLocation)) {
                    x = random.nextInt(size);
                    y = random.nextInt(size);
                    randomLocation = new Location(x, y);
                }
                Rabbit rabbit = new Rabbit();
                w.setTile(randomLocation, rabbit); 

            }
          
            p.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.WHITE, "rabbit-small")); 

            p.show();

        
    }

    private static int getRandomCount(String range, Random random) {
        String[] parts = range.split("-");
        int min = Integer.parseInt(parts[0]);
        int max = Integer.parseInt(parts[1]);
        return random.nextInt(max - min + 1) + min;
    }
}