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
        moteur = new Moteur(0, 0.05, 0, 1, 2, 3, 4, 5, 6 , 7, 8, 9, 10);
        scc = new SCC(moteur);
        GUI = new GUI(moteur.getNiveaux());
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
