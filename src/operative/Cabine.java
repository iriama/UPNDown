package operative;

import interfaces.ICabine;

import java.math.BigDecimal;

public class Cabine implements ICabine {

    private BigDecimal position;

    public Cabine(double position) {
        this.position = new BigDecimal(position);
    }

    /**
     * Déplacement de la cabine vers le haut
     *
     * @param pas vitesse
     */
    public void monter(double pas) {
        position = position.add(BigDecimal.valueOf(pas));
    }

    /**
     * Déplacement de la cabine vers le bas
     *
     * @param pas vitesse
     */
    public void descendre(double pas) {
        position = position.subtract(BigDecimal.valueOf(pas));
    }

    /**
     * Renvoi la position de la cabine (format double)
     *
     * @return position de la cabine
     */
    public double getPosition() {
        return position.doubleValue();
    }

    @Override
    public String toString() {
        return "[Cabine] position actuelle : " + getPosition();
    }
}
