package applicative.gui;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class CabinePanel extends JPanel {

    private GUI gui;
    private HashMap<Double, Integer> positionYNiveaux;

    public CabinePanel(GUI gui) {
        super();

        this.gui = gui;

        positionYNiveaux = new HashMap<Double, Integer>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // -- Niveaux
        int nbNiveaux = gui.getNiveaux().size();
        Dimension d = gui.dimensionReelle();
        double niveauHeight = (d.height-1) / (nbNiveaux*1.0);
        int lineWidth = d.width/3;

        for (int i=0; i<nbNiveaux; i++) {
            int y =  (int)Math.floor((d.height-1) - niveauHeight * i);
            Double niveau = (Double) gui.getNiveaux().toArray()[i];

            positionYNiveaux.put(niveau, y);

            if (Math.abs(gui.getPosition() - niveau) < 0.001) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLACK);
            }

            g.drawLine(0, y, lineWidth, y);
            g.drawString(Integer.toString(niveau.intValue()), lineWidth + 3, y);
        }


        // -- Cabine
        g.setColor(Color.BLUE);
        int recHeight = (int)Math.floor(niveauHeight / 3);
        int recWidth = 20;
        double floor = Math.floor(gui.getPosition()); // 1
        double progression = gui.getPosition() - floor; // 0.4
        int graphPosition = (int)Math.round(positionYNiveaux.get(floor) - niveauHeight * progression) - recHeight;

        g.fillRect(lineWidth/2 - recWidth/2, graphPosition, recWidth, recHeight);

    }

}
