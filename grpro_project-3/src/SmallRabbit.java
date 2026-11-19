import itumulator.world.World;

public class SmallRabbit extends Animal {
    @Override
    public void act(World world) {
        if(!updateAge(world)) {
            return; // Afslutter metoden, hvis kaninen er død.
        }

        if(age == 6){
            world.setTile(world.getLocation(this), new BigRabbit(age, energy)); // Opgraderer kaninen til en BigRabbit, hvis den er 6 år gammel.
            return; // Afslutter metoden, da kaninen nu er blevet opgraderet.
        }

        wasNight = world.isNight(); // Opdaterer wasNight for at holde styr på, om det er nat eller dag i næste tur.

    
        moveRandomlyandEat(world);
}

}

