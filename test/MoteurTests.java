import static org.junit.jupiter.api.Assertions.assertEquals;

import enums.ECauseArretUrgence;
import operative.Moteur;
import org.junit.jupiter.api.Test;


public class MoteurTests {

    @Test
    void initialisation() {
        Moteur moteur = TestsUtils.moteurType();

        assertEquals(0, moteur.positionCabine(), "le moteur doit être au niveau 0 par défaut.");
        assertEquals("ARRET", moteur.statut().name(), "le moteur doit être à l'arrêt par défaut.");
        assertEquals("HAUT", moteur.direction().name(), "le moteur doit avoir pour direction 'HAUT' par défaut.");
    }

    @Test
    void monter() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();

        moteur.monter();

        assertEquals("HAUT", moteur.direction().name(), "le moteur doit avoir pour direction 'HAUT'.");
        assertEquals("MARCHE", moteur.statut().name(), "le moteur doit avoir pour statut 'MARCHE'.");

        moteur.etape(false);

        assertEquals("HAUT", moteur.direction().name(), "le moteur doit toujours avoir pour direction 'HAUT'.");
        assertEquals("MARCHE", moteur.statut().name(), "le moteur doit toujours avoir pour statut 'MARCHE'.");

        if (moteur.positionCabine() <= 0)
            throw new AssertionError("la cabine doit s'être déplacée vers le haut.");
    }

    @Test
    void descendre() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();

        moteur.monter();
        moteur.etape(false);

        double positionAvantDescente = moteur.positionCabine();

        moteur.descendre();

        assertEquals("BAS", moteur.direction().name(), "le moteur doit avoir pour direction 'BAS'.");
        assertEquals("MARCHE", moteur.statut().name(), "le moteur doit avoir pour statut 'MARCHE'.");


        moteur.etape(false);

        assertEquals("BAS", moteur.direction().name(), "le moteur doit toujours avoir pour direction 'BAS'.");
        assertEquals("MARCHE", moteur.statut().name(), "le moteur doit toujours avoir pour statut 'MARCHE'.");


        if (moteur.positionCabine() >= positionAvantDescente)
            throw new AssertionError("la cabine doit s'être déplacée vers le bas.");
    }

    @Test
    void arretUrgence() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();

        moteur.monter();
        moteur.etape(false);

        double niveau = moteur.positionCabine();
        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        assertEquals("ARRET_URGENCE", moteur.statut().name(), "le moteur doit être en arrêt d'urgence.");

        moteur.etape(false);
        assertEquals("ARRET_URGENCE", moteur.statut().name(), "le moteur doit toujours être en arrêt d'urgence.");
        assertEquals(niveau, moteur.positionCabine(), "la cabine ne doit pas s'être deplacée.");
    }

    @Test
    void arretProchainNiveau() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();

        moteur.monter();
        moteur.arretProchainNiveau();

        for (int i = 0 ; i<=10; i++) {
            moteur.etape(false);
        }

        assertEquals("ARRET", moteur.statut().name(), "le moteur doit être à l'arrêt.");
    }

    @Test
    void arretUrgenceAutomatiqueHaut() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(1);

        moteur.monter();

        moteur.etape(false);

        assertEquals("ARRET_URGENCE", moteur.statut().name(), "le moteur doit être en arrêt urgence.");
    }

    @Test
    void arretUrgenceAutomatiqueBas() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType();

        moteur.descendre();
        moteur.etape(false);

        assertEquals("ARRET_URGENCE", moteur.statut().name(), "le moteur doit être en arrêt urgence.");
    }
}
