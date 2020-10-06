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
    public boolean arretUrgence;
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

    public Vector<Requete> fileRequetes() {
        return this.fileRequetes;
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
        Vector<Requete> satisfaites = new Vector<Requete>();
        if (arret) {
            for (int i = 0; i < fileRequetes.size(); i++) {
                Requete requete = fileRequetes.get(i);
                if (requete.etage == etage) {
                    // satisfaite
                    System.out.println("[SCC] " + requete + " satisfaite");
                    satisfaites.add(requete);
                }
            }

            for (int i=0; i<satisfaites.size(); i++) {
                fileRequetes.remove(satisfaites.get(i));
            }

            System.out.println("[SCC] requetes restantes : " + fileRequetes);

            // reprendre
            if (fileRequetes.size() > 0)
                actionner();

            arret = false;
        }


        boolean dansMonSens = false;
        for (Requete requete: fileRequetes) {
            // plus haut que nous
            if ((requete.etage < etage && direction == EDirection.BAS) || (requete.etage > etage && direction == EDirection.HAUT)) {
                // requete sans sens ou direction pareil que nous
                if (!requete.sens || requete.direction == direction) {
                    dansMonSens = true;
                    break;
                }
            }
        }

        // décider si on doit s'arrêter au niveau (étage) suivant
        int suivant = direction == EDirection.HAUT ? etage + 1 : etage - 1;

        for (int i = 0 ; i<fileRequetes.size(); i++) {
            Requete requete = fileRequetes.get(i);
            // ignorer les requetes qui ne sont pas dans le même sens
            if (dansMonSens && requete.sens && requete.direction != direction) continue;

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

        Requete choix = fileRequetes.get(0);

        for (Requete requete: fileRequetes) {
            if (requete.equals(choix)) continue;

            // en premier les requetes dans le meme sens
            if (requete.etage > etage && direction == EDirection.HAUT)
                choix = requete;
            else if (requete.etage < etage && direction == EDirection.BAS)
                choix = requete;
        }


        int suivant = etage;

        if (choix.etage < etage) {
            System.out.println("[SCC] descendre()");
            moteur.descendre();
            direction = EDirection.BAS;
            suivant = etage-1;

        } else if (choix.etage > etage) {
            System.out.println("[SCC] monter()");
            moteur.monter();
            direction = EDirection.HAUT;
            suivant = etage+1;
        } else { // on est déjà arrêté ici
            fileRequetes.remove(choix);
            return;
        }

        if (!arret && choix.etage == suivant) {
            System.out.println("[SCC] arrêt prochain niveau.");
            moteur.arretProchainNiveau();
            arret = true;
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
        arret = false;
    }

    /**
     * Annule un arrêt d'urgence en cours
     */
    @Override
    public void stopperArretUrgence() {
        if (!arretUrgence) return;

        System.out.println("[SCC] arrêt urgence annulé.");

        fileRequetes.clear();
        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        arretUrgence = false;
        arret = false;

    }

}
