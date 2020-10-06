package operative;

import enums.ECauseArretUrgence;
import enums.EDirection;
import enums.EStatut;
import interfaces.IMoteur;
import interfaces.IMoteurListener;

import java.util.Vector;

public class Moteur implements IMoteur {

    private static final int DUREE_MIN_ARRET_MS  = 5000;

    private EStatut statut;
    private EDirection direction;

    private int nbEtages;
    private double pas;
    private Cabine cabine;
    private boolean arretProchainNiveau;
    private double narretProchainNiveau;
    private Vector<IMoteurListener> listeners;


    public Moteur(double pas, int nbEtages) {
        listeners = new Vector<IMoteurListener>();
        statut = EStatut.ARRET;
        direction = EDirection.HAUT;
        cabine = new Cabine(0);
        this.pas = pas;
        arretProchainNiveau = false;
        narretProchainNiveau = -1;

        this.nbEtages = nbEtages;
    }

    private void changerDirection(EDirection direction) {
        if (this.direction == direction) return;

        System.out.println("[MOTEUR] changement direction : " + this.direction + " -> " + direction);
        this.direction = direction;
    }

    private void changerStatut(EStatut statut) {
        if (this.statut == statut) return;

        System.out.println("[MOTEUR] changement statut : " + this.statut + " -> " + statut);
        this.statut = statut;
    }

    private void arret() {
        changerStatut(EStatut.ARRET);
    }

    public void addListener(IMoteurListener listener) {
        listeners.add(listener);
    }

    public void monter() {
        changerDirection(EDirection.HAUT);
        changerStatut(EStatut.MARCHE);
    }

    public void descendre() {
        changerDirection(EDirection.BAS);
        changerStatut(EStatut.MARCHE);
    }

    public void arretProchainNiveau() {
        arretProchainNiveau = true;
        narretProchainNiveau = getNiveauActuel();
        System.out.println("[MOTEUR] arrêt au prochain niveau demandé.");
    }

    public void arretUrgence(ECauseArretUrgence cause) {

        if (statut == EStatut.ARRET_URGENCE && cause == ECauseArretUrgence.PASSAGER) {
            changerStatut(EStatut.ARRET);
            System.out.println("[MOTEUR] cause annulation arrêt urgence : " + cause);

            return;
        }

        changerStatut(EStatut.ARRET_URGENCE);
        System.out.println("[MOTEUR] cause arrêt urgence : " + cause);
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
    }


    public EDirection getDirection() {
        return direction;
    }

    public EStatut getStatut() {
        return statut;
    }

    public void setStatut(EStatut statut) {
        changerStatut(statut);
    }

    @Override
    public String toString() {
        return "[MOTEUR] statut : " + statut + " | " + cabine;
    }

    int dernierEtage = -1;
    // public pour tests
    public void etape(boolean attenteMonteeDescente) throws InterruptedException {
        double position = getNiveauActuel();


        for (int etage = 0; etage< nbEtages; etage++) {

            // on est sur un niveau
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
                        listeners.get(listeners.size() - i - 1).niveauAtteint();
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

    public double getNiveauActuel() {
        return cabine.getPosition();
    }

}
