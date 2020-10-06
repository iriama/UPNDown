package applicative.gui;

import java.awt.*;

public class BoutonEtage {
    public Shape forme;
    public boolean clic;
    public boolean survol;

    public BoutonEtage(Shape forme, boolean clic, boolean survol) {
        this.forme = forme;
        this.clic = clic;
        this.survol = survol;
    }
}
