import operative.Moteur;

public class TestsUtils {
    public static Moteur moteurType(double positionInitiale) {
        return new Moteur(positionInitiale,0.1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }
}
