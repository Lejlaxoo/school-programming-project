import itumulator.simulator.Actor;  
import itumulator.world.World; 
import itumulator.world.Location;

public class Rabbit implements Actor {
   private  int age; 
   private  int energy; 
   private int lastAgeDay;

   public Rabbit() {
        this.age = 0; 
        this.energy = 10; 
        this.lastAgeDay = -1;
    }

    @Override 
    public void act(World world) {
    if (world.isDay()) { 
       int currentDay = world.getCurrentTime() / World.getTotalDayDuration();
                 if (currentDay > lastAgeDay) {
                     age++;
                     lastAgeDay = currentDay;
                     updateEnergy(world);
                        if (age >= 12) {
                         return;
                        }
                  } 
       
    
     Location location = world.getLocation(this);
     Object food = world.getNonBlocking(location); 
        
        
        if (food instanceof Grass) {
            world.delete(food); 
            this.energy++; 
        } else {
            this.energy--; 
        } 

        if(energy <= 0) {
            world.delete(this);
             return;
        }
        } 
    }
    public void updateEnergy(World world) {
        if ( age >= 1 && age <= 5) { 
            energy = 10; 
        } else if (age >= 6 && age <= 10) {
            energy = 5; 
        } else if (age == 11) { 
            energy = 2;   
        } else { 
            energy = 0;    
            world.delete(this); 
        }  
    }
}
