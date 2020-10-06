package interfaces;

import enums.EDirection;

public interface ISCC {
    /**
     * Requête d'un étage depuis la cabine
     *
     * @param etage étage demandé
     */
    void requeteCabine(int etage);

    /**
     * Requete d'un étage depuis l'étage lui-même
     *
     * @param etageSource l'étage
     * @param direction   direction du déplacement
     */
    void requeteEtage(int etageSource, EDirection direction);

    /**
     * Déclenche un arrêt immédiat d'urgence
     */
    void declencherArretUrgence();

    /**
     * Annule un arrêt d'urgence en cours
     */
    void stopperArretUrgence();
}
