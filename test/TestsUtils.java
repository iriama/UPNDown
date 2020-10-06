import operative.Moteur;

public class TestsUtils {

    public final static int nbEtages = 11;

    public static Moteur moteurType() {
        return new Moteur(0.1, nbEtages);
    }

    public static Moteur moteurType(int nbEtages) {
        return new Moteur(0.1, nbEtages);
    }
}
