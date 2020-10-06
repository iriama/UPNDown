package applicative;

import applicative.gui.GUI;
import operative.Moteur;

import javax.swing.*;

public class Simulation implements Runnable {

    private static Moteur moteur;
    private static SCC scc;
    private static GUI GUI;
    private static Thread threadSimulation;

    public static void main(String[] args) throws InterruptedException {
        int nbEtages = 10;

        moteur = new Moteur(0.05, nbEtages);
        scc = new SCC(moteur, nbEtages);
        GUI = new GUI(nbEtages);
        threadSimulation = new Thread(new Simulation());

        SwingUtilities.invokeLater(() -> GUI.setVisible(true));
        threadSimulation.start();

        scc.requeteCabine(5);
        scc.requeteCabine(8);

        threadSimulation.join();
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                moteur.etape(true);
                GUI.updatePosition(moteur.getNiveauActuel());

                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
