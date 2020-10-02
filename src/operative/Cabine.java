package operative;
import interfaces.ICabine;

public class Cabine implements ICabine {

    private double position;

    public Cabine(double position) {
        this.position = position;
    }

    public void monter(double pas) {
        position += pas;
    }
    public void descendre(double pas) {
        position -= pas;
    }
    public double getPosition() {
       return position;
    }

    @Override
    public String toString() {
        return "[Cabine] position actuelle : " + position;
    }
}
