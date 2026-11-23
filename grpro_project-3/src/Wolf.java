import itumulator.world.World;

public class Wolf extends Animal {
    private int turnsWithoutHunting = 0;
    @Override 
    public void act(World world){
        if(hunt(world)){
            turnsWithoutHunting = 0;
            return;
        }
        
        turnsWithoutHunting ++;
        if (turnsWithoutHunting >= 6){
            die(world);
            return;
        }
        
        moveRandomly(world);
    }
}
