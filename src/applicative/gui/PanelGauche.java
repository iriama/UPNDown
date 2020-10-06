package applicative.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Simulation d'une cabine d'un ascenseur
 */
public class PanelGauche extends JPanel {

    private final GUI gui;
    private final HashMap<Integer, Integer> positionYEtages;
    public final HashMap<Integer, BoutonEtage> boutonsHaut;
    public final HashMap<Integer, BoutonEtage> boutonsBas;
    public boolean pret = false;


    public PanelGauche(GUI gui) {
        super();

        this.gui = gui;

        positionYEtages = new HashMap<Integer, Integer>();
        boutonsHaut = new HashMap<Integer, BoutonEtage>();
        boutonsBas = new HashMap<Integer, BoutonEtage>();
    }

    private void initialiser() {
        int nbEtages = gui.nbEtages();
        Dimension d = gui.dimensionReelle();
        double etageHeight = (d.height - 1) / (nbEtages * 1.0);
        lineWidth = d.width / 4;

        for (int etage = 0; etage < nbEtages; etage++) {
            int y = (int) Math.floor((d.height - 1) - etageHeight * etage);
            positionYEtages.put(etage, y);

            // haut
            Shape boutonHaut = new Rectangle(lineWidth + margeXligne, y - boxSize*2 - 2, boxSize, boxSize);
            boutonsHaut.put(etage, new BoutonEtage(boutonHaut, false, false));

            // bas
            Shape boutonBas = new Rectangle(lineWidth + margeXligne, y - boxSize - 2, boxSize, boxSize);
            boutonsBas.put(etage, new BoutonEtage(boutonBas, false, false));
        }
    }

    private int lineWidth;
    private final int boxSize = 18;
    private final int margeXligne = 35;

    @Override
    protected void paintComponent(Graphics g) {

        if (!pret) {
            initialiser();
            pret = true;
        }

        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, boxSize + 2));

        // -- Etages
        double position = gui.getPosition();
        int nbEtages = gui.nbEtages();
        Graphics2D g2d = (Graphics2D)g;

        for (int etage = 0; etage < nbEtages; etage++) {

            int y = positionYEtages.get(etage);

            if (position == (double) etage && !gui.arretUrgence()) {
                g2d.setColor(Color.GREEN);

            } else {
                g2d.setColor(Color.BLACK);

                // haut
                BoutonEtage boutonHaut = boutonsHaut.get(etage);
                g2d.draw(boutonHaut.forme);

                if (boutonHaut.clic) {
                    g2d.setColor(Color.GREEN);
                    g2d.fill(boutonHaut.forme);
                    g2d.setColor(Color.BLACK);
                }
                else if (boutonHaut.survol) {
                    g2d.setColor(Color.CYAN);
                    g2d.fill(boutonHaut.forme);
                    g2d.setColor(Color.BLACK);
                }

                g2d.drawString("⇑", lineWidth + margeXligne + 4, y - boxSize - 3);

                // bas
                BoutonEtage boutonBas = boutonsBas.get(etage);
                g2d.draw(boutonBas.forme);
                if (boutonBas.clic) {
                    g2d.setColor(Color.GREEN);
                    g2d.fill(boutonBas.forme);
                    g2d.setColor(Color.BLACK);
                }
                else if (boutonBas.survol) {
                    g2d.setColor(Color.CYAN);
                    g2d.fill(boutonBas.forme);
                    g2d.setColor(Color.BLACK);
                }
                g2d.drawString("⇓", lineWidth + margeXligne + 4, y - 3);

            }

            g2d.drawLine(0, y, lineWidth, y);
            g2d.drawString(Integer.toString(etage), lineWidth + 3, y);
        }

        Dimension d = gui.dimensionReelle();
        double etageHeight = (d.height - 1) / (nbEtages * 1.0);

        // -- Cabine
        if (gui.arretUrgence())
            g2d.setColor(Color.RED);
        else if (position == Math.floor(position) && !Double.isInfinite(position))
            g2d.setColor(Color.GREEN);
        else
            g2d.setColor(Color.BLUE);

        int recHeight = (int) Math.floor(etageHeight / 3);
        int recWidth = recHeight;
        int floor = (int)Math.floor(position); // 1
        double progression = position - floor; // 0.4

        int graphPosition = (int) Math.round(positionYEtages.get(floor) - etageHeight * progression) - recHeight;

        g2d.fillRect(lineWidth / 2 - recWidth / 2, graphPosition, recWidth, recHeight);

    }

}
