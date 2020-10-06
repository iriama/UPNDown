package applicative.gui;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class CabinePanel extends JPanel {

    private GUI gui;
    private HashMap<Integer, Integer> positionYEtages;

    public CabinePanel(GUI gui) {
        super();

        this.gui = gui;

        positionYEtages = new HashMap<Integer, Integer>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // -- Etages
        double position = gui.getPosition();
        int nbEtages = gui.nbEtages();
        Dimension d = gui.dimensionReelle();
        double etageHeight = (d.height-1) / (nbEtages*1.0);
        int lineWidth = d.width/4;

        for (int etage=0; etage<nbEtages; etage++) {
            int y =  (int)Math.floor((d.height-1) - etageHeight * etage);

            positionYEtages.put(etage, y);

            if (position == (double)etage) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLACK);
            }

            g.drawLine(0, y, lineWidth, y);
            g.drawString(Integer.toString(etage), lineWidth + 3, y);
        }


        // -- Cabine
        if (position == Math.floor(position) && !Double.isInfinite(position))
            g.setColor(Color.GREEN);
        else
            g.setColor(Color.BLUE);

        int recHeight = (int)Math.floor(etageHeight / 3);
        int recWidth = recHeight;
        double floor = Math.floor(position); // 1
        double progression = position - floor; // 0.4
        int graphPosition = (int)Math.round(positionYEtages.get(floor) - etageHeight * progression) - recHeight;

        g.fillRect(lineWidth/2 - recWidth/2, graphPosition, recWidth, recHeight);

    }

}
