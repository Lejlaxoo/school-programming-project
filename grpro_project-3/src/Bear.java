import itumulator.world.World;
public class Bear extends Animal
{
    private int turnsWithoutHunting = 0;
    @Override
    public void act(World world) {
        
        if (hunt(world) || eatGrass(world)) {
            turnsWithoutHunting = 0; // Bjørnen har spist, så tælleren starter forfra
            return;
        }

        if (world.isDay()) {
            turnsWithoutHunting++;

            if (turnsWithoutHunting >= 10) {
            die(world);
            return;
        }
        }

        moveRandomly(world);

    }
}

