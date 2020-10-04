package operative;
import interfaces.ICabine;

import java.math.BigDecimal;

public class Cabine implements ICabine {

    private BigDecimal position;

    public Cabine(double position)  {
        this.position = new BigDecimal(position);
    }

    public void monter(double pas) {
        position = position.add(BigDecimal.valueOf(pas));
    }
    public void descendre(double pas) {
        position = position.subtract(BigDecimal.valueOf(pas));
    }

    public double getPosition() {
        return position.doubleValue();
    }

    @Override
    public String toString() {
        return "[Cabine] position actuelle : " + getPosition();
    }
}
