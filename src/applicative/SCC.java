package applicative;

import enums.ECauseArretUrgence;
import enums.EDirection;
import enums.EStatusMoteur;
import interfaces.IMoteurListener;
import interfaces.ISCC;
import operative.Moteur;

import java.util.Vector;

/**
 * Le système contrôle-commande de l'ascenseur
 */
public class SCC implements ISCC, IMoteurListener {

    /**
     * Requete générique (depuis la cabine) sans indication de direction
     */
    protected class Requete {
        protected final double niveau;

        protected Requete(double niveau) {
            this.niveau = niveau;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Requete)) return false;

            return niveau == ((Requete)obj).niveau;
        }
    }

    /**
     * Requete depuis un étage avec indication de direction
     */
    protected class RequeteEtage extends Requete {
        protected final EDirection direction;

        protected RequeteEtage(double niveau, EDirection direction) {
            super(niveau);
            this.direction = direction;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RequeteEtage)) return false;

            return niveau == ((RequeteEtage)obj).niveau && direction == ((RequeteEtage)obj).direction;
        }
    }

    protected class FileRequetes extends Vector<Requete> {
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Requete)) return false;

            if (o instanceof RequeteEtage) {

                RequeteEtage obj = (RequeteEtage)o;

                for (Requete requete: this) {
                    if (requete instanceof RequeteEtage && requete.niveau == obj.niveau && ((RequeteEtage)(requete)).direction == obj.direction) {
                        return true;
                    }
                }
                
                return false;
            }
            
            // Requete normale
            for (Requete requete: this) {
                if (requete.niveau == ((Requete)o).niveau) {
                    return true;
                }
            }
            return false;
        }
    }

    private Moteur moteur;
    private FileRequetes fileRequetes;
    private boolean etaitEnArretUrgence = false;;


    public SCC(Moteur moteur) {
        this.moteur = moteur;
        System.out.println("[SCC] initialisation...");
        moteur.addListener(this);
        fileRequetes = new FileRequetes();
    }

    /**
     * Relance le SCC suite à une annulation d'arrêt d'urgence
     */
    private void relance() {
        if (etaitEnArretUrgence && moteur.getStatut() == EStatusMoteur.ARRET) {
            etaitEnArretUrgence = false;
            niveauAtteint(moteur.getNiveauActuel());
        }
    }

    /**
     * Requête depuis un étage
     * @param niveauActuel l'étage depuis lequel la requête est demandé
     * @param direction l'indication de la direction de l'étage qu'on compte y aller
     */
    public void requeteEtage(double niveauActuel, EDirection direction) {

        if (moteur.getStatut() == EStatusMoteur.ARRET_URGENCE)
            return;

        if (!moteur.getNiveaux().contains(niveauActuel)) {
            System.err.println("[SCC] erreur non-fatale, niveau demandé " + niveauActuel + " n'existe pas. ignoré.");
            return;
        }

        RequeteEtage requete = new RequeteEtage(niveauActuel, direction);

        if (fileRequetes.contains(requete)) {
            System.out.println("[SCC] requeteEtage niveau " + niveauActuel + " (" + direction + ") déjà en file. ignoré.");
            return;
        }

        System.out.println("[SCC] requeteEtage pour le niveau " + niveauActuel + " (" + direction + ").");
        fileRequetes.add(requete);

        // reprendre si après un arrêt d'urgence
        relance();
    }

    /**
     * Requete simple depuis la cabine
     * @param niveauDestination l'étage où on veut y aller
     */
    public void requeteCabine(double niveauDestination) {

         if (moteur.getStatut() == EStatusMoteur.ARRET_URGENCE)
             return;

         Requete requete = new Requete(niveauDestination);

         if (!moteur.getNiveaux().contains(niveauDestination)) {
             System.err.println("[SCC] erreur non-fatale, niveau demandé " + niveauDestination + " n'existe pas. ignoré.");
             return;
         }

         if (fileRequetes.contains(requete)) {
             System.out.println("[SCC] requeteCabine niveau " + niveauDestination + " déjà en file. ignoré.");
             return;
         }


        System.out.println("[SCC] requeteCabine pour le niveau " + niveauDestination);
        fileRequetes.add(requete);

        // reprendre si après un arrêt d'urgence
        relance();
    }

    /**
     * Fonction à être appélé par les capteurs d'atteinte d'un niveau, execution de la logique du SCC et la prise de décision (selection direciton/niveau/arrêt prochain niveau etc..)
     * @param niveauActuel
     */
    public void niveauAtteint(double niveauActuel) {

        if (fileRequetes.size() < 1) return;

        double niveauChoisi = fileRequetes.get(0).niveau;

        // --- Moteur à l'arret, selectionner le prochain niveau à atteindre de sorte à minimiser le changement de direction
        if (moteur.getStatut() == EStatusMoteur.ARRET) {

            // choisir le premier de la file dans la même direction sinon le premier de la file

            for (Requete requete: fileRequetes) {
                // on ignore les requetes qui veulent changer de direction
                if (requete instanceof RequeteEtage && ((RequeteEtage)requete).direction != moteur.getDirection())
                    continue;

                if (moteur.getDirection() == EDirection.HAUT && requete.niveau > niveauActuel) {
                    niveauChoisi = requete.niveau;
                    break;
                }
                else if (moteur.getDirection() == EDirection.BAS && requete.niveau < niveauActuel) {
                    niveauChoisi = requete.niveau;
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

        EDirection direction =  moteur.getDirection();

        double niveauProchain = 0;

        if (direction == EDirection.HAUT) {

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

        for (Requete requete: fileRequetes) {
            // on ignore les requetes qui veulent changer de direction qui ne sont pas selectionnées
            if (requete.niveau != niveauChoisi && requete instanceof RequeteEtage && ((RequeteEtage)requete).direction != moteur.getDirection())
                continue;

            if (requete.niveau == niveauProchain) {
                moteur.arretProchainNiveau();
                fileRequetes.remove(requete);
                break;
            }
        }

    }

    /**
     * Déclenche un arrêt d'urgence
     */
    public void declencherArretUrgence() {
        System.out.println("[SCC] demande d'arrêt d'urgence.");
         // détruire les autres reqûetes
         fileRequetes.clear();
         moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
         etaitEnArretUrgence = true;
    }

    /**
     * Annule un arrêt d'urgence
     */
    public void stopperArretUrgence() {
        System.out.println("[SCC] annulaiton de la demande d'arrêt d'urgence.");
        moteur.setStatut(EStatusMoteur.ARRET);
    }
}
