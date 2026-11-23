import itumulator.world.Location;
import itumulator.world.World;
import java.util.Random;

public class SmallRabbit extends Animal {
    @Override
    public void act(World world) {
        if(!updateAge(world)) {
            return; // Afslutter metoden, hvis kaninen er død.
        }

        if (world.isDay()) {
            wakeUp(world);
        }
       
        if (!world.isOnTile(this)) {
            wasNight = world.isNight(); // skal stå før return, ellers ældes kaninen forkert
            return;
        }

        if(age >= 3){
            Location rabbitLocation = world.getLocation(this);

            BigRabbit bigRabbit = new BigRabbit(age, energy);

            bigRabbit.wasNight = world.isNight(); // Overfører wasNight status til den nye BigRabbit og beholder stye på dag/nat ved overgnagen
            bigRabbit.hole = this.hole; // Overfører rabbitHole referencen til den nye BigRabbit, så den ikke laver flere huller på samme Tile.

            world.delete(this); // fjerne den lille kanin fra verdenen, da den nu bliver opgraderet til en stor kanin.

            world.setTile(rabbitLocation, bigRabbit); // Opgraderer kaninen til en BigRabbit, hvis den er 3 år gammel.
            return; // Afslutter metoden, da kaninen nu er blevet opgraderet.
        }
        
       if (world.isNight()) {
            getHole(world);
            moveTowardHole(world);
            sleepIfInHole(world);
        } else {
            moveRandomly(world);
            if(!eatGrass(world) && new Random().nextInt(3) == 0){
                energy --; //miste 1 energi hvsi begge betingelser er opfyldt 
            }
            
            if(energy <= 0) {
                die(world);
                return;
            }
        }

        wasNight = world.isNight(); 
    }
}