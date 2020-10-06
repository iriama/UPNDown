package interfaces;

public interface ICabine {
    /**
     * Déplacement de la cabine vers le haut
     *
     * @param pas vitesse
     */
    void monter(double pas);

    /**
     * Déplacement de la cabine vers le bas
     *
     * @param pas vitesse
     */
    void descendre(double pas);
}
