package applicative.gui;

import applicative.Requete;
import applicative.SCC;
import enums.EDirection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class GUI extends JFrame {

    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 400;

    private final int nbEtages;
    private final PanelGauche panelGauche;
    private final PanelDroite panelDroite;
    private double position;

    private SCC scc;

    public GUI(int nbEtages, SCC scc) {
        super();
        this.scc = scc;
        this.nbEtages = nbEtages;
        panelGauche = new PanelGauche(this);
        panelDroite = new PanelDroite(this);
        build();

        panelGauche.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseMoved(e);

                Point pointeur = e.getPoint();

                for (int etage = 0; etage < nbEtages; etage++) {
                    BoutonEtage haut = panelGauche.boutonsHaut.get(etage);
                    BoutonEtage bas = panelGauche.boutonsBas.get(etage);

                    if (haut.forme.contains(pointeur)) {
                        scc.requeteEtage(etage, EDirection.HAUT);
                        haut.clic = true;
                        break;
                    }
                    else if (bas.forme.contains(pointeur)) {
                        scc.requeteEtage(etage, EDirection.BAS);
                        bas.clic = true;
                        break;
                    }
                }

                panelGauche.repaint();
            }
        });

        panelDroite.boutonArretUrgence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scc.declencherArretUrgence();
                panelGauche.repaint();
            }
        });

        panelDroite.boutonAnnulationArretUrgence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scc.stopperArretUrgence();
                panelGauche.repaint();
            }
        });

        ActionListener actionBouton = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton bouton = (JButton)e.getSource();
                scc.requeteCabine(Integer.parseInt(bouton.getText()));
                bouton.setBackground(Color.CYAN);
            }
        };

        for (int i=0; i<nbEtages; i++) {
            panelDroite.boutonsEtages.get(i).addActionListener(actionBouton);
        }

    }

    public boolean arretUrgence() {
        return scc.arretUrgence;
    }

    public double getPosition() {
        return position;
    }

    public void updatePosition(double position) {
        this.position = position;

        if (panelGauche.pret) {
            Vector<Requete> file = scc.fileRequetes();
            for (int etage = 0; etage < nbEtages; etage++) {
                BoutonEtage haut = panelGauche.boutonsHaut.get(etage);
                BoutonEtage bas = panelGauche.boutonsBas.get(etage);

                haut.clic = file.contains(new Requete(etage, EDirection.HAUT));
                bas.clic = file.contains(new Requete(etage, EDirection.BAS));
            }
        }

        for (int i=0; i<nbEtages; i++) {
            JButton bouton = panelDroite.boutonsEtages.get(i);

            if (scc.fileRequetes().contains(new Requete(i))) {
                bouton.setBackground(Color.CYAN);
            } else {
                bouton.setBackground(null);
            }
        }

        panelGauche.repaint();
    }

    public int nbEtages() {
        return nbEtages;
    }

    private void build() {
        setTitle("[EQUIPE 27] UPNDown");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        //setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(intialisationPanels());
    }

    private JPanel intialisationPanels() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        panel.add(panelGauche);
        panel.add(panelDroite);

        return panel;
    }

    public Dimension dimensionReelle() {
        Dimension size = getSize();
        Insets insets = getInsets();
        if (insets != null) {
            size.height -= insets.top + insets.bottom;
            size.width -= insets.left + insets.right;
        }
        return size;
    }

}
