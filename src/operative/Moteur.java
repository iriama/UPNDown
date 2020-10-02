package operative;

import applicative.SCC;
import enums.ECauseArretUrgence;
import enums.EStatusMoteur;
import interfaces.IMoteur;

import java.util.TreeSet;

public class Moteur implements IMoteur, Runnable {

    private static final int DUREE_MIN_ARRET_MS  = 5000;

    private EStatusMoteur statut;
    private EStatusMoteur statut_precedent;

    private TreeSet<Double> niveaux;
    private double pas;
    private Cabine cabine;
    private boolean arretProchainNiveau;
    private double narretProchainNiveau;
    private SCC scc;


    public Moteur(SCC scc, double pas, double ... niveaux) {
        this.scc = scc;
        statut = EStatusMoteur.ARRET;
        cabine = new Cabine(0);
        this.pas = pas;
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
        this.niveaux = new TreeSet<Double>();

        for (double niveau: niveaux) {
            this.niveaux.add(niveau);
        }

    }

    private void changerStatut(EStatusMoteur nouveau) {
        if(statut == nouveau) return;

        System.out.println("[MOTEUR] changement statut : " + statut + " -> " + nouveau);
        statut_precedent = statut;
        statut = nouveau;
    }

    private double niveauMax() {
        return this.niveaux.last();
    }

    public void monter() {
        changerStatut(EStatusMoteur.HAUT);
    }

    public void descendre() {
        changerStatut(EStatusMoteur.BAS);
    }

    public void arretProchainNiveau() {
        arretProchainNiveau = true;
        narretProchainNiveau = cabine.getPosition();
        System.out.println("[MOTEUR] arrêt au prochain niveau demandé.");
    }

    public void arretUrgence(ECauseArretUrgence cause) {
        changerStatut(EStatusMoteur.ARRET_URGENCE);
        System.out.println("[MOTEUR] cause arrêt urgence : " + cause);
        arretProchainNiveau = false;
        narretProchainNiveau = -1;
    }

    private void arret() {
        changerStatut(EStatusMoteur.ARRET);
    }


    public TreeSet<Double> getNiveaux() {
        return niveaux;
    }

    public EStatusMoteur getStatut() {
        return statut;
    }

    public void setStatut(EStatusMoteur statut) {
        changerStatut(statut);
    }

    public double getNiveauActuel() {
        return cabine.getPosition();
    }

    @Override
    public String toString() {
        return "[MOTEUR] statut : " + statut + " | " + cabine;
    }

    @Override
    public void run() {
        try {
            for (; ; ) {

                double position = cabine.getPosition();


                if (arretProchainNiveau && narretProchainNiveau != position) {
                    for (double niveau : niveaux) {

                        if (Math.abs(position - niveau) < pas*0.1) {

                            System.out.println("[MOTEUR] arrêt de " + DUREE_MIN_ARRET_MS + " ms minimum.");
                            this.arret();
                            arretProchainNiveau = false;
                            narretProchainNiveau = -1;
                            Thread.sleep(DUREE_MIN_ARRET_MS);

                            //if (statut_precedent == EStatusMoteur.HAUT || statut_precedent == EStatusMoteur.BAS)
                            //changerStatut(statut_precedent);

                            break;
                        }
                    }
                }


                switch (statut) {
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


                scc.actionMoteur();

                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
