package interfaces;

public interface ISCC {
    void lancer();
    //void actionMoteur();
    void niveauAtteint(double niveauActuel);
    void requete(double niveau);
    void declencherArretUrgence();
    void stopperArretUrgence();
}
