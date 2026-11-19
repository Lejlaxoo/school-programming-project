import itumulator.world.Location;
import itumulator.world.World;

public class SmallRabbit extends Animal {
    @Override
    public void act(World world) {
        if(!updateAge(world)) {
            return; // Afslutter metoden, hvis kaninen er død.
        }

        if(age >= 3){
            Location rabbitLocation = world.getLocation(this);

            BigRabbit bigRabbit = new BigRabbit(age, energy);

            bigRabbit.wasNight = this.wasNight; // Overfører wasNight status til den nye BigRabbit og beholder stye på dag/nat ved overgnagen
            bigRabbit.rabbitHole = this.rabbitHole; // Overfører rabbitHole referencen til den nye BigRabbit, så den ikke laver flere huller på samme Tile.

            world.delete(this); // fjerne den lille kanin fra verdenen, da den nu bliver opgraderet til en stor kanin.

            world.setTile(rabbitLocation, bigRabbit); // Opgraderer kaninen til en BigRabbit, hvis den er 3 år gammel.
            return; // Afslutter metoden, da kaninen nu er blevet opgraderet.
        }
       if (world.isNight()) {
    getRabbitHole(world);
    moveTowardsRabbitHole(world);
    } else {
    moveRandomlyandEat(world);
}

wasNight = world.isNight(); 
}
}