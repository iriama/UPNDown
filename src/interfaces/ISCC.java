package interfaces;

public interface ISCC {
    void lancer();
    void actionMoteur();
    void requete(double niveau);
    void declencherArretUrgence();
    void stopperArretUrgence();
}
