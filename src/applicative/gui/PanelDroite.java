package applicative.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PanelDroite extends JPanel {

    private GUI gui;
    public HashMap<Integer, JButton> boutonsEtages;
    public JButton boutonArretUrgence;
    public JButton boutonAnnulationArretUrgence;

    public PanelDroite(GUI gui) {
        super();

        this.gui = gui;
        boutonsEtages = new HashMap<Integer, JButton>();

        setLayout(new GridLayout(2, 1));

        JPanel panelBoutons = new JPanel();
        panelBoutons.setLayout(new GridLayout(gui.nbEtages()/2, 2));

        for (int etage = 0; etage < gui.nbEtages(); etage++) {
            JButton bouton = new JButton(Integer.toString(etage));
            boutonsEtages.put(etage, bouton);
            panelBoutons.add(bouton);
        }

        boutonArretUrgence = new JButton("SOS");
        boutonAnnulationArretUrgence = new JButton("OK");

        boutonArretUrgence.setBackground(Color.ORANGE);
        boutonAnnulationArretUrgence.setBackground(Color.GREEN);

        panelBoutons.add(boutonArretUrgence);
        panelBoutons.add(boutonAnnulationArretUrgence);

        add(panelBoutons);
    }


}
