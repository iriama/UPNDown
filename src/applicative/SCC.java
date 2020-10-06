package applicative;

import enums.ECauseArretUrgence;
import enums.EDirection;
import enums.EStatut;
import interfaces.IMoteur;
import interfaces.IMoteurListener;
import interfaces.ISCC;
import operative.Moteur;

import java.util.HashSet;
import java.util.Vector;


public class SCC implements ISCC, IMoteurListener {

    protected class Requete {
        int niveau;
        boolean sens;
        EDirection direction;

        public Requete(int niveau) {
            this.niveau = niveau;
            sens = false;
        }

        public Requete(int niveau, EDirection direction) {
            this.niveau = niveau;
            sens = true;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Requete)) return false;

            Requete requete = (Requete)obj;

            return sens ? requete.sens && niveau == requete.niveau && direction == requete.direction : !requete.sens && requete.niveau == niveau;
        }

        @Override
        public String toString() {
            return "requete@" + niveau + (sens ? ":" + direction : "");
        }
    }


    public static void main(String[] args) throws InterruptedException {

        Moteur moteur = new Moteur(0.1, 10);
        SCC scc = new SCC(moteur, 10);

        scc.requeteCabine(5);
        scc.requeteCabine(3);
        scc.requeteCabine(8);

        for (int i = 0; i < 200; i++)
            moteur.etape(false);

    }

    private IMoteur moteur;
    private int niveauActuel = 0;
    private int nbNiveaux;
    private Vector<Requete> fileRequetes;
    private EDirection direction;
    private int niveau;
    private boolean arret;
    private boolean arretUrgence;


    public SCC(IMoteur moteur, int nbNiveaux) {
        this.moteur = moteur;
        this.nbNiveaux = nbNiveaux;
        fileRequetes = new Vector<Requete>();
        direction = EDirection.HAUT;
        niveau = -1;
        arret = false;
        arretUrgence = false;

        moteur.addListener(this);
    }

    @Override
    public void niveauAtteint() {

        if (direction == EDirection.HAUT)
            niveau++;
        else
            niveau--;

        System.out.println("[SCC] niveau " + niveau);

        boolean requeteMemeSens = false;

        // si on s'est arrêté, marquer les requetes comme satisfaites
        if (arret) {
            for (int i = 0; i < fileRequetes.size(); i++) {
                Requete requete = fileRequetes.get(i);
                if (requete.niveau == niveau) {
                    // satisfaite
                    System.out.println("[SCC] " + requete + " satisfaite");
                    fileRequetes.remove(requete);
                }
                else if (!requeteMemeSens && ((requete.sens && requete.direction == direction) || (requete.niveau < niveau && direction == EDirection.BAS) || (requete.niveau > niveau && direction == EDirection.HAUT))) {
                    requeteMemeSens = true;
                }
            }

            System.out.println("[SCC] requetes restantes : " + fileRequetes);

            // reprendre
            if (fileRequetes.size() > 0)
                actionner();

            arret = false;
        }

        // décider si on doit s'arrêter au niveau suivant
        int suivant = direction == EDirection.HAUT ? niveau + 1 : niveau - 1;

        for (Requete requete : fileRequetes) {
            // ignorer les requetes qui ne sont pas dans le même sens
            if (requeteMemeSens && requete.sens && requete.direction != direction) continue;

            if (requete.niveau == suivant) {
                System.out.println("[SCC] arrêt prochain niveau demandé pour " + requete);
                moteur.arretProchainNiveau();
                arret = true;
                break;
            }
        }

    }

    private void actionner() {

        if (arretUrgence) return;

        Requete requete = fileRequetes.get(0);

        if (requete.niveau < niveau) {
            System.out.println("[SCC] descendre()");
            moteur.descendre();
            direction = EDirection.BAS;
        }
        else if (requete.niveau > niveau) {
            System.out.println("[SCC] monter()");
            moteur.monter();
            direction = EDirection.HAUT;
        }
    }

    @Override
    public void requeteCabine(int niveauDestination) {

        if (arretUrgence) return;

        if (niveauDestination < 0 || niveauDestination > nbNiveaux) {
            System.out.println("[SCC] niveau invalide, ignoré.");
            return;
        }


        Requete requete = new Requete(niveauDestination);

        System.out.println("[SCC] requeteCabine " + requete);

        if (fileRequetes.contains(requete)) {
            System.out.println("[SCC] requete dupliquée, ignorée.");
            return;
        }

        fileRequetes.add(requete);

        if (fileRequetes.size() == 1)
            actionner();
    }

    @Override
    public void requeteEtage(int niveauSource, EDirection direction) {

        if (arretUrgence) return;

        if (niveauSource < 0 || niveauSource > nbNiveaux) {
            System.out.println("[SCC] niveau invalide, ignoré.");
            return;
        }

        Requete requete = new Requete(niveauSource, direction);
        System.out.println("[SCC] requeteEtage " + requete);

        if (fileRequetes.contains(requete)) {
            System.out.println("[SCC] requete dupliquée, ignorée.");
            return;
        }

        fileRequetes.add(requete);

        if (fileRequetes.size() == 1)
            actionner();
    }

    @Override
    public void declencherArretUrgence() {
        if (arretUrgence) return;

        System.out.println("[SCC] arrêt urgence declenché.");

        fileRequetes.clear();
        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        arretUrgence = true;
    }

    @Override
    public void stopperArretUrgence() {
        if (!arretUrgence) return;

        System.out.println("[SCC] arrêt urgence annulé.");

        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        arretUrgence = false;
    }
}
