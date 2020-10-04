package applicative;

import enums.ECauseArretUrgence;
import enums.EDirectionMoteur;
import enums.EStatusMoteur;
import interfaces.IMoteurListener;
import interfaces.ISCC;
import operative.Moteur;

import java.util.Vector;

public class SCC implements ISCC, IMoteurListener {

    private Moteur moteur;
    private Vector<Double> file;
    private boolean etaitEnArretUrgence = false;

    public SCC(Moteur moteur) {
        this.moteur = moteur;
        System.out.println("[SCC] initialisation...");
        moteur.addListener(this);
        file = new Vector<Double>();
    }

    public void requete(double niveau) {

         if (moteur.getStatut() == EStatusMoteur.ARRET_URGENCE)
             return;

         if (!moteur.getNiveaux().contains(niveau)) {
             System.err.println("[SCC] erreur non-fatale, niveau demandé " + niveau + " n'existe pas. ignoré.");
             return;
         }

         if (file.contains(niveau)) {
             System.out.println("[SCC] niveau " + niveau + " déjà en file. ignoré.");
             return;
         }


        System.out.println("[SCC] requête pour le niveau " + niveau);
        file.add(niveau);

        // reprendre après un arrêt d'urgence
        if (etaitEnArretUrgence && moteur.getStatut() == EStatusMoteur.ARRET) {
            niveauAtteint(moteur.getNiveauActuel());
            etaitEnArretUrgence = false;
        }
    }

    public void niveauAtteint(double niveauActuel) {

        // --- Moteur à l'arret, selectionner le prochain niveau à atteindre de sorte à minimiser le changement de direction
        if (moteur.getStatut() == EStatusMoteur.ARRET && file.size() > 0) {

            // choisir le premier de la file dans la même direction sinon le premier de la file
            double niveauChoisi = file.get(0);

            for (double niveau: file) {
                if (moteur.getDirection() == EDirectionMoteur.HAUT && niveau > niveauActuel) {
                    niveauChoisi = niveau;
                    break;
                }
                else if (moteur.getDirection() == EDirectionMoteur.BAS && niveau < niveauActuel) {
                    niveauChoisi = niveau;
                    break;
                }
            }

            if (niveauActuel < niveauChoisi) {
                moteur.monter();
            } else if (niveauActuel > niveauChoisi){
                moteur.descendre();
            }
        }

        // --- Moteur déjà en marche, verifier si on doit s'arrêter au prochain niveau
        if (moteur.getStatut() != EStatusMoteur.MARCHE) return;

        EDirectionMoteur direction =  moteur.getDirection();

        double niveauProchain = 0;

        if (direction == EDirectionMoteur.HAUT) {

            for (double niveau: moteur.getNiveaux()) {
                if (niveau > niveauActuel) {
                    niveauProchain = niveau;
                    break;
                }
            }

        } else { // BAS
            for (double niveau: moteur.getNiveaux()) {
                if (niveau < niveauActuel) {
                    niveauProchain = niveau;
                }
                if (niveau > niveauActuel) {
                    break;
                }
            }
        }

        if (file.contains(niveauProchain)) {
            moteur.arretProchainNiveau();
            file.remove(niveauProchain);
        }

    }

    public void declencherArretUrgence() {
        System.out.println("[SCC] demande d'arrêt d'urgence.");
         // détruire les autres reqûetes
         file.clear();
         moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
         etaitEnArretUrgence = true;
    }

    public void stopperArretUrgence() {
        System.out.println("[SCC] annulaiton de la demande d'arrêt d'urgence.");
        moteur.setStatut(EStatusMoteur.ARRET);
    }

    public static void main(String[] args) throws InterruptedException {
         /*
        SCC scc = new SCC();

        scc.requete(5);
        scc.requete(2);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        scc.requete(1);
                        scc.requete(8);
                    }
                },
                10000
        );

        scc.lancer();
        */
    }
}
