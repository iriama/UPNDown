package applicative;

import enums.ECauseArretUrgence;
import enums.EStatusMoteur;
import interfaces.ISCC;
import operative.Moteur;

import java.util.Vector;

public class SCC implements ISCC {

    private Moteur moteur;
    private Thread t_moteur;
    private Vector<Double> file;


     public SCC() {
        System.out.println("[SCC] initialisation...");
        moteur = new Moteur(this,0.1, 0, 1, 2, 3, 4, 5, 9, 10, 15, 20);
        t_moteur = new Thread(moteur);
        file = new Vector<Double>();
    }

    public void lancer()  {
         try {
             t_moteur.start();
             t_moteur.join();
         } catch (InterruptedException e) {
             System.err.println("[SCC] erreur fatale : le lancement a échoué.");
             System.exit(1);
         }
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
    }

    public void actionMoteur() {

        double niveauActuel = moteur.getNiveauActuel();

         // -- Moteur à l'arrêt
        if (moteur.getStatut() == EStatusMoteur.ARRET && file.size() > 0) {

            double premierFile = file.get(0);

            if (niveauActuel < premierFile) {
                moteur.monter();
            } else if (niveauActuel > premierFile){
                moteur.descendre();
            }

            return;
        }

        // --- Moteur déjà en marche
        EStatusMoteur direction =  moteur.getStatut() == EStatusMoteur.HAUT || moteur.getStatut() == EStatusMoteur.BAS ? moteur.getStatut() : null;

        if (direction == null) return;

        double niveauProchain = 0;

        if (direction == EStatusMoteur.HAUT) {

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
    }

    public void stopperArretUrgence() {
        System.out.println("[SCC] annulaiton de la demande d'arrêt d'urgence.");
        moteur.setStatut(EStatusMoteur.ARRET);
    }

    public static void main(String[] args) throws InterruptedException {
        SCC scc = new SCC();

        scc.lancer();
    }


}
