import static org.junit.jupiter.api.Assertions.assertEquals;
import applicative.SCC;
import enums.EDirectionMoteur;
import enums.EStatusMoteur;
import interfaces.IMoteurListener;
import operative.Moteur;
import org.junit.jupiter.api.Test;

import java.util.Vector;

public class SCCTests {

    @Test
    void requete() throws InterruptedException {

        // requetes etage 0 : 5+2+1 ; requetes etage 5 : 1+8+3
        // resultat attendu en minimisant les changements de direction : 1 -> 2 -> 5 -> 8 -> 3 -> 1
        Moteur moteur = TestsUtils.moteurType(0.0);
        SCC scc = new SCC(moteur);
        Vector<Double> niveauxDesservies = new Vector<Double>();

        moteur.addListener(new IMoteurListener() {
            @Override
            public void niveauAtteint(double niveauActuel) {
                if (moteur.getStatut() == EStatusMoteur.ARRET && niveauActuel > 0 && (niveauxDesservies.size() < 1 || niveauxDesservies.lastElement() != niveauActuel)) {
                    niveauxDesservies.add(niveauActuel);
                }

                if (niveauActuel == 5 && moteur.getDirection() == EDirectionMoteur.HAUT) {
                    scc.requete(1);
                    scc.requete(8);
                    scc.requete(3);
                }
            }
        });

        scc.requete(5);
        scc.requete(2);
        scc.requete(1);

        for(int i = 0; i<1000; i++) {
            moteur.etape(false);
        }

        if (niveauxDesservies.size() != 6) {
            System.out.println(niveauxDesservies);
            throw new AssertionError("l'ascenseur doit desservir exactement 6 étages.");
        }

        if (niveauxDesservies.get(0) != 1)
            throw  new AssertionError("l'ascenseur doit servir en 1er le niveau 1.");
        else if (niveauxDesservies.get(1) != 2)
            throw  new AssertionError("l'ascenseur doit servir en 2eme le niveau 2.");
        else if (niveauxDesservies.get(2) != 5)
            throw  new AssertionError("l'ascenseur doit servir en 3eme le niveau 5.");
        else if (niveauxDesservies.get(3) != 8)
            throw  new AssertionError("l'ascenseur doit servir en 4eme le niveau 8.");
        else if (niveauxDesservies.get(4) != 3)
            throw  new AssertionError("l'ascenseur doit servir en 5eme le niveau 3.");
        else if (niveauxDesservies.get(5) != 1)
            throw  new AssertionError("l'ascenseur doit servir en dernier le niveau 1.");

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt à la fin.");

        if (moteur.getNiveauActuel() != 1) {
            throw new AssertionError("l'ascenseur doit être au niveau 1 à la fin.");
        }
    }

    @Test
    void declencherArretUrgence() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(0.0);
        SCC scc = new SCC(moteur);

        scc.requete(10);

        double positionArretUrgence = 0;

        for(int i = 0; i<100; i++) {
            moteur.etape(false);

            if (i == 60) {
                scc.declencherArretUrgence();
                positionArretUrgence = moteur.getNiveauActuel();
            }
        }

        assertEquals("ARRET_URGENCE", moteur.getStatut().name(), "le moteur doit être en arrêt d'urgence.");
        assertEquals(positionArretUrgence, moteur.getNiveauActuel(), "l'ascenseur ne doit pas avoir bougé depuis la position de l'arrêt d'urgence.");
    }

    @Test
    void stopperArretUrgence() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(0.0);
        SCC scc = new SCC(moteur);

        scc.requete(10);


        for(int i = 0; i<1000; i++) {
            moteur.etape(false);

            if (i == 20) {
                scc.declencherArretUrgence();
            }
            else if (i == 21) {
                scc.stopperArretUrgence();
                scc.requete(10);
            }
        }

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être en arrêt normal.");


        if (moteur.getNiveauActuel() != 10) {
            throw new AssertionError("l'ascenseur doit être au niveau 10 à la fin.");
        }
    }

}
