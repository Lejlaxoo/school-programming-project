import java.util.ArrayList;
import java.util.List; 

public class WolfPack { 
  private final List<Wolf> wolves = new ArrayList<>();
    private Hole den;  // Flokkens fælles hule.


    public void addWolf(Wolf wolf) {  // Tilføjer en ulv til flokken.
        wolves.add(wolf); 
    }
   
    public List<Wolf> getWolves() {  // Returnerer alle ulvene i flokken.
        return wolves;
    }

    public Hole getDen() {  // Returnerer flokkens hule.
        return den;
    }

    public void setDen(Hole den) { // Gemmer flokkens hule.
        this.den = den;
    }
}

    
}
