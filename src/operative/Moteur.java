package operative;

import enums.ECauseArretUrgence;
import enums.EDirectionMoteur;
import enums.EStatusMoteur;
import interfaces.IMoteur;
import interfaces.IMoteurListener;

import java.util.TreeSet;
import java.util.Vector;

public class Moteur implements IMoteur {

    private static final int DUREE_MIN_ARRET_MS  = 5000;

    private EStatusMoteur statut;
    private EDirectionMoteur direction;

    private TreeSet<Double> niveaux;
    private double pas;
    private Cabine cabine;
    private boolean arretProchainNiveau;
    private double narretProchainNiveau;
    private Vector<IMoteurListener> listeners;


    public Moteur(double positionInitiale, double pas, double ... niveaux) {
        listeners = new Vector<IMoteurListener>();
        statut = EStatusMoteur.ARRET;
        direction = EDirectionMoteur.HAUT;
        cabine = new Cabine(positionInitiale);
        this.pas = pas;
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
        this.niveaux = new TreeSet<Double>();

        for (double niveau: niveaux) {
            this.niveaux.add(niveau);
        }
    }

    private void changerDirection(EDirectionMoteur direction) {
        if (this.direction == direction) return;

        System.out.println("[MOTEUR] changement direction : " + this.direction + " -> " + direction);
        this.direction = direction;
    }

    private void changerStatut(EStatusMoteur statut) {
        if (this.statut == statut) return;

        System.out.println("[MOTEUR] changement statut : " + this.statut + " -> " + statut);
        this.statut = statut;
    }

    private double niveauMax() {
        return this.niveaux.last();
    }

    private void arret() {
        changerStatut(EStatusMoteur.ARRET);
    }

    public void addListener(IMoteurListener listener) {
        listeners.add(listener);
    }

    public void monter() {
        changerDirection(EDirectionMoteur.HAUT);
        changerStatut(EStatusMoteur.MARCHE);
    }

    public void descendre() {
        changerDirection(EDirectionMoteur.BAS);
        changerStatut(EStatusMoteur.MARCHE);
    }

    public void arretProchainNiveau() {
        arretProchainNiveau = true;
        narretProchainNiveau = getNiveauActuel();
        System.out.println("[MOTEUR] arrêt au prochain niveau demandé.");
    }

    public void arretUrgence(ECauseArretUrgence cause) {
        changerStatut(EStatusMoteur.ARRET_URGENCE);
        System.out.println("[MOTEUR] cause arrêt urgence : " + cause);
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
    }

    public TreeSet<Double> getNiveaux() {
        return niveaux;
    }

    public EDirectionMoteur getDirection() {
        return direction;
    }

    public EStatusMoteur getStatut() {
        return statut;
    }

    public void setStatut(EStatusMoteur statut) {
        changerStatut(statut);
    }

    @Override
    public String toString() {
        return "[MOTEUR] statut : " + statut + " | " + cabine;
    }

    // public pour tests
    public void etape(boolean attenteMonteeDescente) throws InterruptedException {
        double position = getNiveauActuel();

        for (double niveau : niveaux) {

            // on est sur un niveau
            if (position == niveau) {

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

                // transmettre l'information aux SCC en écoute (les nouvelles écoutes en premier)
                for (int i = 0; i < listeners.size(); i++) {
                    listeners.get( listeners.size() - i - 1 ).niveauAtteint(niveau);
                }

                break;
            }
        }

        if (statut == EStatusMoteur.MARCHE) {

            switch (direction) {
                case HAUT -> {
                    double niveauMax = niveauMax();


                    if (position + pas > niveauMax) {
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
