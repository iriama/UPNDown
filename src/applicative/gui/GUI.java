package applicative.gui;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 400;

    private final int nbEtages;
    private final CabinePanel cabinePanel;
    private double position;

    public GUI(int nbEtages) {
        super();
        this.nbEtages = nbEtages;
        cabinePanel = new CabinePanel(this);
        build();
    }

    public double getPosition() {
        return position;
    }

    public void updatePosition(double position) {
        this.position = position;
        cabinePanel.repaint();
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
        setContentPane(buildContentPanel());
    }

    private JPanel buildContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        panel.add(cabinePanel);

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
