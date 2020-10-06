package applicative;

import enums.EDirection;

/**
 * Requête d'un passager
 */
public class Requete {
    public int etage;
    public boolean sens;
    public EDirection direction;

    public Requete(int etage) {
        this.etage = etage;
        sens = false;
    }

    public Requete(int etage, EDirection direction) {
        this.etage = etage;
        sens = true;
        this.direction = direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Requete)) return false;

        Requete requete = (Requete) obj;

        return sens ? requete.sens && etage == requete.etage && direction == requete.direction : !requete.sens && requete.etage == etage;
    }

    @Override
    public String toString() {
        return "requete@" + etage + (sens ? ":" + direction : "");
    }
}
