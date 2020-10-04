import static org.junit.jupiter.api.Assertions.assertEquals;

import enums.ECauseArretUrgence;
import operative.Moteur;
import org.junit.jupiter.api.Test;


public class MoteurTests {

    @Test
    void initialisation() {
        Moteur moteur = TestsUtils.moteurType(0);

        assertEquals(0, moteur.getNiveauActuel(), "le moteur doit être au niveau 0 par défaut.");
        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt par défaut.");
        assertEquals("HAUT", moteur.getDirection().name(), "le moteur doit avoir pour direction 'HAUT' par défaut.");
    }

    @Test
    void monter() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(0);

        moteur.monter();

        assertEquals("HAUT", moteur.getDirection().name(), "le moteur doit avoir pour direction 'HAUT'.");
        assertEquals("MARCHE", moteur.getStatut().name(), "le moteur doit avoir pour statut 'MARCHE'.");

        moteur.etape(false);

        assertEquals("HAUT", moteur.getDirection().name(), "le moteur doit toujours avoir pour direction 'HAUT'.");
        assertEquals("MARCHE", moteur.getStatut().name(), "le moteur doit toujours avoir pour statut 'MARCHE'.");

        if (moteur.getNiveauActuel() <= 0)
            throw new AssertionError("la cabine doit s'être déplacée vers le haut.");
    }

    @Test
    void descendre() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(1);

        moteur.descendre();

        assertEquals("BAS", moteur.getDirection().name(), "le moteur doit avoir pour direction 'BAS'.");
        assertEquals("MARCHE", moteur.getStatut().name(), "le moteur doit avoir pour statut 'MARCHE'.");


        moteur.etape(false);

        assertEquals("BAS", moteur.getDirection().name(), "le moteur doit toujours avoir pour direction 'BAS'.");
        assertEquals("MARCHE", moteur.getStatut().name(), "le moteur doit toujours avoir pour statut 'MARCHE'.");


        if (moteur.getNiveauActuel() >= 1.0)
            throw new AssertionError("la cabine doit s'être déplacée vers le bas.");
    }

    @Test
    void arretUrgence() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(0);

        moteur.monter();
        moteur.etape(false);

        double niveau = moteur.getNiveauActuel();
        moteur.arretUrgence(ECauseArretUrgence.PASSAGER);
        assertEquals("ARRET_URGENCE", moteur.getStatut().name(), "le moteur doit être en arrêt d'urgence.");

        moteur.etape(false);
        assertEquals("ARRET_URGENCE", moteur.getStatut().name(), "le moteur doit toujours être en arrêt d'urgence.");
        assertEquals(niveau, moteur.getNiveauActuel(), "la cabine ne doit pas s'être deplacée.");
    }

    @Test
    void arretProchainNiveau() throws InterruptedException {
        Moteur moteur = TestsUtils.moteurType(0);

        moteur.monter();
        moteur.arretProchainNiveau();

        for (int i = 0 ; i<=10; i++) {
            moteur.etape(false);
        }

        assertEquals("ARRET", moteur.getStatut().name(), "le moteur doit être à l'arrêt.");
    }
}
