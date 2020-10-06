package applicative;

import enums.ECauseArretUrgence;
import enums.EDirection;
import interfaces.IEcouteurEtageAtteint;
import interfaces.IMoteur;
import interfaces.ISCC;

import java.util.Vector;

/**
 * Système contrôle commande
 */
public class SCC implements ISCC, IEcouteurEtageAtteint {

    private final IMoteur moteur;
    private final int nbEtages;
    private final Vector<Requete> fileRequetes;
    private EDirection direction;
    private int etage;
    private boolean arret;
    private boolean arretUrgence;
    public SCC(IMoteur moteur, int nbEtages) {
        this.moteur = moteur;
        this.nbEtages = nbEtages;
        fileRequetes = new Vector<Requete>();
        direction = EDirection.HAUT;
        etage = -1;
        arret = false;
        arretUrgence = false;


        moteur.ajouterEcouteurEtageAtteint(this);
    }

    /**
     * Fonction à executer à chaque passage de niveau
     */
    @Override
    public void etageAtteint() {

        if (direction == EDirection.HAUT)
            etage++;
        else
            etage--;

        System.out.println("[SCC] étage " + etage);

        boolean requeteMemeSens = false;

        // si on s'est arrêté, marquer les requetes comme satisfaites
        if (arret) {
            for (int i = 0; i < fileRequetes.size(); i++) {
                Requete requete = fileRequetes.get(i);
                if (requete.etage == etage) {
                    // satisfaite
                    System.out.println("[SCC] " + requete + " satisfaite");
                    fileRequetes.remove(requete);
                } else if (!requeteMemeSens && ((requete.sens && requete.direction == direction) || (requete.etage < etage && direction == EDirection.BAS) || (requete.etage > etage && direction == EDirection.HAUT))) {
                    requeteMemeSens = true;
                }
            }

            System.out.println("[SCC] requetes restantes : " + fileRequetes);

            // reprendre
            if (fileRequetes.size() > 0)
                actionner();

            arret = false;
        }

        // décider si on doit s'arrêter au niveau (étage) suivant
        int suivant = direction == EDirection.HAUT ? etage + 1 : etage - 1;

        for (Requete requete : fileRequetes) {
            // ignorer les requetes qui ne sont pas dans le même sens
            if (requeteMemeSens && requete.sens && requete.direction != direction) continue;

            if (requete.etage == suivant) {
                System.out.println("[SCC] arrêt prochain niveau (étage) demandé pour " + requete);
                moteur.arretProchainNiveau();
                arret = true;
                break;
            }
        }

    }

    /**
     * Actionne le moteur et le met en movement
     */
    private void actionner() {

        if (arretUrgence) return;

        Requete requete = fileRequetes.get(0);

        if (requete.etage < etage) {
            System.out.println("[SCC] descendre()");
            moteur.descendre();
            direction = EDirection.BAS;
        } else if (requete.etage > etage) {
            System.out.println("[SCC] monter()");
            moteur.monter();
            direction = EDirection.HAUT;
        }
    }

    /**
     * Requête d'un étage depuis la cabine
     *
     * @param etage étage demandé
     */
    @Override
    public void requeteCabine(int etage) {

        if (arretUrgence) return;

        if (etage < 0 || etage > nbEtages) {
            System.out.println("[SCC] étage invalide, ignoré.");
            return;
        }


        Requete requete = new Requete(etage);

        System.out.println("[SCC] requeteCabine " + requete);

        if (fileRequetes.contains(requete)) {
            System.out.println("[SCC] requete dupliquée, ignorée.");
            return;
        }

        fileRequetes.add(requete);

        if (fileRequetes.size() == 1)
            actionner();
    }

    /**
     * Requete d'un étage depuis l'étage lui-même
     *
     * @param etageSource l'étage
     * @param direction   direction du déplacement
     */
    @Override
    public void requeteEtage(int etageSource, EDirection direction) {

        if (arretUrgence) return;

        if (etageSource < 0 || etageSource > nbEtages) {
            System.out.println("[SCC] étage invalide, ignoré.");
            return;
        }

        Requete requete = new Requete(etageSource, direction);
        System.out.println("[SCC] requeteEtage " + requete);

        if (fileRequetes.contains(requete)) {
            System.out.println("[SCC] requete dupliquée, ignorée.");
            return;
        }

        fileRequetes.add(requete);

        if (fileRequetes.size() == 1)
            actionner();
    }

    /**
     * Déclenche un arrêt immédiat d'urgence
     */
    @Override
    public void declencherArretUrgence() {
        if (arretUrgence) return;

        System.out.println("[SCC] arrêt urgence declenché.");

        fileRequetes.clear();
        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        arretUrgence = true;
    }

    /**
     * Annule un arrêt d'urgence en cours
     */
    @Override
    public void stopperArretUrgence() {
        if (!arretUrgence) return;

        System.out.println("[SCC] arrêt urgence annulé.");

        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        arretUrgence = false;
    }

    /**
     * Requête d'un passager
     */
    protected class Requete {
        int etage;
        boolean sens;
        EDirection direction;

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
}
