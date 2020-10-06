package operative;

import enums.ECauseArretUrgence;
import enums.EDirection;
import enums.EStatut;
import interfaces.IEcouteurEtageAtteint;
import interfaces.IMoteur;

import java.util.Vector;

/**
 * Simulation d'un moteur d'ascenseur
 */
public class Moteur implements IMoteur {

    private static final int DUREE_MIN_ARRET_MS = 5000;
    private final int nbEtages;
    private final double pas;
    private final Cabine cabine;
    private final Vector<IEcouteurEtageAtteint> listeners;
    private EStatut statut;
    private EDirection direction;
    private boolean arretProchainNiveau;
    private double narretProchainNiveau;
    private int dernierEtage;


    public Moteur(double pas, int nbEtages) {
        listeners = new Vector<IEcouteurEtageAtteint>();
        statut = EStatut.ARRET;
        direction = EDirection.HAUT;
        cabine = new Cabine(0);
        this.pas = pas;
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
        dernierEtage = -1;

        this.nbEtages = nbEtages;
    }

    /**
     * Change la direction du moteur
     *
     * @param direction nouvelle direction
     */
    private void changerDirection(EDirection direction) {
        if (this.direction == direction) return;

        System.out.println("[MOTEUR] changement direction : " + this.direction + " -> " + direction);
        this.direction = direction;
    }

    /**
     * Change l'état du moteur (marche/arrêt/arrêt urgence)
     *
     * @param statut nouvel état
     */
    private void changerStatut(EStatut statut) {
        if (this.statut == statut) return;

        System.out.println("[MOTEUR] changement statut : " + this.statut + " -> " + statut);
        this.statut = statut;
    }

    /**
     * Helper; change l'état du moteur à l'arrêt + log
     */
    private void arret() {
        changerStatut(EStatut.ARRET);
    }


    /**
     * Ajoute un objet IEcouteurEtageAtteint à la liste d'écoute
     *
     * @param ecouteur
     */
    public void ajouterEcouteurEtageAtteint(IEcouteurEtageAtteint ecouteur) {
        listeners.add(ecouteur);
    }

    /**
     * Actionne le moteur vers le haut
     */
    public void monter() {
        changerDirection(EDirection.HAUT);
        changerStatut(EStatut.MARCHE);
    }

    /**
     * Actionne le moteur vers le bas
     */
    public void descendre() {
        changerDirection(EDirection.BAS);
        changerStatut(EStatut.MARCHE);
    }

    /**
     * Programme un arrêt au prochain étage
     */
    public void arretProchainNiveau() {
        arretProchainNiveau = true;
        narretProchainNiveau = positionCabine();
        System.out.println("[MOTEUR] arrêt au prochain niveau demandé.");
    }

    /**
     * Lance immédiatement un arrêt d'urgence
     *
     * @param cause cause de l'arrêt d'urgence
     */
    public void arretUrgence(ECauseArretUrgence cause) {

        if (statut == EStatut.ARRET_URGENCE && cause == ECauseArretUrgence.PASSAGER) {
            changerStatut(EStatut.ARRET);
            System.out.println("[MOTEUR] cause annulation arrêt urgence : " + cause);

            return;
        }

        arretProchainNiveau = false;

        changerStatut(EStatut.ARRET_URGENCE);
        System.out.println("[MOTEUR] cause arrêt urgence : " + cause);
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
    }


    /**
     * Renvoi la direction du moteur (simulation)
     *
     * @return direction
     */
    public EDirection direction() {
        return direction;
    }

    /**
     * Renvoi le statut du moteur (simulation)
     *
     * @return statut
     */
    public EStatut statut() {
        return statut;
    }


    @Override
    public String toString() {
        return "[MOTEUR] statut : " + statut + " | " + cabine;
    }


    /**
     * Une étape dans le temps du moteur (déplacement effectif/ou non de la cabine)
     *
     * @param attenteMonteeDescente spécifie si le moteur lance une attente active pendant les arrêts
     * @throws InterruptedException
     */
    public void etape(boolean attenteMonteeDescente) throws InterruptedException {
        double position = positionCabine();


        for (int etage = 0; etage < nbEtages; etage++) {

            // on est sur un étage
            if (position == etage) {

                // arrêt prochain niveau
                if (arretProchainNiveau && narretProchainNiveau != position) {
                    this.arret();
                    arretProchainNiveau = false;
                    narretProchainNiveau = -1;
                    if (attenteMonteeDescente) {
                        System.out.println("[MOTEUR] arrêt de " + DUREE_MIN_ARRET_MS + " ms minimum.");
                        Thread.sleep(DUREE_MIN_ARRET_MS);
                    }
                }

                if (dernierEtage != etage) {
                    // transmettre l'information aux SCC en écoute (les nouvelles écoutes en premier)
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(listeners.size() - i - 1).etageAtteint();
                    }

                    dernierEtage = etage;
                }

                break;
            }
        }

        if (statut == EStatut.MARCHE) {

            switch (direction) {
                case HAUT -> {
                    if (position + pas > nbEtages - 1) {
                        arretUrgence(ECauseArretUrgence.NIVEAU_MAX);
                        break;
                    }

                    cabine.monter(pas);
                    System.out.println(this);
                }
                case BAS -> {
                    if (position - pas < 0) {
                        arretUrgence(ECauseArretUrgence.NIVEAU_MIN);
                        break;
                    }

                    cabine.descendre(pas);
                    System.out.println(this);
                }
            }
        }
    }

    /**
     * Renvoi la position de la cabine (simulation)
     *
     * @return position de la cabine
     */
    public double positionCabine() {
        return cabine.getPosition();
    }

}
