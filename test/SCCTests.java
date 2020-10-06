import static org.junit.jupiter.api.Assertions.assertEquals;
import applicative.SCC;
import enums.EDirection;
import enums.EStatut;
import interfaces.IMoteurListener;
import operative.Moteur;
import org.junit.jupiter.api.Test;

import java.util.Vector;

public class SCCTests {

    @Test
    void requeteCabine() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();
        SCC scc = new SCC(moteur, TestsUtils.nbEtages);

        scc.requeteCabine(5);

        for (int i = 0; i<100; i++)
            moteur.etape(false);

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt.");
        assertEquals("HAUT", moteur.getDirection().name(), "le moteur doit avoir pour direction 'HAUT'.");
        assertEquals(5, moteur.getNiveauActuel(), "le moteur doit être au niveau 5.");

        scc.requeteCabine(2);

        for (int i = 0; i<100; i++)
            moteur.etape(false);

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt.");
        assertEquals("BAS", moteur.getDirection().name(), "le moteur doit avoir pour direction 'BAS'.");
        assertEquals(2, moteur.getNiveauActuel(), "le moteur doit être au niveau 2.");
    }

    @Test
    void requeteEtage() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();
        SCC scc = new SCC(moteur, TestsUtils.nbEtages);

        scc.requeteEtage(5, EDirection.HAUT);

        for (int i = 0; i<100; i++)
            moteur.etape(false);

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt.");
        assertEquals("HAUT", moteur.getDirection().name(), "le moteur doit avoir pour direction 'HAUT'.");
        assertEquals(5, moteur.getNiveauActuel(), "le moteur doit être au niveau 5.");

        scc.requeteEtage(2, EDirection.HAUT);

        for (int i = 0; i<100; i++)
            moteur.etape(false);

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt.");
        assertEquals("BAS", moteur.getDirection().name(), "le moteur doit avoir pour direction 'BAS'.");
        assertEquals(2, moteur.getNiveauActuel(), "le moteur doit être au niveau 2.");
    }

    @Test
    void prioriteRequetes() throws InterruptedException {

        // position initialie : 0 ; [position cabine] ; ^ en montée ; v en descente
        // [0] 5 (haut) + 6 (bas) ; [5] 1 + 10 ; [6] 7
        // résultat attendu : ^ 5 ->  ^ 10 -> 6 v -> 1 v -> 7 ^

        Moteur moteur = TestsUtils.moteurType();
        SCC scc = new SCC(moteur, TestsUtils.nbEtages);
        Vector<Integer> niveauxDesservies = new Vector<Integer>();

        moteur.addListener(new IMoteurListener() {
            int sequence = 0;

            @Override
            public void niveauAtteint() {
                if (moteur.getStatut() != EStatut.ARRET) return;

                int niveauActuel = (int)moteur.getNiveauActuel();

                //System.out.println(niveauActuel);

                System.out.println("[TEST] niveau : " + niveauActuel);

                if (niveauActuel == 5 && sequence == 0) {
                    scc.requeteCabine(1);
                    scc.requeteCabine(10);
                    sequence++;
                }

                else if (niveauActuel == 6 && sequence == 1) {
                    scc.requeteCabine(7);
                    sequence++;
                }

                niveauxDesservies.add(niveauActuel);

            }
        });

        scc.requeteEtage(5, EDirection.HAUT);
        scc.requeteEtage(6, EDirection.BAS);

        for(int i = 0; i<1000; i++) {
            moteur.etape(false);
        }

        if (niveauxDesservies.size() != 5) {
            System.out.println(niveauxDesservies);
            throw new AssertionError("l'ascenseur doit desservir exactement 5 étages.");
        }

        // résultat attendu : ^ 5 ->  ^ 10 -> 6 v -> 1 v -> 7 ^

        if (niveauxDesservies.get(0) != 5)
            throw  new AssertionError("l'ascenseur doit servir en 1er le niveau 5.");
        else if (niveauxDesservies.get(1) != 10)
            throw  new AssertionError("l'ascenseur doit servir en 2eme le niveau 10.");
        else if (niveauxDesservies.get(2) != 6)
            throw  new AssertionError("l'ascenseur doit servir en 3eme le niveau 6.");
        else if (niveauxDesservies.get(3) != 1)
            throw  new AssertionError("l'ascenseur doit servir en 4eme le niveau 1.");
        else if (niveauxDesservies.get(4) != 7)
            throw  new AssertionError("l'ascenseur doit servir en dernier le niveau 7.");

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt à la fin.");

        if (moteur.getNiveauActuel() != 7) {
            throw new AssertionError("l'ascenseur doit être au niveau 7 à la fin.");
        }
    }


    @Test
    void declencherArretUrgence() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();
        SCC scc = new SCC(moteur, TestsUtils.nbEtages);

        scc.requeteCabine(10);

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
        Moteur moteur = TestsUtils.moteurType();
        SCC scc = new SCC(moteur, TestsUtils.nbEtages);

        scc.requeteCabine(10);


        for(int i = 0; i<1000; i++) {
            moteur.etape(false);

            if (i == 20) {
                scc.declencherArretUrgence();
            }
            else if (i == 21) {
                scc.stopperArretUrgence();
                scc.requeteCabine(10);
            }
        }

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être en arrêt normal.");


        if (moteur.getNiveauActuel() != 10) {
            throw new AssertionError("l'ascenseur doit être au niveau 10 à la fin.");
        }
    }

}
