package interfaces;

import enums.ECauseArretUrgence;

public interface IMoteur {
    /**
     * Actionne le moteur vers le haut
     */
    void monter();

    /**
     * Actionne le moteur vers le bas
     */
    void descendre();

    /**
     * Programme un arrêt au prochain étage
     */
    void arretProchainNiveau();

    /**
     * Lance immédiatement un arrêt d'urgence
     *
     * @param cause cause de l'arrêt d'urgence
     */
    void arretUrgence(ECauseArretUrgence cause);

    /**
     * Ajoute un objet IEcouteurEtageAtteint à la liste d'écoute
     *
     * @param ecouteur objet en écoute
     */
    void ajouterEcouteurEtageAtteint(IEcouteurEtageAtteint ecouteur);
}
