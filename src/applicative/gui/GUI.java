package applicative.gui;

import javax.swing.*;
import java.awt.*;
import java.util.TreeSet;

public class GUI extends JFrame {

    private final int WINDOW_HEIGHT = 600;
    private final int WINDOW_WIDTH = 400;

    private TreeSet<Double> niveaux;
    private CabinePanel cabinePanel;
    private double position;

    public GUI(TreeSet<Double> niveaux) {
        super();
        this.niveaux = niveaux;
        cabinePanel = new CabinePanel(this);
        build();
    }

    public TreeSet<Double> getNiveaux() {
        return niveaux;
    }

    public double getPosition() {
        return position;
    }

    public void updatePosition(double position) {
        this.position = position;
        cabinePanel.repaint();
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
